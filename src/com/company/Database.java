package com.company;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public abstract class Database {


    protected static Connection dbConnection = null; // prisijungimas prie duombazes
    protected static PreparedStatement dbPrepareStatement = null; // uzklausu siuntimui
    protected static Statement dbStatement = null;

    private static String dbUser = "root";
    private static String dbPassword = "";
    private static String dbHost = "localhost:3306";
    private static String dbName = "exchange_rate_db";

    private static ArrayList<String> tableNames = new ArrayList<String>();
    private static TreeMap<String, String> currencyLongNames = new TreeMap<String, String>() {{
        put("USD", "United States Dollar");
        put("EUR", "Euro");
        put("GBP", "British Pound");
        put("AUD", "Australian Dollar");
        put("BGN", "Bulgarian Lev");
        put("BRL", "Brazilian Real");
        put("CAD", "Canadian Dollar");
        put("CHF", "Swiss Franc");
        put("CNY", "Chinese Yuan");
        put("CZK", "Czech Koruna");
        put("DKK", "Danish Krone");
        put("HKD", "Hong Kong Dollar");
        put("HRK", "Croatian Kuna");
        put("HUF", "Hungarian Forint");
        put("IDR", "Indonesian Rupiah");
        put("ILS", "Israeli New Shekel");
        put("INR", "Indian Rupee");
        put("ISK", "Icelandic Króna");
        put("JPY", "Japanese Yen");
        put("KRW", "South Korean won");
        put("MXN", "Mexican Peso");
        put("MYR", "Malaysian Ringgit");
        put("NOK", "Norwegian Krone");
        put("NZD", "New Zealand Dollar");
        put("PHP", "Philippine Piso");
        put("PLN", "Poland Złoty");
        put("RON", "Romanian Leu");
        put("RUB", "Russian Ruble");
        put("SEK", "Swedish Krona");
        put("SGD", "Singapore Dollar");
        put("THB", "Thai Baht");
        put("TRY", "Turkish Lira");
        put("ZAR", "South African Rand");
    }};

    public Database() {
    }

    public static TreeMap<String, String> getCurrencyLongNames() {
        return currencyLongNames;
    }

    public static void setTableNames() {
        try {
            DatabaseMetaData metadata = dbConnection.getMetaData();
            ResultSet rs = metadata.getTables("exchange_rate_db", null, null, new String[]{"TABLE", "VIEW"});
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                if (tableName.contains("_rates")) {
                    Database.tableNames.add(tableName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void getAllData() {
        for (String table : tableNames) {
            if (table.contains("_rates")) {
                getTableData(table);
            }
        }
    }

    public static void printAllData() {
        for (Map.Entry<String, Currency> currency : Currency.getCurrencies().entrySet()){
            System.out.println("Printing currency key " + currency.getKey());
            System.out.println("Name: " + currency.getValue().getNameLong());
            System.out.println("Name short: " + currency.getValue().getNameShort());
            System.out.println("Name of table: " + currency.getValue().getTableName());

            for (Map.Entry<String, ExchangeRate> curr : currency.getValue().getExchangeRates().entrySet()) {
                System.out.println("Exchange rates key: " + curr.getKey());
                System.out.println("Exchange rates currencyId: " + curr.getValue().getId());
                System.out.println("Exchange rates currencyName: " + curr.getValue().getCurrency());
                System.out.println("Exchange rates exchangeRate: " + curr.getValue().getExchangeRate());
            }
            System.out.println();

        }
    }


    public static void getTableData(String tableName) {
        try {
            String selectQueryStatement = "SELECT * from " + tableName;
            dbPrepareStatement = dbConnection.prepareStatement(selectQueryStatement);
            ResultSet results = dbPrepareStatement.executeQuery();

            TreeMap<String, ExchangeRate> currencyExchangeMap = new TreeMap<String, ExchangeRate>();
            String shortName = tableName.substring(0, 3).toUpperCase();
            while (results.next()) {
                /* Gauname rezultatus is duombazes ir issaugome i laikinus darbinius kintamuosius */
                int id = results.getInt("id");
                String currency = results.getString("currency");
                Double exchangeRate = results.getDouble("buy_rate");

                currencyExchangeMap.put(currency, new ExchangeRate(id, currency, exchangeRate));
            }
            Currency newCurrency = new Currency(tableName, shortName, currencyLongNames.get(shortName), currencyExchangeMap);

            Currency.getCurrencies().put(shortName, newCurrency);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static boolean checkIfDBExists() {
        /* Patikriname ar irasytas JDBC driveris darbui su mysql */
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            Database.dbConnection = DriverManager.getConnection("jdbc:mysql://" + dbHost + "/?user=" + dbUser + "&password=" + dbPassword);
            // Connection connection = <your java.sql.Connection>
            ResultSet resultSet = dbConnection.getMetaData().getCatalogs();

            //iterate each catalog in the ResultSet
            while (resultSet.next()) {
                // Get the database name, which is at position 1
                String databaseName = resultSet.getString(1);
                if (databaseName.equals(Database.dbName)) {
                    return true;
                }
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void makeDBConnection() {
        if (Database.checkIfDBExists()) {
            System.out.println("Database exists");
            try {
                // DriverManager: The basic service for managing a set of JDBC drivers.
                Database.dbConnection = DriverManager.getConnection("jdbc:mysql://" + dbHost + "/" + dbName + "?useSSL=true", dbUser, dbPassword);
                System.out.println("Database connection successful");
                Database.setTableNames();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Database does not exist");
        }
    }




}
