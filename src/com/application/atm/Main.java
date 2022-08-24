package com.application.atm;

import com.application.atm.service.ATM_Service;

import java.sql.Statement;

public class Main {

    public static void main(String[] args) {

        try {
            ATM_Service.executionBlock();
        } catch (Exception exception)
        {
            System.out.println("Error : " + exception.getMessage());
        }

    }
}
