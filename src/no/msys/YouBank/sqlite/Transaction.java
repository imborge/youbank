package no.msys.YouBank.sqlite;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Author: Børge André Jensen
 * Author URL: http://borgizzle.com/
 */
public class Transaction {
    private long id;
    private long loanId;
    private BigDecimal amount;
    private Date date;

    public Transaction(long id, long loanId, BigDecimal amount, Date date) {
        this.id = id;
        this.loanId = loanId;
        this.amount = amount;
        this.date = date;
    }

    public Transaction() {

    }

    public long getId() {

        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getLoanId() {
        return loanId;
    }

    public void setLoanId(long loanId) {
        this.loanId = loanId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
