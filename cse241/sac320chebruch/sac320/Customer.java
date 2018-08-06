/*
	Sam Chebruch
	Sprign 2018
	CSE 241
	Final Project
*/



import java.sql.*;
import java.util.Scanner;

/**Customer class allows creation of a customer to be stored into the database*/
public class Customer
{
	private int cust_id;
	private String [] address;
	private String first;
	private String last;


	public Customer(int cust_id, String [] address,  String first, String last)
	{
		this.cust_id = cust_id;
		this.address = address;
		this.first = first;
		this.last = last;
	}

	public Customer(Connection con, Statement s)
	{
		Customer c = createCustomer(con, s);
		this.cust_id = c.cust_id;
		this.address = c.address;
		this.first = c.first;
		this.last= c.last;
	}

	public Customer(){}


	public int getCustID()
	{
		return cust_id;
	}

	public String []  getAddress()
	{
		return address;
	}

	public String getFirst()
	{
		return first;
	}
	public String getLast()
	{
		return last;
	}

	public void setFirst(String first)
	{
		this.first = first;
	}

	public void setLast(String last)
	{
		this.last = last;
	}
	public void setID(int cust_id)
	{
		this.cust_id = cust_id;
	}

	public String toStringCust()
	{
		return "insert into customer values('" + getCustID() + "', '" + getFirst() + "', '" + getLast() + "')";
	}

	public void insertNewCust(int LocId, Connection con, Statement s)
	{
			try
        	{
           		String LocIns = "insert into Location values(" + LocId + ", '" + getAddress()[0] + "', '" + getAddress()[1] + "', '" + getAddress()[2] + "', '" + getAddress()[3] + "')";
				int k = s.executeUpdate(LocIns);
           		String custIns = "insert into Customer values('" + getCustID() + "', '"+ getFirst() + "', '"+ getLast() +  "')";
				int j = s.executeUpdate(custIns);
            	String livesIns = "insert into lives_at values('" +getCustID() + "', " + LocId + ")";
				int i = s.executeUpdate(livesIns);
        	}catch(Exception e)
        	{
				System.out.println("Database update failed.  Please try again");
        	}
	}


	public FreqShopper createCustomer(Connection con, Statement s)
	{
		Scanner in = new Scanner(System.in);
		System.out.println("Thank you for your interest in BRC!  Please enter your name and address to complete your account");
		String []  address = new String[4];
		String first = null;
		String last = null;
		int streetNo = 0;
		while(true)
		{
			System.out.println("Enter First Name (no special characters, and no more than 20 characters): ");
			while(true)
			{
				first = in.nextLine();
				if(first.length() > 20 || !first.matches("[A-Za-z-]+[a-zA-Z]"))
				{
					System.out.println("Invalid entry. Please enter a valid first name (as described above).");
					continue;
				}
				break;
			}
			System.out.println("Enter Last Name (no special characters, and no more than 20 characters): ");
			while(true)
			{
				last = in.nextLine();
				if(last.length() > 20 || !last.matches("[A-Za-z-]+[a-zA-Z]"))
				{
					System.out.println("Invalid entry. Please enter a valid last name (as described above)");
 					continue;
				}break;
			}

			System.out.println("Enter street no (no more than 10 characters)");
			while(true)
			{
				try
				{
					String tmp = in.nextLine();
					if(tmp.length() > 10)
					{
						System.out.println("Street No. can only be up to 10 digits. Please re-enter");
						continue;
					}
					streetNo = Integer.parseInt(tmp);
					if(streetNo < 0)
					{
						System.out.println("You can't have a negative street number! Please re-enter it.");
						continue;
					}
					address[0] = tmp;
					break;
				}catch(NumberFormatException e)
				{
					System.out.println("Something went wrong.  Please enter your street number as a number (don't include any spaces)");
				}
			}
			String streetName = null;
			while(true)
			{
				System.out.println("Enter street name (20 characters or less). Enter in the format 'street_name (space) street_identifier'. Example: 'Turtle Rd'. Include no special characters");
				streetName = in.nextLine();
				if(streetName.length() > 20 || !streetName.matches("[a-zA-Z0-9-_]+[ ][a-zA-Z]+"))
				{
					System.out.println("Please make sure the street name is valid and is no more than 20 characters. Please re-enter: ");
					continue;
				}
				break;
			}

			address[1] = streetName;
			String city = null;
			System.out.println("Enter City (no more than 20 characters, no special characters)");
			while(true)
			{
				city = in.nextLine();
				if(city.length() > 20 || !city.matches("[a-zA-Z- ]+[a-zA-Z]"))
				{
					System.out.println("Invalid input. Please enter something valid, as described above: ");
					continue;
				}break;
			}
			address[2] = city;
			System.out.println("Enter State (no more than 20 characters)");
			String state = null;
			while(true)
			{
				state = in.nextLine();
				if(state.length() > 20 || !state.matches("[a-zA-Z- ]+[a-zA-Z]"))
				{
					System.out.println("Invalid state format. Please re-enter");
      			    continue;
				}break;
			}
			address[3] = state;
			System.out.println("Please confirm by typing 'confirm' and enter if the following is correct.  If something is incorrect, type 'edit' and press enter\n");
			System.out.println("CUSTOMER INFORMATION\n");
			System.out.println("First name: " + first + ", Last Name: " + last + "\nAddress: " + address[0] + " " + address[1] + ", " + address[2] + ", " + address[3]);
			if(!confirm())
			{
				continue;
			}
			break;
		}
		FreqShopper f =  new FreqShopper(assignID(con ,s, "Customer"), address, first, last);
		System.out.println("Congratulations on becoming a new Customer. Your ID is " + f.getCustID() + "! Remember that, you'll need it to log in!");
		return f;
	}




