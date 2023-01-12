package com.amica.billing.parse;

public class ParserFactory {

    public static Parser createParser(String filename){
        if(filename.contains(".csv")){
            return new CSVParser();
        }else{//TODO add flatparser
            return null;
        }
    }
}
