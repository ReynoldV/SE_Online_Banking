public class BankSystem
{
    public static void main(String[] args)
    {
        try
        {
            BankAutomated BA = new BankAutomated();
            new LoginPage(BA);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}
