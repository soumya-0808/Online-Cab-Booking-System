package com.dal.cabby.cabSelection;
import com.dal.cabby.cabPrice.CabPriceCalculator;
import com.dal.cabby.cabPrice.ICabPriceCalculator;
import com.dal.cabby.dbHelper.DBHelper;
import com.dal.cabby.dbHelper.IPersistence;
import com.dal.cabby.io.InputFromUser;
import com.dal.cabby.io.Inputs;
import com.dal.cabby.pojo.Booking;
import com.dal.cabby.util.ConsolePrinter;

import java.sql.SQLException;

public class CabSelection implements ICabSelection{
    CabSelectionDAO bestCab;
    private IPersistence iPersistence;
    private Inputs inputs;
    private ICabPriceCalculator cabPriceCalculator;
    private ICabSelectionWithGender cabSelectionWithGender;
    private ICabSelectionWithoutGender cabSelectionWithoutGender;
    public String sourceLocation, destinationLocation;

    public CabSelection(Inputs inputs) throws SQLException {
        this.inputs = inputs;
        cabPriceCalculator = new CabPriceCalculator(inputs);
        try {
            iPersistence = DBHelper.getInstance();
        } catch (SQLException e) {
            ConsolePrinter.printErrorMsg("Database connection error: " + e.getMessage());
            throw e;
        }
        cabSelectionWithGender = new CabSelectionWithGender(inputs, this);
        cabSelectionWithoutGender = new CabSelectionWithoutGender(inputs, this);
    }

    public static void main(String[] args) throws SQLException {
        CabSelection cabSelection = new CabSelection(new InputFromUser());
        cabSelection.preferredCab(1, 20);
    }

    /*
        This method serves as presentation layer for Cab Selection feature. This method takes Cab preference,source and
        destination locations from user to book Cab. It also gives an option to book a cab based on gender of driver.
     */
    @Override
    public Booking preferredCab(int custId, double hour) throws SQLException {
        try {
            System.out.println("Enter your cab preference.");
            System.out.println("1. Micro or Mini");
            System.out.println("2. Prime Sedan");
            System.out.println("3. Prime SUV");
            System.out.println("4. Luxury Class");
            int cabType = inputs.getIntegerInput();
            
            if (cabType < 1 || cabType > 4) {
                ConsolePrinter.printErrorMsg("Invalid cab type selected. Defaulting to Micro/Mini.");
                cabType = 1;
            }
            
            System.out.println("Enter Source location");
            sourceLocation = inputs.getStringInput();
            if (sourceLocation == null || sourceLocation.trim().isEmpty()) {
                ConsolePrinter.printErrorMsg("Source location cannot be empty");
                return null;
            }
            
            System.out.println("Enter Destination location");
            destinationLocation = inputs.getStringInput();
            if (destinationLocation == null || destinationLocation.trim().isEmpty()) {
                ConsolePrinter.printErrorMsg("Destination location cannot be empty");
                return null;
            }
            
            if (sourceLocation.equalsIgnoreCase(destinationLocation)) {
                ConsolePrinter.printErrorMsg("Source and destination cannot be the same");
                return null;
            }

            System.out.println("Do you want to book cab based on Gender of Cab Driver??");
            System.out.println("1. YES ");
            System.out.println("2. NO ");
            int input = inputs.getIntegerInput();
            
            if (input != 1 && input != 2) {
                ConsolePrinter.printErrorMsg("Invalid option selected. Defaulting to no gender preference.");
                input = 2;
            }
            
            switch (input) {
                case 1:
                    bestCab = cabSelectionWithGender.withGenderPreference();
                    break;
                case 2:
                    bestCab = cabSelectionWithoutGender.withoutGenderPreference();
                    break;
            }
            
            if (bestCab == null) {
                ConsolePrinter.printErrorMsg("No suitable cab found. Please try again later.");
                return null;
            }
            
            double price = cabPriceCalculator.priceCalculation(sourceLocation, destinationLocation, cabType, hour);
            
            // Add customer information to the booking for the driver's reference
            Booking booking = new Booking(-1, custId, bestCab.driver_Id, -1, sourceLocation,
                                        destinationLocation, "", price, false);
            
            ConsolePrinter.printSuccessMsg("Cab selected successfully! Driver ID: " + bestCab.driver_Id);
            return booking;
        } catch (Exception e) {
            ConsolePrinter.printErrorMsg("Error during cab selection: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}

