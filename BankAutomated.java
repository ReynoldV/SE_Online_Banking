import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class BankAutomated
{
    List<CA> customerAccounts = Collections.synchronizedList(new ArrayList<CA>());
    private final ConcurrentHashMap<String, CA> customerHash;

    @SuppressWarnings("SpellCheckingInspection")
    public enum State {REGISTER, FORGOT, HOME, ACCOUNT, ETRANS, BANKTRANS, FUNDTRANS, MEETREQ, MAKEREP, LOCATE,
                        NOTIF, NOTIFSET, PRIVSET, EDITPROF, SETTINGS}

    public BankAutomated()
    {   
        // Email -> Account, thread save Hash map
        this.customerHash = new ConcurrentHashMap<>();

        System.out.println("Loading customer objects...");

        // Load customer account data from the "People.ser" serialized file
        try (FileInputStream accountsInput = new FileInputStream("People.ser");
            BufferedInputStream bufferedIn = new BufferedInputStream(accountsInput);
            ObjectInputStream accountObject = new ObjectInputStream(bufferedIn)) {
            
            // Read each serialized object until the end of the file is reached
            while (true) {
                try {
                    // Read the next CA object from the serialized file
                    CA account = (CA) accountObject.readObject();

                    // Add the account to customerAccounts arrayList
                    customerAccounts.add(account);

                    // Add email and object to hashmap
                    customerHash.put(account.email, account);

                } catch (EOFException ex) {
                    // End of file reached, break out of the loop
                    break;
                }
            }
        } catch (FileNotFoundException ex) {
            // usually means nothing is inside
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        
        System.out.println("Loaded " + customerAccounts.size() + " customer objects.");
        
        // Debug customerAccounts.forEach(CA::print);
        // Debug customerAccounts.forEach(CA::printHistory);
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

    public boolean validDOB(String month, String day, String year)
    {
        if (month.equals("02"))
        {
            // For February, we check for leap years
            int numYear = Integer.parseInt(year);
            int numDay = Integer.parseInt(day);
            if (numYear %  4 == 0)
            {
                if (numYear % 100 == 0)
                {
                    if (numYear % 400 == 0)
                    {
                        return numDay <= 29;
                    }
                    else
                    {
                        return numDay <= 28;
                    }
                }
                else
                {
                    return numDay <= 29;
                }
            }
            else
            {
                return numDay <= 28;
            }
        }
        else if (month.equals("04") || month.equals("06") || month.equals("09") || month.equals("11"))
        {
            return !day.equals("31");
        }

        return true;
    }

    //Checks for the validity of the card number by various measures, i.e. length, starting digit, Luhn's algorithm
    public boolean validCard(String cardNum)
    {
        // If the length is not between 13 and 19 digits, then the card number is invalid
        if (cardNum.length() < 13 || cardNum.length() > 19)
        {
            return false;
        }

        // If input was not all digits, then cardNum is invalid
        if (!onlyNumeric(cardNum))
        {
            return false;
        }

        // Card numbers only start with 4 (Visa), 3 (American Express), 2 or 5 (Mastercard)
        if (cardNum.charAt(0) != '2' && cardNum.charAt(0) != '3' && cardNum.charAt(0) != '4' &&
                cardNum.charAt(0) != '5')
        {
            return false;
        }

        // Luhn's algorithm
        int sum = 0;
        boolean alternate = false;

        for (int i = cardNum.length() - 1; i >= 0; i--)
        {
            int n = cardNum.charAt(i) - '0';

            if (alternate)
            {
                n = n*2;
            }

            if (n>9)
            {
                sum += (n/10);
                sum += n%10;
            }
            else
            {
                sum += n;
            }

            alternate = !alternate;
        }

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

        // Create and return new CA object
        CA customer = new CA(firstName, lastName, phoneNum, address, gender, dob, email, password, cardNum, cardExpiry, cvv);

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
    public void transferFunds(int transferAmount, String fromAccount, String toAccount)
    {

    }

    // Allow users to etransfer from their account to another user with using the receiver's email
    public void etransfer(int amount, String receiverEmail)
    {

    }

    // Allow users to make a bank transfer from their account to another user using the receiver's
    // bank account number
    public void bankTransfer(int amount, String receiverAcc)
    {

    }

    // Change the customer's details in the settings tab. Customers can change their address, email,
    // and phone number
    public void changeSettings(CA customer)
    {

    }

    public void logout()
    {
        System.out.println("Uploading customer objects...");
    
        // Create FileOutputStream and BufferedOutputStream for writing to file
        try (FileOutputStream accountsFile = new FileOutputStream("People.ser");
            BufferedOutputStream bufferedOut = new BufferedOutputStream(accountsFile);
            ObjectOutputStream accountObject = new ObjectOutputStream(bufferedOut)) {
            
            // Write each customer account to the file
            customerAccounts.forEach(account -> {
                try {
                    accountObject.writeObject(account);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        System.out.println("Uploaded customer objects.");
    }
}
