package com.dal.cabby.booking;

import com.dal.cabby.dbHelper.DBHelper;
import com.dal.cabby.dbHelper.IPersistence;
import com.dal.cabby.pojo.Booking;
import com.dal.cabby.pojo.UserType;
import com.dal.cabby.util.ConsolePrinter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookingService implements IBookingService {
    private final IPersistence iPersistence;

    public BookingService() throws SQLException {
        this.iPersistence = DBHelper.getInstance();
    }

    @Override
    public void saveBooking(Booking booking) throws SQLException {
        String query = String.format("insert into bookings(driver_id, cust_id, cab_id, travel_time, estimated_price, source, destination) values(%d, %d, %d, '%s', %f, '%s', '%s')",
                booking.getDriverId(), booking.getCustomerId(), booking.getCabId(), booking.getTravelTime(), booking.getPrice(), booking.getSource(), booking.getDestination());
        iPersistence.executeCreateOrUpdateQuery(query);
        
        // After saving the booking, notify the driver
        notifyDriver(booking.getDriverId(), booking);
    }
    
    /**
     * Notifies a driver about a new booking by adding a notification entry in the database
     * @param driverId The ID of the driver to notify
     * @param booking The booking details
     * @throws SQLException If there's an error accessing the database
     */
    private void notifyDriver(int driverId, Booking booking) throws SQLException {
        // First check if the notifications table exists, if not create it
        try {
            String checkTableQuery = "SHOW TABLES LIKE 'driver_notifications'";
            ResultSet resultSet = iPersistence.executeSelectQuery(checkTableQuery);
            if (!resultSet.next()) {
                // Table doesn't exist, create it
                String createTableQuery = "CREATE TABLE driver_notifications (" +
                        "notification_id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "driver_id INT, " +
                        "booking_id INT, " +
                        "message TEXT, " +
                        "is_read BOOLEAN DEFAULT false, " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (driver_id) REFERENCES driver(driver_id), " +
                        "FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE" +
                        ")";
                iPersistence.executeCreateOrUpdateQuery(createTableQuery);
            }
            
            // Get the last inserted booking ID
            String getLastBookingIdQuery = "SELECT LAST_INSERT_ID() as booking_id";
            ResultSet bookingIdResult = iPersistence.executeSelectQuery(getLastBookingIdQuery);
            int bookingId = 0;
            if (bookingIdResult.next()) {
                bookingId = bookingIdResult.getInt("booking_id");
            }
            
            if (bookingId > 0) {
                // Create notification message
                String message = String.format("New booking from %s to %s at %s. Estimated fare: $%.2f", 
                        booking.getSource(), booking.getDestination(), booking.getTravelTime(), booking.getPrice());
                
                // Insert notification
                String insertNotificationQuery = String.format(
                        "INSERT INTO driver_notifications(driver_id, booking_id, message) VALUES(%d, %d, '%s')",
                        driverId, bookingId, message);
                iPersistence.executeCreateOrUpdateQuery(insertNotificationQuery);
            }
        } catch (SQLException e) {
            System.err.println("Error notifying driver: " + e.getMessage());
            // Don't throw the exception as this is a non-critical operation
        }
    }

    @Override
    public Booking getBooking(int booking_id) throws SQLException {
        String query = String.format("select * from bookings where booking_id=%d;", booking_id);
        ResultSet resultSet = iPersistence.executeSelectQuery(query);
        if (resultSet.next()) {
            int customerId = resultSet.getInt("cust_id");
            int driverId = resultSet.getInt("driver_id");
            int cabId = resultSet.getInt("cab_id");
            String source = resultSet.getString("source");
            String destination = resultSet.getString("destination");
            String travelTime = resultSet.getString("travel_time");
            double price = resultSet.getDouble("estimated_price");
            boolean isCancelled = resultSet.getBoolean("is_cancelled");
            boolean hasDriverCancelled = resultSet.getBoolean("has_driver_cancelled");
            boolean hasCustomerCancelled = resultSet.getBoolean("has_customer_cancelled");
            Booking booking = new Booking(booking_id, customerId, driverId, cabId, source, destination, travelTime, price, isCancelled);
            booking.setHasDriverCancelled(hasDriverCancelled);
            booking.setCancelled(hasCustomerCancelled);
            booking.setCancelled(isCancelled);
            return booking;
        }
        return null;
    }

    @Override
    public void cancelBooking(int booking_id, UserType cancelledBy) throws SQLException {
        boolean hasDriverCancelled = false;
        boolean hasCustomerCancelled = false;
        if (cancelledBy == UserType.DRIVER) {
            hasDriverCancelled = true;
        } else if (cancelledBy == UserType.CUSTOMER) {
            hasCustomerCancelled = true;
        } else {
            throw new RuntimeException("Wrong usertype: " + cancelledBy.toString());
        }
        String query = String.format("update bookings set is_cancelled=%b, has_driver_cancelled=%b, has_customer_cancelled=%b where booking_id=%d;",
                true, hasDriverCancelled, hasCustomerCancelled, booking_id);
        iPersistence.executeCreateOrUpdateQuery(query);
    }

    @Override
    public Booking getCustomerOpenBooking(int cust_id) throws SQLException {
        String query = String.format("select * from bookings where cust_id=%d and is_trip_done=false and is_cancelled=false order by created_at desc limit 1;", cust_id);
        ResultSet resultSet = iPersistence.executeSelectQuery(query);
        if (resultSet.next()) {
            int bookingId = resultSet.getInt("booking_id");
            int customerId = resultSet.getInt("cust_id");
            int driverId = resultSet.getInt("driver_id");
            int cabId = resultSet.getInt("cab_id");
            String source = resultSet.getString("source");
            String destination = resultSet.getString("destination");
            String travelTime = resultSet.getString("travel_time");
            double price = resultSet.getDouble("estimated_price");
            return new Booking(bookingId, customerId, driverId, cabId, source, destination, travelTime, price, false);
        }
        return null;
    }

    @Override
    public List<Booking> getDriverOpenBookings(int driver_id) throws SQLException {
        String query = String.format("select * from bookings where driver_id=%d and is_trip_done=false and is_cancelled=false;", driver_id);
        ResultSet resultSet = iPersistence.executeSelectQuery(query);
        List<Booking> bookings = new ArrayList<>();
        while (resultSet.next()) {
            int bookingId = resultSet.getInt("booking_id");
            int customerId = resultSet.getInt("cust_id");
            int driverId = resultSet.getInt("driver_id");
            int cabId = resultSet.getInt("cab_id");
            String source = resultSet.getString("source");
            String destination = resultSet.getString("destination");
            String travelTime = resultSet.getString("travel_time");
            double price = resultSet.getDouble("estimated_price");
            Booking booking = new Booking(bookingId, customerId, driverId, cabId, source, destination, travelTime, price, false);
            bookings.add(booking);
        }
        return bookings;
    }

    @Override
    public int getCustomerTotalBookings(int cust_id) throws SQLException {
        String query = String.format("select count(*) from bookings where cust_id=%d;", cust_id);
        ResultSet resultSet = iPersistence.executeSelectQuery(query);
        if (resultSet.next()) {
            return resultSet.getInt(1);

        }
        return -1;
    }

    @Override
    public int getDriverTotalBookings(int driver_id) throws SQLException {
        String query = String.format("select count(*) from bookings where driver_id=%d;", driver_id);
        ResultSet resultSet = iPersistence.executeSelectQuery(query);
        if (resultSet.next()) {
            return resultSet.getInt(1);
        }
        return -1;
    }

    @Override
    public void markBookingComplete(int bookingId) throws SQLException {
        String q = String.format("update bookings set is_trip_done=true where booking_id=%d", bookingId);
        iPersistence.executeCreateOrUpdateQuery(q);
    }

    @Override
    public void completeTrip(int bookingId, int driverId, int custId, double tripAmount, double distanceCovered,
                             String tripStartTime, String tripEndTime) throws SQLException, ParseException {
        java.sql.Date startTime = getSQLFormatDate(tripStartTime);
        java.sql.Date endTime = getSQLFormatDate(tripEndTime);
        String q = String.format("insert into trips(" +
                        "driver_id, cust_id, booking_id, trip_amount, distance_covered, " +
                        "trip_start_time, trip_end_time) values(%d, %d, %d, %f, %f, '%s', '%s')",
                driverId, custId, bookingId, tripAmount, distanceCovered, tripStartTime, tripEndTime);
        iPersistence.executeCreateOrUpdateQuery(q);
    }

    @Override
    public int getDriverUnreadNotificationsCount(int driverId) throws SQLException {
        try {
            // Check if the notifications table exists
            String checkTableQuery = "SHOW TABLES LIKE 'driver_notifications'";
            ResultSet tableResult = iPersistence.executeSelectQuery(checkTableQuery);
            if (!tableResult.next()) {
                // Table doesn't exist
                return 0;
            }
            
            // Get count of unread notifications
            String query = String.format(
                "SELECT COUNT(*) as count FROM driver_notifications " +
                "WHERE driver_id = %d AND is_read = false", driverId);
            
            ResultSet resultSet = iPersistence.executeSelectQuery(query);
            
            if (resultSet.next()) {
                return resultSet.getInt("count");
            }
            
            return 0;
        } catch (SQLException e) {
            System.err.println("Error getting notification count: " + e.getMessage());
            return 0; // Return 0 instead of throwing exception for non-critical operation
        }
    }
    
    @Override
    public ResultSet getAndMarkDriverNotifications(int driverId) throws SQLException {
        try {
            // Check if the notifications table exists
            String checkTableQuery = "SHOW TABLES LIKE 'driver_notifications'";
            ResultSet tableResult = iPersistence.executeSelectQuery(checkTableQuery);
            if (!tableResult.next()) {
                // Table doesn't exist
                return null;
            }
            
            // Get unread notifications for the driver with customer information
            String query = String.format(
                "SELECT n.notification_id, n.message, n.created_at, " +
                "b.booking_id, b.source, b.destination, b.travel_time, b.estimated_price, " +
                "c.name as customer_name, c.phone as customer_phone " +
                "FROM driver_notifications n " +
                "JOIN bookings b ON n.booking_id = b.booking_id " +
                "JOIN customer c ON b.cust_id = c.cust_id " +
                "WHERE n.driver_id = %d AND n.is_read = false " +
                "ORDER BY n.created_at DESC", driverId);
            
            ResultSet resultSet = iPersistence.executeSelectQuery(query);
            
            // Mark all notifications as read in a separate query
            String updateQuery = String.format(
                "UPDATE driver_notifications SET is_read = true WHERE driver_id = %d AND is_read = false", 
                driverId);
            iPersistence.executeCreateOrUpdateQuery(updateQuery);
            
            return resultSet;
        } catch (SQLException e) {
            System.err.println("Error getting and marking notifications: " + e.getMessage());
            throw e; // Rethrow the exception to be handled by the caller
        }
    }

    private java.sql.Date getSQLFormatDate(String dateInStr) throws ParseException {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = formatter.parse(dateInStr);
        return new java.sql.Date(date.getTime());
    }
}
