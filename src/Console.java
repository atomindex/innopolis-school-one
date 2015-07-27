import java.util.Scanner;

/**
 * Created by Atom on 7/24/2015.
 */
public class Console {
    private static Scanner in = new Scanner(System.in);



    //Запрашивает и возвращает число, с обработкой ошибок
    public static int getInt() {
        while (!in.hasNextInt()) {
            in.nextLine();
            System.out.print("Значение должно быть числом\n> ");
        }

        int result = in.nextInt();
        in.nextLine();

        return result;
    }

    //Запрашивает и возвращает число, с обработкой ошибок
    public static double getDouble() {
        while (!in.hasNextDouble()) {
            in.nextLine();
            System.out.print("Значение должно быть числом\n> ");
        }

        double result = in.nextDouble();
        in.nextLine();

        return result;
    }



    //Вывод меню
    public static void printMenu(Account account) {
        if (account != null) System.out.print("1. Выйти из аккаунта " + account.getAccountNumber() + "\t\t");
        else System.out.print("1. Зайти в аккаунт\t\t\t\t");
        System.out.println("9. Список клиентов");
        System.out.print("2. Вывод остатка на счете\t\t");
        System.out.println("10. Список аккаунтов");
        System.out.print("3. Перевести деньги\t\t\t\t");
        System.out.println("11. Отчет для клиента");
        System.out.print("4. Изменить тип аккаунта\t\t");
        System.out.println("12. Отчет для брэнча");
        System.out.print("5. Добавить брэнч\t\t\t\t");
        System.out.println("13. Отчет для всех брэнчей");
        System.out.print("6. Добавить клиента\t\t\t\t");
        System.out.println("14. Сгенерировать транзакции");
        System.out.print("7. Добавить аккаунт\t\t\t\t");
        System.out.println("15. Выйти");
        System.out.println("8. Список брэнчей");
        System.out.print("> ");
    }



    //Запрашивает и возвращает аккаунт
    public static Account getAccount(Bank bank, String label) {
        Account account = null;

        while (account == null) {
            System.out.print(label + "\n> ");
            account = bank.getAccount(in.nextLine());

            if (account == null)
                System.out.print("Аккаунта не существует. ");
            else if (account.isClosed())
                System.out.print("Аккаунт закрыт. ");
        }

        return account;
    }

    //Запрашивает и возвращает аккаунт
    public static Account getAccount(Bank bank) {
        return getAccount(bank, "Введите номер аккаунта");
    }

    //Запрашивает и возвращает брэнч
    public static Branch getBranch(Bank bank, String label) {
        Branch branch = null;

        while (branch == null) {
            System.out.print(label + "\n> ");
            int branchId = getInt();

            branch = bank.getBranch(branchId - 1);

            if (branch == null)
                System.out.print("Брэнча не существует. ");
        }

        return branch;
    }

    //Запрашивает и возвращает брэнч
    public static Branch getBranch(Bank bank) {
        return getBranch(bank, "Введите идентификатор брэнча");
    }

    //Запрашивает и возвращает клиента
    public static Customer getCustomer(Bank bank, String label) {
        Customer customer = null;

        while (customer == null) {
            System.out.print(label + "\n> ");
            int customerId = getInt();

            customer = bank.getCustomer(customerId - 1);

            if (customer == null)
                System.out.print("Клиента не существует. ");
        }

        return customer;
    }

    //Запрашивает и возвращает клиента
    public static Customer getCustomer(Bank bank) {
        return getCustomer(bank, "Введите идентификатор клиента");
    }



    //Запрашивает данные и создает брэнч
    public static Branch newBranch() {
        System.out.print("Введите данные брэнча:\nМенеджер > ");
        String manager = in.nextLine();
        System.out.print("Адрес > ");
        String address = in.nextLine();
        System.out.print("Телефон > ");
        String phone = in.nextLine();
        System.out.print("Часы приема > ");
        String buisnesHours = in.nextLine();

        return new Branch(manager, address, phone, buisnesHours);
    }

    //Запрашивает данные и создает клиента
    public static Customer newCustomer() {
        System.out.print("Введите данные клиента\nИмя > ");
        String firstName = in.nextLine();
        System.out.print("Фамилия > ");
        String lastName = in.nextLine();
        System.out.print("Отчество > ");
        String patronym = in.nextLine();
        System.out.print("Дата рождения > ");
        String birth = in.nextLine();
        System.out.print("Пол (male/female) > ");
        String gender = in.nextLine();
        System.out.print("Номер пасорта > ");
        String pasport = in.nextLine();

        return new Customer(firstName, lastName, patronym, birth, gender == "male", pasport);
    }

    //Запрашивает данные и создает аккаунт
    public static Account newAccount(Bank bank) {
        System.out.print("Введите данные аккаунта:\nНомер аккаунта > ");
        String accountNumber;
        while (true) {
            accountNumber = in.nextLine();
            Account account = bank.getAccount(accountNumber);
            if (account == null) break;
            else System.out.print("Аккаунт уже существует.\n> ");
        }
        System.out.print("Пароль > ");
        String password = in.nextLine();

        System.out.print("Баланс > ");
        double balance = getDouble();

        return new Account(accountNumber, password, balance, false);
    }



    //Перевод денег
    public static void transfer(Bank bank, Account fromAccount) {
        if (fromAccount == null)
            fromAccount = getAccount(bank, "Введите номер аккаунта, с которого переводить деньги");

        if (fromAccount.isClosed()) {
            System.out.print("Аккаунт закрыт. Перевод денег с данного аккаунта не возможен.");
            return;
        }

        Account toAccount = getAccount(bank, "Введите номер аккаунта, на который переводить деньги");

        double money = 0;
        while (true) {
            System.out.print("Введите количество денег, которое нужно перевести\n> ");
            money = getDouble();
            if (money < 0)
                System.out.print("Число должно быть положительным. ");
            else break;
        }

        Transaction transaction = new Transaction("Перевод денег", fromAccount, toAccount, money);
        transaction.update();

        if (fromAccount.getBalance() < transaction.getSum()) {
            System.out.format(
                    "Операция отменена.\nНедостаточно средств на счете%nБаланс на счете: %.2fр.%nТребуемая сумма (с учетом комиссии): %.2fр.%n%n",
                    fromAccount.getBalance(),
                    transaction.getSum()
            );
        } else {
            System.out.format(
                    "Со счета %s на счет %s%nбудет переведено %.2fр., коммиссия %.2fр. (%.2f%%)%nИтого: %.2fр.%nХотите продолжить перевод (y/n)?%n> ",
                    fromAccount.getAccountNumber(),
                    toAccount.getAccountNumber(),
                    money,
                    transaction.getCommission(),
                    transaction.getCommissionPercent(),
                    transaction.getSum()
            );

            while (true) {
                String answer = in.nextLine();
                answer = answer.trim().toLowerCase();
                if (answer.length() < 1)
                    System.out.print("Хотите продолжить перевод (y/n)?\n> ");
                else if (answer.charAt(0) == 'n') {
                    System.out.print("Операция отменена\n\n");
                    break;
                } else if (answer.charAt(0) == 'y') {
                    System.out.print("Перевод выполнен\n\n");
                    bank.performTransaction(transaction);
                    break;
                } else
                    System.out.print("Хотите продолжить перевод (y/n)?\n> ");
            }
        }
    }
}
