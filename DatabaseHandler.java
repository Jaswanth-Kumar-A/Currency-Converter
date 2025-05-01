
import javax.swing.JOptionPane;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {

    private static final String DB_URL = "jdbc:sqlite:c:/Users/jaswa/Documents/Currency_Converter/Currency converter/currency_converter.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            createTable();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Database initialization failed: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void createTable() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL); Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS conversions ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "from_currency TEXT NOT NULL,"
                    + "to_currency TEXT NOT NULL,"
                    + "amount REAL NOT NULL,"
                    + "converted_amount REAL NOT NULL,"
                    + "conversion_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
        }
    }

    public static void saveConversion(String fromCurrency, String toCurrency, double amount, double convertedAmount) {
        String sql = "INSERT INTO conversions (from_currency, to_currency, amount, converted_amount) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fromCurrency);
            pstmt.setString(2, toCurrency);
            pstmt.setDouble(3, amount);
            pstmt.setDouble(4, convertedAmount);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Failed to save conversion: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static List<ConversionRecord> getConversionHistory() {
        List<ConversionRecord> history = new ArrayList<>();
        String sql = "SELECT * FROM conversions ORDER BY conversion_date DESC";

        try (Connection conn = DriverManager.getConnection(DB_URL); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ConversionRecord record = new ConversionRecord();
                record.setFromCurrency(rs.getString("from_currency"));
                record.setToCurrency(rs.getString("to_currency"));
                record.setAmount(rs.getDouble("amount"));
                record.setConvertedAmount(rs.getDouble("converted_amount"));
                record.setConversionDate(rs.getTimestamp("conversion_date"));
                history.add(record);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Failed to load history: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        return history;
    }
}
