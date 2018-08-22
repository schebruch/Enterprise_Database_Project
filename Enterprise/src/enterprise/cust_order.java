/*
	Sam Chebruch
	Spring 2018
	CSE 241
	Final Project
 */

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Class that represents a customer order, whether it is online or at a physical location
 */
public class cust_order {

    private int Loc_id;
    private int cust_id;
    private int orderNum;
    private String type;
    private String date;
    private ArrayList<Product> products = new ArrayList<>();
    private ArrayList<Integer> qty = new ArrayList<>();
    private ArrayList<String> custBuys = new ArrayList<>();

    public cust_order(int cust_id, int Loc_id, String type, int orderNum, String date) {
        this.Loc_id = Loc_id;
        this.cust_id = cust_id;
        this.orderNum = orderNum;
        this.type = type;
        this.date = date;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public Date parseDate(String date) {
        SimpleDateFormat d = new SimpleDateFormat("MM/dd/yyyy");
        try {
            return d.parse(date);
        } catch (Exception e) {
            System.out.println("Couldn't parse date. Aborting now. Please restart the program");
            System.exit(0);
        }
        return null;
    }

    public String getDate() {
        return date;
    }

    public int getCustID() {
        return cust_id;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public ArrayList<Integer> getQty() {
        return qty;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void makeOrder(Connection con, Statement s, int Loc_id) {
        boolean freqShopper = false;
        double totalPmt = 0;
        Integer qnty = 0;
        int prod_id = 0;
        int cat_id = 0;
        Scanner in = new Scanner(System.in);
        if (!FreqShopper.isFreqShopper(con, s, cust_id)) {
            System.out.println("Would you like to become a frequent shopper? Enter 'yes' or 'no'");
            String response = null;
            while (true) {
                response = in.nextLine();
                if (!response.equalsIgnoreCase("yes") && !response.equalsIgnoreCase("no")) {
                    System.out.println("Please enter 'yes' or 'no'");
                    continue;
                }
                break;
            }
            if (response.equalsIgnoreCase("yes")) {
                FreqShopper.updateFreqShopper(con, s, cust_id);
                freqShopper = true;
            }
        }
        while (true) {
            System.out.println("Thank you for shopping with BRC! Please select a category to make an order:");
            Product.displayCats(con, s);
            System.out.println("Please select an integer option corresponding to the category!");
            while (true) {
                try {
                    cat_id = Integer.parseInt(in.next());
                    //there are 4 categories
                    if (cat_id > 4 || cat_id < 1) {
                        System.out.println("Please select a category shown and try again");
                        continue;
                    }
                    break;
                } catch (Exception e) {
                    System.out.println("Please select an integer category ID!");
                }
            }
            System.out.println("Thank you for shopping with BRC! Here is a list of products that we currently hold for that category: ");
            Product.displayProductsInCategory(con, s, cat_id, Loc_id);
            System.out.println("Please follow these instructions to choose your product!");
            System.out.println("Enter the product ID");
            try {
                prod_id = Integer.parseInt(in.next());
            } catch (Exception e) {
                System.out.println("Please enter a valid product ID");
                continue;
            }
            String q = "select* from product where prod_id = " + prod_id + " and cat_id = " + cat_id;
            ResultSet r = null;
            try {
                r = s.executeQuery(q);
                if (!r.next()) {
                    System.out.println("Please give a valid produt ID for category: " + cat_id);
                    continue;
                }
            } catch (Exception e) {
                System.out.println("Could not generate product list");
                System.exit(0);
            }

            System.out.println("Please enter the quantity of this product you'd like: ");
            while (true) {
                try {
                    qnty = Integer.parseInt(in.next());
                    if (qnty < 0 || qnty > 10000000) {
                        System.out.println("You can't order a negative quantity! Please enter a positive number. Also, don't order over 10 million items");
                        continue;
                    }
                    break;
                } catch (Exception e) {
                    System.out.println("Please enter the quantity as an integer");
                    continue;
                }
            }
            int qtyAv = getQtyAv(con, s, prod_id, cat_id, Loc_id);
            if (qtyAv < qnty) {
                store_order st = new store_order(con, s, Loc_id, orderNum, date, "c_req");
                st.restock(con, s, prod_id, cat_id, qnty - qtyAv);
            }
            boolean sameProduct = isSameProduct(prod_id, cat_id, qnty);
            String custBuysUpdate = null;
            if (!sameProduct) {
                products.add(new Product(prod_id, cat_id, getSize(con, s, prod_id, cat_id), getBrand(con, s, prod_id, cat_id), getName(con, s, prod_id, cat_id)));
                qty.add(qnty);
                custBuysUpdate = "insert into cust_buys(order_num, price, qty, prod_id, cat_id) values(" + orderNum + ", " + getPrice(con, s, prod_id, cat_id, Loc_id) + ", " + qnty + ", " + prod_id + ", " + cat_id + ")";
                custBuys.add(custBuysUpdate);
            }
            removeFromStoredIn(con, s, prod_id, cat_id, qnty, Loc_id);
            System.out.println("Order updated. Would you like to continue ordering? Type 'done' if you're finished, or anything else to continue ordering");
            totalPmt += getPayment(con, s, prod_id, cat_id, qnty, Loc_id);
            if (in.next().equalsIgnoreCase("done")) {
                System.out.println("ORDER FOR CUSTOMER: " + cust_id + ", ORDER NUMBER: " + orderNum);
                displayOrder();
                System.out.println("\nYour total is: $" + totalPmt);
                updateCustOrder(con, s, type);
                for (int i = 0; i < custBuys.size(); i++) {
                    try {
                        int k = s.executeUpdate(custBuys.get(i));
                    } catch (Exception e) {
                        System.out.println("Insert into cust buys failed. Aborting now, please try again later.");
                        System.exit(0);
                    }
                }
                Payment p = new Payment(totalPmt);
                p.assignID(con, s, "payment");
                while (true) {
                    p.setMethod();
                    if (Loc_id == 151 && (p.getMethod().equalsIgnoreCase("Cash") || p.getMethod().equalsIgnoreCase("Check"))) {
                        System.out.println("You can only pay with credit card or bitcoin for online orders. Please enter a valid payment method");
                        continue;
                    }
                    break;
                }
                p.updatePayment(con, s, cust_id, orderNum, date, p.getMethod());
                if (Loc_id == 151) {
                    String dateDelivered = updateShipping(con, s);
                    System.out.println("Thank you for your order. Your order will be delivered three days from " + getDate() + ", which is on: " + dateDelivered);
                }
                if (freqShopper) {
                    System.out.println("Congratulations! You will receive 10% off all orders! Thanks for becoming a frequent shopper!");
                }
                System.out.println("Thank you for ordering from BRC!");
                break;
            }
        }
    }

    private String updateShipping(Connection con, Statement s) {
        updateShipsFrom(con, s);
        Scanner in = new Scanner(System.in);
        int shippingLoc = 0;
        System.out.println("Thank you for ordering online! Would you like the order shipped to your house or a Regork Location? Please enter either 'house' or 'regork'");
        String response = null;
        while (true) {
            response = in.nextLine();
            if (!response.equalsIgnoreCase("House") && !response.equalsIgnoreCase("Regork")) {
                System.out.println("Please enter either 'house' or 'regork'");
                continue;
            }
            break;
        }

        String result = null;
        if (response.equalsIgnoreCase("regork")) {
            Store.displayLocations(con, s);
            System.out.println("Please enter the ID of the Regork Location for which you want the product shipped. Note that Physical Locations can only have IDs ranging from 101 to 150");
            while (true) {
                try {
                    shippingLoc = Integer.parseInt(in.next());
                    if (shippingLoc < 101 || shippingLoc > 150) {
                        System.out.println("Please enter a valid location ID.  It must be between 101 and 150");
                        continue;
                    }
                    break;
                } catch (Exception e) {
                    System.out.println("Please enter the Location ID as an integer!");
                }
            }
            result = updateShipsTo(con, s, shippingLoc);
        } else {
            //shipping loc will be the customer's house
            String q = "select* from customer natural join lives_at where cust_id = " + cust_id;
            ResultSet r = null;
            try {
                Statement s2 = con.createStatement();
                r = s2.executeQuery(q);
                r.next();
                shippingLoc = r.getInt("Loc_id");
                s2.close();
            } catch (Exception e) {
                System.out.println("Retrieval of shipping location failed. Please restart");
                System.exit(0);
            }
            result = updateShipsTo(con, s, shippingLoc);
        }
        return result;
    }

    private void updateShipsFrom(Connection con, Statement s) {
        String q = "insert into ships_from(Loc_id, order_num, date_shipped) values(151" + ", " + orderNum + ", '" + getDate() + "')";
        try {
            Statement s2 = con.createStatement();
            int i = s2.executeUpdate(q);
            s2.close();
        } catch (Exception e) {
            System.out.println("Failed to update ships_from. Please restart");
            System.exit(0);
        }
    }

    private String updateShipsTo(Connection con, Statement s, int shippingLoc) {
        String threeDaysLater = getFutureDate(getDate());
        try {
            Statement s2 = con.createStatement();
            String update = "insert into ships_to(order_num, loc_id, date_delivered) values(" + orderNum + ", " + shippingLoc + ", '" + threeDaysLater + "')";
            int i = s2.executeUpdate(update);
            s2.close();
        } catch (Exception e) {
            System.out.println("Update of ships_to failed. Please restart the system");
            System.exit(0);
        }
        return threeDaysLater;
    }

    private String getFutureDate(String date) {
        SimpleDateFormat d = new SimpleDateFormat("MM/dd/yyyy");
        Calendar cal = Calendar.getInstance();
        cal.setTime(parseDate(date));
        cal.add(Calendar.DATE, 3);
        return d.format(cal.getTime());
    }

    //this method updates the quantity of a product that was previously requested in the current order as to avoid primary key conflict
    private boolean isSameProduct(int prod_id, int cat_id, int qnty) {
        boolean found = false;
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getProdID() == prod_id && products.get(i).getCatID() == cat_id) {
                qty.set(i, qty.get(i) + qnty);
                found = true;
                break;
            }
        }
        return found;
    }

    public void updateCustOrder(Connection con, Statement s, String type) {
        ResultSet r = null;
        String custOrder = "insert into cust_order values(" + orderNum + ", '" + type + "', '" + date + "')";
        String orderFrom = "insert into cust_order_from(order_num, Loc_id) values(" + orderNum + ", " + Loc_id + ")";
        try {
            int i = s.executeUpdate(custOrder);
            i = s.executeUpdate(orderFrom);
        } catch (Exception e) {
            System.out.println("Update to order failed. Transaction aborted. Please restart the program");
            System.exit(0);
        }
    }

    private double getPayment(Connection con, Statement s, int prod_id, int cat_id, int qty, int Loc_id) {
        return getPrice(con, s, prod_id, cat_id, Loc_id) * qty;
    }

    private double getPrice(Connection con, Statement s, int prod_id, int cat_id, int Loc_id) {
        boolean isFreqShopper = FreqShopper.isFreqShopper(con, s, cust_id);
        try {
            String q = "select price from stored_in where prod_id = " + prod_id + " and cat_id = " + cat_id + " and Loc_id = " + Loc_id;
            ResultSet r = s.executeQuery(q);
            r.next();
            if (isFreqShopper) {
                return .9 * (r.getDouble("price"));
            }
            return r.getDouble("price");
        } catch (Exception e) {
            System.out.println("Something went wrong with our system.  Aborting now");
            System.exit(0);
        }
        return -1;
    }

    private void displayOrder() {
        for (int i = 0; i < products.size(); i++) {
            System.out.println("Ordered: " + products.get(i).toString() + ", Qty Requested: " + qty.get(i));
        }
    }

    private void removeFromStoredIn(Connection con, Statement s, int prod_id, int cat_id, int qtyToRemove, int Loc_id) {
        String q = "update stored_in set qty = " + (getQtyAv(con, s, prod_id, cat_id, Loc_id) - qtyToRemove) + " where prod_id = " + prod_id + " and cat_id = " + cat_id + " and Loc_id = " + Loc_id;
        try {
            int i = s.executeUpdate(q);
        } catch (Exception e) {
            System.out.println("Removal failed.  Please try again later");
            System.exit(0);
        }
    }

    private String getSize(Connection con, Statement s, int prod_id, int cat_id) {
        String q = "select prod_size from product where prod_id = " + prod_id + " and cat_id = " + cat_id;
        try {
            ResultSet r = s.executeQuery(q);
            r.next();
            return r.getString("prod_size");
        } catch (Exception e) {
            System.out.println("Couldn't get size of product.  Please try again later");
            System.exit(0);
        }
        System.out.println("size returning nul");
        return null;
    }

    private String getBrand(Connection con, Statement s, int prod_id, int cat_id) {
        String q = "select brand from product where prod_id = " + prod_id + " and cat_id = " + cat_id;
        try {
            ResultSet r = s.executeQuery(q);
            r.next();
            return r.getString("brand");
        } catch (Exception e) {
            System.out.println("Couldn't get brand of product.  Please try again later");
            System.exit(0);
        }
        System.out.println("Brand returning nul");
        return null;
    }

    private String getName(Connection con, Statement s, int prod_id, int cat_id) {
        String q = "select name from product where prod_id = " + prod_id + " and cat_id = " + cat_id;
        try {
            ResultSet r = s.executeQuery(q);
            r.next();
            return r.getString("name");
        } catch (Exception e) {
            System.out.println("Couldn't get name of product.  Please try again later");
            System.exit(0);
        }
        System.out.println("Name returning nul");
        return null;
    }

    private int getQtyAv(Connection con, Statement s, int prod_id, int cat_id, int Loc_id) {
        String q = "select* from stored_in where Loc_id = " + Loc_id + " and prod_id = " + prod_id + " and cat_id = " + cat_id;
        ResultSet r = null;
        try {
            r = s.executeQuery(q);
            r.next();
            return r.getInt("qty");
        } catch (Exception e) {
            System.out.println("Something went wrong. Please restart the system.");
            System.exit(0);
        }
        return -1;
    }

}
