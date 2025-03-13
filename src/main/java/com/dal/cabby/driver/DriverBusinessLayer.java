package com.dal.cabby.driver;

import com.dal.cabby.booking.BookingService;
import com.dal.cabby.booking.IBookingService;
import com.dal.cabby.dbHelper.DBHelper;
import com.dal.cabby.dbHelper.IPersistence;
import com.dal.cabby.incentives.CustomerBonus;
import com.dal.cabby.incentives.ICustomerBonus;
import com.dal.cabby.io.Inputs;
import com.dal.cabby.money.BuyCouponsPage;
import com.dal.cabby.pojo.Booking;
import com.dal.cabby.pojo.UserType;
import com.dal.cabby.profileManagement.*;
import com.dal.cabby.rating.IRatings;
import com.dal.cabby.rating.Ratings;
import com.dal.cabby.rides.DisplayRidesPage;
import com.dal.cabby.rides.IDisplayRidesPage;
import com.dal.cabby.score.CancellationScorer;
import com.dal.cabby.util.Common;
import com.dal.cabby.util.ConsolePrinter;

import javax.mail.MessagingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

/**
 * This class implements the business layer for the driver user. It implements
 * the business layer for the profile managements and driver tasks.
 */
class DriverBusinessLayer {
    private final Inputs inputs;
    private IRatings iRatings = null;
    private IBookingService iBookingService = null;
    private IPersistence iPersistence = null;
    private double initialScore = 4.0;

    public DriverBusinessLayer(Inputs inputs) throws SQLException {
        this.inputs = inputs;
        initialize();
    }

    /**
     * This method initialize the instance for the dependent objects.
     *
     * @throws SQLException
     */
    private void initialize() throws SQLException {
        iRatings = new Ratings();
        iBookingService = new BookingService();
        iPersistence = DBHelper.getInstance();
    }

