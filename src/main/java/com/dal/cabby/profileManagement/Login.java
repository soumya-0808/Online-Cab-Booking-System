package com.dal.cabby.profileManagement;

import com.dal.cabby.io.Inputs;
import com.dal.cabby.pojo.UserType;
import com.dal.cabby.util.ConsolePrinter;

import static java.lang.Thread.sleep;

public class Login implements ILogin {

    Inputs inputs;

    /*
        Login constructor initialising the input type as user defined or
        predefined
     */
    public Login(Inputs inputs) {
        this.inputs = inputs;
    }

    /*
        This method is used to attempt the login and generate a login request
        input as the userType
     */
    @Override
    public boolean attemptLogin(UserType userType) throws InterruptedException {
        IDBOperations db_operations = new DBOperations(userType);
        String userNameOrEmail;
        String password;

        try {
            System.out.println("\n========== LOGIN ==========");
            userNameOrEmail = inputUserName();
            password = inputPassword();
            System.out.println("===========================");
            
            System.out.println("Authenticating... Please wait.");
            
            if (db_operations.validateLoginUser(userNameOrEmail, password, userType)) {
                ConsolePrinter.printSuccessMsg("Login successful!");
                return true;
            }

            ConsolePrinter.printErrorMsg("Login Failed due to invalid credentials.");
            sleep(500); // Give user time to read the message
            return false;
        } catch (Exception e) {
            ConsolePrinter.printErrorMsg("Login error: " + e.getMessage());
            e.printStackTrace();
            sleep(500); // Give user time to read the message
            return false;
        }
    }

    /*
        This get the username as input
     */
    private String inputUserName() {
        System.out.print("Enter Username or Email: ");
        return inputs.getStringInput().trim();
    }

    /*
        This get the password as input
     */
    private String inputPassword() {
        System.out.print("Enter Password: ");
        return inputs.getStringInput().trim();
    }
}
