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

/**Class that represents a manager of a particular store, allows re-orders and price changes*/
public class Manager
{


	private int Loc_id;

	public Manager(){}


	public int getLoc_id()
	{
		return Loc_id;
	}

    public String getDate()
    {
        SimpleDateFormat d = new SimpleDateFormat("MM/dd/yyyy");
        System.out.println(d.format(new Date()));
        return d.format(new Date());
    }

	public void orderLowInventory(Connection con, Statement s)
	{
		store_order order = new store_order(con, s, Loc_id, assignID(con, s, "store_order"), getDate(), "general");
		Scanner in = new Scanner(System.in);
		int qty = 0;
		System.out.println("Category 1: Food\nCategory 2: Drink\nCategory 3: Electronics\nCategory 4: Appliances");
		for(int i = 0; i < 4; i++)
		{
			System.out.println("For category " + (i+1) + " what is the minimum quantity your store requires?");
			while(true)
			{
				try
				{
					qty = Integer.parseInt(in.next());
					if(qty < 0 || qty > 1000000)
					{
						System.out.println("Quantity can't be negative or over 10 million! Please re-enter");
						continue;
					}break;
				}catch(Exception e)
				{
					System.out.println("Please enter minimum quantity as an integer");
					continue;
				}
			}
			//make a store order for all products in category i for which the minimum quantity is greater than actual quantity
			//order minimum quantity - actual quantity units
			String q = "select* from stored_in where cat_id = " + (i+1) + " and Loc_id = " + Loc_id + " and qty < " + qty;
			try
			{
				Statement s2 = con.createStatement();
				ResultSet r = null;
				r = s2.executeQuery(q);
				if(!r.next())
				{
					System.out.println("We did not have to make an order for any product in categroy: " + (i+1));
					continue;
				}
				else
				{
					do
					{
						order.restockGeneral(con, s, r.getInt("prod_id"), i+1, Math.abs(qty - r.getInt("qty")), qty);
					}while(r.next());
				}
				s2.close();
			}catch(Exception e)
			{
				System.out.println("Something went wrong with the restock. Please restart the system.");
				System.exit(0);
			}
		}
	}


	public void changePrice(Connection con, Statement s)
	{
		Product.displayCats(con,s);
		Scanner in = new Scanner(System.in);
		System.out.println("Please select the category ID of what you'd like to change the price of");
		int cat_id = 0;
		while(true)
		{
			try
			{
				cat_id = Integer.parseInt(in.next());
				if(cat_id < 1 || cat_id >4)
				{
					System.out.println("Please enter a valid category");
					continue;
				}
				break;
			}catch(Exception e)
			{
				System.out.println("Please enter an integer category");
			}
		}

		System.out.println("Please enter the product ID of what you'd like to change. Products are listed below");
		Product.displayProductsInCategory(con, s, cat_id, Loc_id);
		int prod_id = 0;
		ResultSet r = null;
		while(true)
		{
			try
			{
				prod_id = Integer.parseInt(in.next());
				String q = "select* from product where cat_id = " + cat_id + " and prod_id = " + prod_id;
				r = s.executeQuery(q);
				if(!r.next())
				{
					System.out.println("You entered an invalid product ID for category: " + cat_id + ". Please re-eneter");
					continue;
				}break;
			}catch(NumberFormatException e)
			{
				System.out.println("Please enter an integer for product ID");
				continue;
			}catch(Exception e)
			{
				System.out.println("Something went wrong. Please restart the system");
				System.exit(0);
			}
		}
		updatePrice(con,s,prod_id, cat_id);
	}


	public void updatePrice(Connection con, Statement s, int prod_id, int cat_id)
	{
		Scanner in = new Scanner(System.in);
		System.out.println("Please enter a price you would like to charge for this product");
		double price;

		while(true)
		{
			try
			{
				price = Double.parseDouble(in.next());
				if(price < 0 || price > 10000000)
				{
					System.out.println("Price can't be negative or above 10 million. Please charge a reasonable price");
					continue;
				}break;
			}catch(Exception e)
			{
				System.out.println("Please enter a valid price, not text!");
				continue;
			}
		}
		ResultSet r = null;
		String update = "update stored_in set price = " + price + " where prod_id = " + prod_id + " and cat_id = " + cat_id+ " and Loc_id = " + Loc_id;
		try
		{
			int i = s.executeUpdate(update);
		}catch(Exception e)
		{
			System.out.println("Update failed. Please restart the transaction");
			System.exit(0);
		}

	}

