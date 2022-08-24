package com.application.atm.service;

import com.application.atm.client.Customer;
import com.application.atm.errors.CustomerNotFoundException;
import com.application.atm.errors.InvalidDepositAmountException;
import com.application.atm.errors.NonEmptyAccountException;
import com.application.atm.errors.pinMissMatchException;
import com.application.atm.test.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class ATM_Service {

    private static Scanner scanner = new Scanner(System.in);

    private static Statement statement;

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

        int cashCount = 0;
        long depositAmount = 0;

        // Count and calculate cash amount to be deposited.
        // Cash counting for 2000 rupees.
        System.out.print("Enter the number of 2000 rupees: ");
        cashCount = scanner.nextInt();
        if (cashCount<0)
            throw new InvalidDepositAmountException();
        else
            depositAmount += (2000 * cashCount);

        // Cash counting for 500 rupees.
        System.out.print("Enter the number of 500 rupees: ");
        cashCount = scanner.nextInt();
        if (cashCount<0)
            throw new InvalidDepositAmountException();
        else
            depositAmount += (500 * cashCount);

        // Cash counting for 200 rupees.
        System.out.print("Enter the number of 200 rupees: ");
        cashCount = scanner.nextInt();
        if (cashCount<0)
            throw new InvalidDepositAmountException();
        else
            depositAmount += (200 * cashCount);

        // Cash counting for 100 rupees.
        System.out.print("Enter the number of 100 rupees: ");
        cashCount = scanner.nextInt();
        scanner.nextLine();
        if (cashCount<0)
            throw new InvalidDepositAmountException();
        else
            depositAmount += (100 * cashCount);

        if (depositAmount == 0)
            throw new InvalidDepositAmountException("Deposit amount cannot be zero! :(");

        // Update balance.
        customer.setBalance(customer.getBalance() + depositAmount);

        return customers;
    }


    public static void executionBlock() throws Exception
    {
       // Getting the list of customers
       List<Customer> customers = getCustomers();

       Test.displayAllCustomers(customers);

       //customers = depositCash(customers,4,"0123");
        customers = closeAccount(customers,6,"0123");

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
            System.out.println(query);

           statement.executeUpdate(query);
       }
    }

}
