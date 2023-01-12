package com.amica.billing;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Optional;

@AllArgsConstructor
@Getter
public class Invoice {
    private int number;
    private double amount;
    private LocalDate invoiceDate;
    private Optional<LocalDate> paidDate;
    private Customer customer;


}
