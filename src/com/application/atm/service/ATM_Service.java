package com.application.atm.service;

import com.application.atm.client.Customer;
import com.application.atm.errors.*;
import com.application.atm.test.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import static com.application.atm.test.Test.displayAllCustomers;

public class ATM_Service {

    private static final Scanner scanner = new Scanner(System.in);

    private static Statement statement;

    private static long latestCustomerId;

    public static List<Customer> getCustomers() throws Exception
    {
        // DB driver setup
        Class.forName("com.mysql.cj.jdbc.Driver");

        // DB URL, username and password.
        String dbURL = "jdbc:mysql://localhost:3306/atmdb";
        String dbUserName = "user";
        String dbPassword = "password";

        // Connecting to DB via connection object.
        Connection connection = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
        System.out.println("Connection to customer DB established successfully.\n\n");

        // Creating statement object from connection object.
        statement = connection.createStatement();

        // Generating result set of list of customers  from DB using statement object.
        String query = "select * from customer";
        ResultSet resultSet = statement.executeQuery(query);

        // Getting the list of customers from result set.
        List<Customer> customers = new ArrayList<>();

        while (resultSet.next())
        {
            long id = resultSet.getLong("id");
            String name = resultSet.getString("name");
            String pin = resultSet.getString("pin");
            long balance = resultSet.getLong("balance");

            customers.add(new Customer(id, name , pin, balance));
        }

        // Returning the list of customers.
        return customers;
    }

    public static List<Customer> openAccount(List<Customer> customers) throws Exception
    {
        // Enter name
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        // Exceptions for Name
        if(name.isEmpty())
            throw new FieldException("name", "empty");

        if(name == null)
            throw new FieldException("name", "null");

        // Enter PIN
        System.out.print("Enter new PIN: ");
        String pin = scanner.nextLine();

        // Exceptions for PIN
        if(pin.isEmpty())
            throw new FieldException("PIN", "empty");

        if(pin == null)
            throw new FieldException("PIN", "null");

        int pinSize = pin.length();

        if(pinSize > 4 || pinSize < 4)
            throw new FieldException("PIN length should be 4. :(");

        for(int i = 0; i < pinSize; i++)
        {
            char temp = pin.charAt(i);
            if(temp < 48 || temp > 57)
                throw new FieldException("PIN should contain digits[0-9] only! :(");
        }

        // Create customer to add in list
        customers.add(new Customer(++latestCustomerId, name, pin, 0));
        System.out.println("Your account has been created successfully :)\nYour account id is " + latestCustomerId + ".");

        return customers;
    }

    public static List<Customer> closeAccount(List<Customer> customers, long id, String pin) throws Exception
    {

        // Check if the customer exists or not.
        Customer customer = null;

        Iterator<Customer> iterator = customers.iterator();
        while (iterator.hasNext())
        {
            customer = iterator.next();
            //System.out.println(customer.toString());

            if(customer.getId() == id)
                break;

            customer = null;
        }

        if (customer == null)
            throw new CustomerNotFoundException(id);

        // Check if the PIN matches or not.
        if (!customer.getPin().equals(pin))
            throw new pinMissMatchException();

        // Check for zero balance
        if (customer.getBalance() > 0)
            throw new NonEmptyAccountException();

        // Delete customer from the list.
        iterator = customers.iterator();
        while (iterator.hasNext())
        {
            customer = iterator.next();
            if(customer.getId() == id)
                break;
        }
        customers.remove(customer);
        System.out.println("Your account has been closed successfully :)\nVisit us again!");
        // Return the list
        return customers;
    }

    public static List<Customer> depositCash(List<Customer> customers, long id, String pin) throws Exception
    {

        // Check if the customer exists or not.
        Customer customer = null;

        Iterator<Customer> iterator = customers.iterator();
        while (iterator.hasNext())
        {
            customer = iterator.next();
            //System.out.println(customer.toString());

            if(customer.getId() == id)
                break;

            customer = null;
        }

        if (customer == null)
            throw new CustomerNotFoundException(id);

        // Check if the PIN matches or not.
        if (!customer.getPin().equals(pin))
            throw new pinMissMatchException();

        // print current balance.
        System.out.println("Your current balance is Rs." + customer.getBalance() + "/-");

        int cashCount = 0;
        long depositAmount = 0;

        // Count and calculate cash amount to be deposited.
        // Cash counting for 2000 rupees.
        System.out.print("Enter the number of 2000 rupees: ");
        cashCount = Integer.parseInt(scanner.nextLine());
        if (cashCount<0)
            throw new InvalidDepositAmountException();
        else
            depositAmount += (2000 * cashCount);

        // Cash counting for 500 rupees.
        System.out.print("Enter the number of 500 rupees: ");
        cashCount = Integer.parseInt(scanner.nextLine());
        if (cashCount<0)
            throw new InvalidDepositAmountException();
        else
            depositAmount += (500 * cashCount);

        // Cash counting for 200 rupees.
        System.out.print("Enter the number of 200 rupees: ");
        cashCount = Integer.parseInt(scanner.nextLine());
        if (cashCount<0)
            throw new InvalidDepositAmountException();
        else
            depositAmount += (200 * cashCount);

        // Cash counting for 100 rupees.
        System.out.print("Enter the number of 100 rupees: ");
        cashCount = Integer.parseInt(scanner.nextLine());

        if (cashCount<0)
            throw new InvalidDepositAmountException();
        else
            depositAmount += (100 * cashCount);

        if (depositAmount == 0)
            throw new InvalidDepositAmountException("Deposit amount cannot be zero! :(");


        // Update balance.
        customer.setBalance(customer.getBalance() + depositAmount);

        // print updated balance.
        System.out.println("Now your current balance is Rs." + customer.getBalance() + "/-");

        return customers;
    }

