import java.util.Scanner;
import java.sql.*;


public class insertStoreBuys
{

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

        System.out.println("Connection success");
	}
}
