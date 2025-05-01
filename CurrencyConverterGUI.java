
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.text.DecimalFormat;
import org.json.JSONObject;

public class CurrencyConverterGUI extends JFrame {

    private JComboBox<String> fromCurrencyCombo, toCurrencyCombo;
    private JTextField amountField;
    private JLabel resultLabel;
    private JButton convertButton, historyButton;
    private final Color BACKGROUND_COLOR = new Color(45, 52, 54);
    private final Color BUTTON_COLOR = new Color(52, 152, 219);
    private final Color TEXT_COLOR = new Color(236, 240, 241);

    public CurrencyConverterGUI() {
        setTitle("Currency Converter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 600);
        setLocationRelativeTo(null);

        // Create gradient panel
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                Color color1 = new Color(41, 128, 185);
                Color color2 = new Color(142, 68, 173);
                GradientPaint gp = new GradientPaint(0, 0, color1, w, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create components with styling
        fromCurrencyCombo = createStyledComboBox();
        toCurrencyCombo = createStyledComboBox();
        amountField = createStyledTextField();
        convertButton = createStyledButton("Convert");
        historyButton = createStyledButton("View History");
        resultLabel = createStyledLabel("");

        // Add components to panels
        mainPanel.add(createStyledLabel("From Currency:"));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(fromCurrencyCombo);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        mainPanel.add(createStyledLabel("To Currency:"));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(toCurrencyCombo);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        mainPanel.add(createStyledLabel("Amount:"));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(amountField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        mainPanel.add(convertButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(historyButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(resultLabel);

        // Add action listeners
        convertButton.addActionListener(e -> convertCurrency());
        historyButton.addActionListener(e -> showHistory());

        add(mainPanel);

        // Load currencies
        loadCurrencies();
    }

    private JComboBox<String> createStyledComboBox() {
        JComboBox<String> combo = new JComboBox<>();
        combo.setBackground(Color.WHITE);
        combo.setForeground(BACKGROUND_COLOR);
        combo.setFont(new Font("Arial", Font.PLAIN, 14));
        combo.setMaximumSize(new Dimension(350, 30));
        return combo;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setMaximumSize(new Dimension(350, 30));
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        return field;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                if (getModel().isPressed()) {
                    g.setColor(BUTTON_COLOR.darker());
                } else if (getModel().isRollover()) {
                    g.setColor(BUTTON_COLOR.brighter());
                } else {
                    g.setColor(BUTTON_COLOR);
                }
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setMaximumSize(new Dimension(350, 35));
        return button;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_COLOR);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void loadCurrencies() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    String[] currencies = CurrencyService.getAllCurrencies();
                    SwingUtilities.invokeLater(() -> {
                        fromCurrencyCombo.removeAllItems();
                        toCurrencyCombo.removeAllItems();
                        for (String currency : currencies) {
                            fromCurrencyCombo.addItem(currency);
                            toCurrencyCombo.addItem(currency);
                        }
                        // Set default selections
                        fromCurrencyCombo.setSelectedItem("USD");
                        toCurrencyCombo.setSelectedItem("EUR");
                    });
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(CurrencyConverterGUI.this,
                                "Error loading currencies: " + e.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    });
                }
                return null;
            }
        };
        worker.execute();
    }

    private void convertCurrency() {
        try {
            String fromCurrency = (String) fromCurrencyCombo.getSelectedItem();
            String toCurrency = (String) toCurrencyCombo.getSelectedItem();

            if (fromCurrency == null || toCurrency == null) {
                JOptionPane.showMessageDialog(this,
                        "Please select both currencies",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String amountText = amountField.getText().trim();
            if (amountText.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter an amount",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double amount = Double.parseDouble(amountText);

            // Show loading indicator
            resultLabel.setText("Converting...");
            convertButton.setEnabled(false);

            SwingWorker<Double, Void> worker = new SwingWorker<>() {
                @Override
                protected Double doInBackground() throws Exception {
                    return CurrencyService.convertCurrency(fromCurrency, toCurrency, amount);
                }

                @Override
                protected void done() {
                    try {
                        double result = get();
                        DecimalFormat df = new DecimalFormat("#,##0.00");
                        String resultText = String.format("%s %s = %s %s",
                                df.format(amount), fromCurrency,
                                df.format(result), toCurrency);
                        resultLabel.setText(resultText);

                        // Save to database
                        DatabaseHandler.saveConversion(fromCurrency, toCurrency, amount, result);
                    } catch (Exception e) {
                        resultLabel.setText("Conversion failed");
                        JOptionPane.showMessageDialog(CurrencyConverterGUI.this,
                                "Error converting currency: " + e.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        convertButton.setEnabled(true);
                    }
                }
            };
            worker.execute();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid number",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showHistory() {
        // This will be implemented in a separate HistoryDialog class
        new HistoryDialog(this).setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new CurrencyConverterGUI().setVisible(true);
        });
    }
}
