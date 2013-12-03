package no.msys.YouBank.sqlite;

import android.widget.DatePicker;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.ArrayList;
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
        this.dueDate = new Date(dueDate.getYear(), dueDate.getMonth(), dueDate.getDayOfMonth());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
