package com.amica.billing;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Map;

@Log
public class Reporter {

    private static Billing billing;
    private static String folder;
    private LocalDate asOf;

    public Reporter(Billing billing, String folder, LocalDate asOf) {
        this.billing = billing;
        this.folder = folder;
        this.asOf = asOf;
        billing.addInvoiceListener(Reporter::onInvoiceChanged);
        billing.addCustomerListener(Reporter::onCustomerChanged);
    }

    public static void onInvoiceChanged(Invoice invoice){
        //reportInvoicesOrderedByNumber();
        //reportInvoicesOrderedByIssueDate();
        //reportInvoicesGroupedByCustomer();
        reportOverdueInvoices();
    }

    public static void onCustomerChanged(Customer customer){
        reportCustomersAndVolume();
    }



    public static void reportInvoicesOrderedByNumber(){
        try (PrintWriter out = new PrintWriter (new FileWriter(folder + "bynumber.txt")); ) {
            out.print ("All invoices, ordered by invoice number\n" +
                    "==================================================================\n" +
                    "\n" +
                    "       Customer                     Issued      Amount        Paid\n" +
                    "----  ------------------------  ----------  ----------  ----------");

            for(Invoice i : billing.getInvoicesOrderedByNumber()){
                out.print("\n" + i.getNumber() + "\t" +
                        i.getCustomer().getName() + "\t" +
                        i.getInvoiceDate() + "\t" +
                        i.getAmount() + "\t");
                if(i.getPaidDate().isPresent()){
                    out.print(i.getPaidDate().get());
                }
            }
        } catch (Exception ex) {
            log.warning(() ->
                    "Error writing to file: " + folder);
        }
    }

    public static void reportInvoicesOrderedByIssueDate(){
        try (PrintWriter out = new PrintWriter (new FileWriter(folder + "bydate.txt")); ) {
            out.print ("All invoices, ordered by invoice date\n" +
                    "==================================================================\n" +
                    "\n" +
                    "       Customer                     Issued      Amount        Paid\n" +
                    "----  ------------------------  ----------  ----------  ----------");

            for(Invoice i : billing.getInvoicesOrderedByIssueDate()){
                out.print("\n" + i.getNumber() + "\t" +
                        i.getCustomer().getName() + "\t" +
                        i.getInvoiceDate() + "\t" +
                        i.getAmount() + "\t");
                if(i.getPaidDate().isPresent()){
                    out.print(i.getPaidDate().get());
                }
            }
        } catch (Exception ex) {
            log.warning(() ->
                    "Error writing to file: " + folder);
        }
    }

    public static void reportInvoicesGroupedByCustomer(){
        try (PrintWriter out = new PrintWriter (new FileWriter(folder + "bycustomer.txt")); ) {
            out.print ("All invoices, grouped by customer\n" +
                    "==================================================================\n" +
                    "\n" +
                    "       Customer                     Issued      Amount        Paid\n" +
                    "----  ------------------------  ----------  ----------  ----------");

            for(Invoice i : billing.getInvoicesGroupedByCustomer()){
                out.print("\n" + i.getNumber() + "\t" +
                        i.getCustomer().getName() + "\t" +
                        i.getInvoiceDate() + "\t" +
                        i.getAmount() + "\t");
                if(i.getPaidDate().isPresent()){
                    out.print(i.getPaidDate().get());
                }
            }
        } catch (Exception ex) {
            log.warning(() ->
                    "Error writing to file: " + folder);
        }
    }

    public static void reportOverdueInvoices(){
        try (PrintWriter out = new PrintWriter (new FileWriter(folder + "overdue.txt")); ) {
            out.print ("All overdue invoices\n" +
                    "==================================================================\n" +
                    "\n" +
                    "       Customer                     Issued      Amount        Paid\n" +
                    "----  ------------------------  ----------  ----------  ----------");

            for(Invoice i : billing.getOverdueInvoices()){
                out.print("\n" + i.getNumber() + "\t" +
                        i.getCustomer().getName() + "\t" +
                        i.getInvoiceDate() + "\t" +
                        i.getAmount() + "\t");
                if(i.getPaidDate().isPresent()){
                    out.print(i.getPaidDate().get());
                }
            }
        } catch (Exception ex) {
            log.warning(() ->
                    "Error writing to file: " + folder);
        }
    }

    public static void reportCustomersAndVolume(){
        try (PrintWriter out = new PrintWriter (new FileWriter(folder + "customervolume.txt")); ) {
            out.print ("Customers by volume\n" +
                    "==================================================================\n" +
                    "\n" +
                    "Customer                        Volume\n" +
                    "------------------------  ------------");

            for(Map.Entry<Customer, Double> vol : billing.getCustomersAndVolume().entrySet()){
                out.print("\n" + vol.getKey().getName() + "\t" +
                        vol.getValue() + "\t");
            }
        } catch (Exception ex) {
            log.warning(() ->
                    "Error writing to file: " + folder);
        }
    }
}
