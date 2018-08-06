/*
	Sam Chebruch
	Spring 2018
	CSE 241
	Final Project
*/



import java.sql.*;
import java.util.Scanner;


/**Class that represents a form of payment for a customer order*/
public class Payment
{
	private int pmt_id;
	private double amt;
	private String method;

	public Payment(double amt, String method)
	{
		this.amt = amt;
		this.method = method;
	}

	public Payment(double amt)
	{
		this.amt = amt;
		this.method = getMethod();
	}


    public int assignID(Connection con, Statement s, String tableName)
    {
        try
        {
            String q = "select MAX(to_number(pmt_id)) from "+tableName;
            ResultSet result = s.executeQuery(q);
            result.next();
            int r = result.getInt("MAX(to_number(pmt_id))");
            result.close();
            return (pmt_id = r + 1);
        }catch(Exception e)
        {
            System.out.println("Something went wrong in the servers.  Please relaunch the program");
        }
        return -1;
    }

	public String getMethod()
	{
		return method;
	}
	public void setMethod()
	{
		Scanner in = new Scanner(System.in);
		System.out.println("How are you paying for this? Note that if you're paying online, only 'credit card' and 'bitcoin' are valid");
		System.out.println("Please enter a valid payment option. For example, 'credit card'");
		String method = null;
		while(true)
		{
			method = in.nextLine();
			if(!method.equalsIgnoreCase("Credit Card") && !method.equalsIgnoreCase("Bitcoin") && !method.equalsIgnoreCase("cash") && !method.equalsIgnoreCase("Check"))
			{
				System.out.println("Please enter a valid entry to identify your payment method");
				continue;
			}
			this.method = method;
			break;
		}

	}
	public double getAmount()
	{
		return amt;
	}


	public String toString()
	{

		return String.format("You paid %.2f using %s", amt, method);
	}


	public void updatePayment(Connection con, Statement s, int cust_id, int order_num, String date, String type)
	{
		if(type.equals("credit card"))
		{
			type = "cred_card";
		}
		String updatePmt = "insert into payment(pmt_id, date_made, pmt_type) values(" + pmt_id + ", '" + date + "', '" + type + "')";
		String updatePaysWith = "insert into pays_with(cust_id, pmt_id) values('" + cust_id + "', '" + pmt_id + "')";
		String updatePmtFor = "insert into pmt_for(pmt_id, order_num) values('" + pmt_id + "', " + order_num + ")";
		try
		{
			int i = s.executeUpdate(updatePmt);
			i = s.executeUpdate(updatePaysWith);
			i = s.executeUpdate(updatePmtFor);
		}catch(Exception e)
		{
			System.out.println("Update of payment failed. Please restart the program and try again");
			System.exit(0);
		}
	}

}
