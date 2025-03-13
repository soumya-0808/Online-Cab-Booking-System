package com.dal.cabby.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
    This class will contain all the common methods which can be reused across multiple
    places in the project.
 */
public class Common {
    // Return false if the input is outside the range of start and end.
    public static boolean isInputInvalid(int input, int start, int end) {
        return input < start || input > end;
    }

    // Show the input options and capture the input entered by the user and return it.
    public static void page1Options() {
        System.out.println("\n============= USER OPTIONS =============");
        System.out.println("Please select an option:");
        System.out.println("1: Login");
        System.out.println("2: Registration");
        System.out.println("3: Forgot password?");
        System.out.println("4: Exit");
        System.out.println("========================================");
        System.out.print("Enter your choice (1-4): ");
    }

    public static void simulateCabTrip() {
        try {
            System.out.println("\n======= TRIP IN PROGRESS =======");
            System.out.println("Trip started!");
            for (int i = 0; i < 5; i++) {
                System.out.println("Trip is going on.... " + (i+1) + "/5");
                Thread.sleep(1000);
            }
            System.out.println("Trip finished!");
            System.out.println("================================");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static Date parseDate(String dateStr, String format) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.parse(dateStr);
    }

    /**
     * Clears the console by printing multiple newlines
     */
    public static void clearScreen() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

    /**
     * Displays a section header
     */
    public static void displayHeader(String title) {
        String border = "";
        for (int i = 0; i < title.length() + 8; i++) {
            border += "=";
        }
        System.out.println("\n" + border);
        System.out.println("    " + title);
        System.out.println(border);
    }

    public static void main(String[] args) throws ParseException {
        Date date = parseDate("07/23/2021 14:23:23", "MM/dd/yyyy HH:mm");
        System.out.println("You travel date is " + date.toLocaleString());
    }
}
