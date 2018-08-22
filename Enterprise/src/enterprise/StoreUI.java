/*
	Sam Chebruch
	Spring 2018
	CSE 241
	Final Project
 */

import java.util.Scanner;
import java.sql.*;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * This class allows a clerk to check a customer out of a store
 */
public class StoreUI {

    public void execute() {
        Scanner in = new Scanner(System.in);
        Connection con = null;
        Statement s = null;
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:BRC.db");
            s = con.createStatement();
        } catch (Exception e) {
        }

        Store store = new Store(con, s);

        boolean nextCustomer = true;
        while (nextCustomer) {
            Customer buyer = store.identifyCustomer(con, s);
            store.verifyCustomer(con, s, buyer);
            cust_order storeCheckout = new cust_order(buyer.getCustID(), store.getLoc_id(), "store", store.assignID(con, s, "cust_order"), getDate());
            storeCheckout.makeOrder(con, s, store.getLoc_id());
            nextCustomer = hasNextCustomer();
        }
        System.out.println("Signing out of the BRC System....");
        try {
            con.close();
            s.close();
        } catch (Exception e) {
            System.out.println("Close of connection failed");
        }
    }

    private boolean hasNextCustomer() {
        Scanner in = new Scanner(System.in);
        System.out.println("Do you have another customer in line?  Please type 'yes' or 'no'");
        String response = null;

        while (true) {
            response = in.next();
            if (!response.equalsIgnoreCase("yes") && !response.equalsIgnoreCase("no")) {
                System.out.println("Please enter a valid response");
                continue;
            }
            if (response.equalsIgnoreCase("yes")) {
                return true;
            }
            return false;
        }
    }

    public String getDate() {
        SimpleDateFormat d = new SimpleDateFormat("MM/dd/yyyy");
        System.out.println("Today's Date: " + d.format(new Date()));
        return d.format(new Date());

    }

}
