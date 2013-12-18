package no.boraj.YouBank.sqlite;

import android.widget.DatePicker;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Author: Børge André Jensen
 * Author URL: http://borgizzle.com/
 */
public class Loan {
    private String personName;
    private Date dueDate;
    private long id;

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    private List<Transaction> transactions;

    public Loan(String personName, Date dueDate, List<Transaction> transactions) {
        this.personName = personName;
        this.dueDate = dueDate;
        this.transactions = transactions;
    }

    public Loan() {
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public void setDueDate(DatePicker dueDate) {
        Calendar c = Calendar.getInstance();
        c.set(dueDate.getYear(), dueDate.getMonth(), dueDate.getDayOfMonth());
        this.dueDate = c.getTime();
    }

    public long getId() {
        return id;
    }

    public boolean isNegative() {
        return getAmount().compareTo(BigDecimal.ZERO) == -1;
    }

    public boolean isPositive() {
        return getAmount().compareTo(BigDecimal.ZERO) == 1;
    }

    public boolean isZero() {
        return getAmount().compareTo(BigDecimal.ZERO) == 0;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        BigDecimal sum = BigDecimal.ZERO;

        for (Transaction t : transactions) {
            sum = sum.add(t.getAmount());
        }

        return sum;
    }

    public String getAmountStr() {
        if (isPositive()) {
            return "+" + getAmount().toString();
        }
        return getAmount().toString();
    }
}
