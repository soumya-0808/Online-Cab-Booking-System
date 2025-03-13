package com.dal.cabby;

import com.dal.cabby.dbHelper.DBHelper;
import com.dal.cabby.io.InputFromUser;
import com.dal.cabby.io.Inputs;
import com.dal.cabby.prelogin.PreLoginPage;
import com.dal.cabby.util.Common;
import com.dal.cabby.util.ConsolePrinter;

import java.sql.SQLException;

// Main starting class
public class Application {
    public static void main(String[] args) {
        DBHelper dbHelper = null;
        
        try {
            // Clear console for better visibility
            Common.clearScreen();
            
            System.out.println("=================================================");
            System.out.println("         WELCOME TO CBRAMAN SOUMYA LAUNCHER      ");
            System.out.println("=================================================");
            System.out.println("A comprehensive cab booking management system");
            System.out.println();
            
            // Display developer info
            System.out.println("***** Developed by Soumya Ranjan - CB Raman College *****");
            System.out.println("***** CBRaman Soumya Launcher: A one stop app for your cab booking *****");
            System.out.println();
            
            // Initialize input system
            Inputs inputs = new InputFromUser();
            
            // Start the application
            PreLoginPage preLoginPage = new PreLoginPage(inputs);
            preLoginPage.start();
            
        } catch (Exception e) {
            System.err.println("An error occurred in the application:");
            e.printStackTrace();
        } finally {
            // Clean up database connection
            try {
                if (dbHelper == null) {
                    dbHelper = DBHelper.getInstance();
                }
                if (dbHelper != null) {
                    dbHelper.close();
                    System.out.println("Database connection closed successfully.");
                }
            } catch (SQLException throwables) {
                System.err.println("Error closing database connection:");
                throwables.printStackTrace();
            }
            
            System.out.println("\nThank you for using CBRaman Soumya Launcher!");
            System.out.println("Developed by Soumya Ranjan - CB Raman College");
        }
    }
    
    /**
     * Clear console for better visibility
     */
    private static void clearConsole() {
        Common.clearScreen();
    }
}
