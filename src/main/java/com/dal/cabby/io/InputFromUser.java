package com.dal.cabby.io;

import java.util.Scanner;

/**
 * This class implements the Inputs interface. It will ask the user to enter
 * any input which is being asked by the application.
 */
public class InputFromUser implements Inputs {
    private final Scanner scanner;
    private boolean needsNextLine = false;

    public InputFromUser() {
        scanner = new Scanner(System.in);
    }

    /**
     * @return return the integer input entered by user.
     */
    @Override
    public int getIntegerInput() {
        try {
            // Clear scanner buffer if needed
            if (needsNextLine) {
                scanner.nextLine();
                needsNextLine = false;
            }
            
            // This will block until user inputs something
            int value = scanner.nextInt();
            needsNextLine = true; // Mark that we need to consume the line
            return value;
        } catch (Exception e) {
            // If input is not an integer, consume it and try again
            System.out.println("Invalid input. Please enter a number:");
            scanner.nextLine(); // clear the scanner
            return getIntegerInput();
        }
    }

    /**
     * @return return the String input entered by user.
     */
    @Override
    public String getStringInput() {
        // Clear scanner buffer if needed
        if (needsNextLine) {
            scanner.nextLine();
            needsNextLine = false;
        }
        
        // Scanner.nextLine() will block until user inputs something
        return scanner.nextLine();
    }

    /**
     * @return return the double input entered by user.
     */
    @Override
    public double getDoubleInput() {
        try {
            // Clear scanner buffer if needed
            if (needsNextLine) {
                scanner.nextLine();
                needsNextLine = false;
            }
            
            // This will block until user inputs something
            double value = scanner.nextDouble();
            needsNextLine = true; // Mark that we need to consume the line
            return value;
        } catch (Exception e) {
            // If input is not a double, consume it and try again
            System.out.println("Invalid input. Please enter a number:");
            scanner.nextLine(); // clear the scanner
            return getDoubleInput();
        }
    }

    /**
     * @return return the word input entered by user.
     */
    @Override
    public String getWordInput() {
        // Clear scanner buffer if needed
        if (needsNextLine) {
            scanner.nextLine();
            needsNextLine = false;
        }
        
        // Scanner.next() will block until user inputs something
        String value = scanner.next();
        needsNextLine = true; // Mark that we need to consume the line
        return value;
    }
}