    /**
     * @return return true for successful login, else return false.
     * @throws InterruptedException
     */
    public boolean login() throws InterruptedException {
        System.out.println("Welcome to Driver login page");
        ILogin ILogin = new Login(inputs);
        if (ILogin.attemptLogin(UserType.DRIVER)) {
            System.out.println("Login successful");
            System.out.printf("LoggedID: %d, LoggedIn name: %s\n",
                    LoggedInProfile.getLoggedInId(), LoggedInProfile.getLoggedInName());
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return return true for successful registration else return false.
     */
    public boolean register() {
        System.out.println("Welcome to Driver registration page");
        IRegistration IRegistration = new Registration(inputs);
        return IRegistration.registerUser(UserType.DRIVER);
    }

    /**
     * This method implements password recovery option for Driver user.
     *
     * @return return true for successful password recovery else return false.
     * @throws MessagingException
     * @throws InterruptedException
     */
    public boolean forgotPassword() throws MessagingException, InterruptedException {
        System.out.println("Welcome to Driver forgot password page");
        IForgotPassword IForgotPassword = new ForgotPassword(inputs);
        return IForgotPassword.passwordUpdateProcess(UserType.DRIVER);
    }

    /**
     * @return return true for successful logout else return false.
     */
    boolean logout() {
        return new Logout(inputs).logout();
    }

    /**
     * Implements the start trip from upcoming trips options.
     *
     * @throws SQLException
     * @throws ParseException
     */
    void startTrip() throws SQLException, ParseException {
        List<Booking> bookingsList = iBookingService.getDriverOpenBookings(LoggedInProfile.getLoggedInId());
        if (bookingsList.size() == 0) {
            System.out.println("You have no new bookings\n");
            return;
        }
        System.out.println("List of bookings: ");
        for (Booking b : bookingsList) {
            System.out.printf("BookingId: %d, CustomerId: %d, Source: %s, Destination: %s, Travel-Time: %s\n",
                    b.getBookingId(), b.getCustomerId(), b.getSource(), b.getDestination(), b.getTravelTime());
        }
        System.out.println("Enter the bookingId for which you want to start the trip: ");
        int input = inputs.getIntegerInput();
        iBookingService.markBookingComplete(input);
        Common.simulateCabTrip();
        for (Booking b : bookingsList) {
            if (b.getBookingId() == input) {
                iBookingService.completeTrip(input, LoggedInProfile.getLoggedInId(), b.getCustomerId(), 5.6, 9.8,
                        "2021-01-24 12:35:16", "2021-01-24 12:55:16");
            }
        }
    }

    /**
     * Implements view previous rides for the driver user.
     *
     * @throws SQLException
     */
    void viewRides() throws SQLException {
        IDisplayRidesPage ridesPage = new DisplayRidesPage();
        ridesPage.ridesOptions(LoggedInProfile.getLoggedInId(), UserType.DRIVER);
    }

    /**
     * Rate the customer for the previous rides.
     *
     * @throws SQLException
     */
    void rateCustomer() throws SQLException {
        System.out.println("Rating customer for the completed trip is " +
                "mandatory in the Cabby. It helps us to improve our services." +
                "Hence please rate the customer for the trips");
        System.out.println("Enter customer id:");
        int cust_id = inputs.getIntegerInput();
        System.out.println("Enter trip id:");
        int trip_id = inputs.getIntegerInput();
        System.out.println("Enter the rating between 1-5:");
        int rating = inputs.getIntegerInput();
        IRatings IRatings = new Ratings();
        IRatings.addCustomerRating(cust_id, trip_id, rating);
        ICustomerBonus customerBonus = new CustomerBonus();
        int bonus = customerBonus.giveCustomerBonus(rating);
        ConsolePrinter.printSuccessMsg(String.format("Customer with id: %d is eligible for cashback of %d ",
                cust_id, bonus));
    }

    /**
     * View average rating for the driver.
     *
     * @throws SQLException
     */
    void viewRatings() throws SQLException {
        double avgRating = iRatings.getAverageRatingOfDriver(LoggedInProfile.getLoggedInId());
        if (avgRating == 0) {
            System.out.println("You don't have any rating at the moment");
            return;
        }
        System.out.println("Your average rating is: " + avgRating);
    }

    /**
     * Buy coupons from the money.
     *
     * @throws SQLException
     */
    void buyCoupons() throws SQLException {
        BuyCouponsPage couponsPage = new BuyCouponsPage();
        couponsPage.couponsPage(LoggedInProfile.getLoggedInId(), UserType.DRIVER);
    }

    /**
     * Cancel the upcoming booking.
     *
     * @throws SQLException
     */
    void cancelBooking() throws SQLException {
        IBookingService iBookingService = new BookingService();
        List<Booking> bookingList = iBookingService.getDriverOpenBookings(LoggedInProfile.getLoggedInId());
        if (bookingList.size() == 0) {
            ConsolePrinter.printOutput("You have no booking to cancel.");
            return;
        }
        System.out.println("These are new bookings:");
        for (Booking booking : bookingList) {
            System.out.printf("BookingId: %d, Source: %s, Destination: %s\n",
                    booking.getBookingId(), booking.getSource(), booking.getDestination());
        }
        System.out.println("Now, select the booking-id you want to cancel:");
        int bookingId = inputs.getIntegerInput();
        boolean isBookingPresent = false;
        for (Booking b : bookingList) {
            if (b.getBookingId() == bookingId) {
                isBookingPresent = true;
                break;
            }
        }
        if (!isBookingPresent) {
            ConsolePrinter.printErrorMsg("Invalid booking-id: " + bookingId);
            return;
        }
        iBookingService.cancelBooking(bookingId, UserType.DRIVER);
        CancellationScorer cancellationScorer = new CancellationScorer();
        cancellationScorer.driverCancelled(initialScore, true);
        ConsolePrinter.printSuccessMsg(
                String.format("Your booking with bookingId: %d is cancelled", bookingId));
    }

    /**
     * View upcoming trips of the driver.
     *
     * @throws SQLException
     */
    void viewUpcomingTrip() throws SQLException {
        IBookingService iBookingService = new BookingService();
        List<Booking> bookingList = iBookingService.getDriverOpenBookings(LoggedInProfile.getLoggedInId());
        if (bookingList.size() == 0) {
            ConsolePrinter.printOutput("You have no booking to cancel.");
            return;
        }
        System.out.println("These are your upcoming trips:");
        for (Booking booking : bookingList) {
            String bookingDetails = String.format("Booking details: Source: %s , Destination: %s ,Fare: %f",
                    booking.getSource(), booking.getDestination(), booking.getPrice());
            System.out.println(bookingDetails);
        }
    }

    /**
     * Check for new booking notifications for the driver
     * @throws SQLException If there's an error accessing the database
     */
    void checkNotifications() throws SQLException {
        int driverId = LoggedInProfile.getLoggedInId();
        
        // First check if there are any notifications
        int notificationCount = iBookingService.getDriverUnreadNotificationsCount(driverId);
        if (notificationCount == 0) {
            ConsolePrinter.printOutput("No new notifications");
            return;
        }
        
        try {
            // Get unread notifications and mark them as read
            ResultSet resultSet = iBookingService.getAndMarkDriverNotifications(driverId);
            
            if (resultSet == null) {
                ConsolePrinter.printOutput("No new notifications");
                return;
            }
            
            boolean hasNotifications = false;
            while (resultSet.next()) {
                hasNotifications = true;
                int bookingId = resultSet.getInt("booking_id");
                String message = resultSet.getString("message");
                String createdAt = resultSet.getString("created_at");
                String source = resultSet.getString("source");
                String destination = resultSet.getString("destination");
                String travelTime = resultSet.getString("travel_time");
                double price = resultSet.getDouble("estimated_price");
                String customerName = resultSet.getString("customer_name");
                String customerPhone = resultSet.getString("customer_phone");
                
                System.out.println("\n=== NEW BOOKING NOTIFICATION ===");
                System.out.println("Booking ID: " + bookingId);
                System.out.println("Time: " + createdAt);
                System.out.println("Customer: " + customerName);
                System.out.println("Phone: " + customerPhone);
                System.out.println("From: " + source);
                System.out.println("To: " + destination);
                System.out.println("When: " + travelTime);
                System.out.println("Fare: $" + String.format("%.2f", price));
                System.out.println("================================");
            }
            
            if (!hasNotifications) {
                ConsolePrinter.printOutput("No new notifications");
            }
        } catch (SQLException e) {
            System.err.println("Error checking notifications: " + e.getMessage());
            // Don't throw the exception as this is a non-critical operation
        }
    }

    /**
     * Get the count of unread notifications for the driver
     * @return The number of unread notifications
     * @throws SQLException If there's an error accessing the database
     */
    int getUnreadNotificationsCount() throws SQLException {
        int driverId = LoggedInProfile.getLoggedInId();
        return iBookingService.getDriverUnreadNotificationsCount(driverId);
    }
}
