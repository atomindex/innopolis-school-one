import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;

/**
 * Created by Atom on 7/23/2015.
 */

public class Bank {
    Branch[] branches;
    Transaction[] transactions;
    Customer[] customers;

    Account bankAccount;

    long time;

    public Bank() {
        branches = new Branch[0];
        transactions = new Transaction[0];
        customers = new Customer[0];
        time = 0;
    }

    public void addTime(long secs) {
        time += secs * 1000;
    }

    public long getTime() {
        return (new Date()).getTime() + time;
    }

    public Account getBankAccount() {
        return bankAccount;
    }



    //Добавляет брэнч, возвращает его индекс в массиве
    public int addBranch(Branch branch) {
        Branch[] tempBranches = new Branch[branches.length + 1];
        for (int i = 0; i < branches.length; i++)
            tempBranches[i] = branches[i];
        tempBranches[branches.length] = branch;

        branches = tempBranches;

        branch.setBank(this);

        if (branches.length == 1) {
            bankAccount = new Account("000", "Password", 2_000_000_000, true);
            branch.addAccount(bankAccount);
        }

        return branches.length - 1;
    }

    //Возвращает все брэнчи
    public Branch[] getBranches() {
        return branches;
    }

    //Добавляет клиента
    public int addCustomer(Customer customer) {
        Customer[] tempCustomers = new Customer[customers.length + 1];
        for (int i = 0; i < customers.length; i++)
            tempCustomers[i] = customers[i];
        tempCustomers[customers.length] = customer;

        customers = tempCustomers;

        return customers.length - 1;
    }

    //Возвращает всех клиентов
    public Customer[] getCustomers() {
        return customers;
    }

    //Добавляет аккаунт в брэнч и к клиента
    public void addAccount(Branch branch, Customer customer, Account account) {
        branch.addAccount(account);
        customer.addAccount(account);
    }

    //Возвращает аккаунт по номеру
    public Account getAccount(String accountNumber) {
        for (int i = 0; i < branches.length; i++) {
            Account[] accounts = branches[i].getAccounts();
            for (int j = 0; j < accounts.length; j++) {
                if (accounts[j].getAccountNumber().equals(accountNumber))
                    return accounts[j];
            }
        }
        return null;
    }

    //Возвращает все аккаунты со всех брэнчей
    public Account[] getAccounts() {
        int count = 0;
        for (int i = 0; i < branches.length; i++)
            count += branches[i].getAccounts().length;

        Account[] result = new Account[count];
        int k = 0;
        for (int i = 0; i < branches.length; i++) {
            Account[] accounts = branches[i].getAccounts();
            for (int j = 0; j < accounts.length; j++) {
                result[k++] = accounts[j];
            }
        }

        return result;
    }

    //Возвращает коммиссию для перевода
    public double getCommission(Account from, Account to, double money) {
        if (to == bankAccount) return 0;
        return 5;
    }

    //Возвращает сумму начисления/вычисления денег с аккаунта
    public double getInterest(int accountType, double balance) {
        switch (accountType) {
            case 0: return 0;
            case 1: return -1000;
            case 2: return balance / 100 * 5;
            case 3: return balance / 100;
        }

        return 0;
    }

    //Выполняет транзакцию
    public boolean performTransaction(Transaction transaction) {
        if (transaction.isPerformed()) return false;

        if (!transaction.perform()) return false;

        //Перечисляем деньги на другой счет
        transaction.getFromAccount().subtractMoney(transaction.getMoney());
        transaction.getToAccount().addMoney(transaction.getMoney());

        //Перечисляем коммисию на счет банка
        transaction.getFromAccount().subtractMoney(transaction.getCommission());
        bankAccount.addMoney(transaction.getCommission());

        //Записываем транзицию в массив
        Transaction[] tempTransaction = new Transaction[transactions.length + 1];
        for (int i = 0; i < transactions.length; i++)
            tempTransaction[i] = transactions[i];
        tempTransaction[transactions.length] = transaction;

        transactions = tempTransaction;

        return true;
    }

    //Возвращает все транзакции
    public Transaction[] getTransactions() {
        return transactions;
    }

