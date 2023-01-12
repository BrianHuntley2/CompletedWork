package com.amica.billing.parse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.amica.billing.Customer;
import com.amica.billing.Invoice;

import com.amica.billing.Terms;
import lombok.extern.java.Log;

/**
 * A parser that can read a CSV format with certain expected columns.
 * 
 * @author Will Provost
 */
@Log
public class CSVParser implements Parser{

	public static final int CUSTOMER_COLUMNS = 3;
	public static final int CUSTOMER_FIRST_NAME_COLUMN = 0;
	public static final int CUSTOMER_LAST_NAME_COLUMN = 1;
	public static final int CUSTOMER_TERMS_COLUMN = 2;

	public static final int INVOICE_MIN_COLUMNS = 5;
	public static final int INVOICE_NUMBER_COLUMN = 0;
	public static final int INVOICE_FIRST_NAME_COLUMN = 1;
	public static final int INVOICE_LAST_NAME_COLUMN = 2;
	public static final int INVOICE_AMOUNT_COLUMN = 3;
	public static final int INVOICE_DATE_COLUMN = 4;
	public static final int INVOICE_PAID_DATE_COLUMN = 5;

	/**
	 * Helper that can parse one line of comma-separated text in order to
	 * produce a {@link Customer} object.
	 */
	private Customer parseCustomer(String line) {
		String[] fields = line.split(",");
		if (fields.length == CUSTOMER_COLUMNS) {
			try {
				String firstName = fields[CUSTOMER_FIRST_NAME_COLUMN];
				String lastName = fields[CUSTOMER_LAST_NAME_COLUMN];
				String termsString = fields[CUSTOMER_TERMS_COLUMN];

				//convert the terms string to an enum
				Terms terms = null;
				switch(termsString){
					case "CASH":
						terms = Terms.CASH;
						break;
					case "30":
						terms = Terms.CREDIT_30;
						break;
					case "45":
						terms = Terms.CREDIT_45;
						break;
					case "60":
						terms = Terms.CREDIT_60;
						break;
					case "90":
						terms = Terms.CREDIT_90;
						break;
				}
				//create a customer object and return it
				if(terms != null){
					return new Customer(firstName, lastName, terms);
				}else{
					log.warning(() ->
							"Couldn't find terms, skipping customer: " + line);
				}

			} catch (Exception ex) {
				log.warning(() -> 
					"Couldn't parse terms value, skipping customer: "+ line);
			}
		} else {
			log.warning(() -> 
				"Incorrect number of fields, skipping customer: " + line);
		}

		return null;
	}

	/**
	 * Helper that can parse one line of comma-separated text in order to
	 * produce an {@link Invoice} object.
	 */
	private Invoice parseInvoice(String line, Map<String, Customer> customers) {
		DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String[] fields = line.split(",");
		if (fields.length >= INVOICE_MIN_COLUMNS) {
			try {
				int number = Integer.parseInt(fields[INVOICE_NUMBER_COLUMN]);
				String first = fields[INVOICE_FIRST_NAME_COLUMN];
				String last = fields[INVOICE_LAST_NAME_COLUMN];
				double amount = Double.parseDouble
						(fields[INVOICE_AMOUNT_COLUMN]);
				
				LocalDate date = LocalDate.parse(fields[INVOICE_DATE_COLUMN], parser);
				Optional<LocalDate> paidDate = fields.length > INVOICE_PAID_DATE_COLUMN 
						? Optional.of(LocalDate.parse(fields[INVOICE_PAID_DATE_COLUMN], parser)) 
						: Optional.empty();

				//find the corresponding customer in the map
				Customer customer = customers.get(first + " " + last);
				//create an invoice and return it
				if(customer != null){
					return new Invoice(number, amount, date, paidDate, customer);
				}else{
					log.warning(() ->
							"Couldn't find customer, skipping invoice: " + line);
				}
			} catch (Exception ex) {
				log.warning(() ->
					"Couldn't parse values, skipping invoice: " + line);
			}
		} else {
			log.warning(() -> 
				"Incorrect number of fields, skipping invoice: " + line);
		}

		return null;
	}

	/**
	 * Helper to write a CSV representation of one customer.
	 */
	public String formatCustomer(Customer customer) {
		return String.format("%s,%s,%s", customer.getFirstName(), customer.getLastName(), customer.getTerms());
	}
	
	/**
	 * Helper to write a CSV representation of one invoice.
	 */

	public String formatInvoice(Invoice invoice) {
		return String.format("%d,%s,%s,%.2f,%s,%s",
				invoice.getNumber(), invoice.getCustomer().getFirstName(), invoice.getCustomer().getLastName(),
				invoice.getAmount(), invoice.getInvoiceDate(),
				invoice.getPaidDate().isPresent() ? invoice.getPaidDate().get() : "");
	}

	@Override
	public Stream<Customer> parseCustomers(Stream<String> customerLines) {
		return customerLines.map(this::parseCustomer);
	}

	@Override
	public Stream<Invoice> parseInvoices(Stream<String> invoiceLines, Map<String, Customer> customers) {
		return invoiceLines.map(l -> this.parseInvoice(l, customers));
	}

	@Override
	public Stream<String> produceCustomers(Stream<Customer> customers) {
		return customers.map(this::formatCustomer);
	}

	@Override
	public Stream<String> produceInvoices(Stream<Invoice> invoices) {
		return invoices.map(this::formatInvoice);
	}
}
