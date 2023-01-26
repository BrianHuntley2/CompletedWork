package com.amica.billing;

import static com.amica.billing.TestUtility.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.amica.HasKeys;
import com.amica.billing.parse.CSVParser;
import com.amica.billing.parse.Parser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;


/**
 * Unit test for the {@link Billing} class.
 * This test focuses on the test data set defined in {@link TestUtillity}
 * and prepared data files that reflect that data. We make a copy of the
 * data files at the start of each test case, create the Billing object
 * to load them, and check its getters and query methods.
 * A few more test cases drive updates to the object, and assure that
 * they are reflected in updates to the staged data files.
 * 
 * @author Will Provost
 */
public class BillingTest {

	public static final String SOURCE_FOLDER = "src/test/resources/data";

	public Billing billing = new Billing(TEMP_FOLDER+"/"+CUSTOMERS_FILENAME,
										TEMP_FOLDER+"/"+INVOICES_FILENAME);

	public Consumer<Customer> mockCustomerListener;

	public Consumer<Invoice> mockInvoiceListener;

	/**
	 * Assure that the necessary folders are in place, and make a copy
	 * of the source data files. Install mock objects as listeners for changes.
	 */
	@BeforeEach
	public void setUp() throws IOException {
		Files.createDirectories(Paths.get(TEMP_FOLDER));
		Files.createDirectories(Paths.get(OUTPUT_FOLDER));
		Files.copy(Paths.get(SOURCE_FOLDER, CUSTOMERS_FILENAME), 
				Paths.get(TEMP_FOLDER, CUSTOMERS_FILENAME),
				StandardCopyOption.REPLACE_EXISTING);
		Files.copy(Paths.get(SOURCE_FOLDER, INVOICES_FILENAME), 
				Paths.get(TEMP_FOLDER, INVOICES_FILENAME),
				StandardCopyOption.REPLACE_EXISTING);

		mockCustomerListener = Mockito.mock(Consumer.class);
		mockInvoiceListener = Mockito.mock(Consumer.class);

		billing.addCustomerListener(mockCustomerListener);
		billing.addInvoiceListener(mockInvoiceListener);
	}

	@Test
	public void testGetInvoicesOrderedByNumber(){
		assertThat(billing.getInvoicesOrderedByNumber(), hasNumbers(1, 2, 3, 4, 5, 6));
	}

	@Test
	public void testGetInvoicesOrderedByDate(){
		assertThat(billing.getInvoicesOrderedByDate(), hasNumbers(4, 6, 1, 2, 5, 3));
	}

	@Test
	public void testGetInvoicesGroupedByCustomer(){
		Map<Customer, List<Invoice>> tmp = billing.getInvoicesGroupedByCustomer();
		assertThat(tmp.get(GOOD_CUSTOMERS.get(0)).stream(), hasNumbers(1));
		assertThat(tmp.get(GOOD_CUSTOMERS.get(1)).stream(), hasNumbers(2, 3, 4));
		assertThat(tmp.get(GOOD_CUSTOMERS.get(2)).stream(), hasNumbers(5, 6));
	}

	@Test
	public void testGetOverdueInvoices(){
		assertThat(billing.getOverdueInvoices(AS_OF_DATE), hasNumbers(4, 6, 1));
	}

	@Test
	public void testCreateCustomer(){
		billing.createCustomer("test", "test", Terms.CASH);
		Path customerPath = Paths.get(TEMP_FOLDER, CUSTOMERS_FILENAME);
		try {
			List<String> lines = Files.lines(customerPath).toList();
			boolean containsCustomer = false;
			if(lines.contains("test,test,CASH")){
				containsCustomer = true;
			}
			assertThat(containsCustomer, equalTo(true));
			Mockito.verify(mockCustomerListener).accept(billing.getCustomers().get("test test"));
		}catch (Exception e){
			System.out.println("Couldn't load from given filename.");
		}
	}

	@Test
	public void testCreateInvoices(){
		billing.createInvoice("Customer One", 123);
		Path invoicePath = Paths.get(TEMP_FOLDER, INVOICES_FILENAME);
		try {
			List<String> lines = Files.lines(invoicePath).toList();
			boolean containsInvoice = false;
			for(String line : lines){
				if(line.split(",")[1].equals("Customer") &&
					line.split(",")[2].equals("One") &&
					line.split(",")[3].equals("123.00")){
					containsInvoice = true;
				}
			}
			assertThat(containsInvoice, equalTo(true));
			Mockito.verify(mockInvoiceListener).accept(billing.getInvoicesOrderedByNumber().toList().get(6));
		}catch (Exception e){
			System.out.println("Couldn't load from given filename.");
		}
	}

	@Test
	public void testPayInvoice(){
		billing.payInvoice(1);
		Path invoicePath = Paths.get(TEMP_FOLDER, INVOICES_FILENAME);
		try {
			List<String> lines = Files.lines(invoicePath).toList();
			boolean invoicePaid = false;
			for(String line : lines){
				if(line.split(",")[0].equals("1") &&
						line.split(",")[5] != null){
					invoicePaid = true;
				}
			}
			assertThat(invoicePaid, equalTo(true));
			Mockito.verify(mockInvoiceListener).accept(GOOD_INVOICES.get(0));
		}catch (Exception e){
			System.out.println("Couldn't load from given filename.");
		}
	}


}
