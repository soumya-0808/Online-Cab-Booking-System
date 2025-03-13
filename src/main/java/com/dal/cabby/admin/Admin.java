package com.dal.cabby.admin;

import com.dal.cabby.io.Inputs;
import com.dal.cabby.util.Common;
import com.dal.cabby.util.ConsolePrinter;

import javax.mail.MessagingException;
import java.sql.SQLException;
import java.text.ParseException;

/**
 * This class is responsible for Admin portal. It will expose 2nd page
 * which consists of Login, Registration and Password Recovery.
 * After successful login, it will show the 3rd page which consists of Admin
 * tasks.
 */
public class Admin implements IAdmin {
    private final Inputs inputs;
    private AdminBusinessLayer adminBusinessLayer;

    public Admin(Inputs inputs) throws SQLException {
        this.inputs = inputs;
        try {
            initialize();
        } catch (SQLException e) {
            ConsolePrinter.printErrorMsg("Database error: " + e.getMessage());
            ConsolePrinter.printErrorMsg("Make sure MySQL is running and database is properly set up.");
            throw e;
        }
    }

    /**
     * Initializes instance of dependent objects.
     *
     * @throws SQLException
     */
    private void initialize() throws SQLException {
        AdminDBLayer adminDBLayer = new AdminDBLayer();
        adminBusinessLayer = new AdminBusinessLayer(adminDBLayer, inputs);
    }

    @Override
    public void performTasks() throws SQLException, ParseException, MessagingException, InterruptedException {
        profileManagementTasks();
    }

    /**
     * This method show the options for Login, Registration and Password recovery.
     *
     * @throws SQLException
     * @throws MessagingException
     * @throws InterruptedException
     */
    @Override
    public void profileManagementTasks() throws SQLException, MessagingException, InterruptedException {
        while (true) {
            try {
                Common.displayHeader("ADMIN PORTAL");
                Common.page1Options();
                int input = inputs.getIntegerInput();
                
                switch (input) {
                    case 1:
                        boolean isLoginSuccessful = adminBusinessLayer.login();
                        if (isLoginSuccessful) {
                            performAdminTasks();
                        }
                        break;
                    case 2:
                        boolean isRegistered = adminBusinessLayer.register();
                        if (!isRegistered) {
                            ConsolePrinter.printErrorMsg("Registration failed!");
                        }
                        break;
                    case 3:
                        boolean recoveryStatus = adminBusinessLayer.forgotPassword();
                        if (recoveryStatus) {
                            ConsolePrinter.printSuccessMsg("Password reset successful. Please login with new credentials");
                        }
                        break;
                    case 4:
                        ConsolePrinter.printOutput("Returning to main menu...");
                        return;
                    default:
                        ConsolePrinter.printErrorMsg("Invalid input: " + input);
                        break;
                }
            } catch (Exception e) {
                ConsolePrinter.printErrorMsg("An error occurred: " + e.getMessage());
                e.printStackTrace();
                Thread.sleep(1000); // Give user time to read error
            }
        }
    }

    /**
     * After successful login, Admin will enter the 3rd page which consists
     * of 4 tasks: Approve drivers, Approve Customers, Deactivate drivers and
     * Deactivate customers.
     *
     * @throws SQLException
     */
    @Override
    public void performAdminTasks() throws SQLException {
        while (true) {
            try {
                Common.displayHeader("ADMIN DASHBOARD");
                System.out.println("1. Logout");
                System.out.println("2. Approve Drivers");
                System.out.println("3. Approve Customers");
                System.out.println("4. Deregister Drivers");
                System.out.println("5. Deregister Customers");
                System.out.print("\nEnter choice (1-5): ");

                int input = inputs.getIntegerInput();
                switch (input) {
                    case 1:
                        boolean isLogoutSuccessful = adminBusinessLayer.logout();
                        if (isLogoutSuccessful) {
                            ConsolePrinter.printSuccessMsg("Logged out successfully");
                            return;
                        }
                        break;
                    case 2:
                        adminBusinessLayer.approveDriverAccounts();
                        break;
                    case 3:
                        adminBusinessLayer.approveCustomerAccounts();
                        break;
                    case 4:
                        adminBusinessLayer.deRegisterDriver();
                        break;
                    case 5:
                        adminBusinessLayer.deRegisterCustomer();
                        break;
                    default:
                        ConsolePrinter.printErrorMsg("Invalid input: " + input);
                        break;
                }
            } catch (Exception e) {
                ConsolePrinter.printErrorMsg("An error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}