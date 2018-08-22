/*
	Sam Chebruch
	Spring 2018
	CSE 241
	Final Project
*/



import java.util.Scanner;
import java.util.InputMismatchException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**Class that allows execution of the customer interface*/
public class CustomerUI
{
	public static void execute()
	{
		Connection con = null;
		Statement s = null;
		Scanner in = new Scanner(System.in);
		try
		{
			Class.forName("org.sqlite.JDBC");
			con=DriverManager.getConnection("jdbc:sqlite:BRC.db");
			s = con.createStatement();
			System.out.println("Connection successful");
		}catch(Exception e)
		{
		}
		System.out.println("Welcome to BRC online!  Please select an option!");
		while(true)
		{
			displayOptions();
			int option = getUserOption();
			if(option == 1)
			{
				Customer newCust = new Customer(con,s);
				newCust.insertNewCust(assignID(con, s, "Location", "Loc_id"), con, s);
				System.out.println("Congratualtions on becoming becoming a BRC member!\n\nFirst Name: " + newCust.getFirst() + " Last Name: " + newCust.getLast());
				System.out.println("Update complete");
			}
			if(option == 2)
			{
				boolean isAlreadyCust = Customer.isCust();
				//makes the customer a frequent shopper if they are already a customer
				if(isAlreadyCust)
				{
					updateFreqShopper(con, s);
					continue;
				}
				else
				{
					Customer c = new FreqShopper(con,s);
					updateFreqShopper2((FreqShopper)c, con, s);
					try
					{
						c.insertNewCust(assignID(con, s, "Location", "Loc_id"), con, s);
						int j = s.executeUpdate(c.toString());
						System.out.println("Thank you for becoming a frequent shopper!");
					}catch(Exception e)
					{
						System.out.println("Something went wrong. Please try again later");
						System.exit(0);
					}
				}
			}
			if(option == 3)
			{
				System.out.println("If you are not registered, please type 'no' and we will create your account");
				System.out.println("Please enter 'yes' if you are registered");
				while(true)
				{
					String ans = in.nextLine();
					if(ans.equalsIgnoreCase("no"))
					{
						Customer c = new Customer(con, s);
						c.insertNewCust(assignID(con, s, "Location", "Loc_id"), con, s);
						break;
					}
					if(ans.equalsIgnoreCase("yes"))
					{
						break;
					}
					System.out.println("Please enter 'yes' or 'no'");
				}
				int id = 0;
				boolean restart = false;
				while(true)
				{
					try
					{
						System.out.println("Please enter your ID to make a transaction. If you are in fact not registered, please type in any string follow directions:");
						id = Integer.parseInt(in.next());
						String q = "select* from customer where cust_id =  " + id;
						ResultSet r = s.executeQuery(q);
						if(!r.next())
						{
							System.out.println("Your ID was not found. Please re-enter");
							continue;
						}
						break;
					}
					catch(Exception e)
					{
						System.out.println("Please enter your ID as an integer. If you are not registered, please enter 'no' and we will register you! Otherwise, enter any character and try again!");
						if(in.next().equalsIgnoreCase("no"))
						{
    	                    Customer c = new Customer(con, s);
	                        c.insertNewCust(assignID(con, s, "Location", "Loc_id"), con, s);
        	                restart = true;
							break;
						}
						else
						{
							continue;
						}
					}
				}
				if(restart)
				{
					continue;
				}
				String date = getDate();
				cust_order order = new cust_order(id, 151, "online", assignID(con, s, "cust_order", "order_num"), date);
				order.setType("online");
				order.makeOrder(con, s, 151);
			}
			if(option == 4)
			{
				System.out.println("Thank you for shopping with BRC!");
				try
				{
					con.close();
					s.close();
				}catch(Exception e)
				{
					System.out.println("Close failed");
					System.exit(0);
				}
				System.exit(0);
			}
		}
	}


