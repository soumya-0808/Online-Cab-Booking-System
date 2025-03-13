package com.dal.cabby.customer;

import com.dal.cabby.io.Inputs;
import com.dal.cabby.profileManagement.LoggedInProfile;
import com.dal.cabby.profileManagement.ProfileStatus;
import com.dal.cabby.util.Common;

import javax.mail.MessagingException;
import java.sql.SQLException;
import java.text.ParseException;

import static com.dal.cabby.util.ConsolePrinter.printErrorMsg;
import static com.dal.cabby.util.ConsolePrinter.printSuccessMsg;

/**
 * This class implements presentation layer for the Customer.
 */
public class Customer implements ICustomer {
    private final Inputs inputs;
    private CustomerBusinessLayer customerBusinessLayer;
    private ProfileStatus profileStatus;

    public Customer(Inputs inputs) throws SQLException {
        this.inputs = inputs;
        initialize();
    }

    private void initialize() throws SQLException {
        customerBusinessLayer = new CustomerBusinessLayer(inputs);
        profileStatus = new ProfileStatus();
    }

    @Override
    public void performTasks() throws SQLException, ParseException, MessagingException, InterruptedException {
        profileManagementTasks();
    }

    @Override
    public void profileManagementTasks() throws SQLException, ParseException, MessagingException, InterruptedException {
        while (true) {
            Common.page1Options();
            int input = inputs.getIntegerInput();
            switch (input) {
                case 1:
                    boolean isLoginSuccessful = customerBusinessLayer.login();
                    if (isLoginSuccessful) {
                        printSuccessMsg("Login successful");
                        if (!profileStatus.isCustomerApproved(LoggedInProfile.getLoggedInId())) {
                            printErrorMsg("You are in deactivated state rigth now. " +
                                    "Please contact Soumya Ranjan - CB Raman College: soumya@clg.com");
                            return;
                        }
                        performCustomerTasks();
                    }
                    break;
                case 2:
                    boolean isRegistered = customerBusinessLayer.register();
                    if (!isRegistered) {
                        System.out.println("Registration failed!");
                    }
                    break;
                case 3:
                    boolean recoveryStatus = customerBusinessLayer.forgotPassword();
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

    @Override
    public void performCustomerTasks() throws SQLException, ParseException {
        while (true) {
            System.out.println("\nPlease enter input:");
            System.out.println("1. Logout");
            System.out.println("2. Book Cabs");
            System.out.println("3. View previous Rides");
            System.out.println("4. Rate driver for the trip");
            System.out.println("5. View your current rating");
            System.out.println("6. Buy Coupons");
            System.out.println("7. Cancel booking");
            System.out.println("8. View upcoming trips");
            int input = inputs.getIntegerInput();
            switch (input) {
                case 1:
                    if (customerBusinessLayer.logout()) {
                        return;
                    }
                    break;
                case 2:
                    customerBusinessLayer.bookRides();
                    break;
                case 3:
                    customerBusinessLayer.showRides();
                    break;
                case 4:
                    customerBusinessLayer.rateDriver();
                    break;
                case 5:
                    customerBusinessLayer.viewRatings();
                    break;
                case 6:
                    customerBusinessLayer.buyCoupons();
                    break;
                case 7:
                    customerBusinessLayer.cancelBooking();
                    break;
                case 8:
                    customerBusinessLayer.viewUpcomingTrip();
                    break;
                default:
                    System.out.println("\nInvalid Input");
                    break;
            }
        }
    }
}
