package com.amica.billing.parse;

import com.amica.billing.Customer;
import com.amica.billing.Invoice;
import com.amica.billing.ParserFactory;
import com.amica.billing.db.CachingPersistence;
import lombok.Setter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.stream.Stream;

@Log
@Component
public class ParserPersistence extends CachingPersistence {

    @Value("${ParserPersistence.customersFile}")
    @Setter
    private String customersFile;

    @Value("${ParserPersistence.invoicesFile}")
    @Setter
    private String invoicesFile;

    private Parser parser;

    @Override
    @PostConstruct
    public void load(){
        parser = ParserFactory.createParser(customersFile);
        super.load();
    }

    protected Stream<Customer> readCustomers(){
        try{
            Stream<String> customerLines = Files.lines(Paths.get(customersFile));
            return parser.parseCustomers(customerLines);
        }catch (Exception e){
            log.log(Level.WARNING, "Couldn't load from given filenames.", e);
            return Stream.empty();
        }
    }

    protected Stream<Invoice> readInvoices(){
        try{
            Stream<String> invoiceLines = Files.lines(Paths.get(invoicesFile));
            return parser.parseInvoices(invoiceLines, customers);
        }catch (Exception e){
            log.log(Level.WARNING, "Couldn't load from given filenames.", e);
            return Stream.empty();
        }
    }

    protected void writeCustomer(Customer customer){
        try (PrintWriter out = new PrintWriter(new FileWriter(customersFile))) {
            parser.produceCustomers(customers.values().stream())
                    .forEach(out::println);
        } catch (Exception ex) {
            log.log(Level.WARNING, ex,
                    () -> "Couldn't open " + customersFile + " in write mode.");
        }
    }

    protected void writeInvoice(Invoice invoice){
        try ( PrintWriter out = new PrintWriter(new FileWriter(invoicesFile))) {
            parser.produceInvoices(invoices.values().stream())
                    .forEach(out::println);
        } catch (Exception ex) {
            log.log(Level.WARNING, ex,
                    () -> "Couldn't open " + invoicesFile + " in write mode.");
        }
    }
}
