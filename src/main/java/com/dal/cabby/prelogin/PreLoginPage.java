package com.dal.cabby.prelogin;

import com.dal.cabby.admin.Admin;
import com.dal.cabby.customer.Customer;
import com.dal.cabby.dbHelper.DBHelper;
import com.dal.cabby.dbHelper.IPersistence;
import com.dal.cabby.driver.Driver;
import com.dal.cabby.io.Inputs;
import com.dal.cabby.util.Common;
import com.dal.cabby.util.ConsolePrinter;

import javax.mail.MessagingException;
import java.sql.SQLException;
import java.text.ParseException;

// Prelogin page when user visit the CBRaman Soumya Launcher
public class PreLoginPage {
    private final Inputs inputs;

    public PreLoginPage(Inputs inputs) throws SQLException {
        this.inputs = inputs;
    }

    /* Starting point of Prelogin page. */
    public void start() {
        try {
            welcomeMessage();
            page1();
        } catch (Exception e) {
            ConsolePrinter.printErrorMsg("An error occurred in the application:");
            e.printStackTrace();
        }
    }

    /* Show welcome message when user visits CBRaman Soumya Launcher */
    private void welcomeMessage() {
        // Welcome message is now handled in the Application class
    }

    private void page1() throws SQLException, ParseException, MessagingException, InterruptedException {
        while (true) {
            try {
                System.out.println("\nPlease select your role:");
                System.out.println("1: Admin");
                System.out.println("2: Driver");
                System.out.println("3: Customer");
                System.out.println("4: Exit Application");
                System.out.print("Enter your choice: ");
                
                int input = inputs.getIntegerInput();
                
                if (input == 4) {
                    System.out.println("Exiting application...");
                    return;
                }
                
                switch (input) {
                    case 1:
                        Admin admin = new Admin(inputs);
                        admin.performTasks();
                        break;
                    case 2:
                        Driver driver = new Driver(inputs);
                        driver.performTasks();
                        break;
                    case 3:
                        Customer customer = new Customer(inputs);
                        customer.performTasks();
                        break;
                    default:
                        System.out.println("Invalid input: " + input);
                }
            } catch (Exception e) {
                System.err.println("An error occurred processing your input:");
                e.printStackTrace();
                System.out.println("Returning to main menu...");
            }
        }
    }
}
