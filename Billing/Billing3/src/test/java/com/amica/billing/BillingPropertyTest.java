package com.amica.billing;

import com.amica.escm.configuration.properties.PropertiesConfiguration;

import java.util.Properties;

import static com.amica.billing.TestUtility.CUSTOMERS_FILENAME;
import static com.amica.billing.TestUtility.TEMP_FOLDER;

public class BillingPropertyTest extends BillingTest {
    @Override
    protected String getCustomersFilename(){
        return "customers_configured.csv";
    }

    @Override
    protected String getInvoicesFilename(){
        return "invoices_configured.csv";
    }

    @Override
    protected Billing createTestTarget() {
       Properties properties = new Properties();
       properties.put(Billing.CUSTOMER_FILE, TEMP_FOLDER + "/" + getCustomersFilename());
       properties.put(Billing.INVOICE_FILE , TEMP_FOLDER + "/" + getInvoicesFilename());

       return new Billing(new PropertiesConfiguration(properties));
    }


}
