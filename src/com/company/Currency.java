package com.company;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

public class Currency extends Database {

    private String tableName;
    private String nameShort;
    private String nameLong;
    private TreeMap<String, ExchangeRate> exchangeRates;

    private static TreeMap<String, Currency> currencies = new TreeMap<String, Currency>();

    public Currency(String tableName, String nameShort, String nameLong, TreeMap<String, ExchangeRate> exchangeRates) {
        this.tableName = tableName;
        this.nameShort = nameShort;
        this.nameLong = nameLong;
        this.exchangeRates = exchangeRates;

    }

    public TreeMap<String, ExchangeRate> getExchangeRates() {
        return exchangeRates;
    }

    public void setExchangeRates(TreeMap<String, ExchangeRate> exchangeRates) {
        this.exchangeRates = exchangeRates;
    }

    public static TreeMap<String, Currency> getCurrencies() {
        return currencies;
    }

    public String getTableName() {
        return tableName;
    }

    public String getNameShort() {
        return nameShort;
    }

    public String getNameLong() {
        return nameLong;
    }
}

