package com.company;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.util.Map;

public class AppMain {
    private JPanel panel1;
    private JLabel labelConvertFrom;
    private JLabel labelConvertTo;
    private JLabel labelExchangeRate;
    private JLabel labelAmountToSell;
    private JLabel labelAmountToReceive;
    private JComboBox comboBoxToSell;
    private JComboBox comboBoxToBuy;
    private JLabel labelExchangeRateValue;
    private JTextField textFieldAmountToSell;
    private JTextField textFieldAmountToReceive;
    private JList list1;
    private JButton buttonCalculate;

    private String selectedCurrencyToSell;
    private String selectedCurrencyToBuy;
    private ExchangeRate selectedExchangeRate;

    private DecimalFormat df = new DecimalFormat("##.####");

    public JPanel getPanel1() {
        return panel1;
    }

    public AppMain() {
        populateComboBoxToSell();



        comboBoxToSell.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {

                if (comboBoxToSell.getItemCount() > 0) {
                    if (event.getStateChange() == ItemEvent.SELECTED) {
                        selectedCurrencyToSell = event.getItem().toString();
                        populateComboBoxToBuy(selectedCurrencyToSell);
                    }
                }

            }
        });


        comboBoxToBuy.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                if (comboBoxToBuy.getItemCount() > 0) {
                    if (event.getStateChange() == ItemEvent.SELECTED) {
                        selectedCurrencyToBuy = event.getItem().toString();
                        selectedExchangeRate = Currency.getCurrencies().get(selectedCurrencyToSell).getExchangeRates().get(selectedCurrencyToBuy);
                        labelExchangeRate.setText(df.format(selectedExchangeRate.getExchangeRate()));
                    }
                }
            }
        });


        buttonCalculate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    private void populateComboBoxToSell() {
        if (this.comboBoxToSell.getItemCount() > 0) {
            this.comboBoxToSell.removeAllItems();
        }
        for (Map.Entry<String, Currency> entry : Currency.getCurrencies().entrySet()) {

            this.comboBoxToSell.addItem(entry.getKey());

        }
    }

    private void populateComboBoxToBuy(String exclude) {
        if (this.comboBoxToBuy.getItemCount() > 0) {
            this.comboBoxToBuy.removeAllItems();
        }
        for (Map.Entry<String, Currency> entry : Currency.getCurrencies().entrySet()) {
            if (!exclude.equals(entry.getKey())) {
                this.comboBoxToBuy.addItem(entry.getKey());
            }

        }
    }
}
