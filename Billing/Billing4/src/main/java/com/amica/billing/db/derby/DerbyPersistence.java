package com.amica.billing.db.derby;

import com.amica.billing.Customer;
import com.amica.billing.Invoice;
import com.amica.billing.db.CachingPersistence;
import com.amica.billing.db.CustomerRepository;
import com.amica.billing.db.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import java.util.stream.Stream;

@Component
@Primary
public class DerbyPersistence extends CachingPersistence {

    private CustomerRepository customerRepository;
    private InvoiceRepository invoiceRepository;

    @Autowired
    PlatformTransactionManager txManager;

    public DerbyPersistence(CustomerRepository customerRepository, InvoiceRepository invoiceRepository) {
        this.customerRepository = customerRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    @PostConstruct
    public void load(){
        TransactionTemplate template = new TransactionTemplate(txManager);
        template.executeWithoutResult(status -> super.load());
    }

    protected Stream<Customer> readCustomers(){
        return customerRepository.streamAllBy();
    }

    protected Stream<Invoice> readInvoices(){
        return invoiceRepository.streamAllBy();
    }

    protected void writeCustomer(Customer customer){
        customerRepository.save(customer);
    }

    protected void writeInvoice(Invoice invoice){
        invoiceRepository.save(invoice);
    }

}
