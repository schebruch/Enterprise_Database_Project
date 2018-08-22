/*
	Sam Chebruch
	Spring 2018
	CSE 241
	Final Project
 */

import java.sql.*;

/**
 * Class that represents a particular product sold at BRC
 */
public class Product {

    private int prodId;
    private int catId;
    private String size;
    private String brand;
    private String name;

    public Product(int prodId, int catId, String size, String brand, String name) {
        this.prodId = prodId;
        this.catId = catId;
        this.size = size;
        this.brand = brand;
        this.name = name;
    }

    public int getProdID() {
        return prodId;
    }

    public int getCatID() {
        return catId;
    }

    public static void displayProducts(Connection con, Statement s) {
        try {
            String q = "select* from product";
            ResultSet r = s.executeQuery(q);
            if (!r.next()) {
                System.out.println("There are no products left in the store :( ");
            } else {
                do {
                    System.out.println("Product ID: " + r.getInt("prod_id") + "\t\t\tCategory ID: " + r.getInt("cat_id") + "\t\t\tName: " + r.getString("name") + "\t\t\t\tSize :" + r.getString("prod_size") + "\t\t\tBrand: " + r.getString("brand"));
                    System.out.println();
                    System.out.println();
                } while (r.next());
            }
        } catch (Exception e) {
            System.out.println("Something is wrong with the connection. Please try again later");
        }
    }

    public static void displayCats(Connection con, Statement s) {
        try {
            String q = "select* from categ";
            ResultSet r = s.executeQuery(q);
            if (!r.next()) {
                System.out.println("There are no products left in the store :( ");
            } else {
                do {
                    System.out.println("Category ID: " + r.getInt("cat_id") + " " + "Category name: " + r.getString("cat_name"));
                    System.out.println();
                } while (r.next());
            }
        } catch (Exception e) {
            System.out.println("Something went wrong with the connection, please try again later");
        }
    }

    public static void displayProductsInCategory(Connection con, Statement s, int cat_id) {
        String q = "select* from product where cat_id = " + cat_id;
        ResultSet r = null;
        try {
            r = s.executeQuery(q);
            if (!r.next()) {
                System.out.println("There are no product left in this category :(");
            } else {
                System.out.printf("%-10s\t%-30s\t%-30s\t%-10s", "Product ID", "Name", "Brand", "Size");
                System.out.println();
                do {
                    System.out.printf("%-10s\t%-30s\t%-30s\t%-10s", r.getString("prod_id"), r.getString("name"), r.getString("brand"), r.getString("prod_size"));
                    System.out.println();
                } while (r.next());
            }
        } catch (Exception e) {
            System.out.println("An error occured. Please try again later");
            System.exit(0);
        }
    }

    public static void displayProductsInCategory(Connection con, Statement s, int cat_id, int Loc_id) {
        String q = "select* from stored_in where cat_id = " + cat_id + " and Loc_id = " + Loc_id + " order by prod_id";
        String q2 = "select* from product where cat_id = " + cat_id + " order by prod_id";
        ResultSet r = null;
        ResultSet r2 = null;
        try {
            Statement s2 = con.createStatement();
            Statement s3 = con.createStatement();
            r = s2.executeQuery(q);
            r2 = s3.executeQuery(q2);
            r2.next();
            if (!r.next()) {
                System.out.println("There are no product left in this category :(");
            } else {
                System.out.printf("%-10s\t%-30s\t%-20s\t%-30s\t%-10s", "Product ID", "Name", "Price", "Brand", "Size");
                System.out.println();
                do {
                    System.out.printf("%-10s\t%-30s\t%-20s\t%-30s\t%-10s", r.getString("prod_id"), r2.getString("name"), Product.getPrice(con, s, r.getInt("prod_id"), cat_id, Loc_id), r2.getString("brand"), r2.getString("prod_size"));
                    System.out.println();
                } while (r.next() && r2.next());
            }
            s2.close();
            s3.close();
        } catch (Exception e) {
            System.out.println("An error occured. Please try again later");
            System.exit(0);
        }

    }

    public String toString() {
        return "Product: " + name + " Brand: " + brand;

    }

    public static String getName(Connection con, Statement s, int prod_id, int cat_id) {
        String retVal = null;
        ResultSet r = null;
        try {
            Statement s2 = con.createStatement();
            String q = "select name from product where prod_id = " + prod_id + " and cat_id = " + cat_id;
            r = s2.executeQuery(q);
            r.next();
            retVal = r.getString("name");
            s2.close();
        } catch (Exception e) {
            System.out.println("Retrieval of product name failed. Please restart the system");
            System.exit(0);
        }
        return retVal;
    }

    public static double getPrice(Connection con, Statement s, int prod_id, int cat_id, int Loc_id) {
        ResultSet r = null;
        String q = "select * from stored_in where prod_id = " + prod_id + " and cat_id = " + cat_id + " and Loc_id = " + Loc_id;
        try {
            r = s.executeQuery(q);
            r.next();
            return r.getDouble("price");
        } catch (Exception e) {
            System.out.println("could not retrieve the price. please restart");
            System.exit(0);
        }
        return 0;
    }
}
