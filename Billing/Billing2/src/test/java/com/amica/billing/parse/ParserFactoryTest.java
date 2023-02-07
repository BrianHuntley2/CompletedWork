package com.amica.billing.parse;

import com.amica.billing.Customer;
import com.amica.billing.Invoice;
import com.amica.billing.ParserFactory;
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
import org.mockito.Mockito;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ParserFactoryTest {

    public static final String CUSTOMER_CSV = "customers.csv";

    public static final String CUSTOMER_FLAT = "customers.flat";

    public static final String OTHER_FILETYPE = "abc.xyz";

    public static final String ALL_CAPS = "CUSTOMERS.FLAT";

    public Parser parser;

    public Parser mockParser = Mockito.mock(Parser.class);

    public Supplier<Parser> mockParserFactory = () -> mockParser;

    @BeforeEach
    public void setup(){
        ParserFactory.resetParsers();
    }

    @Test
    public void testCustomerCsvParser(){
        parser = ParserFactory.createParser(CUSTOMER_CSV);
        assertThat(parser, instanceOf(CSVParser.class));
    }

    @Test
    public void testCustomerFlatParser(){
        parser = ParserFactory.createParser(CUSTOMER_FLAT);
        assertThat(parser, instanceOf(FlatParser.class));
    }

    @Test
    public void testOtherFiletype(){
        parser = ParserFactory.createParser(OTHER_FILETYPE);
        assertThat(parser, instanceOf(CSVParser.class));
    }

    @Test
    public void testCaseInsensitive(){
        parser = ParserFactory.createParser(ALL_CAPS);
        assertThat(parser, instanceOf(FlatParser.class));
    }

    @Test
    public void testUniqueExtensionParser(){
        ParserFactory.addParser("xyz", mockParserFactory);
        parser = ParserFactory.createParser(OTHER_FILETYPE);
        assertThat(parser, equalTo(mockParser));
    }

    @Test
    public void testAddExistingParser(){
        assertThrows(IllegalArgumentException.class, () -> ParserFactory.addParser("flat", mockParserFactory));
    }

    @Test
    public void testReplaceParser(){
        ParserFactory.replaceParser("csv", mockParserFactory);
        assertThat(ParserFactory.createParser(CUSTOMER_CSV), equalTo(mockParser));
    }

    @Test
    public void testReplaceNonExistingParser(){
        assertThrows(IllegalArgumentException.class, () -> ParserFactory.replaceParser("abcd", mockParserFactory));
    }
}
