package com.company;

import javax.swing.*;
import java.util.TreeMap;

public class Main {
    public static void main(String[] args) {
        Database.makeDBConnection();

        Database.getGetAllData();
        Database.printAllData();


        showApplication();

    }

    public static void showApplication() {
        /* Sukuriame frame objekta ir parodome varotojui */
        JFrame frame = new JFrame("Currency Exchange Calculator");
        frame.setContentPane(new AppMain().getPanel1());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }

}
