import org.junit.Test;

import javax.swing.*;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

public class TestCase {
    CA dummy;

    @Test
    public void stressTest() {
        BankAutomated BA = new BankAutomated();

        long startTime = System.currentTimeMillis();

        // Batch size is the amount of work done by each thread
        int batchSize = 1000;

        // Split the workload into 1000 threads, race condition handled by hash map inside createAccount
        IntStream.range(0, 1000)

            // Each thread work simultaneously
            .parallel()

            // Each thread do:
            .forEach(batch -> {

                // start and end for each batch
                int start = batch * batchSize;
                int end = start + batchSize;

                // Do work:
                for (int i = start; i < end; i++) {
                    String email = i + "@gmail.com";
                    BA.createAccount("test", "dummy", "416-792-1234", "test street",
                            "Male", "01/01/1990", email, "Hello@World1",
                            "4417123456789113", "01/01/2027", "555");
                }

            });

        long endTime = System.currentTimeMillis();
        double timePassedSeconds = (endTime - startTime);

        System.out.println(timePassedSeconds);

        assert timePassedSeconds <= 2000; // Normal loop is 7-9 seconds, this will take 0.5
        
        startTime = System.currentTimeMillis();

        dummy = BA.loginAccount("12312@gmail.com", "Hello@World1");
        assertNotEquals(dummy, null);

        endTime = System.currentTimeMillis();
        //noinspection IntegerDivisionInFloatingPointContext
        timePassedSeconds = (endTime - startTime)/1000;

        System.out.println(timePassedSeconds);

        assert timePassedSeconds <= 0.1; // Memory Access should be instant

        JOptionPane.showMessageDialog(null, "Test cases passed", "Stress test", JOptionPane.INFORMATION_MESSAGE);

    }

    @Test
    public void testRegister() {

        BankAutomated BA = new BankAutomated();
    
        dummy = BA.createAccount("test", "dummy", "416-792-1234", "test street",
                "Male", "01/01/1990", "test@gmail.com", "Hello@World1",
                "4417123456789113", "01/01/2027", "555");
        assertNotEquals(dummy, null);

        dummy = BA.createAccount("test", "dummy", "416-792-1234", "test street",
                "Female", "01/01/1990", "test@.com", "Hello@World1",
                "4417123456789113", "01/01/2027", "555");
        assertNull(dummy);

        dummy = BA.createAccount("test", "dummy", "416-792-1234", "test street",
                "Other", "01/01/1990", "test@gmail.com", "Hello@World1",
                "4417123456789113", "01/01/2027", "555");
        assertNull(dummy);

        assertEquals(BA.customerAccounts.size(), 1);

        JOptionPane.showMessageDialog(null, "Test cases passed", "testRegister", JOptionPane.INFORMATION_MESSAGE);
        
    }

    @Test
    public void testLogin() {

        BankAutomated BA = new BankAutomated();

        BA.createAccount("test", "dummy", "416-792-1234", "test street",
                "Female", "01/01/1990", "test@gmail.com", "Hello@World1",
                "4417123456789113", "01/01/2027", "555");

        dummy = BA.loginAccount("test@gmail.com", "Hello@World1");
        assertNotEquals(dummy, null);

        dummy = BA.loginAccount("test@gcxvcxvmail.com", "password");
        assertNull(dummy);

        dummy = BA.loginAccount("test@gmail.com", "fc");
        assertNull(dummy);

        JOptionPane.showMessageDialog(null, "Test cases passed", "testLogin", JOptionPane.INFORMATION_MESSAGE);
        
    }

    @Test
    public void testTransferFunds(){
        
        BankAutomated BA = new BankAutomated();

        CA cust = BA.createAccount("Jane", "Doe", "647-123-4567", "123 Example St.", "Female",
                "01/01/2000", "janedoe@example.com", "Password123@", "4417123456789113", 
                "01/01/2030", "123");
        
        // Test 1
        cust.setChequing(10000.0);
        cust.setSavings(5000.0);

        BA.transferFunds(5000.0, "Chequing", cust);
        assert(cust.getChequing() == 5000.0);
        assert(cust.getSavings() == 10000.0);

        // Test 2
        cust.setChequing(10000.0);
        cust.setSavings(5000.0);

        BA.transferFunds(5000.0, "Savings", cust);
        assert(cust.getChequing() == 15000);
        assert(cust.getSavings() == 0);

        // Test 3
        cust.setChequing(10000.0);
        cust.setSavings(5000.0);
        // Ensure correct error value is return if there are insuficcient funds
        assert(BA.transferFunds(5001, "Savings", cust) == 1);
        assert(cust.getChequing() == 10000);
        assert(cust.getSavings() == 5000);

        // Test 4
        cust.setChequing(10000.0);
        cust.setSavings(5000.0);
        // Ensure correct error value is return if there are insuficcient funds
        assert(BA.transferFunds(10001, "Chequing", cust) == 1);
        assert(cust.getChequing() == 10000);
        assert(cust.getSavings() == 5000);

        
        JOptionPane.showMessageDialog(null, "Test cases passed", "testTransferFunds", JOptionPane.INFORMATION_MESSAGE);
    }

