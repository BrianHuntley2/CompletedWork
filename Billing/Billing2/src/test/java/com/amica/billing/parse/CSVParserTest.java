package com.amica.billing.parse;

import com.amica.billing.Customer;
import com.amica.billing.Invoice;
import org.junit.jupiter.api.Test;

import static com.amica.billing.TestUtility.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Unit test for the {@link CSVParser}. Relies on data sets in the 
 * {@link TestUtility} and its own CSV representations of those data sets,
 * help in memory as lists of strings, to drive the parsing and producing
 * methods and expect clean translations between string and object forms.
 * 
 * @author Will Provost
 */
public class CSVParserTest {

	public CSVParser parser = new CSVParser();

	public static final List<String> GOOD_CUSTOMER_DATA = Stream.of
			("Customer,One,CASH",
			 "Customer,Two,45",
			 "Customer,Three,30").toList();
	
	public static final List<String> BAD_CUSTOMER_DATA = Stream.of 
			("Customer,One,CASHY_MONEY", 
			 "Customer,Two",
			 "Customer,Three,30").toList();

	public static final List<String> GOOD_INVOICE_DATA = Stream.of 
			("1,Customer,One,100.00,2022-01-04",
			 "2,Customer,Two,200.00,2022-01-04,2022-01-05",
			 "3,Customer,Two,300.00,2022-01-06",
			 "4,Customer,Two,400.00,2021-11-11",
			 "5,Customer,Three,500.00,2022-01-04,2022-01-08",
			 "6,Customer,Three,600.00,2021-12-04").toList();
	
	public static final List<String> BAD_INVOICE_DATA = Stream.of
			("1,Customer,One,100.00,2022-01-04",
			 "2,Customer,Two,200.00,2022-01-04,2022-01-05",
			 "3,Customer,Two,300.00",
			 "4,Customer,Four,400.00,2021-11-11",
			 "5,Customer,Three,500.00,2022-01-04,20220108",
			 "6,Customer,Three,600.00,2021-12-04").toList();


	@Test
	public void testParseGoodCustomers(){
		List<Customer> result = parser.parseCustomers(GOOD_CUSTOMER_DATA.stream()).toList();
		assertThat(result, sameAsList(GOOD_CUSTOMERS));
	}

	@Test
	public void testParseBadCustomers(){
		List<Customer> result = parser.parseCustomers(BAD_CUSTOMER_DATA.stream()).toList();
		assertThat(result, sameAsList(BAD_CUSTOMERS));
	}

	@Test
	public void testParseGoodInvoices(){
		List<Invoice> result = parser.parseInvoices(GOOD_INVOICE_DATA.stream(), GOOD_CUSTOMERS_MAP).toList();
		assertThat(result, sameAsList(GOOD_INVOICES));
	}

	@Test
	public void testParseBadInvoices(){
		List<Invoice> result = parser.parseInvoices(BAD_INVOICE_DATA.stream(), GOOD_CUSTOMERS_MAP).toList();
		assertThat(result, sameAsList(BAD_INVOICES));
	}

	@Test
	public void testProduceGoodCustomers(){
		List<String> result = parser.produceCustomers(GOOD_CUSTOMERS.stream()).toList();
		assertThat(result, sameAsList(GOOD_CUSTOMER_DATA));
	}

	@Test
	public void testProduceBadCustomers(){
		List<String> result = parser.produceCustomers(BAD_CUSTOMERS.stream()).toList();
		assertThat(result, sameAsList(BAD_CUSTOMER_DATA.subList(2, 3)));
	}

	@Test
	public void testProduceGoodInvoices(){
		List<String> result = parser.produceInvoices(GOOD_INVOICES.stream()).toList();
		assertThat(result, sameAsList(GOOD_INVOICE_DATA));
	}

	@Test
	public void testProduceBadInvoices(){
		List<String> result = parser.produceInvoices(BAD_INVOICES.stream()).toList();
		List<String> expected = new ArrayList<>();
		expected.add(BAD_INVOICE_DATA.get(0));
		expected.add(BAD_INVOICE_DATA.get(1));
		expected.add(BAD_INVOICE_DATA.get(5));
		assertThat(result, sameAsList(expected));
	}

}
