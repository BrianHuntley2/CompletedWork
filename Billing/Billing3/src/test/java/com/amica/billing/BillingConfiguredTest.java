package com.amica.billing;

import com.amica.esa.componentconfiguration.manager.ComponentConfigurationManager;
import com.amica.escm.configuration.api.Configuration;
import org.junit.jupiter.api.BeforeAll;

import static com.amica.billing.Billing.CONFIGURATION_NAME;

public class BillingConfiguredTest extends BillingTest {
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
        return new Billing();
    }

    @BeforeAll
    public static void setup(){
        System.setProperty("env.name", "Configured");
        ComponentConfigurationManager.getInstance().initialize();
    }

}
