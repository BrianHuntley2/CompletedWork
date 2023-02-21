package com.amica.billing.db;

import com.amica.billing.Customer;
import com.amica.billing.Invoice;
import lombok.Getter;

import java.nio.file.Files;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Function.identity;

@Getter
public abstract class CachingPersistence implements Persistence{

    protected Map<String, Customer> customers;
    protected Map<Integer, Invoice> invoices;

    protected abstract Stream<Customer> readCustomers();
    protected abstract Stream<Invoice> readInvoices();
    protected abstract void writeCustomer(Customer customer);
    protected abstract void writeInvoice(Invoice invoice);

    public void load(){
        try (Stream<Customer> customerStream = readCustomers() ) {
            customers = customerStream.collect(Collectors.toMap(Customer::getName, identity()));
            try ( Stream<Invoice> invoiceStream = readInvoices()) {
                invoices = invoiceStream.collect(Collectors.toMap(Invoice::getNumber, identity()));
            }
        } catch (Exception ex) {

        }
    }

    public void saveCustomer(Customer customer){
        customers.put(customer.getName(), customer);
        writeCustomer(customer);
    }

    public void saveInvoice(Invoice invoice){
        invoices.put(invoice.getNumber(), invoice);
        writeInvoice(invoice);
    }
}
