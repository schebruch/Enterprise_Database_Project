/*
	Sam Chebruch
	Spring 2018
	CSE 241
	Final Project
 */

import java.util.Scanner;
import java.sql.*;

/**
 * Class that represents a particular Regork location
 */
public class Store {

    private int Loc_id;
    private String open_date;
    private int orderNum;

    public Store(Connection con, Statement s) {
        initializeStore(con, s);
    }

    public Store(Connection con, Statement s, int Loc_id, String open_date, int orderNum) {
        this.orderNum = orderNum;
        this.Loc_id = Loc_id;
        this.open_date = open_date;
    }

    public int getLoc_id() {
        return Loc_id;
    }

    public String getOpen_Date() {
        return open_date;
    }

    public void setLoc_id(int Loc_id) {
        this.Loc_id = Loc_id;
    }

    public void setOpen_Date(String open_date) {
        this.open_date = open_date;
    }

    public void initializeStore(Connection con, Statement s) {
        Scanner in = new Scanner(System.in);
        displayLocations(con, s);
        System.out.println("Please enter the ID of this Regork Location. Note that Physical Locations can only have IDs ranging from 101 to 150");
        while (true) {
            try {
                Loc_id = Integer.parseInt(in.next());
                if (Loc_id < 101 || Loc_id > 150) {
                    System.out.println("Please enter a valid location ID.  It must be between 101 and 150");
                    continue;
                }
                break;
            } catch (Exception e) {
                System.out.println("Please enter the Location ID as an integer!");
            }
        }

        ResultSet r = null;
        String q = "select open_date from store where Loc_id = " + Loc_id;
        try {
            r = s.executeQuery(q);
            if (!r.next()) {
                System.out.println("There are no stores with Location ID: " + Loc_id + ". Please restart the program");
                System.exit(0);
            } else {
                setOpen_Date(r.getString("open_date"));
            }
        } catch (Exception e) {
            System.out.println("Couldn't find store. Please update the program and try again!");
            System.exit(0);
        }
    }

    public static void displayLocations(Connection con, Statement s) {
        String q = "select* from store natural join Location";
        ResultSet r = null;
        try {
            r = s.executeQuery(q);
            System.out.printf("%-10s\t%-10s\t%-10s\t%-20s\t%-10s\t%-5s\n", "Loc_ID", "Open_Date", "Street_No", "Street_Name", "City", "State");
            while (r.next()) {
                System.out.printf("%-10s\t%-10s\t%-10s\t%-20s\t%-10s\t%-5s\n", r.getInt("Loc_id"), r.getString("open_date"), r.getInt("Street_No"), r.getString("Street_Name"), r.getString("City"), r.getString("State"));
            }

        } catch (Exception e) {
            System.out.println("Could not display Locations. Aborting now. Please restart the program");
            System.exit(0);
        }

    }

    public Customer identifyCustomer(Connection con, Statement s) {
        Scanner in = new Scanner(System.in);
        System.out.println("Is this customer registered already? Type 'yes' or 'no'");
        String registered = null;
        while (true) {
            registered = in.next();
            if (!registered.equalsIgnoreCase("yes") && !registered.equalsIgnoreCase("no")) {
                System.out.println("Please enter 'yes' or 'no'");
                continue;
            }
            break;
        }
        if (registered.equalsIgnoreCase("no")) {
            Customer c = new Customer(con, s);
            int Loc_id = assignID(con, s, "Location");
            c.insertNewCust(Loc_id, con, s);
        }
        System.out.println("Enter the customer's ID");
        ResultSet r = null;
        int cust_id = 0;
        while (true) {
            try {
                cust_id = Integer.parseInt(in.next());
            } catch (Exception e) {
                System.out.println("Customer ID must be an integer. Please try again");
                continue;
            }
            try {
                String q = "select* from customer where cust_id = " + cust_id;
                r = s.executeQuery(q);
                if (!r.next()) {
                    System.out.println("The customer you entered cannot be found. Please enter again");
                    continue;
                }
                break;
            } catch (Exception e) {
                System.out.println("Something went wrong. Please relaunch the system");
                System.exit(0);
            }
        }

        try {
            String q = "select* from customer natural join lives_at natural join location where cust_id = " + cust_id;
            Statement s2 = con.createStatement();
            r = s2.executeQuery(q);
            r.next();
            String[] address = new String[4];
            address[0] = r.getString("street_no");
            address[1] = r.getString("street_name");
            address[2] = r.getString("city");
            address[3] = r.getString("state");
            String first = r.getString("first");
            String last = r.getString("last");
            s2.close();
            return new Customer(cust_id, address, first, last);
        } catch (Exception e) {
            System.out.println("Could not process customer information. Please restart the program and try again");
            System.exit(0);
        }
        return null;
    }

    public void verifyCustomer(Connection con, Statement s, Customer c) {
        ResultSet r = null;
        try {
            String q = "select* from customer where cust_id = " + c.getCustID();
            r = s.executeQuery(q);
            r.next();
            System.out.println("CUSTOMER ID: " + r.getString("cust_id") + "\tFIRST: " + r.getString("first") + "\tLAST: " + r.getString("last"));

        } catch (Exception e) {
            System.out.println("Verification of customer failed. Please relaunch the system.");
            System.exit(0);
        }
    }

    public int assignID(Connection con, Statement s, String tableName) {
        String q = null;
        try {
            if (tableName.equalsIgnoreCase("Location")) {
                q = "select MAX(Loc_id) from " + tableName;
            } else {
                q = "select MAX(order_num) from " + tableName;
            }
            ResultSet result = s.executeQuery(q);
            result.next();
            int r = 0;
            if (tableName.equalsIgnoreCase("Location")) {
                r = result.getInt("MAX(Loc_id)");
            } else {
                r = result.getInt("MAX(order_num)");
            }
            result.close();
            if (tableName.equalsIgnoreCase("cust_order")) {
                orderNum = r + 1;
            }
            return r + 1;
        } catch (Exception e) {
            System.out.println("Something went wrong in the servers.  Please relaunch the program");
        }
        return -1;
    }

}
