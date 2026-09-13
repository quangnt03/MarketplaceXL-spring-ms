package com.example.marketplace.money;

import java.math.BigDecimal;
import java.util.Currency;

public class Money {
    private BigDecimal amount;
    private Currency currency;

    public Money(BigDecimal amount, Currency currency) {
        this.setAmount(amount);
        this.setCurrency(currency);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
