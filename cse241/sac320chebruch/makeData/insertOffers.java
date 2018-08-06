
import java.util.Random;
import java.sql.*;
import java.util.Scanner;


public class insertOffers
{

	private static int getRandomPrice(int i)
    {
        Random r = new Random();
        //if it's the food or drink category
        if(i == 1 || i == 2)
        {
            return r.nextInt(4) + 1;
        }
        if(i == 3)
        {
            return r.nextInt(750) + 250;
        }
        return r.nextInt(1000) + 1000;
    }

	public static void main(String[] args) throws Exception
	{/*
	Scanner in = new Scanner(System.in);
	System.out.println("Enter username");
        String us = null;
        String pas = null;
        us = in.next();
        System.out.println("Enter password");
        pas = in.next();*/
	Class.forName("org.sqlite.JDBC");
        Connection con = null;
        Statement s = null;
        try
        {
	 con=DriverManager.getConnection("jdbc:sqlite:BRC.db");
         s=con.createStatement();
	 System.out.println("Connection good");
        }
        catch(Exception e)
        {
            e.printStackTrace();
            con.close();
            s.close();
        }


		String[] names = {"Andalax", "Asoka", "Cookley", "Konklux", "Lotlux", "Namfix", "Prodder", "Quo Lux", "Tin", "Voltsillam"};

		for(int i = 0; i < names.length; i++)
		{
			for(int j = 1; j < 5; j++)
			{
				String q = "product where cat_id = " + j;
                int rowCount = getRowCount(con, s, q);

				for(int k = 1; k <= rowCount; k++)
				{
					String update = "insert into offers values(" + k + ", " + j + ", '" + names[i] + "', " + getRandomPrice(j) + ")";
					System.out.println(update);
					int kappa = s.executeUpdate(update);
					System.out.println("Update successful");
				}
			}

		}

	}

	private static int getRowCount(Connection con, Statement s, String tableName)
    {
        try
        {
            String q = "select count(*) rowcount from "+tableName;
            ResultSet result = s.executeQuery(q);
            result.next();
            int rc = result.getInt("rowcount");
            result.close();
            return rc;
        }catch(Exception e)
        {
            System.out.println("Something went wrong in the servers.");
            e.printStackTrace();
        }
        return -1;
    }

}

