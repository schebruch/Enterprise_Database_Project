/*
	Sam Chebruch
	Spring 2018
	CSE 241
	Final Project
*/



import java.util.Scanner;
import java.sql.*;

/*Allows execution of the manager interface*/
public class ManagerUI
{
	public void execute()
	{
        Scanner in = new Scanner(System.in);
        Connection con = null;
        Statement s = null;
            try
            {
		Class.forName("org.sqlite.JDBC");
                con=DriverManager.getConnection("jdbc:sqlite:BRC.db");
                s = con.createStatement();
            }catch(Exception e)
            {
            }

		Manager manager = new Manager();
		manager.setLoc_ID(con,s);


		while(true)
		{
			System.out.println("Would you like to:\n1. Order Inventory\n2. Change the price of a good\n3. Quit");
			int option = getOption();
			if(option == 1)
			{
				System.out.println("INVENTORY:\n\n");
				Manager.displayAllInventory(con, s, manager.getLoc_id());
				manager.orderLowInventory(con, s);
			}
			if(option == 2)
			{
				manager.changePrice(con,s);
			}
			if(option == 3)
			{
				System.out.println("Signing out of BRC account for manager of Location " + manager.getLoc_id());
				try
				{
					con.close();
					s.close();
					break;
				}catch(Exception e)
				{
					System.out.println("Connection closed abnormally");
					System.exit(0);
				}
				break;
			}
		}
	}

	public int getOption()
	{
		Scanner in = new Scanner(System.in);
        int option = 0;
        while(true)
        {
            try
            {
                option = Integer.parseInt(in.next());
                if(option < 1 || option > 4)
                {
                    System.out.println("You entered " + option + ", but this is out of range.  Please enter an option between 1 and 4");
                    continue;
                }
                return option;
            }catch(Exception e)
            {
                System.out.println("Option must be a number between 1 and 3.  Please enter a valid option");
            }
        }

	}


}
