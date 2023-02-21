package com.amica.billing.db;

import com.amica.billing.db.derby.DerbyPersistence;
import com.amica.billing.parse.ParserPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Migration {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    InvoiceRepository invoiceRepository;

    @Autowired
    ParserPersistence source;

    @Autowired
    DerbyPersistence target;

    public void migrate(){
        invoiceRepository.deleteAll();
        customerRepository.deleteAll();
        source.load();
        target.load();
        source.getCustomers().forEach((k, v) -> target.saveCustomer(v));
        source.getInvoices().forEach((k, v) -> target.saveInvoice(v));
    }
}
