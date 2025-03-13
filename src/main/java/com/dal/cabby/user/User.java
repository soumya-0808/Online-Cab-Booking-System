package com.dal.cabby.user;

import javax.mail.MessagingException;
import java.sql.SQLException;
import java.text.ParseException;

public interface User {
    /**
     * Consists of list of steps, like profile-managemnents, admin tasks, etc
     */
    void performTasks() throws SQLException, ParseException, MessagingException, InterruptedException;

    /**
     * Performs basic operations like login, registration and password
     * recovery.
     */
    void profileManagementTasks() throws SQLException, ParseException, MessagingException, InterruptedException;
}
