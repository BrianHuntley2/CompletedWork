package com.amica.billing;

import com.amica.billing.db.Migration;
import com.amica.billing.db.derby.DerbyPersistence;
import com.amica.billing.db.derby.MigrateCSVToDerby;
import lombok.extern.java.Log;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.logging.Level;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes=ReporterDerbyIntegrationTest.Config.class)
@Log
public class ReporterDerbyIntegrationTest extends ReporterIntegrationTestBase {

    @Autowired
    private DerbyPersistence derbyPersistence;

    @BeforeAll
    public static void init(){
        try(AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class)){
            Migration migration = context.getBean(Migration.class);
            migration.migrate();
        }catch(Exception e){
            log.log(Level.WARNING, "Couldn't load from given filenames.", e);
        }
    }

    @Override
    @BeforeEach
    public void setUp() throws IOException {
        super.setUp();
        derbyPersistence.load();
    }

    @Override
    @Test
    @Transactional
    public void testCreateCustomer(){
        super.testCreateCustomer();
    }

    @Override
    @Test
    @Transactional
    public void testCreateInvoice(){
        super.testCreateInvoice();
    }

    @Override
    @Test
    @Transactional
    public void testPayInvoice(){
        super.testPayInvoice();
    }

    @ComponentScan
    @EnableAutoConfiguration
    @EntityScan(basePackageClasses=Customer.class)
    @EnableTransactionManagement
    @PropertySource(value={"classpath:test.properties","classpath:migration.properties"})
    public static class Config {

    }
}
