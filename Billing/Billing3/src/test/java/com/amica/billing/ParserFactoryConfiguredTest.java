package com.amica.billing;

import com.amica.billing.parse.QuotedCSVParser;
import com.amica.esa.componentconfiguration.manager.ComponentConfigurationManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.amica.billing.ParserFactory.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.amica.billing.parse.CSVParser;
import com.amica.billing.parse.FlatParser;
import com.amica.billing.parse.Parser;


public class ParserFactoryConfiguredTest {

    private Parser mockParser = mock(Parser.class);
    private Supplier<Parser> mockParserFactory = () -> mockParser;

    @BeforeAll
    public static void setup(){
        System.setProperty("env.name", "Quoted");
        ComponentConfigurationManager.getInstance().initialize();
    }

    @Test
    public void testDefaultParser(){
        assertThat(createParser("abc.xyz"), instanceOf(FlatParser.class));
    }

    @Test
    public void testCSVParser(){
        assertThat(createParser("abc.csv"), instanceOf(QuotedCSVParser.class));
    }
}
