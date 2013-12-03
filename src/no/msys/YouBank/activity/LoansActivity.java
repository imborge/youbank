package no.msys.YouBank.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import no.msys.YouBank.sqlite.Loan;
import no.msys.YouBank.LoanAdapter;
import no.msys.YouBank.R;
import no.msys.YouBank.sqlite.LoanDataSource;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LoansActivity extends Activity {
    private LoanDataSource dataSource;
    private ListView gvActiveLoans;
    private LoanAdapter loanAdapter;
    private List<Loan> loans;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        dataSource = new LoanDataSource(this);
        try {
            dataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        loans = dataSource.getAllLoans();

        loanAdapter = new LoanAdapter(this, this.loans);

        gvActiveLoans = (ListView)findViewById(R.id.lvActiveLoans);
        gvActiveLoans.setAdapter(loanAdapter);

        final LoansActivity xthis = this;

        gvActiveLoans.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(xthis, ManageLoanActivity.class);
                startActivity(intent);
//                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(xthis);
//                Loan loan = loans.get(i);
//                String message = "";
//                if (loan.getAmount().compareTo(BigDecimal.ZERO) == -1) {
//                    BigDecimal positive_sum = loan.getAmount().multiply(new BigDecimal("-1"));
//                    message = String.format("You owe %s %s money.", loan.getPersonName(), positive_sum.toString());
//                } else {
//                    message = String.format("%s owes you %s money.", loan.getPersonName(), loan.getAmount().plus().toString());
//                }
//                dlgAlert.setMessage(message);
//                dlgAlert.setTitle("Loan status");
//                dlgAlert.setPositiveButton("Ok", null);
//                dlgAlert.create().show();
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
                        dataSource.deleteLoan(loan);
                        loans = dataSource.getAllLoans();
                        gvActiveLoans.setAdapter(new LoanAdapter(xthis, loans));
                        loanAdapter.notifyDataSetChanged();
                    }
                });
                dlgAlert.setNegativeButton("No", null);
                dlgAlert.create().show();
                return false;  //To change body of implemented methods use File | Settings | File Templates.
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult", "woopwoop!");
        loans = dataSource.getAllLoans();

        // TODO: Find another way to refresh the ListView, this feels way to hackish
        gvActiveLoans.setAdapter(new LoanAdapter(this, loans));
        loanAdapter.notifyDataSetChanged();
    }
}
