public class BankSystem
{
    public static void main(String[] args)
    {
        try
        {
            BankAutomated BA = new BankAutomated();
            BA.clearPeopleFile();
            CA test= BA.createAccount("Mo", "N", "6667771111", "Yonge Street", "Male",
                                "01/02/2003", "this2@gmail.com", "Hello@World1",
                            "4417123456789113", "0521", "111");
            new HomePage(new LoginPage(BA), BA, test);
            //new LoginPage(BA);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}
