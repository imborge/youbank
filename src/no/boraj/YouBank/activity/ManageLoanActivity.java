package no.boraj.YouBank.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.*;
import no.boraj.YouBank.R;
import no.boraj.YouBank.TransactionAdapter;
import no.boraj.YouBank.Utility;
import no.boraj.YouBank.sqlite.Loan;
import no.boraj.YouBank.sqlite.MyDAO;
import no.boraj.YouBank.sqlite.Transaction;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

/**
 * Author: Børge André Jensen
 * Author URL: http://borgizzle.com/  §
 */
public class ManageLoanActivity extends Activity {
    ListView listTransactions;
    TextView tvDueDate, tvAmount, tvLoanString;
    MyDAO dao;
    Loan loan;
    TransactionAdapter transactionAdapter;
    Button btnPayBack, btnBorrowMore;

    private void updateView(Loan l) {
        loan = l;

        String loanString = "";
        if (loan.isNegative()) {
            BigDecimal positive_sum = loan.getAmount().multiply(new BigDecimal("-1"));
            loanString = String.format("You owe %s %s money.", loan.getPersonName(), positive_sum.toString());
        } else {
            loanString = String.format("%s owes you %s money.", loan.getPersonName(), loan.getAmount().plus().toString());
        }
        tvLoanString.setText(loanString);
        tvAmount.setText(loan.getAmountStr());
        tvAmount.setTextColor(loan.isNegative() ? Utility.Color.RED : Utility.Color.GREEN);

        tvDueDate.setText(Utility.dateToStr(loan.getDueDate()));

        btnBorrowMore.setText(loan.isNegative() ? R.string.manageloan_borrowmore : R.string.manageloan_getpayed);
        btnPayBack.setText(loan.isNegative() ? R.string.manageloan_makepayment : R.string.manageloan_loanmore);

        transactionAdapter = new TransactionAdapter(this, loan.getTransactions());
        listTransactions.setAdapter(transactionAdapter);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.manageloan);

        dao = new MyDAO(this);

        try {
            dao.open();
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        listTransactions = (ListView) findViewById(R.id.list);

        Bundle b = getIntent().getExtras();
        final long loanId = b.getLong("loanId");

        final Loan l = new Loan();
        l.setId(loanId);

        this.loan = dao.getLoan(l);

        tvLoanString = (TextView) findViewById(R.id.tvLoanString);
        tvAmount = (TextView) findViewById(R.id.tvAmount);
        tvDueDate = (TextView) findViewById(R.id.tvDueDate);
        btnPayBack = (Button) findViewById(R.id.btnPayBack);
        btnBorrowMore = (Button) findViewById(R.id.btnBorrowMore);

        final ManageLoanActivity xthis = this;

        listTransactions.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (loan.getTransactions().size() > 1) {
                    final Transaction transaction = loan.getTransactions().get(i);

                    AlertDialog.Builder builder = new AlertDialog.Builder(xthis);
                    builder.setTitle("Delete transaction");
                    builder.setMessage("Are you sure you want to delete this transaction?");
                    builder.setNegativeButton("No", null);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dao.deleteTransaction(transaction);

                            Loan loan1 = new Loan();
                            loan1.setId(loanId);

                            updateView(dao.getLoan(loan1));
                            Toast toast = Toast.makeText(getApplicationContext(), "Deleted.", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
                    builder.create().show();
                }
                return false;
            }
        });

        updateView(loan);
    }

    public void onButtonClick(View view) {
        AlertDialog.Builder builder = null;
        switch (view.getId()) {
            case R.id.btnEditDueDate:
                builder = new AlertDialog.Builder(this);

                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(loan.getDueDate().getTime());

                final DatePicker datePicker = new DatePicker(this);
                datePicker.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                builder.setTitle("Edit due date");
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Loan loan1 = new Loan();
                        loan1.setId(loan.getId());
                        loan1.setDueDate(datePicker);
                        dao.editLoanDueDate(loan1);
                        updateView(dao.getLoan(loan1));
                        Toast toast = Toast.makeText(getApplicationContext(), "Saved.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.setView(datePicker);
                builder.create().show();
                break;

            case R.id.btnBorrowMore:
                builder = new AlertDialog.Builder(this);
                builder.setTitle(loan.isNegative() ? R.string.manageloan_borrowmore : R.string.manageloan_getpayed);
                LinearLayout layoutBorrow = new LinearLayout(this);
                final EditText inputBorrow = new EditText(this);
                inputBorrow.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                TextView textViewBorrow = new TextView(this);
                textViewBorrow.setText("Amount: ");

                layoutBorrow.setOrientation(LinearLayout.VERTICAL);
                layoutBorrow.setPadding(Utility.dpToPx(this, 10), 0, Utility.dpToPx(this, 10), 0);

                layoutBorrow.addView(textViewBorrow);
                layoutBorrow.addView(inputBorrow);

                builder.setView(layoutBorrow);
                builder.setPositiveButton(loan.isNegative() ? R.string.manageloan_borrowmore : R.string.manageloan_getpayed, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        BigDecimal amount = new BigDecimal(inputBorrow.getText().toString());
                        amount = amount.multiply(new BigDecimal("-1"));

                        // Create new transaction
                        Transaction transaction = new Transaction();
                        transaction.setLoanId(loan.getId());
                        transaction.setAmount(amount);
                        transaction.setDate(new Date());

                        dao.createTransaction(loan, transaction);
                        // Update list

                        updateView(dao.getLoan(loan));

                        Toast toast = Toast.makeText(getApplicationContext(), "Saved.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
                builder.setNegativeButton("Cancel", null);

                AlertDialog dlg = builder.create();
                builder.create().show();
                break;

            case R.id.btnPayBack:
                builder = new AlertDialog.Builder(this);
                builder.setTitle(loan.isNegative() ? R.string.manageloan_makepayment : R.string.manageloan_loanmore);
                LinearLayout layout = new LinearLayout(this);
                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                TextView textView = new TextView(this);
                textView.setText("Amount: ");

                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setPadding(Utility.dpToPx(this, 10), 0, Utility.dpToPx(this, 10), 0);

                layout.addView(textView);
                layout.addView(input);

                builder.setView(layout);
                builder.setPositiveButton(loan.isNegative() ? R.string.manageloan_makepayment : R.string.manageloan_loanmore, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        BigDecimal amount = new BigDecimal(input.getText().toString());

                        // Create new transaction
                        Transaction transaction = new Transaction();
                        transaction.setLoanId(loan.getId());
                        transaction.setAmount(amount);
                        transaction.setDate(new Date());

                        dao.createTransaction(loan, transaction);
                        // Update list

                        updateView(dao.getLoan(loan));

                        Toast toast = Toast.makeText(getApplicationContext(), "Saved.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.create().show();

                break;
        }
    }
}