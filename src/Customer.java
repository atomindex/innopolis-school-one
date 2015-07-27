/**
 * Created by Atom on 7/23/2015.
 */
public class Customer {
    private static int ids = 1;

    private Account[] accounts;

    private int id;
    private String firstName;
    private String lastName;
    private String patronym;

    private String birth;
    private boolean gender; //true = male
    private String passportNumber;



    public Customer(String firstName, String lastName, String patronym, String birth, boolean gender, String passportNumber) {
        id = ids++;

        accounts = new Account[0];

        this.firstName = firstName;
        this.lastName = lastName;
        this.patronym  = patronym;

        this.birth = birth;
        this.gender = gender;
        this.passportNumber = passportNumber;
    }



    //Добавление аккаунта
    public int addAccount(Account account) {
        if (account.getCustomer() == this) return -1;

        Account[] tempAccounts = new Account[accounts.length + 1];
        for (int i = 0; i < accounts.length; i++)
            tempAccounts[i] = accounts[i];
        tempAccounts[accounts.length] = account;

        account.setCustomer(this);

        accounts = tempAccounts;

        return accounts.length - 1;
    }

    //Возвращает список аккаунтов
    public Account[] getAccounts() {
        return accounts;
    }



    //Возвращает идентификатор
    public int getId() {
        return id;
    }

    //Возвращает ФИО
    public String getFullName() {
        return firstName + " " + lastName + " " + patronym;
    }
}