    @Test
    public void testETransfer() {

        BankAutomated BA = new BankAutomated();

        CA jane = BA.createAccount("Jane", "Doe", "647-123-4567", "123 Example St.", "Female",
                "01/01/2000", "janedoe@example.com", "Password123@", "4417123456789113", 
                "01/01/2030", "123");

        CA john = BA.createAccount("John", "Doe", "647-987-6543", "321 Example St.", "Male",
                "01/01/2000", "johndoe@example.com", "Password123@", "4417123456789113", 
                "01/01/2030", "123");

        // Test 1
        john.setChequing(10000);
        jane.setChequing(10000);
        assert(BA.etransfer(5000, "janedoe@example.com", john) == 0);
        assert(john.getChequing() == 5000);
        assert(jane.getChequing() == 15000);

        // Test 2
        john.setChequing(10000);
        jane.setChequing(10000);
        assert(BA.etransfer(5000, "johndoe@example.com", jane) == 0);
        assert(jane.getChequing() == 5000);
        assert(john.getChequing() == 15000);

        // Error code tests
        john.setChequing(10000);
        jane.setChequing(10000);

        // Invalid Email adress (returns code 1)
        assert(BA.etransfer(500, "johndoe", jane) == 1);
        assert(john.getChequing() == 10000);
        assert(jane.getChequing() == 10000);


        // Insufficient funds (returns code 2)
        assert(BA.etransfer(10001, "johndoe@example.com", jane) == 2);
        assert(john.getChequing() == 10000);
        assert(jane.getChequing() == 10000);

        // Email not found (returns code 3)
        assert(BA.etransfer(500, "test@example.com", jane) == 3);
        assert(john.getChequing() == 10000);
        assert(jane.getChequing() == 10000);


        JOptionPane.showMessageDialog(null, "Test cases passed", "testTransferFunds", JOptionPane.INFORMATION_MESSAGE);
    }

    @Test
    public void testBankTransfer()
    {
        BankAutomated BA = new BankAutomated();

        CA jane = BA.createAccount("Jane", "Doe", "647-123-4567", "123 Example St.", "Female",
                "01/01/2000", "janedoe@example.com", "Password123@", "4417123456789113", 
                "01/01/2030", "123");

        CA john = BA.createAccount("John", "Doe", "647-987-6543", "321 Example St.", "Male",
                "01/01/2000", "johndoe@example.com", "Password123@", "4417123456789113", 
                "01/01/2030", "123");
        
        jane.setBankNumber("12345");
        john.setBankNumber("54321");

        // Test 1
        john.setChequing(10000);
        jane.setChequing(10000);
        assert(BA.bankTransfer(5000, "12345", john) == 0);
        assert(john.getChequing() == 5000);
        assert(jane.getChequing() == 15000);

        // Test 2
        john.setChequing(10000);
        jane.setChequing(10000);
        assert(BA.bankTransfer(5000, "54321", jane) == 0);
        assert(jane.getChequing() == 5000);
        assert(john.getChequing() == 15000);

        // Error code tests
        john.setChequing(10000);
        jane.setChequing(10000);

        // Invalid bank number adress (returns code 1)
        assert(BA.bankTransfer(500, "123456789", jane) == 1);
        assert(john.getChequing() == 10000);
        assert(jane.getChequing() == 10000);


        // Insufficient funds (returns code 2)
        assert(BA.bankTransfer(10001, "54321", jane) == 2);
        assert(john.getChequing() == 10000);
        assert(jane.getChequing() == 10000);

        // Email not found (returns code 3)
        assert(BA.bankTransfer(500, "98765", jane) == 3);
        assert(john.getChequing() == 10000);
        assert(jane.getChequing() == 10000);

        JOptionPane.showMessageDialog(null, "Test cases passed", "testTransferFunds", JOptionPane.INFORMATION_MESSAGE);
    }
    // Future test cases for test and integration phase
        
}
