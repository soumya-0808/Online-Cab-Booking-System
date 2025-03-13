package com.dal.cabby.driver;

import com.dal.cabby.io.Inputs;
import com.dal.cabby.money.DriverEarnings;
import com.dal.cabby.money.IDriverEarnings;
import com.dal.cabby.profileManagement.LoggedInProfile;
import com.dal.cabby.profileManagement.ProfileStatus;
import com.dal.cabby.util.Common;

import javax.mail.MessagingException;
import java.sql.SQLException;
import java.text.ParseException;

import static com.dal.cabby.util.ConsolePrinter.printErrorMsg;
import static com.dal.cabby.util.ConsolePrinter.printSuccessMsg;

/**
 * This class implements IDriver interface and implements the
 * presentation layer of the Driver user.
 */
public class Driver implements IDriver {
    private final Inputs inputs;
    private DriverBusinessLayer driverBusinessLayer;
    private ProfileStatus profileStatus;
    private IDriverEarnings driverEarnings;

    public Driver(Inputs inputs) throws SQLException, ParseException {
        this.inputs = inputs;
        intialize();
    }

    /**
     * Initialize the instance of the dependent objects.
     *
     * @throws SQLException
     */
    private void intialize() throws SQLException {
        driverBusinessLayer = new DriverBusinessLayer(inputs);
        profileStatus = new ProfileStatus();
        driverEarnings = new DriverEarnings();
    }

    @Override
    public void performTasks() throws SQLException, ParseException, MessagingException, InterruptedException {
        profileManagementTasks();
    }

    /**
     * This method implements presentation layer for Login, Registration and Password recovery.
     *
     * @throws SQLException
     * @throws ParseException
     * @throws MessagingException
     * @throws InterruptedException
     */
    @Override
    public void profileManagementTasks() throws SQLException, ParseException, MessagingException, InterruptedException {
        while (true) {
            Common.page1Options();
            int input = inputs.getIntegerInput();
            switch (input) {
                case 1:
                    boolean isLoginSuccessful = driverBusinessLayer.login();
                    if (isLoginSuccessful) {
                        printSuccessMsg("Login successful");
                        if (!profileStatus.isDriverAproved(LoggedInProfile.getLoggedInId())) {
                            printErrorMsg("You are in deactivated state rigth now. " +
                                    "Please contact Soumya Ranjan - CB Raman College: soumya@clg.com");
                            return;
                        }
                        performDriverTasks();
                    }
                    break;
                case 2:
                    boolean isRegistered = driverBusinessLayer.register();
                    if (!isRegistered) {
                        System.out.println("Registration failed!");
                    }
                    break;
                case 3:
                    boolean recoveryStatus = driverBusinessLayer.forgotPassword();
                    if (recoveryStatus) {
                        System.out.println("Password reset successful. Please login with new credentials");
                    }
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid input: " + input);
            }
        }
    }

    /**
     * This method implements the presentation layer for the Driver tasks
     * which he will be able to perform once he do successfull login.
     *
     * @throws SQLException
     * @throws ParseException
     */
    @Override
    public void performDriverTasks() throws SQLException, ParseException {
        while (true) {
            // Check for unread notifications
            int notificationCount = 0;
            try {
                notificationCount = driverBusinessLayer.getUnreadNotificationsCount();
            } catch (Exception e) {
                // Ignore errors when checking notifications
            }
            
            displayDriverMenu(notificationCount);
            
            int input = inputs.getIntegerInput();
            switch (input) {
                case 1:
                    boolean isLogoutSuccessful = driverBusinessLayer.logout();
                    if (isLogoutSuccessful) {
                        return;
                    }
                    break;
                case 2:
                    driverBusinessLayer.startTrip();
                    break;
                case 3:
                    driverBusinessLayer.viewRides();
                    break;
                case 4:
                    earningsPage();
                    break;
                case 5:
                    driverBusinessLayer.rateCustomer();
                    break;
                case 6:
                    driverBusinessLayer.viewRatings();
                    break;
                case 7:
                    driverBusinessLayer.buyCoupons();
                    break;
                case 8:
                    driverBusinessLayer.cancelBooking();
                    break;
                case 9:
                    driverBusinessLayer.viewUpcomingTrip();
                    break;
                case 10:
                    driverBusinessLayer.checkNotifications();
                    break;
                default:
                    System.out.println("\nInvalid Input");
                    break;
            }
        }
    }

    /**
     * Display the driver menu with notification count
     * @param notificationCount Number of unread notifications
     */
    private void displayDriverMenu(int notificationCount) {
        System.out.println("\n===== DRIVER MENU =====");
        System.out.println("1. Logout");
        System.out.println("2. Start trip");
        System.out.println("3. View previous rides");
        System.out.println("4. View incomes");
        System.out.println("5. Rate customer for the trip");
        System.out.println("6. View your current rating");
        System.out.println("7. Buy Coupons");
        System.out.println("8. Cancel booking");
        System.out.println("9. View upcoming trips");
        
        // Show notification count if there are any
        if (notificationCount > 0) {
            System.out.println("10. Check new booking notifications (" + notificationCount + " new)");
        } else {
            System.out.println("10. Check booking notifications");
        }
        System.out.println("======================");
        System.out.print("Enter your choice: ");
    }

    private void earningsPage() throws SQLException {
        int userID = LoggedInProfile.getLoggedInId();
        System.out.println("\n**** Earnings Page ****");
        System.out.println("1. Daily earnings: ");
        System.out.println("2. Monthly earnings: ");
        System.out.println("3. Earning between a specific period: ");
        System.out.println("4. Return to the previous page: ");
        System.out.print("Please enter a selection: ");
        int input = inputs.getIntegerInput();
        switch (input) {
            case 1:
                System.out.print("Enter the date in DD/MM/YYYY format: ");
                String inputDate = inputs.getStringInput();
                System.out.println(driverEarnings.getDailyEarnings(userID, inputDate));
                break;
            case 2:
                System.out.print("Enter the month in MM/YYYY format: ");
                String inputMonth = inputs.getStringInput();
                System.out.println(driverEarnings.getMonthlyEarnings(userID, inputMonth));
                break;
            case 3:
                System.out.print("Enter the start date (DD/MM/YYYY): ");
                String startDate = inputs.getStringInput();
                System.out.print("Enter the end date (DD/MM/YYYY): ");
                String endDate = inputs.getStringInput();
                System.out.println(driverEarnings.getSpecificPeriodEarnings(userID, startDate,
                    endDate));
                break;
            case 4:
                return;
            default:
                System.out.println("\nInvalid Selection");
                break;
        }
    }
}