import java.util.ArrayList;
import java.util.stream.Collectors;
import java.lang.Math;

// customer account
public class CA extends People
{
    String address;
    String cardNum;
    String password;
    String gender;
    String dob;
    String cardExpiry;
    String cvv;
    double chequing;
    double savings;
    ArrayList<Transaction> chequingHist;
    ArrayList<Transaction> savingsHist;
    ArrayList<Report> reportSus;
    ArrayList<Request> requests;

    // NOTE: Will need to remove later, don't remove or everything breaks
    // private static final long serialVersionUID = 1L;

    public CA(String firstName, String lastName, String phoneNum, String address, String gender, String dob,
              String email, String password, String cardNum, String cardExpiry, String cvv, double chequing,
              double savings)
    {
        super(firstName, lastName, email, phoneNum);
        this.address = address;
        this.password = password;
        this.cardNum = cardNum;
        this.cardExpiry = cardExpiry;
        this.cvv = cvv;
        this.gender = gender;
        this.dob = dob;
        this.chequing = chequing;
        this.savings = savings;

        chequingHist = new ArrayList<Transaction>();
        savingsHist = new ArrayList<Transaction>();
        reportSus = new ArrayList<Report>();
        requests = new ArrayList<Request>();
    }

    // UPDATE ALL SETTERS AND GETTERS TO BE FOR EACH PARAMETER
    public void setCardExpiry()
    {
        this.cardExpiry = cardExpiry;

    }

    public String getCardExpiry()
    {
        return cardExpiry;
    }
    
    // Get and Set cvv
    public void setCvv()
    {
        this.cvv = cvv;

    }

    public String getCvv()
    {

        return cvv;
    }
    // Set and get Password
    public void setPassword()
    {
        this.password = password;
    }

    public String getPassword()
    {
        return password;

    }
    
    //Set and get Dob
    public void setDob()
    {
        this.dob = dob;

    }

    public String getDob()
    {
        return dob;

    }
    //Get and Set Gender
    public void setGender()
    {
        this.gender = gender;

    }

    public String getGender()
    {
        return gender;

    }
    // Setter and getter for address
    public String getAddress()
    {
        return this.address;
    }
    public void setAddress(String Address)
    {
        this.address = Address;
    }

    // Getter for card number, chequing funds, and savings funds
    public void setCardNum()
    {
        this.cardNum = cardNum;

    }
    public String getCardNum()
    {
        return cardNum;
    }

    public void setChequing()
    {
        this.chequing = chequing;

    }
    public double getChequing()
    {
        return Math.round(chequing *100.0)/100.0;
    }

    public void setSaving()
    {
        this.savings = savings;

    }
    public double getSavings()
    {
        return Math.round(savings*100.0)/100.0;
    }

    // Getter and setter for chequing transaction history
    public ArrayList<Transaction> getChequingHist()
    {
        return chequingHist;
    }
    public void addChequing(Transaction chequingTransaction)
    {
        chequingHist.add(chequingTransaction);
    }

    // Getter and setter for savings transaction history
    public ArrayList<Transaction> getSavingsHist()
    {
        return savingsHist;
    }
    public void addSaving(Transaction savingTransaction)
    {
        savingsHist.add(savingTransaction);
    }

    // Getter and setter for suspicious activity reports
    public ArrayList<Report> getReportSus()
    {
        return reportSus;
    }
    public void addReport(Report suspiciousReport)
    {
        reportSus.add(suspiciousReport);
    }

    // Getter and setter for request history
    public ArrayList<Request> getRequests()
    {
        return requests;
    }
    public void addRequests(Request request)
    {
        requests.add(request);
    }
    public void print() {
        System.out.printf("First Name: %-10s Last Name: %-10s | Email: %-25s | Password: %-20s " +
                " | Address: %-20s\n", firstName, lastName, email, password, address);
    }
    public void printHistory() {
        System.out.printf("%s %s -> ", firstName, lastName);
        System.out.printf("Chequing Transaction History: %-57s | Savings Transaction History: %-65s %n",
                chequingHist == null ? "No chequing transactions" : chequingHist.stream().map(Transaction::toString).collect(Collectors.joining(", ")),
                savingsHist == null ? "No savings transactions" : savingsHist.stream().map(Transaction::toString).collect(Collectors.joining(", "))
        );
    }
}