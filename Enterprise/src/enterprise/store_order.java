/*
	Sam Chebruch
	Spring 2018
	CSE 241
	Final Project
 */

import java.sql.*;
import java.util.Scanner;
import java.util.ArrayList;

/**
 * A class that represents a store order, whether it is a restock or triggered
 * by a customer order
 */
public class store_order {

    private int Loc_id;
    private int order_num;
    private String date;
    private String purpose;
    private ArrayList<Product> products;
    private ArrayList<Integer> qty;

    //default constructor assumes the order is online
    public store_order(Connection con, Statement s, int order_num, String date, String purpose) {
        this.order_num = order_num;
        this.date = date;
        this.purpose = purpose;
        Loc_id = 151;
        order_num = assignID(con, s, "store_order");
    }

    public store_order(Connection con, Statement s, int Loc_id, int order_num, String date, String purpose) {
        this(con, s, order_num, date, purpose);
        this.Loc_id = Loc_id;
    }

    public String getDate() {
        return date;
    }

    private int assignID(Connection con, Statement s, String tableName) {
        try {
            String q = "select MAX(order_num) from " + tableName;
            ResultSet result = s.executeQuery(q);
            result.next();
            int r = result.getInt("MAX(order_num)");
            result.close();
            return r + 1;
        } catch (Exception e) {
            System.out.println("Something went wrong in the servers.  Please relaunch the program");
        }
        return -1;
    }

    //for the special case that a warehouse runs out of something, assume the minimum is 100
    public void restock(Connection con, Statement s, int prod_id, int cat_id, int deficit) {
        int qtyToOrder = deficit + 100;
        int wareOrdId = assignID(con, s, "store_orders");
        String storeOrders = "insert into store_orders(order_num, Loc_id, date_ordered) values(" + wareOrdId + ", " + Loc_id + ", '" + date + "')";
        String storeOrder = "insert into store_order(order_num, purpose) values(" + assignID(con, s, "store_order") + ", '" + purpose + "')";
        String vendor = findCheapVendor(con, s, prod_id, cat_id);
        double price = getStoreBuysPrice(con, s, vendor, prod_id, cat_id);
        String updateOrdFrom = "insert into store_order_from(order_num, name) values(" + wareOrdId + ", '" + vendor + "')";
        String updateStoreBuys = "insert into store_buys(order_num, cat_id, prod_id, qty, price, buy_date) values(" + wareOrdId + ", " + cat_id + ", " + prod_id + ", " + qtyToOrder + ", " + price + ", '" + date + "')";
        String updateStoredIn = "update stored_in set qty = " + (qtyToOrder) + " where prod_id = " + prod_id + " and cat_id = " + cat_id + " and Loc_id = " + Loc_id;
        try {
            int i = s.executeUpdate(storeOrder);
            i = s.executeUpdate(storeOrders);
            i = s.executeUpdate(updateOrdFrom);
            i = s.executeUpdate(updateStoreBuys);
            i = s.executeUpdate(updateStoredIn);
        } catch (Exception e) {
            System.out.println("updates failed");
            System.exit(0);
        }
    }

    public void restockGeneral(Connection con, Statement s, int prod_id, int cat_id, int qtyToOrder, int minQty) {
        int wareOrdId = assignID(con, s, "store_orders");
        String storeOrders = "insert into store_orders(order_num, Loc_id, date_ordered) values(" + wareOrdId + ", " + Loc_id + ", '" + date + "')";
        String storeOrder = "insert into store_order(order_num, purpose) values(" + assignID(con, s, "store_order") + ", '" + purpose + "')";
        String vendor = findCheapVendor(con, s, prod_id, cat_id);
        double price = getStoreBuysPrice(con, s, vendor, prod_id, cat_id);
        String updateOrdFrom = "insert into store_order_from(order_num, name) values(" + wareOrdId + ", '" + vendor + "')";
        String updateStoreBuys = "insert into store_buys(order_num, cat_id, prod_id, qty, price, buy_date) values(" + wareOrdId + ", " + cat_id + ", " + prod_id + ", " + qtyToOrder + ", " + price + ", '" + getDate() + "')";
        String updateStoredIn = "update stored_in set qty = " + minQty + " where prod_id = " + prod_id + " and cat_id = " + cat_id + " and Loc_id = " + Loc_id;
        try {
            int i = s.executeUpdate(storeOrder);
            i = s.executeUpdate(storeOrders);
            i = s.executeUpdate(updateOrdFrom);
            i = s.executeUpdate(updateStoreBuys);
            i = s.executeUpdate(updateStoredIn);
        } catch (Exception e) {
            System.out.println("updates failed");
            System.exit(0);
        }
    }

    public double getStoreBuysPrice(Connection con, Statement s, String name, int prod_id, int cat_id) {
        String q = "select price from offers where name = '" + name + "' and prod_id = " + prod_id + " and cat_id = " + cat_id;
        try {
            ResultSet r = s.executeQuery(q);
            r.next();
            return r.getDouble("price");

        } catch (Exception e) {
            System.out.println("Store buys price query failed. Please try again later");
            System.exit(0);
        }
        return -1;
    }

    private String findCheapVendor(Connection con, Statement s, int prod_id, int cat_id) {
        String q = "select name from offers where prod_id = " + prod_id + " and cat_id = " + cat_id + " order by price";
        try {
            ResultSet r = s.executeQuery(q);
            r.next();
            return r.getString("name");
        } catch (Exception e) {
            System.out.println("couldn't find vendor. Transaction terminated");
            System.exit(0);
        }
        return null;
    }

}
