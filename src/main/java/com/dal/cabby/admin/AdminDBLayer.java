package com.dal.cabby.admin;

import com.dal.cabby.dbHelper.DBHelper;
import com.dal.cabby.dbHelper.IPersistence;
import com.dal.cabby.pojo.Profile;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class acts as DB layer for the Admin class. It talks with the
 * global Persistence module to operate any database related queries.
 */
class AdminDBLayer {
    private final IPersistence iPersistence;

    AdminDBLayer() throws SQLException {
        this.iPersistence = DBHelper.getInstance();
    }

    /*
    Fetch the list of drivers who are not in the activated state.
     */
    List<Profile> listOfDriversToBeApproved() throws SQLException {
        String query = "select driver_id, name from driver where status = false order by driver_id desc";
        ResultSet resultSet = iPersistence.executeSelectQuery(query);
        List<Profile> profileList = new ArrayList<>();
        while (resultSet.next()) {
            int driverId = resultSet.getInt("driver_id");
            String driverName = resultSet.getString("name");
            profileList.add(new Profile(driverId, driverName));
        }
        return profileList;
    }

    /*
    Fetch the list of customers who are not in the activated state.
     */
    List<Profile> listOfCustomersToBeApproved() throws SQLException {
        String query = "select cust_id, name from customer where status = false order by cust_id desc";
        ResultSet resultSet = iPersistence.executeSelectQuery(query);
        List<Profile> profileList = new ArrayList<>();
        while (resultSet.next()) {
            int custId = resultSet.getInt("cust_id");
            String custName = resultSet.getString("name");
            profileList.add(new Profile(custId, custName));
        }
        return profileList;
    }
}
