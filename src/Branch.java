/**
 * Created by Atom on 7/24/2015.
 */
public class Branch {
    private static int ids = 1;

    private Bank bank;
    private Account[] accounts;

    private int id;
    private String manager;
    private String address;
    private String telephoneNumber;
    private String businessHours;



    public Branch(String manager, String address, String telephoneNumber, String businessHours) {
        id = ids++;

        accounts = new Account[0];

        this.manager = manager;
        this.address = address;
        this.telephoneNumber = telephoneNumber;
        this.businessHours = businessHours;
    }



    //Устанавливает банк, к которому относится брэнч
    public void setBank(Bank bank) {
        this.bank = bank;
    }

    //Получает банк, к которому относится брэнч
    public Bank getBank() {
        return bank;
    }



    //Добавляет аккаунт в брэнч
    public int addAccount(Account account) {
        Account[] tempAccounts = new Account[accounts.length + 1];
        for (int i = 0; i < accounts.length; i++)
            tempAccounts[i] = accounts[i];
        tempAccounts[accounts.length] = account;

        account.setBranch(this);

        accounts = tempAccounts;

        return accounts.length - 1;
    }

    //Возврщает список аккаунтов
    public Account[] getAccounts() {
        return accounts;
    }



    //Возвращает идентификатор
    public int getId() {
        return id;
    }

    //Возвращает менеджера
    public String getManager() {
        return manager;
    }

    //Возвращает адрес
    public String getAddress() {
        return address;
    }

    //Возвращает номер телефона
    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    //Возвращает часы приема
    public String getBusinessHours() {
        return businessHours;
    }
}
