package no.msys.YouBank.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Børge André Jensen
 * Author URL: http://borgizzle.com/
 */
public class MyDAO {
    private SQLiteDatabase db;
    private SQLiteHelper dbHelper;

    public MyDAO(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        if (db != null) {
            db.close();
        }
    }

    public long createLoan(Loan loan, Transaction transaction) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_LOANS_PERSON, loan.getPersonName());
        values.put(SQLiteHelper.COLUMN_LOANS_DUEDATE, loan.getPersonName());

        long loanId = db.insert(SQLiteHelper.TABLE_LOANS, null, values);

        Loan loanIdLoan = new Loan();
        loanIdLoan.setId(loanId);

        long transId = createTransaction(loanIdLoan, transaction);

        return 0;
    }

    public void deleteLoan(Loan loan) {
        db.delete(SQLiteHelper.TABLE_LOANS, SQLiteHelper.COLUMN_COMMON_ID + " = " + loan.getId(), null);
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

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-DD");
            try {
                loan.setDueDate(dateFormat.parse(cursor.getString(2)));
            } catch (ParseException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

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

        return db.insert(SQLiteHelper.TABLE_TRANSACTIONS, null, values);
    }

    public void deleteTransaction(Transaction transaction) {
        db.delete(SQLiteHelper.TABLE_TRANSACTIONS, SQLiteHelper.COLUMN_COMMON_ID + " = " + transaction.getId(), null);
    }

    public List<Transaction> getAllTransactionsForLoan(Loan loan) {
        List<Transaction> transactions = new ArrayList<Transaction>();
        String query = String.format("SELECT %s,%s,%s,%s FROM %s WHERE %s = %s",
                SQLiteHelper.COLUMN_COMMON_ID,
                SQLiteHelper.COLUMN_TRANSACTIONS_LOANID,
                SQLiteHelper.COLUMN_TRANSACTIONS_DATE,
                SQLiteHelper.COLUMN_TRANSACTIONS_AMOUNT,
                SQLiteHelper.TABLE_TRANSACTIONS, // Table
                SQLiteHelper.COLUMN_TRANSACTIONS_LOANID, loan.getId()); // WHERE =
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Transaction t = new Transaction();
            t.setId(cursor.getInt(0));
            t.setLoanId(cursor.getInt(1));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-DD");
            try {
                t.setDate(dateFormat.parse(cursor.getString(2)));
            } catch (ParseException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
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

