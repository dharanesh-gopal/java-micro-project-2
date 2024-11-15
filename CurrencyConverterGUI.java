import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

class CurrencyConverter {
    private Map<String, Double> exchangeRates;

    public CurrencyConverter() {
        this.exchangeRates = new HashMap<>();
    }

    public void addExchangeRate(String currency, double rate) {
        exchangeRates.put(currency.toUpperCase(), rate);
    }

    public double convert(String fromCurrency, String toCurrency, double amount) throws Exception {
        fromCurrency = fromCurrency.toUpperCase();
        toCurrency = toCurrency.toUpperCase();

        if (!exchangeRates.containsKey(fromCurrency)) {
            throw new Exception("Currency not found: " + fromCurrency);
        }
        if (!exchangeRates.containsKey(toCurrency)) {
            throw new Exception("Currency not found: " + toCurrency);
        }

        double fromRate = exchangeRates.get(fromCurrency);
        double toRate = exchangeRates.get(toCurrency);
        return (amount / fromRate) * toRate;
    }

    public void printRates() {
        for (Map.Entry<String, Double> entry : exchangeRates.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    public Map<String, Double> getExchangeRates() {
        return exchangeRates;
    }
}

class FileHandler {
    public static void loadExchangeRates(CurrencyConverter converter, String filename) throws FileNotFoundException {
        File file = new File(filename);
        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split(":");

            if (parts.length == 2) {
                String currency = parts[0];
                double rate = Double.parseDouble(parts[1]);
                converter.addExchangeRate(currency, rate);
            }
        }
        scanner.close();
    }

    public static void saveExchangeRates(CurrencyConverter converter, String filename) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        for (Map.Entry<String, Double> entry : converter.getExchangeRates().entrySet()) {
            writer.write(entry.getKey() + ":" + entry.getValue());
            writer.newLine();
        }
        writer.close();
    }
}

public class CurrencyConverterGUI extends JFrame {
    private CurrencyConverter converter;
    private JTextField fromCurrencyField, toCurrencyField, amountField, rateField, currencyCodeField;
    private JLabel resultLabel;

    public CurrencyConverterGUI() {
        converter = new CurrencyConverter();
        String filename = "rates.txt";

        // Load initial exchange rates
        try {
            FileHandler.loadExchangeRates(converter, filename);
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "rates.txt file not found.");
        }

        setTitle("Currency Converter");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(7, 2));

        // UI Components for Conversion
        add(new JLabel("From Currency:"));
        fromCurrencyField = new JTextField();
        add(fromCurrencyField);

        add(new JLabel("To Currency:"));
        toCurrencyField = new JTextField();
        add(toCurrencyField);

        add(new JLabel("Amount:"));
        amountField = new JTextField();
        add(amountField);

        JButton convertButton = new JButton("Convert");
        add(convertButton);

        resultLabel = new JLabel("Converted amount: ");
        add(resultLabel);

        // UI Components for Adding/Updating Rates
        add(new JLabel("Add/Update Currency Code:"));
        currencyCodeField = new JTextField();
        add(currencyCodeField);

        add(new JLabel("Exchange Rate:"));
        rateField = new JTextField();
        add(rateField);

        JButton updateRateButton = new JButton("Add/Update Rate");
        add(updateRateButton);

        // Action Listeners
        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String fromCurrency = fromCurrencyField.getText().toUpperCase();
                    String toCurrency = toCurrencyField.getText().toUpperCase();
                    double amount = Double.parseDouble(amountField.getText());
                    double convertedAmount = converter.convert(fromCurrency, toCurrency, amount);
                    resultLabel.setText("Converted amount: " + convertedAmount + " " + toCurrency);
                    
                    // Close the GUI after conversion
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(CurrencyConverterGUI.this, "Error: " + ex.getMessage());
                }
            }
        });

        updateRateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String currency = currencyCodeField.getText().toUpperCase();
                    double rate = Double.parseDouble(rateField.getText());
                    converter.addExchangeRate(currency, rate);
                    FileHandler.saveExchangeRates(converter, filename);
                    JOptionPane.showMessageDialog(CurrencyConverterGUI.this, "Exchange rate updated!");

                    // Close the GUI after updating
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(CurrencyConverterGUI.this, "Error: " + ex.getMessage());
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CurrencyConverterGUI().setVisible(true);
        });
    }
}
