package no.boraj.YouBank.activity;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import no.boraj.YouBank.Utility;
import no.boraj.YouBank.sqlite.Loan;
import no.boraj.YouBank.LoanAdapter;
import no.boraj.YouBank.R;
import no.boraj.YouBank.sqlite.MyDAO;

public class LoansActivity extends Activity {
    private MyDAO dao;
    private ListView gvActiveLoans;
    private List<Loan> loans;
    private TextView tvTotalSum;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        dao = new MyDAO(this);
        try {
            dao.open();
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        loans = dao.getAllLoans();

        gvActiveLoans = (ListView) findViewById(R.id.lvActiveLoans);
        tvTotalSum = (TextView) findViewById(R.id.tvTotalSum);

        updateView();

        // for usage in event listeners
        final LoansActivity xthis = this;

        gvActiveLoans.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Loan loan = loans.get(i);
                Intent intent = new Intent(xthis, ManageLoanActivity.class);
                Bundle b = new Bundle();
                b.putLong("loanId", loan.getId());
                intent.putExtras(b);
                startActivityForResult(intent, 0);
            }
        });

        gvActiveLoans.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                final Loan loan = loans.get(i);
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(xthis);
                String message = String.format("Delete loan?");
                dlgAlert.setMessage(message);
                dlgAlert.setTitle("Confirm");
                dlgAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dao.deleteLoan(loan);

                        updateView();

                        Toast toast = Toast.makeText(getApplicationContext(), "Deleted.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
                dlgAlert.setNegativeButton("No", null);
                dlgAlert.create().show();
                return false;
            }
        });

        Button btnNewLoan = (Button) findViewById(R.id.btnNewLoan);
        btnNewLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(xthis, NewLoanActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }

    private void updateView() {
        loans = dao.getAllLoans();
        gvActiveLoans.setAdapter(new LoanAdapter(this, loans));
        BigDecimal sum = BigDecimal.ZERO;
        for (Loan x : loans) {
            sum = sum.add(x.getAmount());
        }

        String sumText = (sum.compareTo(BigDecimal.ZERO) == -1) ? sum.toString() : "+" + sum.toString();
        tvTotalSum.setTextColor((sum.compareTo(BigDecimal.ZERO) == -1) ? Utility.Color.RED : Utility.Color.GREEN);
        tvTotalSum.setText(sumText);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        updateView();
    }
}
