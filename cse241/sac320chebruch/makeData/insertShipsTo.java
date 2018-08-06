import java.sql.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class insertShipsTo
{


    public static String getDate()
    {
        SimpleDateFormat d = new SimpleDateFormat("MM/dd/yyyy");
        System.out.println(d.format(new Date()));
        return d.format(new Date());

    }

	public static void main(String[] args) throws Exception
	{

        Connection con = null;
        Statement s = null;
	Class.forName("org.sqlite.JDBC");
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


		String q = "select Lives_at.Loc_id from (ships_from natural join pmt_for natural join pays_with natural join customer) inner join lives_at using(cust_id) order by order_num";
		//for each order

		ResultSet r = s.executeQuery(q);
		Statement s2 = con.createStatement();
		if(!r.next()){}
		else
		{
			int i = 1;
			do
			{
				//System.out.println(r.getString("Loc_id") + " " );
				String update = "insert into ships_to values(" + i + ", " + r.getInt("Loc_ID") + ", '" + getDate() + "')";
				System.out.println(update);
				i++;
				int j = s2.executeUpdate(update);
			}while(	r.next());
		}

	}

}
