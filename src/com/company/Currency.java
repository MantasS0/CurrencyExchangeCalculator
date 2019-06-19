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



    @Override
    public void updateData() {
        for (Map.Entry<String, ExchangeRate> currency : this.exchangeRates.entrySet()) {
            try {
                String insertQueryStatement = "UPDATE " + this.tableName + " SET `sold_rate` = ?, `buy_rate` = ? WHERE " + this.tableName + ".`currency` = ?";
                dbPrepareStatement = dbConnection.prepareStatement(insertQueryStatement);
                dbPrepareStatement.setDouble(1, currency.getValue().getExchangeRate());
                dbPrepareStatement.setDouble(2, currency.getValue().getExchangeRate());
                dbPrepareStatement.setString(3, currency.getKey());
                dbPrepareStatement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void createData() {
        for (Map.Entry<String, ExchangeRate> currency : this.exchangeRates.entrySet()) {
            try {
                String insertQueryStatement = "INSERT INTO " + this.tableName + " (`currency`, `sold_rate`, `buy_rate`) VALUES (?, ?, ?)";
                dbPrepareStatement = dbConnection.prepareStatement(insertQueryStatement);
                dbPrepareStatement.setString(1, currency.getKey());
                dbPrepareStatement.setDouble(2, currency.getValue().getExchangeRate());
                dbPrepareStatement.setDouble(3, currency.getValue().getExchangeRate());
                dbPrepareStatement.execute();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
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