    public static List<Customer> withdrawCash(List<Customer> customers, long id, String pin) throws Exception
    {
        // Check if the customer exists or not.
        Customer customer = null;

        Iterator<Customer> iterator = customers.iterator();
        while (iterator.hasNext())
        {
            customer = iterator.next();
            //System.out.println(customer.toString());

            if(customer.getId() == id)
                break;

            customer = null;
        }

        if (customer == null)
            throw new CustomerNotFoundException(id);

        // Check if the PIN matches or not.
        if (!customer.getPin().equals(pin))
            throw new pinMissMatchException();

        // Current balance print
        System.out.println("Your current balance is Rs." + customer.getBalance() + "/-");

        long withdrawAmount = 0;
        int cashCount = 0;

        // Input the with-draw amount
        System.out.print("Enter the amount to be withdrawn [available only multiple of 100]: ");
        withdrawAmount = Long.parseLong(scanner.nextLine());

        // Check for incorrect amount.
        if(withdrawAmount == 0)
            throw new IncorrectFundsException();
        if(withdrawAmount < 0)
            throw new IncorrectFundsException("Incorrect amount entered! :(");
        if(withdrawAmount % 100 != 0)
            throw new IncorrectFundsException("Incorrect amount entered! please give a value which is multiple of 100 :(");
        if(withdrawAmount > customer.getBalance())
            throw new IncorrectFundsException("Insufficient funds! :(\nYour current balance is: Rs." + customer.getBalance() + "/-");

        // Update amount on list
        customer.setBalance(customer.getBalance() - withdrawAmount);

        // Denomination calculations
        String line = "Cash dispensed: ";
        while(withdrawAmount >= 2000){
            cashCount = (int) withdrawAmount/2000;
            withdrawAmount %= 2000;
            break;
        }

        line += "2000s="+cashCount+" ";
        cashCount = 0;

        while(withdrawAmount >= 500){
            cashCount = (int) withdrawAmount/500;
            withdrawAmount %= 500;
            break;
        }

        line += "500s="+cashCount+" ";
        cashCount = 0;

        while(withdrawAmount >= 200){
            cashCount = (int) withdrawAmount/200;
            withdrawAmount %= 200;
            break;
        }

        line += "200s="+cashCount+" ";
        cashCount = 0;

        while(withdrawAmount >= 100){
            cashCount = (int) withdrawAmount/100;
            withdrawAmount %= 100;
            break;
        }

        line += "100s="+cashCount;


        // Denominations print
        System.out.println(line);
        // Updated Balance print
        System.out.println("Now your current balance is Rs." + customer.getBalance() + "/-");

        return customers;
    }


    public static void executionBlock() throws Exception
    {
        // Getting the list of customers
        List<Customer> customers = getCustomers();

        // Storing the customer ID of the last customer
        latestCustomerId = customers.get(customers.size()-1).getId();

       Test.displayAllCustomers(customers);

          boolean flag = false;
          while(!flag)
          {
              try
              {
                  String str = "";
                  str += "\n------------------------------";
                  str += "\n| [A] Open New Account       |";
                  str += "\n------------------------------";
                  str += "\n| [B] Close existing account |";
                  str += "\n------------------------------";
                  str += "\n| [C] Deposit                |";
                  str += "\n------------------------------";
                  str += "\n| [D] Withdraw               |";
                  str += "\n------------------------------";
                  str += "\n| [E] Exit                   |";
                  str += "\n------------------------------";
                  str += "\nEnter your choice: ";
                  System.out.print(str);
                  char choice = scanner.nextLine().charAt(0);
                  long customerID = 0;
                  String pin = null;

                  switch (choice) {
                      case 'A':

                      case 'a':// Open account
                          customers = openAccount(customers);
                          break;

                      case 'B':

                      case 'b':// Close account
                          // Enter Account Number (your customer ID)
                          System.out.print("Enter account number (your customer ID): ");
                          customerID = Long.parseLong(scanner.nextLine());
                          System.out.print("Enter your PIN: ");
                          pin = scanner.nextLine();
                          customers = closeAccount(customers, customerID, pin);
                          break;

                      case 'C':

                      case 'c':// Deposit cash
                          // Enter customer id
                          System.out.print("Enter account number (your customer ID): ");
                          customerID = Long.parseLong(scanner.nextLine());
                          System.out.print("Enter your PIN: ");
                          pin = scanner.nextLine();
                          customers = depositCash(customers, customerID, pin);
                          break;

                      case 'D':

                      case 'd': // Withdraw cash
                          // Enter customer id
                          System.out.print("Enter account number (your customer ID): ");
                          customerID = Long.parseLong(scanner.nextLine());
                          System.out.print("Enter your PIN: ");
                          pin = scanner.nextLine();
                          customers = withdrawCash(customers, customerID, pin);
                          break;

                      case 'E':

                      case 'e': // Exit
                          flag = true;
                          break;

                      default:
                          throw new Exception("Invalid choice! :(");

                  }

              }
              catch(Exception exception)
              {
                  System.out.println("Error: " + exception.getMessage());
              }

          }


       Test.displayAllCustomers(customers);

       statement.executeUpdate("truncate table customer");
       Iterator<Customer> iterator = customers.iterator();
       while (iterator.hasNext())
       {
            Customer customer = iterator.next();
            String query = "insert into customer values (";
            query += customer.getId() + ", ";
            query += "'" + customer.getName() + "', ";
            query += "'" + customer.getPin() + "', ";
            query += customer.getBalance() + ")";
           // System.out.println(query);

           statement.executeUpdate(query);
       }
    }

}
