import java.util.Random;

/**
 * Created by Atom on 7/23/2015.
 */
public class Main {

    //Создает брэнчи, клиентов и аккаунты
    public static void generateData(Bank bank) {
        //Добавляем брэнчи
        System.out.println("# Добавляем брэнчи");
        bank.addBranch(new Branch("Менеджерович Менеджер Менеджерович", "г. Иннополис, ул. Университетская, д. 5", "222-333-444", "10-17"));
        bank.addBranch(new Branch("Иванов Александр Игоревич", "г. Иннополис, ул. Спортивная, д. 22", "555-666-777", "09-18"));
        bank.addBranch(new Branch("Васюков Антон Викторович", "г. Казань, Проспект Октября, д. 11", "888-999-111", "10-19"));
        bank.addBranch(new Branch("Галкина Анна Анатольевна", "г. Москва, Проспект Ленина, д. 5", "231-456-987", "10-19"));
        Branch[] branches = bank.getBranches();

        //Добавляем пользователей
        System.out.println("# Добавляем пользователей");
        Customer customer1 = new Customer("Иван", "Иванов", "Иванович", "05.07.1992", true, "");
        bank.addCustomer(customer1);
        Customer customer2 = new Customer("Егоров", "Евгений", "Михайлович", "20.11.1983", true, "");
        bank.addCustomer(customer2);
        Customer customer3 = new Customer("Юзеров", "Юзер", "Юзерович", "19.02.1961", true, "");
        bank.addCustomer(customer3);

        //Добавляем аккаунты
        System.out.println("# Добавляем аккаунты");
        bank.addAccount(branches[0], customer1, new Account("123", "321", 6_000_000));
        bank.addAccount(branches[2], customer1, new Account("456", "654", 50_000_000));

        bank.addAccount(branches[1], customer2, new Account("789", "987", 500_000));

        bank.addAccount(branches[0], customer3, new Account("111", "111", 40_000));
        bank.addAccount(branches[3], customer3, new Account("222", "222", 500_000));
        System.out.println("");
    }

    //Генерирует транзакции между текущими пользователями
    public static void generateTransitions(Bank bank, int days) {
        Random random = new Random();

        //Количество секунд в сутках
        int day = 24 * 60 * 60;

        //Создаем двумерный массив с аккаунтами
        Branch[] branches = bank.getBranches();
        Account[][] accounts = new Account[branches.length][];
        for (int i = 0; i < accounts.length; i++)
            accounts[i] = branches[i].getAccounts();

        int currentDay = 0;
        int prevDay = 0;
        int firstDay = (int)((bank.getTime() / 1000.0) / day);
        int secs = 0;

        System.out.println("#day 1");
        while (true) {
            prevDay = currentDay;
            currentDay = (int)((bank.getTime() / 1000.0 + secs) / day - firstDay);
            //Если день поменялся, обновляем банк
            if (currentDay != prevDay) {
                bank.updateBank();
                if (currentDay < days)
                    System.out.println("#day " + (currentDay + 1));
                else if (currentDay >= days) break;
            }

            //Добавляем рандомное количество секунд ко времени банка
            secs = random.nextInt(day / 4);
            bank.addTime(secs);

            //Выбираем случайный аккаунт
            int fromBranchIndex = 0;
            do {
                fromBranchIndex = random.nextInt(accounts.length);
            } while (accounts[fromBranchIndex].length == 0);
            int fromAccountIndex = random.nextInt(accounts[fromBranchIndex].length);

            int toBranchIndex = 0;
            int toAccountIndex = 0;
            do {
                do {
                    toBranchIndex = random.nextInt(accounts.length);
                } while (accounts[toBranchIndex].length == 0);
                toAccountIndex = random.nextInt(accounts[toBranchIndex].length);
            } while (fromBranchIndex == toBranchIndex && fromAccountIndex == toAccountIndex);

            //Создаем транзакцию
            Transaction t = new Transaction(
                    "Перевод денег",
                    accounts[fromBranchIndex][fromAccountIndex],
                    accounts[toBranchIndex][toAccountIndex],
                    random.nextDouble() * Math.min(5000, accounts[fromBranchIndex][fromAccountIndex].getBalance())
            );
            if (bank.performTransaction(t))
                System.out.println(t.toString());
        }

    }

