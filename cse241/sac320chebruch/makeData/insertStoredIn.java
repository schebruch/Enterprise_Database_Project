import java.sql.*;
import java.util.Scanner;
import java.util.Random;

public class insertStoredIn
{

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

	public static void main(String[] args) throws Exception
	{
	Class.forName("org.sqlite.JDBC");
        Connection con = null;
        Statement s = null;
        try
        {
		con=DriverManager.getConnection("jdbc:sqlite:BRC.db");
            s=con.createStatement();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            con.close();
            s.close();
        }




		for(int k = 101; k < 152; k++)
		{
			for(int i =1; i < 5; i++)
			{
				//get the number of products in category i
				String q = "product where cat_id = " + i;
				int rowCount = getRowCount(con, s, q);
				for(int j = 1; j <= rowCount; j++)
				{
					Random r = new Random();
					//generate random quantity between 0 and 100
					int randQuant = r.nextInt(100);
					int randPrice = getRandPrice(i);
					String update = "insert into stored_in values(" + k + ", " +  j + ", "+ i + ", " + randQuant+ ", " + randPrice+ ")";
					System.out.println(update);
					int a = s.executeUpdate(update);
					System.out.println("successful update");
				}
			}
		}
	}


	private static int getRandPrice(int i)
	{
		Random r = new Random();
		//if it's the food or drink category
		if(i == 1 || i == 2)
		{
			return r.nextInt(10) + 1;
		}
		if(i == 3)
		{
			return r.nextInt(2500) + 500;
        }
		return r.nextInt(3000) + 1000;
    }

}