    //Возвращает все транзакции за указанный период
    public Transaction[] getTransactions(int month, int year) {
        Calendar mycal = new GregorianCalendar(year, month - 1, 1);
        int endOfMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
        Date dateFrom = null;
        Date dateTo = null;
        try {
            String strMonth = month < 10 ? "0" + month : Integer.toString(month);
            String strEndOfMonth = endOfMonth < 10 ? "0" + endOfMonth : Integer.toString(endOfMonth);
            dateFrom = dateFormat.parse("01." + strMonth + "." + year + " 00:00:00");
            dateTo = dateFormat.parse(strEndOfMonth + "." + strMonth + "." + year + " 23:59:59");
        } catch (Exception e) {
            return null;
        }

        long startTime = dateFrom.getTime();
        long endTime = dateTo.getTime();

        Transaction[] result = new Transaction[transactions.length];

        int count = 0;
        for (int i = 0; i < transactions.length; i++) {
            if (transactions[i].getTime() >= startTime && transactions[i].getTime() <= endTime)
                result[count++] = transactions[i];
        }

        if (result.length == count) return result;

        Transaction[] cutResult = new Transaction[count];
        for (int i = 0; i < count; i++)
            cutResult[i] = result[i];

        return cutResult;
    }

    //Возвращает все транзакции указанного аккаунта, за указанный период
    public Transaction[] getTransactions(Account account, int month, int year) {
        Calendar mycal = new GregorianCalendar(year, month - 1, 1);
        int endOfMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
        Date dateFrom = null;
        Date dateTo = null;
        try {
            String strMonth = month < 10 ? "0" + month : Integer.toString(month);
            String strEndOfMonth = endOfMonth < 10 ? "0" + endOfMonth : Integer.toString(endOfMonth);
            dateFrom = dateFormat.parse("01." + strMonth + "." + year + " 00:00:00");
            dateTo = dateFormat.parse(strEndOfMonth + "." + strMonth + "." + year + " 23:59:59");
        } catch (Exception e) {
            return null;
        }

        long startTime = dateFrom.getTime();
        long endTime = dateTo.getTime();

        Transaction[] result = new Transaction[transactions.length];

        int count = 0;
        for (int i = 0; i < transactions.length; i++) {
            if (transactions[i].getTime() >= startTime && transactions[i].getTime() <= endTime &&
               (transactions[i].getFromAccount() == account || transactions[i].getToAccount() == account))
                result[count++] = transactions[i];
        }

        if (result.length == count) return result;

        Transaction[] cutResult = new Transaction[count];
        for (int i = 0; i < count; i++)
            cutResult[i] = result[i];

        return cutResult;
    }

    //Возвращает транзакции брэнча
    public Transaction[] getTransactions(Branch branch, int month, int year) {
        Calendar mycal = new GregorianCalendar(year, month - 1, 1);
        int endOfMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
        Date dateFrom = null;
        Date dateTo = null;
        try {
            String strMonth = month < 10 ? "0" + month : Integer.toString(month);
            String strEndOfMonth = endOfMonth < 10 ? "0" + endOfMonth : Integer.toString(endOfMonth);
            dateFrom = dateFormat.parse("01." + strMonth + "." + year + " 00:00:00");
            dateTo = dateFormat.parse(strEndOfMonth + "." + strMonth + "." + year + " 23:59:59");
        } catch (Exception e) {
            return null;
        }

        long startTime = dateFrom.getTime();
        long endTime = dateTo.getTime();

        Transaction[] result = new Transaction[transactions.length];

        int count = 0;
        for (int i = 0; i < transactions.length; i++) {
            if (transactions[i].getTime() >= startTime && transactions[i].getTime() <= endTime &&
               (transactions[i].getFromBranch() == branch || transactions[i].getToBranch() == branch))
                result[count++] = transactions[i];
        }

        if (result.length == count) return result;

        Transaction[] cutResult = new Transaction[count];
        for (int i = 0; i < count; i++)
            cutResult[i] = result[i];

        return cutResult;
    }

    public void updateBank() {
        for (int i = 0; i < branches.length; i++) {
            Account[] accounts = branches[i].getAccounts();

            for (int j = 0; j < accounts.length; j++) {
                if (accounts[j].isClosed()) continue;

                double interests = getInterest(accounts[j].getAccountType(), accounts[j].getBalance()) / 30;

                if (interests < 0) {
                    interests = Math.abs(interests);

                    Transaction transaction = new Transaction(
                            "Снятие денег банком",
                            accounts[j],
                            bankAccount,
                            interests
                    );

                    if (accounts[j].getBalance() < transaction.getSum()) {
                        accounts[j].close(true);
                        System.out.println(accounts[j].getAccountNumber() + " закрыт из за недостатка денег");
                    } else {
                        if (performTransaction(transaction))
                            System.out.println(transaction.toString());
                    }
                } else if (interests > 0) {
                    Transaction transaction = new Transaction(
                            "Начисление денег банком",
                            bankAccount,
                            accounts[j],
                            interests
                    );
                    if (performTransaction(transaction))
                        System.out.println(transaction.toString());
                }

                accounts[j].updateType();
            }
        }
    }

