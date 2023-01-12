package com.amica.billing;

import com.amica.billing.parse.Parser;
import com.amica.billing.parse.ParserFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Billing {
    private String customerFilename;
    private String invoiceFilename;
    private Parser parser;
    private Map<String, Customer> customerMap;
    private List<Invoice> invoiceList;

    public Billing(String customerFilename, String invoiceFilename) {
        try {
            this.customerFilename = customerFilename;
            this.invoiceFilename = invoiceFilename;
            this.parser = ParserFactory.createParser(customerFilename);
            customerMap = parser.parseCustomers(Files.lines(Paths.get(customerFilename)))
                                .collect(Collectors.toMap(Customer::getName, c->c));
            invoiceList = parser.parseInvoices(Files.lines(Paths.get(invoiceFilename)), customerMap)
                                .collect(Collectors.toList());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Map<String, Customer> getCustomers() {
        return Collections.unmodifiableMap(customerMap);
    }

    public List<Invoice> getInvoices() {
        return Collections.unmodifiableList(invoiceList);
    }

    private Comparator<Invoice> compareByNumber = Comparator.comparing(Invoice::getNumber);

    public List<Invoice> getInvoicesOrderedByNumber(){
        return invoiceList.stream()
                .sorted(compareByNumber)
                .collect(Collectors.toList());
    }
//
//    public List<Invoice> getInvoicesOrderedByIssueDate(){
//
//    }
//
//    public List<Invoice> getInvoicesGroupedByCustomer(){
//
//    }
//
//    public List<Invoice> getOverdueInvoices(){
//
//    }
//
//    public List<Invoice> getCustomersAndVolume(){
//
//    }
}
