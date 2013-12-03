package no.msys.YouBank;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import no.msys.YouBank.sqlite.Loan;

import java.math.BigDecimal;
import java.util.List;

/**
 * Author: Børge André Jensen
 * Author URL: http://borgizzle.com/
 */
public class LoanAdapter extends BaseAdapter {
    private Context context;
    private List<Loan> loans;

    public LoanAdapter(Context context, List<Loan> loans) {
        this.context = context;
        this.loans = loans;
    }

    @Override
    public int getCount() {
        return loans.size();
    }

    @Override
    public Object getItem(int i) {
        return loans.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Loan loan = loans.get(i);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.loanlist, null);
        }
        TextView tvName = (TextView) view.findViewById(R.id.tvName);
        TextView tvSum = (TextView) view.findViewById(R.id.tvSum);

        String plus = "";

        if (loan.getAmount().compareTo(BigDecimal.ZERO) == -1) {
            tvSum.setTextColor(Color.RED);
        } else {
            tvSum.setTextColor(Color.GREEN);
            plus = "+";
        }

        tvName.setText(loan.getPersonName());
        tvSum.setText(plus + loan.getAmount().toString());

        return view;
    }
}