    public double getMinimalBalance() {
        return 1000;
    }

    private static String[] months = { "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь" };

    private static String addZero(int val) {
        return val < 10 ? "0" + val : Integer.toString(val);
    }

    public static void createPdf(Transaction[] transactions, int month, int year, String description, String filenamePrefix) {
        Document document = new Document(PageSize.A4);
        document.setMargins(30f, 30f, 30f, 30f);

        try {
            BaseFont baseFont = BaseFont.createFont("arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            BaseFont bbaseFont = BaseFont.createFont("arialbd.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

            Font font = new Font(baseFont, 9, Font.NORMAL, BaseColor.BLACK);
            Font bfont = new Font(bbaseFont, 9, Font.NORMAL, BaseColor.BLACK);
            Font headerFont = new Font(baseFont, 16, Font.NORMAL, BaseColor.BLACK);
            Font descriptionFont = new Font(baseFont, 10, Font.NORMAL, BaseColor.GRAY);

            String filename = "C:\\" + filenamePrefix + addZero(month) + "." + year + ".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(filename));

            document.open();

            Paragraph header = new Paragraph("Отчет за " + months[month-1] + ", " + year, headerFont);
            header.setSpacingAfter(0.5f);
            document.add(header);

            Paragraph descriptionParagraph = new Paragraph(description, descriptionFont);
            descriptionParagraph.setSpacingAfter(30f);
            document.add(descriptionParagraph);

            Image image = Image.getInstance("logo.jpg");
            image.scaleAbsoluteWidth(140);
            image.scaleAbsoluteHeight(50);
            image.setAbsolutePosition(document.right() - image.getScaledWidth(), document.top() - image.getScaledHeight());
            document.add(image);

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setSpacingBefore(0f);
            table.setSpacingAfter(0f);
            table.setWidths(new float[] {5f, 15f, 10f, 10f, 10f, 7f});

            PdfPCell[] headerCells = new PdfPCell[6];
            headerCells[0] = new PdfPCell(new Phrase("#", bfont));
            headerCells[1] = new PdfPCell(new Phrase("Операция", bfont));
            headerCells[2] = new PdfPCell(new Phrase("Отправитель", bfont));
            headerCells[3] = new PdfPCell(new Phrase("Получатель", bfont));
            headerCells[4] = new PdfPCell(new Phrase("Сумма", bfont));
            headerCells[5] = new PdfPCell(new Phrase("Время", bfont));

            for (int i = 0; i < headerCells.length; i++) {
                headerCells[i].setPadding(4);
                headerCells[i].setPaddingBottom(6);
                headerCells[i].setGrayFill(0.95f);
                table.addCell(headerCells[i]);
            }

            table.completeRow();

            for (int i = 0; i < transactions.length; i++) {
                PdfPCell[] cells = new PdfPCell[6];
                cells[0] = new PdfPCell(new Phrase(Integer.toString(transactions[i].getId()), font));
                cells[1] = new PdfPCell(new Phrase(transactions[i].getName(), font));
                cells[2] = new PdfPCell(new Phrase(transactions[i].getFromAccount().getAccountNumber() + "\n(Бранч " + transactions[i].getFromAccount().getBranch().getId() + ")", font));
                cells[3] = new PdfPCell(new Phrase(transactions[i].getToAccount().getAccountNumber() + "\n(Бранч " + transactions[i].getToAccount().getBranch().getId() + ")", font));
                cells[4] = new PdfPCell(new Phrase(String.format("%.2fр.", transactions[i].getSum()), font));
                cells[5] = new PdfPCell(new Phrase(transactions[i].getStringTime(), font));

                for (int j = 0; j < cells.length; j++) {
                    cells[j].setPadding(4);
                    cells[j].setPaddingBottom(6);
                    table.addCell(cells[j]);
                }
                table.completeRow();
            }

            document.add(table);
            document.close();

            System.out.print("Файл сохранен в " + filename + "\n\n");
        } catch (Exception e) {
            document.close();
            System.out.print("Ошибка сохранения файла\n\n");
        }
    }

    public Branch getBranch(int id) {
        if (id < 0 || id >= branches.length)
            return null;
        return branches[id];
    }

    public Customer getCustomer(int id) {
        if (id < 0 || id >= customers.length)
            return null;
        return customers[id];
    }

    private static String[] atypes = { "Simple", "Chequing", "Savings", "Business" };

    public String[] getAccountTypes() {
        return atypes;
    }

    public String getAccountTypeName(int t) {
        return t >= 0 && t < atypes.length ? atypes[t] : "";
    }
}