	public static String getDate()
	{
		SimpleDateFormat d = new SimpleDateFormat("MM/dd/yyyy");
		System.out.println("Today's Date: " + d.format(new Date()));
		return d.format(new Date());

	}
	//this method handles someone who falsely claims they already are a customer, and creates a new customer if the ID entered doesn't exist.
	public static void updateFreqShopper(Connection con, Statement s)
	{
		Scanner in = new Scanner(System.in);
		int id = 0;
		System.out.println("Please enter your ID (as an integer!)");
		while(true)
		{
			try
			{
				id = Integer.parseInt(in.next());
				break;
			}catch(Exception e)
			{
				System.out.println("Please enter only an integer as an ID.\nIf you don't have an ID, please type in the word 'no'. Otherwise, enter anything else: ");
				if(in.next().equalsIgnoreCase("no"))
				{
					break;
				}
			}
		}


		String idExists = "select* from customer where cust_id = "+ id;
		try{
			ResultSet r = s.executeQuery(idExists);
			Customer tmp = null;
			//if the id they entered doesn't exist, create a new customer and assign them that ID
			if(!r.next())
			{
				System.out.println("We are unable to find records of your involvement in BRC.  Please answer the following questions to become a BRC customer");
				tmp = new FreqShopper(con, s);
				id = tmp.getCustID();
				Customer c = updateFreqShopper2((FreqShopper)tmp, con, s);
				c.insertNewCust(assignID(con, s, "Location", "Loc_id"), con, s);
				System.out.println("cust update success");
				int	j = s.executeUpdate(c.toString());
				System.out.println("frequent shopper update successful");
			}
			else
			{
				//check if they are already a frequent shopper
				String f = "select* from freq_shopper where cust_id = " + id;
				r = s.executeQuery(f);
				if(r.next())
				{
					System.out.println("You're already a frequent shopper!");
					return;
				}
				String q = "select* from customer where cust_id = '" + id+"'";
				r = s.executeQuery(q);
				r.next();
				FreqShopper tmp2 = new FreqShopper();
				tmp2.setID(id);
				tmp2.setFirst(r.getString("first"));
				tmp2.setLast(r.getString("last"));
				FreqShopper c = updateFreqShopper2(tmp2, con, s);
				System.out.println(c.toString());
				int j = s.executeUpdate(c.toString());
				System.out.println("update successful");
			}
		}catch(java.sql.SQLException e)
		{
			e.printStackTrace();
			System.out.println("Something went wrong. Please restart the program");
			System.exit(0);
		}
	}


	//this method is a helper to updateFreqShopper in that it gets the required data for a frequent shopper
	public static FreqShopper updateFreqShopper2(FreqShopper tmp, Connection con, Statement s)
	{
		char gender = 0;
		Scanner in = new Scanner(System.in);
		System.out.println("Please enter your gender (M for male, F for female)");
		while(true)
		{
			String temp = in.nextLine();
			if(temp.length() != 1)
			{
				System.out.println("Please enter only 'M' or 'F'");
				continue;
			}
			gender = temp.charAt(0);
			if(gender != 'm' && gender != 'M' && gender != 'f' && gender != 'F')
			{
				System.out.println("Please enter only 'M' or 'F'");
				continue;
            }
			break;
		}
		System.out.println("Please enter your email address (no more than 50 characters)");
		String email = null;
		while(true)
		{
			email = in.nextLine();
			if(email.length() > 50 || !email.matches("[A-Za-z0-9_.-]+[a-zA-Z0-9][@][A-Za-z_0-9-]+[.][a-zA-Z.]+[a-zA-Z]+"))
			{
				System.out.println("Email can only be less than 50 characters and must be valid.  Please re-enter your email:");
				continue;
			}
			break;
		}
		String phone = null;
		while(true)
		{
			boolean pass = true;
	        System.out.println("Please enter your phone number with no special characters (example: 1111111111)");
			phone = in.next();
			if(phone.length() != 10)
			{
				System.out.println("Please make sure the length of your phone input is 10 characters");
				continue;
			}
			for(int i = 0; i < phone.length(); i++)
			{
				if(!Character.isDigit(phone.charAt(i)))
				{
					pass = false;
					System.out.println("Invalid phone number.  Only digits are allowed.  Please re-enter the phone number");
					break;
				}
			}
			if(!pass)
			{
				continue;
			}
			break;
		}
		tmp.setGender(gender);
		tmp.setEmail(email);
		tmp.setPhone(phone);
		return tmp;
	}

	public static void displayOptions()
	{
		System.out.println("\n1. Create account");
		System.out.println("2. Become a frequent shopper");
		System.out.println("3. Make an online order");
		System.out.println("4. Quit");
		System.out.println("Please select as an integer: for example, '1' for option 1");
	}

	public static int getUserOption()
	{
		Scanner in = new Scanner(System.in);
		int option = 0;
		while(true)
        {
            try
            {
                option = Integer.parseInt(in.next());
                if(option >= 1 && option <= 4)
                {
                    break;
                }
				System.out.println("Please enter your option as an integer from 1 to 3");
            }catch(NumberFormatException e)
            {
                System.out.println("Option must be an integer.  Please re-enter");
            }
        }
		return option;
	}



	private static int assignID(Connection con, Statement s, String tableName, String pk)
	{
		try
		{
			String q = "select MAX(" + pk + ") from "+tableName;
			System.out.println(q);
			ResultSet result = s.executeQuery(q);
			System.out.println("Result retrieved");
			result.next();
			int r = result.getInt("MAX(" + pk +")");
			result.close();
			System.out.println("Customer ID: " + (r+1));
			return r+1;
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Something went wrong in the servers.  Please relaunch the program");
		}
		return -1;
	}
}
