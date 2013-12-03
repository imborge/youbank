package no.msys.YouBank.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import no.msys.YouBank.R;
import no.msys.YouBank.sqlite.Loan;
import no.msys.YouBank.sqlite.LoanDataSource;
import no.msys.YouBank.sqlite.MyDAO;

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
        Context context = getApplicationContext();
        Loan loan = new Loan();
        loan.setPersonName(txtTo.getText().toString());

        BigDecimal amount = new BigDecimal(txtAmount.getText().toString());

        if (radioFrom.isChecked()) {
            amount = amount.multiply(new BigDecimal("-1"));
        }

        loan.setAmount(amount);
        loan.setDueDate(dateDue);

        long id = dataSource.createLoan(loan);

        Toast toast = Toast.makeText(context, "Saved with id: " + id, Toast.LENGTH_SHORT);
        finish();
        toast.show();
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