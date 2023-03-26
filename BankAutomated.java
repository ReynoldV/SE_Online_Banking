import static org.junit.Assume.assumeNotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class BankAutomated
{
    List<CA> customerAccounts = Collections.synchronizedList(new ArrayList<CA>());
    private final ConcurrentHashMap<String, CA> customerHash;
    private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    @SuppressWarnings("SpellCheckingInspection")
    public enum State {HOME, ACCOUNT, ETRANS, BANKTRANS, FUNDTRANS, MEETREQ, MAKEREP, LOCATE,
                        NOTIF, NOTIFSET, PRIVSET, EDITPROF, SETTINGS}

    public BankAutomated() {
        // Email -> Account, thread safe Hash map
        this.customerHash = new ConcurrentHashMap<>();

        System.out.println("Loading customer objects...");

        long startTime = System.currentTimeMillis();

        // Load customer account data from the "People.ser" serialized file
        try (FileInputStream accountsInput = new FileInputStream("People.ser");
             BufferedInputStream bufferedIn = new BufferedInputStream(accountsInput);
             ObjectInputStream accountObject = new ObjectInputStream(bufferedIn)) {

            // Read all the CA objects from the serialized file into a list
            List<CA> accounts = new ArrayList<>();
            while (true) {
                try {
                    CA account = (CA) accountObject.readObject();
                    accounts.add(account);
                } catch (EOFException ex) {
                    break;
                }
            }

            // Submit each account to the executor for processing
            List<Future<Void>> futures = new ArrayList<>();
            for (CA account : accounts) {
                futures.add(executor.submit(() -> {
                    customerAccounts.add(account);
                    customerHash.put(account.email, account);
                    return null;
                }));
            }

            // Wait for all tasks to complete and handle any exceptions
            futures.forEach(future -> {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                }
            });

        } catch (FileNotFoundException ex) {
            // usually means nothing is inside
            System.out.println("File is empty");
        } catch (IOException ex) {
            // System.out.println("nothing inside");
            // usually means the file is corrupted or nothing inside
            // ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            // the CA object that was serialized was changed after it had been serialized
            clearPeopleFile();
        }

        long endTime = System.currentTimeMillis();
        double timePassedSeconds = (endTime - startTime) / 1000.0;

        System.out.println("Loaded " + customerAccounts.size() + " customer objects. In: " + timePassedSeconds + "s");
    }

    // Clear the People.ser file
    public void clearPeopleFile() {
        try {

            // Delete the file
            Path of = Path.of("People.ser");
            Files.deleteIfExists(of);
            customerAccounts.clear();
            customerHash.clear();
    
            // Create a new empty file
            Files.createFile(of);
            
            // Print a message
            System.out.println("Cleared People.ser file");

        // File doesn't exist
        } catch (NoSuchFileException ex) {
            System.out.println("People.ser file doesn't exist");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Validate email format
    public boolean validEmail(String email)
    {
        if (email.contains("@"))
        {
            String username = email.substring(0, email.indexOf('@'));
            if (username.equals(""))
            {
                return false;
            }
        }
        return email.contains("@") && (email.contains(".com") || email.contains(".ca")) &&
                ( (email.indexOf(".com") - email.indexOf("@") >= 2) || (email.indexOf(".ca") - email.indexOf("@") >= 2) );
    }

    //Checks if email already exists
    public boolean existingEmail(String email)
    {
        return customerHash.containsKey(email);
    }

    // Validate password format
    public boolean validPassword(String password)
    {
        if (password.length() < 8)
        {
            return false;
        }

        ArrayList<Character> specialChar = new ArrayList<>(
                Arrays.asList('!' , '"' , '#' , '$' , '%' , '&' , '\'' , '(' , ')' , '*' , '+' , '-' , '.' , '/' , ':' ,
                        ';' , '<' , '=' , '>' , '?' , '@' , '[' , '\\' , ']' , '^' , '_' , '`' , '{' , '|' , '}' , '~')
        );

        int lowerCharCount = 0; int upperCharCount = 0; int numCount = 0; int specialCount = 0;

        // Check if the password contains at least one of each type of character
        for (int i = 0; i < password.length(); i++)
        {
            char current = password.charAt(i);
            if (current >= 'A' && current <= 'Z')
            {
                upperCharCount++;
            }
            else if (current >= 'a' && current <= 'z')
            {
                lowerCharCount ++;
            }
            else if (current >= '0' && current <= '9')
            {
                numCount++;
            }
            else if (specialChar.contains(current))
            {
                specialCount++;
            }
        }
        return lowerCharCount > 0 && upperCharCount > 0 && numCount > 0 && specialCount > 0;
    }

    // This allows a CA to log in to the system, and authenticates this user
    public CA loginAccount(String email, String password)
    {
        System.out.println("Logging in customer with email: " + email);

        // Multithreaded Stream
        CA customer = (CA) customerHash.get(email);
        if (customer != null && password.equals(customer.password)) {
            return customer;
        }

        System.out.println("Customer with email: " + email + " not found.");
        return null;
    }

    // Checks if the string contains numbers only
    public boolean onlyNumeric(String str)
    {
        for(int i = 0; i < str.length(); i++)
        {
            if (!Character.isDigit(str.charAt(i)))
            {
                return false;
            }
        }

        return true;
    }

    // Checks if the CVV of the card is valid.
    public boolean validCVV(String cvv)
    {
        if (onlyNumeric(cvv))
        {
            return cvv.length() == 3 || cvv.length() == 4;
        }

        return false;
    }

    // Checks if the date of birth is valid
    public boolean validDOB(String month, String day, String year)
    {
        int numYear = Integer.parseInt(year);
        int numDay = Integer.parseInt(day);

        switch(month) {
            case "02":
                if (numYear % 4 == 0 && (numYear % 100 != 0 || numYear % 400 == 0)) {
                    return numDay <= 29;
                } else {
                    return numDay <= 28;
                }
            case "04":
            case "06":
            case "09":
            case "11":
                return numDay <= 30;
            default:
                return numDay <= 31;
        }
    }

    //Checks for the validity of the card number by various measures, i.e. length, starting digit, Luhn's algorithm
    public boolean validCard(String cardNum)
    {
        // If the length is not between 13 and 19 digits, then the card number is invalid
        // If input was not all digits, then cardNum is invalid
        // Card numbers only start with 4 (Visa), 3 (American Express), 2 or 5 (Mastercard)

        if (cardNum.length() < 13 || cardNum.length() > 19 || !onlyNumeric(cardNum) ||
                !(cardNum.charAt(0) == '4' || cardNum.charAt(0) == '3' || cardNum.charAt(0) == '2' || cardNum.charAt(0) == '5'))
        {
            return false;
        }

        // Luhn's algorithm to check if the card number is valid
        int sum = 0;
        boolean alternate = false;

        // Loop through the card number backwards
        for (int i = cardNum.length() - 1; i >= 0; i--)
        {

            // Get the digit at the current index
            int n = cardNum.charAt(i) - '0';

            // If the current digit is the second digit from the right, then double it
            if (alternate)
            {
                n = n*2;
            }

            // If the current digit is greater than 9, then add the two digits together
            if (n>9)
            {
                sum += (n/10);
                sum += n%10;
            }

            // Else, add the current digit to the sum
            else
            {
                sum += n;
            }

            // Alternate between true and false
            alternate = !alternate;
        }

        // If the sum is divisible by 10, then the card number is valid
        return sum % 10 == 0;
    }

    // This function returns

    // This allows a new client to register for an account by inputting their details
    public CA createAccount(String firstName, String lastName, String phoneNum, String address, String gender, String dob,
                            String email, String password, String cardNum, String cardExpiry, String cvv)
    {
        // Check if email already exists in the ConcurrentHashMap
        if (existingEmail(email))
        {
            System.out.println("Email already exists");
            return null;
        }

        // Check if email is valid
        if (!validEmail(email))
        {
            System.out.println("Email is invalid");
            return null;
        }

        // Check if password is valid
        if (!validPassword(password))
        {
            System.out.println("Password is invalid");
            return null;
        }

        // Check if card number is valid
        if (!validCard(cardNum))
        {
            System.out.println("Card Number is invalid");
            return null;
        }

        //Generate a random amount between 0 and 10,000 for chequing, savings, and bankNumber
        Random rand = new Random();
        double randCheq = rand.nextDouble()*10000;
        double randSav = rand.nextDouble()*10000;
        String bankNum = String.valueOf(rand.nextInt(10000));

        // Create and return new CA object
        CA customer = new CA(firstName, lastName, phoneNum, address, gender, dob, email, password, cardNum, cardExpiry,
                             cvv, randCheq, randSav, bankNum);

        // Add the new account to the customerAccounts list and customerHash map
        customerAccounts.add(customer);
        customerHash.put(email, customer);

        return customer;
    }

    // Allows customers to create a report about any suspicious activity, one for customers without
    // accounts, and one for logged in customers
    public void makeReport(String customerFName, String customerLName, String email)
    {

    }
    public void makeReport(CA customer)
    {

    }

    // Depending on request type, this gets the correct receiver and adds the customer request to their
    // correct argument (Request arraylist)
    public void makeRequest(String type)
    {

    }

    // Allow users to transfer between chequing and savings accounts
    // ERROR CODES: returns 0 if successful, 1 if insufficient funds
    public int transferFunds(double transferAmount, String fromAccount, CA customer)
    {
        if (fromAccount.equals("Chequing"))
        {
            if (transferAmount > customer.getChequing())
            {
                return 1;
            }
            customer.setChequing(customer.getChequing() - transferAmount);
            customer.setSavings(customer.getSavings() + transferAmount);
            return 0;
        }
        else
        {
            if (transferAmount > customer.getSavings())
            {
                return 1;
            }
            customer.setChequing(customer.getChequing() + transferAmount);
            customer.setSavings(customer.getSavings() - transferAmount);
            return 0;
        }
    }

    // Allow users to etransfer from their account to another user with using the receiver's email
    // ERROR CODES: returns 0 if successful, returns 1 if email is not valid, returns 2 if 
    // customer has insufficient funds, returns 3 if recieverEmail is not found
    public int etransfer(double amount, String receiverEmail, CA customer)
    {
        if (!validEmail(receiverEmail))
        {
            return 1;
        }
        else if (amount > customer.getChequing())
        {
            return 2;
        }
        CA receiver = customerHash.get(receiverEmail);

        if (receiver == null)
        {
            return 3;
        }

        receiver.setChequing(receiver.getChequing() + amount);
        customer.setChequing(customer.getChequing() - amount);
        return 0;
    }

    // Allow users to make a bank transfer from their account to another user using the receiver's
    // bank account number
    // ERROR CODES: returns 0 if successful, returns 1 if bank number is not 5 digits,
    // returns 2 if chequing balance is too low for transfer amount, 
    // returns 3 if no valid receiver account is found with bankNumber receiverAcc
    public int bankTransfer(int amount, String receiverAcc, CA customer)
    {
        if (receiverAcc.length() != 5)
        {
            return 1;
        }
        if (amount > customer.getChequing())
        {
            return 2;
        }

        CA receiver = null;
        // Loop through the accounts in the hashmap to find matching
        for (CA account : customerHash.values())
        {
            if (account.getBankNumber() == receiverAcc)
            {
                receiver = account;
                break;
            }
        }

        if (receiver != null)
        {
            receiver.setChequing(receiver.getChequing() + amount);
            customer.setChequing(customer.getChequing() - amount);
            return 0;
        }
        else 
        {
            return 3;
        }


    }

    // Change the customer's details in the settings tab. Customers can change their address, email,
    // and phone number
    public void changeSettings(CA customer)
    {

    }
    //returns all the required addresses for findUs
    public ArrayList<String> addresses()
    {
        ArrayList<String> lst = new ArrayList<String>();


        lst.add("10153 King George Blvd, Vancouver, BC");
        lst.add("255 Yonge Street, Toronto, ON");
        lst.add("2210 Bank Street, Ottawa, ON");
        lst.add("1955 Chandler Road, New York City, NY");
        lst.add("21 Lovecraft Lane, Montreal, QC");
        lst.add("1965 Herbert Blvd, Halifax, NS");

        return lst; 
    }
    public void logout()
    {

        long startTime = System.currentTimeMillis();

        System.out.println("Uploading customer objects");

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        // Create a Callable that writes each customer account to the file
        Callable<Void> task = () -> {

            // Try-with-resources to automatically close the streams
            try (FileOutputStream accountsFile = new FileOutputStream("People.ser");
                
                // BufferedOutputStream is used to improve performance 
                BufferedOutputStream bufferedOut = new BufferedOutputStream(accountsFile);

                // ObjectOutputStream is used to write objects to the file
                ObjectOutputStream accountObject = new ObjectOutputStream(bufferedOut)) {

                // Write each customer account to the file
                customerAccounts.forEach(account -> {
                    try {
                        accountObject.writeObject(account);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
                return null;

            // Catch any exceptions that may occur
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
        };

        // Submit the task to the executor for each available processor
        List<Future<Void>> futures = new ArrayList<>();
        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            futures.add(executor.submit(task));
        }

        // Wait for all tasks to complete and shutdown the executor
        futures.forEach(future -> {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException ex) {
                ex.printStackTrace();
            }
        });

        long endTime = System.currentTimeMillis();
        double timePassedSeconds = (endTime - startTime) / 1000.0;

        System.out.println("Uploaded " + customerAccounts.size() + " customer objects. In: " + timePassedSeconds + "s");

        executor.shutdown();

    }
}
