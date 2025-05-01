
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class HistoryDialog extends JDialog {

    private JTable historyTable;

    public HistoryDialog(JFrame parent) {
        super(parent, "Conversion History", true);
        setSize(600, 400);
        setLocationRelativeTo(parent);

        // Create table model
        String[] columns = {"Date", "From", "To", "Amount", "Converted Amount"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Create and configure table
        historyTable = new JTable(model);
        historyTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(historyTable);

        // Load data
        loadHistoryData();

        // Add to dialog
        add(scrollPane);
    }

    private void loadHistoryData() {
        DefaultTableModel model = (DefaultTableModel) historyTable.getModel();
        model.setRowCount(0);

        DecimalFormat df = new DecimalFormat("#,##0.00");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        List<ConversionRecord> history = DatabaseHandler.getConversionHistory();
        for (ConversionRecord record : history) {
            model.addRow(new Object[]{
                sdf.format(record.getConversionDate()),
                record.getFromCurrency(),
                record.getToCurrency(),
                df.format(record.getAmount()),
                df.format(record.getConvertedAmount())
            });
        }
    }
}