    public static void main(String[] args) {
        Bank bank = new Bank();

        //Создаем брэнчи, клиентов и аккаунты
        generateData(bank);

        Account currentAccount = null;

        int i = -1;
        while (i != 15) {
            //Выводим меню
            Console.printMenu(currentAccount);

            i = Console.getInt();

            switch (i) {
                case 1:
                    //Вход / выход пользователя
                    if (currentAccount == null) {
                        currentAccount = Console.getAccount(bank);
                        System.out.print("Вход выполнен\n\n");
                    } else {
                        currentAccount = null;
                        System.out.print("Выход выполнен\n\n");
                    }
                    break;

                case 2: {
                    Account account = currentAccount == null ? Console.getAccount(bank) : currentAccount;
                    System.out.format("Текущий баланс: %.2fр. %n%n", account.getBalance());
                    break;
                }

                case 3:
                    //Перевод денег
                    Console.transfer(bank, currentAccount);
                    break;

                case 4: {
                    //Смена типа аккаунта (вручную)
                    String[] types = bank.getAccountTypes();

                    Account account = currentAccount == null ? Console.getAccount(bank) : currentAccount;
                    System.out.print("Тип аккаунта " + types[account.getAccountType()] + ", изменить на:\n");

                    for (int j = 1; j < types.length; j++)
                        System.out.println(j + " - " + types[j]);
                    System.out.print("Введите номер типа\n> ");

                    loop: while (true) {
                        int t = Console.getInt();
                        switch (t) {
                            case 1:
                                if (account.getBalance() > 1000 || account.getAccountType() >= 1) {
                                    account.changeType(t);
                                    System.out.print("Тип аккаунта изменен на " + types[t] + "\n\n");
                                    break loop;
                                } else System.out.print("Аккаунт должен иметь больше 1000р. на балансе\n> ");
                                break;
                            case 2:
                                if (account.getBalance() > 50_000 || account.getAccountType() >= 2) {
                                    account.changeType(t);
                                    System.out.print("Тип аккаунта изменен на " + types[t] + "\n\n");
                                    break loop;
                                } else System.out.print("Аккаунт должен иметь больше 50000р. на балансе\n> ");
                                break;
                            case 3:
                                if (account.getBalance() > 5_000_000 || account.getAccountType() >= 3) {
                                    account.changeType(t);
                                    System.out.print("Тип аккаунта изменен на " + types[t] + "\n\n");
                                    break loop;
                                } else System.out.print("Аккаунт должен иметь больше 5000000р. на балансе\n> ");
                                break;
                            default:
                                System.out.print("Такого типа не существует\n> ");
                        }
                    }

                    break;
                }

                case 5: {
                    //Добавить бранч
                    Branch branch = Console.newBranch();
                    System.out.print("Брэнч " + (bank.addBranch(branch) + 1) + " добавлен\n\n");
                    break;
                }

                case 6: {
                    //Добавить клиента
                    Customer customer = Console.newCustomer();
                    bank.addCustomer(customer);
                    System.out.print("Клиент добавлен\n\n");
                    break;
                }

                case 7: {
                    //Добавить аккаунт
                    Branch branch = Console.getBranch(bank, "Введите идентификатор брэнча, к которому нужно прикрепить аккаунт");
                    Customer customer = Console.getCustomer(bank);
                    System.out.print("\n");
                    Account account = Console.newAccount(bank);

                    bank.addAccount(branch, customer, account);
                    System.out.print("Аккаунт добавлен\n\n");
                    break;
                }

                case 8: {
                    //Список бранчей
                    Branch[] branches = bank.getBranches();
                    for (int j = 0; j < branches.length; j++)
                        System.out.format("%d - %s, %s, %s, Часы приема %s%n",
                                branches[j].getId(),
                                branches[j].getAddress(),
                                branches[j].getTelephoneNumber(),
                                branches[j].getManager(),
                                branches[j].getBusinessHours()
                        );
                    System.out.print("\n");
                    break;
                }

                case 9: {
                    //Список клиентов
                    Customer[] customers = bank.getCustomers();
                    for (int j = 0; j < customers.length; j++)
                        System.out.format("%d - %s%n",
                                customers[j].getId(),
                                customers[j].getFullName()
                        );
                    System.out.print("\n");
                    break;
                }

                case 10: {
                    //Список аккаунтов
                    Account[] accounts = bank.getAccounts();
                    for (int j = 0; j < accounts.length; j++)
                        System.out.format("%s - %s, Брэнч %d, Тип %s, Баланс %.2fр.%n",
                                accounts[j].getAccountNumber(),
                                accounts[j].isBankAccount() ? "Банковский аккаунт" : accounts[j].getCustomer().getFullName(),
                                accounts[j].getBranch().getId(),
                                bank.getAccountTypeName(accounts[j].getAccountType()),
                                accounts[j].getBalance()
                        );
                    System.out.print("\n");
                    break;
                }

                case 11: {
                    //Отчет для клиента
                    Account account = Console.getAccount(bank);

                    System.out.print("Введите месяц за который генерировать отчет:\nМесяц > ");
                    int month = Console.getInt();
                    System.out.print("Год > ");
                    int year = Console.getInt();

                    Transaction[] transactions = bank.getTransactions(account, month, year);
                    if (transactions.length > 0) {
                        bank.createPdf(transactions, month, year, "Транзакции клиента: " + account.getCustomer().getFullName() + "\nСчет: " + account.getAccountNumber(), "Report1-" + account.getAccountNumber() + "-");
                    } else
                    System.out.print("Нет транзаций за указанный месяц\n\n");
                    break;
                }

                case 12: {
                    //Отчет для бранча
                    Branch branch = Console.getBranch(bank);

                    System.out.print("Введите месяц за который генерировать отчет:\nМесяц > ");
                    int month = Console.getInt();
                    System.out.print("Год > ");
                    int year = Console.getInt();

                    Transaction[] transactions = bank.getTransactions(branch, month, year);
                    if (transactions.length > 0) {
                        bank.createPdf(transactions, month, year, "Транзакции брэнча " + branch.getId(), "Report2-branch" + branch.getId() + "-");
                    } else
                        System.out.print("Нет транзаций за указанный месяц\n\n");
                    break;
                }

                case 13: {
                    System.out.print("Введите месяц за который генерировать отчет:\nМесяц > ");
                    int month = Console.getInt();
                    System.out.print("Год > ");
                    int year = Console.getInt();

                    //Отчет для всех бранчей
                    Transaction[] transactions = bank.getTransactions(month, year);
                    if (transactions.length > 0) {
                        bank.createPdf(transactions, month, year, "Транзакции всех брэнчей", "Report3-all-");
                    } else
                        System.out.print("Нет транзаций за указанный месяц\n\n");
                    break;
                }

                case 14:
                    //Сгенерировать транзакции
                    System.out.print("Введите количество дней\n> ");
                    int days;
                    while (true) {
                        days = Console.getInt();
                        if (days < 1) System.out.print("Число должно быть больше 0\n> ");
                        else break;
                    }
                    generateTransitions(bank, days);
                    System.out.print("Транзакции сгенерированны\n\n");
                    break;

                case 15:
                    //Выход
                    break;

                default: System.out.print("Такой команды нет\n\n");
            }
        }
    }

}
