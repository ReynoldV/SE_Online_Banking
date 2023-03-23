import org.junit.Test;

import javax.swing.*;
import java.util.stream.IntStream;

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

        dummy = BA.loginAccount("1@gmail.com", "Hello@World1");
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

    // Future test cases for test and integration phase
    
}
