package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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
    private DefaultListModel model = new DefaultListModel();
    private JList list1;
    private JButton buttonCalculate;

    private String selectedCurrencyToSell;
    private String selectedCurrencyToBuy;
    private ExchangeRate selectedExchangeRate;
    private Boolean lastTypedInToSell = false;
    private Boolean lastTypedInToBuy = false;

    private DecimalFormat df4 = new DecimalFormat("##.####");
    private DecimalFormat df2 = new DecimalFormat("##.##");

    public JPanel getPanel1() {
        return panel1;
    }

    public JList getList1() {
        return list1;
    }

    public void setList1(JList list1) {
        this.list1 = list1;
    }

    public AppMain() {

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
                        labelExchangeRateValue.setText(df4.format(selectedExchangeRate.getExchangeRate()));
                    }
                }
            }
        });

        textFieldAmountToSell.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                lastTypedInToBuy = false;
                lastTypedInToSell = true;

            }
        });

        textFieldAmountToReceive.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                lastTypedInToSell = false;
                lastTypedInToBuy = true;

            }
        });

        buttonCalculate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String toSell = textFieldAmountToSell.getText();
                String toBuy = textFieldAmountToReceive.getText();
                if (toSell.isEmpty() || toBuy.isEmpty()) {
                    if (!toSell.isEmpty()) {
                        calculateAndPrintToSell(toSell);
                    } else if (!toBuy.isEmpty()) {
                        calculateAndPrintToBuy(toBuy);
                    }
                } else {
                    if (lastTypedInToSell) {
                        textFieldAmountToReceive.setText("");
                        calculateAndPrintToSell(toSell);
                    } else if (lastTypedInToBuy) {
                        textFieldAmountToSell.setText("");
                        calculateAndPrintToBuy(toBuy);
                    }
                }
            }
        });

        populateComboBoxToSell();

        populateHelpList();

    }

    private void calculateAndPrintToSell(String toSell) {
        Double amount = selectedExchangeRate.getExchangeRate() * Double.parseDouble(toSell);
        textFieldAmountToReceive.setText(df2.format(amount));
        DAO.insertTransaction(this.selectedCurrencyToSell, Double.parseDouble(toSell), this.selectedCurrencyToBuy, amount, selectedExchangeRate.getExchangeRate());
    }

    private void calculateAndPrintToBuy(String toBuy) {
        Double amount = Double.parseDouble(toBuy) / selectedExchangeRate.getExchangeRate();
        textFieldAmountToSell.setText(df2.format(amount));
        DAO.insertTransaction(this.selectedCurrencyToSell, amount, this.selectedCurrencyToBuy, Double.parseDouble(toBuy), selectedExchangeRate.getExchangeRate());
    }

    private void populateComboBoxToSell() {
        if (this.comboBoxToSell.getItemCount() > 0) {
            this.comboBoxToSell.removeAllItems();
        }
        for (Map.Entry<String, Currency> entry : Currency.getCurrencies().entrySet()) {

            this.comboBoxToSell.addItem(entry.getKey());

        }
    }

    public void populateHelpList() {
        this.list1.setEnabled(false);
        this.list1.setModel(this.model);
        this.list1= new JList(this.model);

        if (!this.model.isEmpty()){
            this.model.clear();
        }

        for (Map.Entry<String, String> currencyNames : Database.getCurrencyLongNames().entrySet()){
            String listLine = currencyNames.getKey() + " = " + currencyNames.getValue();
                    this.model.addElement(listLine);
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
