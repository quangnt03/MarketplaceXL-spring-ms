package com.example.marketplace.money;

import java.math.BigDecimal;
import java.util.Currency;

public final record Money(BigDecimal amount, Currency currency) { }
