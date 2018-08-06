import java.sql.*;
import java.util.Scanner;
import java.io.*;
import java.util.Random;
public class insertCustOrder
{
	public static void main(String[] args) throws Exception
	{
		Connection con = null;
		Statement s = null;
		try
		{
			con=DriverManager.getConnection("jdbc:sqlite:Tutoring.db");
			s=con.createStatement();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			con.close();
			s.close();
		}

		System.out.println("Connection success");


		String update = null;
		String [] types = {"store", "online"};
		Random rand = new Random();

		String q = "select date_made from payment";
		ResultSet r = s.executeQuery(q);


		update = "insert into cust_order values(500, 'online', '1/23/2018')";
		int j = s.executeUpdate(update);

/*
		if(!r.next()){System.out.println("Empty");}
		else
		{
			int i = 1;
			do
			{
				int j = 0;
            	String date = r.getString("date_made");
            	int random = rand.nextInt(2) + 0;
            	String type = types[random];
            	update = "insert into cust_order(order_num, type, date_placed) values(" + i + ", '" + type + "', '"+date;
				update += "')";
				i++;
				System.out.println(update);
				j = s.executeUpdate(update);
				System.out.println("j returned" + j);
			}while(r.next());
		}*/
/*
		for(int i = 1; i <= 200; i++)
		{
			String date = r.getString("date_made");
			int random = rand.nextInt(2) + 0;
			String type = types[random];
			update = "insert into cust_order values(" + i + ", " + type + ", "+date;
			System.out.println(update);
			r.next();
		}*/
		s.close();
		con.close();
	}
}
