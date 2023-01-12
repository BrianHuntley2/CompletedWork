package com.amica.billing;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;

@AllArgsConstructor
@Log
public class Reporter {

    private Billing billing;
    private String folder;
    private LocalDate asOf;


    public void reportInvoicesOrderedByNumber(){
        try (PrintWriter out = new PrintWriter (new FileWriter(folder + "test.txt")); ) {
            out.print ("All invoices, ordered by invoice number\n" +
                    "==================================================================\n" +
                    "\n" +
                    "       Customer                     Issued      Amount        Paid\n" +
                    "----  ------------------------  ----------  ----------  ----------");

            for(Invoice i : billing.getInvoicesOrderedByNumber()){
                out.print(i.getNumber() + i.getCustomer().getName() + i.getInvoiceDate() + i.getAmount() + i.getPaidDate() + "\n");
            }
        } catch (Exception ex) {
            log.warning(() ->
                    "Error writing to file: " + folder);
        }
    }

    public void reportInvoicesOrderedByIssueDate(){

    }

    public void reportInvoicesGroupedByCustomer(){

    }

    public void reportOverdueInvoices(){

    }

    public void reportCustomersAndVolume(){

    }
}
