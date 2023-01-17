package com.amica.billing;

import com.amica.billing.parse.Parser;
import com.amica.billing.parse.ParserFactory;
import lombok.extern.java.Log;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log
public class Billing {
    private String customerFilename;
    private String invoiceFilename;
    private Parser parser;
    private Map<String, Customer> customerMap;
    private List<Invoice> invoiceList;
    private List<Consumer<Invoice>> invoiceListeners;
    private List<Consumer<Customer>> customerListeners;

    public Billing(String customerFilename, String invoiceFilename) {
        try {
            this.customerFilename = customerFilename;
            this.invoiceFilename = invoiceFilename;
            this.parser = ParserFactory.createParser(customerFilename);
            this.customerMap = parser.parseCustomers(Files.lines(Paths.get(customerFilename)))
                                .collect(Collectors.toMap(Customer::getName, c->c));
            this.invoiceList = parser.parseInvoices(Files.lines(Paths.get(invoiceFilename)), customerMap)
                                .collect(Collectors.toList());
            this.invoiceListeners = new ArrayList<Consumer<Invoice>>();
            this.customerListeners = new ArrayList<Consumer<Customer>>();

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

    private void saveCustomers(){
        try (PrintWriter out = new PrintWriter (new FileWriter(customerFilename)); ) {
            Stream<String> lines = parser.produceCustomers(customerMap.values().stream());
            lines.forEach(s -> out.print(s+"\n"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void saveInvoices(){
        try (PrintWriter out = new PrintWriter (new FileWriter(invoiceFilename)); ) {
            Stream<String> lines = parser.produceInvoices(invoiceList.stream());
            lines.forEach(s -> out.print(s+"\n"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void payInvoice(int invoiceNumber){
        for(Invoice i : invoiceList){
            if(i.getNumber() == invoiceNumber && i.getPaidDate().isEmpty()){
                i.setPaidDate(Optional.of(LocalDate.now()));
                saveInvoices();
                invoiceListeners.forEach(c -> c.accept(i));
            }
        }
    }

    public void createCustomer(String first, String last, Terms term){
        Customer customer = new Customer(first, last, term);
        customerMap.put(first + " " + last, customer);
        saveCustomers();
        customerListeners.forEach(c -> c.accept(customer));
    }

    public void createInvoice(String name, double amount){
        if(customerMap.containsKey(name)){
            Invoice invoice = new Invoice(invoiceList.size(), amount, LocalDate.now(), Optional.empty(), customerMap.get(name));
            invoiceList.add(invoice);
            saveInvoices();
            invoiceListeners.forEach(c -> c.accept(invoice));
        }else{
            log.warning(() ->
                    "Couldn't find customer, skipping invoice.");
        }
    }

    public void addInvoiceListener(Consumer<Invoice> listener){
        invoiceListeners.add(listener);
    }

    public void removeInvoiceListener(Consumer<Invoice> listener){
        invoiceListeners.remove(listener);
    }

    public void addCustomerListener(Consumer<Customer> listener){
        customerListeners.add(listener);
    }

    public void removeCustomerListener(Consumer<Customer> listener){
        customerListeners.remove(listener);
    }


    private Comparator<Invoice> compareByNumber = Comparator.comparing(Invoice::getNumber);

    public List<Invoice> getInvoicesOrderedByNumber(){
        return invoiceList.stream()
                .sorted(compareByNumber)
                .collect(Collectors.toList());
    }

    private Comparator<Invoice> compareByIssueDate = Comparator.comparing(Invoice::getInvoiceDate);

    public List<Invoice> getInvoicesOrderedByIssueDate(){
        return invoiceList.stream()
                .sorted(compareByIssueDate)
                .collect(Collectors.toList());
    }

    public List<Invoice> getInvoicesGroupedByCustomer(){
        Map<Customer, List<Invoice>> grouped = invoiceList.stream()
                                                .collect(Collectors.groupingBy(Invoice::getCustomer));
        List<Invoice> ret = new ArrayList<Invoice>();
        for(Map.Entry<Customer, List<Invoice>> kv : grouped.entrySet()){
            ret.addAll(kv.getValue());
        }
        return ret;
    }

    public List<Invoice> getOverdueInvoices(){
        return invoiceList.stream()
                .filter(i -> i.getPaidDate().isEmpty())
                .collect(Collectors.toList());
    }

    public Map<Customer, Double> getCustomersAndVolume(){
        List<Customer> customers = invoiceList.stream()
                .map(Invoice::getCustomer).collect(Collectors.toList());

        Map<Customer, Double> ret = new HashMap<>();
        for(Customer c : customers){
            ret.put(c,
            invoiceList.stream()
                    .filter(i -> i.getCustomer() == c)
                    .mapToDouble(Invoice::getAmount)
                    .sum()
            );
        }
        return ret;
    }

}
