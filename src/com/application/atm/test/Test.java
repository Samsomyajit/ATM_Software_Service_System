package com.application.atm.test;

import com.application.atm.client.Customer;

import java.util.Iterator;
import java.util.List;

public class Test {

    public static void displayAllCustomers(List<Customer> customers)
    {
        Customer customer = null;
        Iterator<Customer> iterator = customers.iterator();
        while (iterator.hasNext())
        {
            customer = iterator.next();
            System.out.println(customer.toString());
        }
    }

}
