/**
 * Created by Atom on 7/23/2015.
 */

public class Account {
    private static int ids = 1;

    private Branch branch;
    private Customer customer;

    private int id;
    private String accountNumber;
    private String password;
    private double balance;
    private boolean closed;

    private int accountType;
    private boolean customType;
    private boolean bankAccount;



    public Account(String accountNumber, String password, double balance, boolean bankAccount) {
        id = ids++;

        this.accountNumber = accountNumber;
        this.password = password;
        this.balance = balance;

        accountType = bankAccount ? 0 : -1;
        this.bankAccount = bankAccount;

        updateType();

        closed = false;
    }

    public Account(String accountNumber, String password, double balance) {
        this(accountNumber, password, balance, false);
    }



    //Устанавливает брэнч
    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    //Возвращает брэнч
    public Branch getBranch() {
        return branch;
    }

    //Устанавливает клиента
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    //Возвращает клиента
    public Customer getCustomer() {
        return customer;
    }



    //Добавляет деньги на аккаунт
    public void addMoney(double money) {
        balance += money;
        if (closed && balance >= branch.getBank().getMinimalBalance())
            closed = false;
    }

    //Вычитает деньги из аккаунта
    public void subtractMoney(double money) {
        balance -= money;
        if (balance <= 0)
            closed = true;
    }



    //Обновляет тип аккаунта
    public void updateType() {
        if (customType || bankAccount) return;

        if (accountType < 0) {

            if (balance < 1000) accountType = 0;
            else if (balance < 50_000) accountType = 1;
            else if (balance <= 5_000_000) accountType = 2;
            else if (balance > 5_000_000) accountType = 3;

        } else if (accountType == 2 && balance > 5_000_000)
            accountType = 3;
    }

    //Изменяет тип аккаунта
    public void changeType(int t) {
        accountType = t;
        customType = true;
    }

    //Возвращает тип аккаунта
    public int getAccountType() {
        return accountType;
    }



    //Возвращает номер аккаунта
    public String getAccountNumber() {
        return accountNumber;
    }

    //Возвращает баланс
    public double getBalance() {
        return balance;
    }

    //Закрывает / открывает аккаунт
    public void close(boolean closed) {
        this.closed = closed;
    }

    //Проверяет закрыт ли аккаунт
    public boolean isClosed() {
        return closed;
    }

    //Проверяет является ли аккаунт, аккаунтом банка
    public boolean isBankAccount() {
        return bankAccount;
    }
}