    private int assignID(Connection con, Statement s, String tableName)
    {
        try
        {
            String q = "select MAX(to_number(cust_id)) from "+tableName;
            ResultSet result = s.executeQuery(q);
            result.next();
            int r = result.getInt("MAX(to_number(cust_id))");
            result.close();
            return r+1;
        }catch(Exception e)
        {
            System.out.println("Something went wrong in the servers.  Please relaunch the program");
        }
        return -1;
    }

    private boolean confirm()
    {
        Scanner in = new Scanner(System.in);
        String conf = null;
        while(true)
            {
                conf = in.next();
                    if(!conf.equalsIgnoreCase("confirm") && !conf.equalsIgnoreCase("edit"))
                    {
                     System.out.println("Please type 'confirm' or 'edit'");
                    }
                    else
                     break;
            }
        if(conf.equalsIgnoreCase("confirm"))
        {
            return true;
        }
        return false;
    }
    public static boolean isCust()
    {
        Scanner in = new Scanner(System.in);
        System.out.println("Are you already registered with BRC?  Enter 'yes' or 'no'");
        String ans = null;
        while(true)
        {
            ans = in.nextLine();
            if(!ans.equalsIgnoreCase("yes") && !ans.equalsIgnoreCase("no"))
            {
                System.out.println("Invalid option entered.  Are you already registered with BRC?");
            }
            else
            {
                if(ans.equalsIgnoreCase("yes"))
                {
                    return true;
                }
                return false;
            }
        }
    }

	public static Customer getExistingCustomer(Connection con, Statement s, int cust_id)
	{
		String q = "select* from customer where cust_id = " + cust_id;
		String q2 = "select* from lives_at natural join Location where cust_id = " + cust_id;
		ResultSet r = null;
		String first = null;
		String last = null;
		String []address = new String[4];

		try
		{
			Statement s2 = con.createStatement();
			r = s2.executeQuery(q);
			r.next();
			first = r.getString("first");
			last = r.getString("last");
			Statement s3 = con.createStatement();
			r = s3.executeQuery(q2);
			r.next();
			address[0] = r.getString("street_no");
			address[1] = r.getString("street_name");
			address[2]= r.getString("City");
			address[3] =r.getString("state");
			s2.close();
			s3.close();
		}
		catch(Exception e)
		{
			System.out.println("Retrieval of customer information failed. Please relaunch the program");
			System.exit(0);
		}
		return new FreqShopper(cust_id, address, first, last);

	}

}
