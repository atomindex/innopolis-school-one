import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Atom on 7/22/2015.
 */
public class Transaction {
    private static int ids = 1;

    private int id;

    private String name;

    private Account fromAccount;
    private Account toAccount;
    private double money;

    private Branch fromBranch;
    private Branch toBranch;

    private boolean performed;
    private double commissionPercent;
    private double commission;
    private double sum;

    private long time;



    public Transaction(String name, Account from, Account to, double money) {
        id = ids++;

        this.name = name;

        this.fromAccount = from;
        this.toAccount = to;
        this.money = money;

        performed = false;

        update();
    }



    //Перерасчитывает данные (проценты и т.д.)
    public void update() {
        if (performed) return;

        fromBranch = fromAccount.getBranch();
        toBranch = toAccount.getBranch();

        commissionPercent = fromBranch.getBank().getCommission(toAccount, fromAccount, money);
        commission = money / 100.0 * commissionPercent;
        sum = money + commission;

        time = fromBranch.getBank().getTime();
    }

    //Выполняет транзакцию, и блокирует ее для изменений
    public boolean perform() {
        if (performed) return false;

        update();
        if (fromAccount == toAccount || fromAccount.isClosed() || fromAccount.getBalance() < sum)
            return false;

        performed = true;
        return true;
    }

    //Возвращает true, если транзакция выполнена
    public boolean isPerformed() {
        return performed;
    }



    //Возвращает идентификатор
    public int getId() {
        return id;
    }

    //Возвращает имя(тип) транзакции
    public String getName() {
        return name;
    }

    //Возвращает время исполнения транзакции
    public long getTime() {
        return time;
    }

    //Возвращает время исполнения транзакции в читаемом виде
    public String getStringTime() {
        return (new SimpleDateFormat("dd.MM.yyyy HH:mm:ss")).format(new Date(time));
    }



    //Возвращает аккаунт, с которого перечисляются деньги
    public Account getFromAccount() {
        return fromAccount;
    }

    //Возвращает аккаунт, на который перечисляются деньги
    public Account getToAccount() {
        return toAccount;
    }

    //Возвращает сумму денег для перевода
    public double getMoney() {
        return money;
    }



    //Возвращает брэнч, с которого были переведены деньги
    public Branch getFromBranch() {
        return fromBranch;
    }

    //Возвращает брэнч, на который были переведены деньги
    public Branch getToBranch() {
        return toBranch;
    }

    //Возвращает комиссию, взятую при переводе в проентах
    public double getCommissionPercent() {
        return commissionPercent;
    }

    //Возврщаеь комиссию, взятую при переводе в деньгах
    public double getCommission() {
        return commission;
    }

    //Возвращает обсую сумму, которая была снята со счета отправителя
    public double getSum() {
        return sum;
    }



    //Возвращает некоторые данные транзакции в виде строки
    public String toString() {
        return String.format("%s from: %s to: %s, money: %.2fр., time: %s",
                name,
                fromAccount.getAccountNumber(),
                toAccount.getAccountNumber(),
                sum,
                getStringTime()
        );
    }
}
