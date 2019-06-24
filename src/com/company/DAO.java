package com.company;

import java.sql.SQLException;
import java.sql.Time;
import java.util.Map;


public class DAO extends Database{

    public DAO() {
    }

    public static void insertTransaction(String soldCurrency, Double soldAmount, String boughtCurrency, Double boughtAmount, Double exchangeRate) {
        try {
            //INSERT INTO `transactions` (`id`, `sold_currency`, `sold_amount`, `bought_currency`, `bought_amount`, `exchange_rate`, `time_stamp`) VALUES (NULL, 'USD', '500', 'EUR', '400', '0.9', CURRENT_TIMESTAMP);

            String insertQueryStatement = "INSERT INTO `transactions` (`sold_currency`, `sold_amount`, `bought_currency`, `bought_amount`, `exchange_rate`) VALUES (?, ?, ?, ?, ?)";
            dbPrepareStatement = dbConnection.prepareStatement(insertQueryStatement);
            dbPrepareStatement.setString(1, soldCurrency);
            dbPrepareStatement.setDouble(2, soldAmount);
            dbPrepareStatement.setString(3, boughtCurrency);
            dbPrepareStatement.setDouble(4, boughtAmount);
            dbPrepareStatement.setDouble(5, exchangeRate);
            dbPrepareStatement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
