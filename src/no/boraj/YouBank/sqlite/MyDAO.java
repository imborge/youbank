package no.boraj.YouBank.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

/**
 * Author: Børge André Jensen
 * Author URL: http://borgizzle.com/
 */
public class MyDAO {
    private SQLiteDatabase db;
    private SQLiteHelper dbHelper;
    private Calendar calendar;

    public MyDAO(Context context) {
        dbHelper = new SQLiteHelper(context);
        calendar = Calendar.getInstance();
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        if (db != null) {
            db.close();
        }
    }

    public void editLoanDueDate(Loan loan) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_LOANS_DUEDATE, loan.getDueDate().getTime());
        db.update(SQLiteHelper.TABLE_LOANS, values, SQLiteHelper.COLUMN_COMMON_ID + " = " + loan.getId(), null);
    }

    public long createLoan(Loan loan, Transaction transaction) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_LOANS_PERSON, loan.getPersonName());
        values.put(SQLiteHelper.COLUMN_LOANS_DUEDATE, loan.getDueDate().getTime());

        long loanId = db.insert(SQLiteHelper.TABLE_LOANS, null, values);

        Loan loanIdLoan = new Loan();
        loanIdLoan.setId(loanId);

        long transId = createTransaction(loanIdLoan, transaction);

        return loanId;
    }

    public void deleteLoan(Loan loan) {
        deleteTransactions(loan.getTransactions());
        db.delete(SQLiteHelper.TABLE_LOANS, SQLiteHelper.COLUMN_COMMON_ID + " = " + loan.getId(), null);
    }

    public Loan getLoan(Loan loan) {
        Loan out = new Loan();

        String query = String.format("SELECT %s, %s, %s FROM %s WHERE %s = %s", SQLiteHelper.COLUMN_COMMON_ID, SQLiteHelper.COLUMN_LOANS_PERSON, SQLiteHelper.COLUMN_LOANS_DUEDATE, SQLiteHelper.TABLE_LOANS, SQLiteHelper.COLUMN_COMMON_ID, loan.getId());
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        out.setId(cursor.getLong(0));
        out.setPersonName(cursor.getString(1));
        out.setDueDate(new Date(cursor.getLong(2)));

        out.setTransactions(getAllTransactionsForLoan(loan));
        return out;
    }

    public List<Loan> getAllLoans() {
        List<Loan> loans = new ArrayList<Loan>();
        String query = String.format("SELECT %s, %s, %s FROM %s", SQLiteHelper.COLUMN_COMMON_ID, SQLiteHelper.COLUMN_LOANS_PERSON, SQLiteHelper.COLUMN_LOANS_DUEDATE, SQLiteHelper.TABLE_LOANS);
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Loan loan = new Loan();
            loan.setId(cursor.getInt(0));
            loan.setPersonName(cursor.getString(1));
            loan.setDueDate(new Date(cursor.getLong(2)));

            loans.add(loan);
            cursor.moveToNext();
        }

        if (cursor != null) {
            cursor.close();
        }

        for (Loan loan : loans) {
            loan.setTransactions(getAllTransactionsForLoan(loan));
        }

        return loans;
    }

    public long createTransaction(Loan loan, Transaction transaction) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_TRANSACTIONS_AMOUNT, transaction.getAmount().toString());
        values.put(SQLiteHelper.COLUMN_TRANSACTIONS_LOANID, loan.getId());
        calendar.setTime(new Date());
        values.put(SQLiteHelper.COLUMN_TRANSACTIONS_DATE, calendar.getTimeInMillis());

        return db.insert(SQLiteHelper.TABLE_TRANSACTIONS, null, values);
    }

    public void deleteTransaction(Transaction transaction) {
        db.delete(SQLiteHelper.TABLE_TRANSACTIONS, SQLiteHelper.COLUMN_COMMON_ID + " = " + transaction.getId(), null);
    }

    public void deleteTransactions(List<Transaction> transactions) {
        StringBuilder sb = new StringBuilder();
        for (Transaction t : transactions) {
            sb.append(t.getId());
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);

        String query = String.format("delete from %s where %s in(%s)", SQLiteHelper.TABLE_TRANSACTIONS, SQLiteHelper.COLUMN_COMMON_ID, sb.toString());
        db.execSQL(query);
    }

    public List<Transaction> getAllTransactionsForLoan(Loan loan) {
        List<Transaction> transactions = new ArrayList<Transaction>();
        String query = String.format("SELECT %s,%s,%s,%s FROM %s WHERE %s = %s ORDER BY %s desc",
                SQLiteHelper.COLUMN_COMMON_ID,
                SQLiteHelper.COLUMN_TRANSACTIONS_LOANID,
                SQLiteHelper.COLUMN_TRANSACTIONS_DATE,
                SQLiteHelper.COLUMN_TRANSACTIONS_AMOUNT,
                SQLiteHelper.TABLE_TRANSACTIONS, // Table
                SQLiteHelper.COLUMN_TRANSACTIONS_LOANID, loan.getId(), // WHERE =
                SQLiteHelper.COLUMN_COMMON_ID); // ORDER BY
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Transaction t = new Transaction();
            t.setId(cursor.getInt(0));
            t.setLoanId(cursor.getInt(1));
            calendar.setTimeInMillis(cursor.getLong(2));
            t.setDate(calendar.getTime());
            t.setAmount(new BigDecimal(cursor.getString(3)));

            transactions.add(t);
            cursor.moveToNext();
        }

        if (cursor != null) {
            cursor.close();
        }

        return transactions;
    }
}

