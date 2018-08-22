/*
	Sam Chebruch
	Spring 2018
	CSE 241
	Final Project
*/



import java.util.Scanner;
import java.sql.*;
import java.io.*;
/**Subclass of customer, meant for customers who choose to be "frequent shoppers"*/
public class FreqShopper extends Customer
{

	private char gender;
	private String email;
	private String phone;

	public FreqShopper(int cust_id, String [] address, String first, String last)
	{
		super(cust_id, address, first, last);
	}

	public FreqShopper(Connection con, Statement s)
	{
		super(con,s);
	}

	public FreqShopper(){
		this.gender = 0;
		this.email = null;
		this.phone = null;
	}


	public char getGender()
	{
		return gender;
	}

	public String getEmail()
	{
		return email;
	}

	public String getPhone()
	{
		return phone;
	}

	public void setGender(char gender)
	{
		this.gender = gender;
	}
	public void setEmail(String email)
	{
		this.email = email;
	}
	public void setPhone(String phone)
	{
		int phoneCount = 0;
		char [] charPhone = new char [phone.length() + 10];
		for(int i = 0; i < 3; i++)
		{
			charPhone[i] = phone.charAt(phoneCount++);
		}
		charPhone[3] = '-';
		for(int i = 4; i < 7; i++)
		{
			charPhone[i]= phone.charAt(phoneCount++);
		}
		charPhone[7] = '-';
		for(int i = 8; i < 12; i++)
		{
			charPhone[i] = phone.charAt(phoneCount++);
		}
		this.phone = String.valueOf(charPhone);
	}
	public String toString()
	{
		return "insert into freq_shopper values('" + getCustID() + "', '" +  getGender() + "', '" + getEmail() + "', [" + getPhone() + "])";
	}

	public static void updateFreqShopper(Connection con, Statement s, int cust_id)
	{
		Customer c = Customer.getExistingCustomer(con,s,cust_id);
		FreqShopper tmp = CustomerUI.updateFreqShopper2((FreqShopper)c, con, s);
		try
		{
			int j = s.executeUpdate(tmp.toString());
		}catch(Exception e)
		{
			System.out.println("Something went wrong");
		}
	}

	public static boolean isFreqShopper(Connection con, Statement s, int cust_id)
	{
		String q = "select* from freq_shopper where cust_id = " + cust_id;
		ResultSet r = null;
		try
		{
			Statement s2 = con.createStatement();
			r = s2.executeQuery(q);
			if(!r.next())
			{
				return false;
			}
			s2.close();
			return true;
		}catch(Exception e)
		{
			System.out.println("Retrieval of frequent shopper status failed. Please restart the system");
			System.exit(0);

		}
		return false;
	}
}

