package no.msys.YouBank.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Author: Børge André Jensen
 * Author URL: http://borgizzle.com/
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    // Tables
    public static final String TABLE_LOANS = "loans";
    public static final String TABLE_TRANSACTIONS = "transactions";

    // Common columns
    public static final String COLUMN_COMMON_ID = "_id";

    // Loan columns
    public static final String COLUMN_LOANS_PERSON = "person_name";
    public static final String COLUMN_LOANS_DUEDATE = "due_date";

    // Transactions columns
    public static final String COLUMN_TRANSACTIONS_LOANID = "loan_id";
    public static final String COLUMN_TRANSACTIONS_AMOUNT = "amount";
    public static final String COLUMN_TRANSACTIONS_DATE = "date";

    private static final String DATABASE_NAME = "youbank.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_LOANS_CREATE = "create table "
            + TABLE_LOANS + "("
            + COLUMN_COMMON_ID + " integer primary key autoincrement,"
            + COLUMN_LOANS_PERSON + " text not null,"
            + COLUMN_LOANS_DUEDATE + " DATE);";

    private static final String TABLE_TRANSACTIONS_CREATE = "create table "
            + TABLE_TRANSACTIONS + "("
            + COLUMN_COMMON_ID + " integer primary key autoincrement,"
            + COLUMN_TRANSACTIONS_LOANID + " integer not null,"
            + COLUMN_TRANSACTIONS_DATE + " DATE default (datetime('now','localtime')),"
            + COLUMN_TRANSACTIONS_AMOUNT + " decimal(13,4) not null);";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_LOANS_CREATE);
        db.execSQL(TABLE_TRANSACTIONS_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int old_version, int new_version) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOANS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        onCreate(db);
    }
}