	public static void displayAllInventory(Connection con, Statement s, int Loc_id)
	{
		String q = "select* from stored_in  where Loc_id = " + Loc_id;
		ResultSet r = null;
		try
		{
			r = s.executeQuery(q);
			if(!r.next())
			{
				System.out.println("There is nothing stored in this location");
			}else{
				System.out.printf("%-40s\t%-10s\t%-10s\t%-10s", "name", "prod_id", "cat_id", "qty");
				System.out.println();
				do{
					System.out.printf("%-40s\t%-10s\t%-10s\t%-10s", Product.getName(con,s,r.getInt("prod_id"), r.getInt("cat_id")), r.getString("prod_id"), r.getString("cat_id"),r.getString("qty"));
					System.out.println();
				}while(r.next());
			}
		}catch(Exception e)
		{
			System.out.println("Something went wrong. Please restart the system.");
			System.exit(0);
		}
		System.out.println("\nShown above is all the inventory quantities for every product in store: " + Loc_id);
	}


    public static void displayAllInventory2(Connection con, Statement s, int Loc_id)
    {
        String q = "select* from stored_in where Loc_id = " + Loc_id;
		ResultSet r = null;
        try
        {
            r = s.executeQuery(q);
            if(!r.next())
            {
                System.out.println("There is nothing stored in this location");
            }else{
                System.out.printf("%-40s\t%10s\t%10s\t%10s","name", "prod_id", "cat_id", "price");
                System.out.println();
                do{
                    System.out.printf("%-40s\t%10s\t%10s\t%10s", Product.getName(con, s, r.getInt("prod_id"), r.getInt("cat_id")),r.getString("prod_id"), r.getString("cat_id"), r.getString("price"));
                    System.out.println();
                }while(r.next());
            }
        }catch(Exception e)
        {
            System.out.println("Something went wrong. Please restart the system.");
            System.exit(0);
        }
        System.out.println("Shown above is all the price for every product in store: " + Loc_id);
    }


	public void setLoc_ID(Connection con, Statement s)
	{
		Scanner in = new Scanner(System.in);
		displayLocations(con, s, 100, 152);
		System.out.println("Please enter the location ID of your store.");
		while(true)
		{
			try
			{
				Loc_id = Integer.parseInt(in.next());
				if(Loc_id < 101 || Loc_id > 151)
				{
					System.out.println("Please enter a store location ID in range");
					continue;
				}break;
			}catch(Exception e)
			{
				System.out.println("Please enter an integer location from the list");
			}
		}
	}


	public void displayLocations(Connection con, Statement s, int low, int high)
	{
		ResultSet r = null;
		try
		{
			String q = "select* from Location where Loc_id > " + low + " and Loc_id < " + high;
			r = s.executeQuery(q);
			if(!r.next())
			{
				System.out.println("Couldn't find locations. Please relaunch the program");
				System.exit(0);
			}
			else
			{
				System.out.printf("%-5s\t\t%-10s\t\t%-20s\t\t%-20s\t\t%-5s","Loc_id", "Street_No", "Street_Name", "City", "State");
				System.out.println();
				do
				{
					System.out.printf("%-5s\t\t%-10s\t\t%-20s\t\t%-20s\t\t%-5s",r.getString("Loc_id"),r.getString("Street_NO"),r.getString("Street_Name"),r.getString("city"), r.getString("state"));
					System.out.println();
				}while(r.next());
			}
		}catch(Exception e)
		{
			System.out.println("Retrieval of locations failed. Please restart the program and try again");
			System.exit(0);
		}
	}

     private int assignID(Connection con, Statement s, String tableName)
     {
        try
        {
            String q = "select MAX(to_number(order_num)) from "+tableName;
            ResultSet result = s.executeQuery(q);
            result.next();
            int r = result.getInt("MAX(to_number(order_num))");
            result.close();
            return r+1;
        }catch(Exception e)
        {
            System.out.println("Something went wrong in the servers.  Please relaunch the program");
        }
        return -1;
    }
}
