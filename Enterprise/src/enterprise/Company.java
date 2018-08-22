/*
	Sam Chebruch
	Spring 2018
	CSE 241
	Final Project
 */

import java.util.Scanner;

/**
 * Main class, allows access of all interfaces
 */
public class Company {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Welcome to BRC!  Are you a:\n1. Customer\n2. Clerk\n3. Manager");
        System.out.println("\nPlease enter a number only (for example: '1' for 'Customer')");
        int option = 0;
        while (true) {
            try {
                option = Integer.parseInt(in.next());
                if (option < 1 || option > 3) {
                    System.out.println("You entered " + option + ", but this is out of range.  Please enter an option between 1 and 3");
                    continue;
                }
                break;
            } catch (Exception e) {
                System.out.println("Option must be a number between 1 and 3.  Please enter a valid option");
            }
        }
        //if they are a customer
        if (option == 1) {
            CustomerUI ui = new CustomerUI();
            ui.execute();
        }
        //if they are a clerk
        if (option == 2) {
            StoreUI ui = new StoreUI();
            ui.execute();
        }
        //if they are a manager
        if (option == 3) {
            ManagerUI ui = new ManagerUI();
            ui.execute();
        }
    }
}
