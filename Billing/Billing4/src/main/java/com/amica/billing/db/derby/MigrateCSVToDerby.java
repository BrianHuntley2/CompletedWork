package com.amica.billing.db.derby;

import com.amica.billing.Customer;
import com.amica.billing.Invoice;
import com.amica.billing.Terms;
import com.amica.billing.db.CustomerRepository;
import com.amica.billing.db.InvoiceRepository;
import com.amica.billing.db.Migration;
import com.amica.billing.parse.ParserPersistence;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.LocalDate;
import java.util.stream.Collectors;

@ComponentScan(basePackageClasses={CustomerRepository.class, ParserPersistence.class})
@EnableAutoConfiguration
@EnableJpaRepositories(basePackageClasses=CustomerRepository.class)
@EntityScan(basePackageClasses= Customer.class)
@EnableTransactionManagement
@PropertySource(value=
        {"classpath:DB.properties","classpath:migration.properties"})
public class MigrateCSVToDerby {

    public static void main(String[] args){
        try(ConfigurableApplicationContext context = SpringApplication.run(MigrateCSVToDerby.class)){
//            DerbyPersistence derbyPersistence = context.getBean(DerbyPersistence.class);
//            System.out.println(derbyPersistence.getCustomers());
//            System.out.println(derbyPersistence.getInvoices());
            Migration migration = context.getBean(Migration.class);
            CustomerRepository customerRepository = context.getBean(CustomerRepository.class);
            InvoiceRepository invoiceRepository = context.getBean(InvoiceRepository.class);
            System.out.println("Before " + customerRepository.count());
            System.out.println(invoiceRepository.count());
            migration.migrate();
            System.out.println("After " + customerRepository.count());
            System.out.println(invoiceRepository.count());
        }catch (Exception e){

        }
    }
}