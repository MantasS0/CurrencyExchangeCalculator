package com.company;

import java.sql.*;
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


    private static String[] tableNames = {"eur_rates", "usd_rates", "gbp_rates", "transactions"};
    private static HashMap<String, String> currencyLongNames = new HashMap<String, String>() {{
        put("USD", "United States Dollar");
        put("EUR", "Euro");
        put("GBP", "British Pound");
    }};

    public static HashMap<String, String> getCurrencyLongNames() {
        return currencyLongNames;
    }

    public abstract void updateData();

//    public abstract void deleteData();

    public abstract void createData();

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
                Database.checkTables();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Database does not exist");
            try {
                String createDatabaseStatement = "CREATE DATABASE " + Database.dbName + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
                dbStatement = dbConnection.createStatement();
                dbStatement.executeUpdate(createDatabaseStatement);
                System.out.println("Created new Database \"" + Database.dbName + "\"...");
                String useDatabase = "USE " + Database.dbName + ";";
                dbStatement.executeUpdate(useDatabase);
                Database.checkTables();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void checkTables() {
        try {
            String[] tables = {"eur_rates", "usd_rates", "gbp_rates", "transactions"};
            DatabaseMetaData metadata = dbConnection.getMetaData();
            for (int i = 0; i < tables.length; i++) {
                ResultSet rs = metadata.getTables(null, null, tables[i], null);
                if (!rs.next()) {
                    createTable(tables[i]);
//                    System.out.println("Table " + tables[i] + " created");
                }
            }
            System.out.println("Table check successful");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTable(String tableName) {
        String createTableStatement = null;
        if (tableName.equals("eur_rates")) {
            createTableStatement = "CREATE TABLE `" + Database.dbName + "`.`eur_rates` ( `id` INT UNSIGNED NOT NULL AUTO_INCREMENT , `currency` VARCHAR(5) NOT NULL , `sold_rate` DOUBLE UNSIGNED NOT NULL , `buy_rate` DOUBLE UNSIGNED NOT NULL , `updated` TIMESTAMP on update CURRENT_TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP , PRIMARY KEY (`id`)) ENGINE = InnoDB;";

        } else if (tableName.equals("usd_rates")) {
            createTableStatement = "CREATE TABLE `" + Database.dbName + "`.`usd_rates` ( `id` INT UNSIGNED NOT NULL AUTO_INCREMENT , `currency` VARCHAR(5) NOT NULL , `sold_rate` DOUBLE UNSIGNED NOT NULL , `buy_rate` DOUBLE UNSIGNED NOT NULL , `updated` TIMESTAMP on update CURRENT_TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP , PRIMARY KEY (`id`)) ENGINE = InnoDB;";

        } else if (tableName.equals("gbp_rates")) {
            createTableStatement = "CREATE TABLE `" + Database.dbName + "`.`gbp_rates` ( `id` INT UNSIGNED NOT NULL AUTO_INCREMENT , `currency` VARCHAR(5) NOT NULL , `sold_rate` DOUBLE UNSIGNED NOT NULL , `buy_rate` DOUBLE UNSIGNED NOT NULL , `updated` TIMESTAMP on update CURRENT_TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP , PRIMARY KEY (`id`)) ENGINE = InnoDB;";

        } else if (tableName.equals("transactions")) {
            createTableStatement = "CREATE TABLE `" + Database.dbName + "`.`transactions` ( `id` INT UNSIGNED NOT NULL AUTO_INCREMENT , `sold_currency` VARCHAR(5) NOT NULL , `sold_amount` DOUBLE UNSIGNED NOT NULL , `bought_currency` VARCHAR(5) NOT NULL , `bought_amount` DOUBLE UNSIGNED NOT NULL , `exchange_rate` DOUBLE UNSIGNED NOT NULL , `time_stamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP , PRIMARY KEY (`id`)) ENGINE = InnoDB;";
        }

        if (createTableStatement != null) {
            try {
                dbStatement = dbConnection.createStatement();
                dbStatement.executeUpdate(createTableStatement);
                System.out.println("Table \"" + tableName + "\" created...");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}
