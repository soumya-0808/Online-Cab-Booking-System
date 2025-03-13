package com.dal.cabby.profileManagement;

import com.dal.cabby.driver.Driver;
import com.dal.cabby.io.PredefinedInputs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.mail.MessagingException;
import java.sql.SQLException;
import java.text.ParseException;

import static com.dal.cabby.util.Constants.*;

class LogoutTest {
    @Test
    void logout() {
        System.out.println("Testing logout flow");
        PredefinedInputs inputs = new PredefinedInputs();
        String userName = "driver1";
        String password = "driver1@123";
        inputs.add(LOGIN).add(userName).add(password)
                .add(DRIVER_LOGOUT).add("y").add(EXIT);
        try {
            Driver driver = new Driver(inputs);
            driver.performTasks();
        } catch (SQLException | ParseException throwables) {
            throwables.printStackTrace();
            Assertions.fail(throwables.getMessage());
        } catch (MessagingException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}