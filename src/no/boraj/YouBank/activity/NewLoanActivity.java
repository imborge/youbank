package no.boraj.YouBank.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import no.boraj.YouBank.R;
import no.boraj.YouBank.sqlite.Loan;
import no.boraj.YouBank.sqlite.MyDAO;
import no.boraj.YouBank.sqlite.Transaction;

import java.math.BigDecimal;
import java.sql.SQLException;

/**
 * Author: Børge André Jensen
 * Author URL: http://borgizzle.com/
 */
public class NewLoanActivity extends Activity {
    MyDAO dao;
    TextView tvToFrom;
    EditText txtTo;
    EditText txtAmount;
    DatePicker dateDue;
    RadioButton radioTo;
    RadioButton radioFrom;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.newloan);

        tvToFrom = (TextView)findViewById(R.id.tvToFrom);
        txtTo = (EditText) findViewById(R.id.txtTo);
        txtAmount = (EditText) findViewById(R.id.txtAmount);
        dateDue = (DatePicker) findViewById(R.id.dateDue);

        radioTo = (RadioButton) findViewById(R.id.radioBorrowTo);
        radioFrom = (RadioButton) findViewById(R.id.radioBorrowFrom);

        dao = new MyDAO(this);

        try {
            dao.open();
        } catch (SQLException ex) {

        }
    }

    public void onBtnSaveClicked(View view) {
        if (txtTo.getText().toString().equals("") && txtAmount.getText().toString().equals("")) {
            txtTo.setError("Required");
            txtAmount.setError("Required");
        } else if (txtTo.getText().toString().equals("")) {
            txtTo.setError("Required.");
        } else if (txtAmount.getText().toString().equals("")) {
            txtAmount.setError("Required.");
        } else {
            Context context = getApplicationContext();
            Loan loan = new Loan();
            loan.setPersonName(txtTo.getText().toString().trim());
            loan.setDueDate(dateDue);

            BigDecimal amount = new BigDecimal(txtAmount.getText().toString());

            if (radioFrom.isChecked()) {
                amount = amount.multiply(new BigDecimal("-1"));
            }

            Transaction t = new Transaction();
            t.setAmount(amount);

            long id = dao.createLoan(loan, t);

            Toast toast = Toast.makeText(context, "Saved.", Toast.LENGTH_SHORT);
            finish();
            toast.show();
        }
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        if (checked) {
            switch (view.getId()) {
                case R.id.radioBorrowFrom:
                    tvToFrom.setText("Borrow from:");
                    break;
                case R.id.radioBorrowTo:
                    tvToFrom.setText("Loan to:");
                    break;
            }
        }
    }
}