package no.boraj.YouBank;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import no.boraj.YouBank.sqlite.Transaction;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Author: Børge André Jensen
 * Author URL: http://borgizzle.com/
 */
public class TransactionAdapter extends BaseAdapter {
    private Context context;
    private List<Transaction> transactions;

    public TransactionAdapter(Context context, List<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    @Override
    public int getCount() {
        return transactions.size();
    }

    @Override
    public Object getItem(int i) {
        return transactions.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Transaction transaction = transactions.get(i);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.loanlist, null);
        }
        TextView tvName = (TextView) view.findViewById(R.id.tvName);
        TextView tvSum = (TextView) view.findViewById(R.id.tvSum);

        String plus = "";

        if (transaction.getAmount().compareTo(BigDecimal.ZERO) == -1) {
            tvSum.setTextColor(Utility.Color.RED);
        } else {
            tvSum.setTextColor(Utility.Color.GREEN);
            plus = "+";
        }

        Date transDate = transaction.getDate();

        tvName.setText(Utility.dateToStr(transDate));
        tvSum.setText(plus + transaction.getAmount().toString());

        return view;
    }
}
