package filecrypt.ui;

import filecrypt.db.DatabaseManager;
import filecrypt.model.OperationRecord;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 * Dialog displaying recorded operations from the database with high-contrast text and tables.
 */
public class HistoryDialog extends JDialog {

    private final DatabaseManager dbManager;
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private JLabel countLabel;

    public HistoryDialog(JFrame parent) {
        super(parent, "Operation History (Database Audit Log)", true);
        this.dbManager = DatabaseManager.getInstance();
        initUI();
        loadHistoryData();
    }

    private void initUI() {
        setSize(880, 500);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.BG_HEADER);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel titleLabel = new JLabel("Audit & Operation History");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.TEXT_WHITE);

        countLabel = new JLabel("Total Records: 0");
        countLabel.setFont(UITheme.FONT_SUBTITLE);
        countLabel.setForeground(UITheme.TEXT_ACCENT);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(countLabel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Table Model & JTable
        String[] columns = {"ID", "Timestamp", "File Name", "Cipher", "Mode", "Key (Masked)", "Size", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        historyTable = new JTable(tableModel);
        historyTable.setFont(new Font("Segoe UI", Font.BOLD, 13));
        historyTable.setForeground(Color.BLACK);
        historyTable.setBackground(Color.WHITE);
        historyTable.setRowHeight(28);
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyTable.setFillsViewportHeight(true);
        historyTable.setShowGrid(true);
        historyTable.setGridColor(UITheme.BORDER_COLOR);

        JTableHeader tableHeader = historyTable.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableHeader.setBackground(new Color(226, 232, 240));
        tableHeader.setForeground(new Color(15, 23, 42));
        tableHeader.setReorderingAllowed(false);

        // Column widths
        historyTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(170);
        historyTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        historyTable.getColumnModel().getColumn(4).setPreferredWidth(90);
        historyTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        historyTable.getColumnModel().getColumn(6).setPreferredWidth(80);
        historyTable.getColumnModel().getColumn(7).setPreferredWidth(90);

        // High contrast table cell renderer
        historyTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                }

                // High contrast color coding
                if (column == 4 && value != null) {
                    String mode = value.toString();
                    if ("ENCRYPT".equalsIgnoreCase(mode)) {
                        setForeground(new Color(4, 120, 87)); // Bold dark green
                    } else if ("DECRYPT".equalsIgnoreCase(mode)) {
                        setForeground(new Color(29, 78, 216)); // Bold dark blue
                    }
                } else if (column == 7 && value != null) {
                    String status = value.toString();
                    if ("SUCCESS".equalsIgnoreCase(status)) {
                        setForeground(new Color(4, 120, 87));
                    } else {
                        setForeground(new Color(220, 38, 38)); // Bold red
                    }
                } else if (!isSelected) {
                    setForeground(new Color(15, 23, 42)); // Crisp dark slate
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Action Bar
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        bottomBar.setBackground(UITheme.BG_APP);

        JButton refreshBtn = UITheme.createButton("Refresh", UITheme.NEUTRAL_BTN, UITheme.NEUTRAL_BTN_HOVER, UITheme.TEXT_WHITE);
        refreshBtn.addActionListener(e -> loadHistoryData());

        JButton clearBtn = UITheme.createButton("Clear History", UITheme.DANGER, UITheme.DANGER_HOVER, UITheme.TEXT_WHITE);
        clearBtn.addActionListener(e -> clearHistoryData());

        JButton closeBtn = UITheme.createSecondaryButton("Close");
        closeBtn.addActionListener(e -> dispose());

        bottomBar.add(refreshBtn);
        bottomBar.add(clearBtn);
        bottomBar.add(closeBtn);

        add(bottomBar, BorderLayout.SOUTH);
    }

    public void loadHistoryData() {
        tableModel.setRowCount(0);
        List<OperationRecord> records = dbManager.getAllOperations();
        for (OperationRecord r : records) {
            tableModel.addRow(new Object[]{
                    r.getId(),
                    r.getTimestamp(),
                    r.getFileName(),
                    r.getCipherType(),
                    r.getOperationMode(),
                    r.getKeyUsed(),
                    r.getFileSize(),
                    r.getStatus()
            });
        }
        countLabel.setText("Total Records: " + records.size());
    }

    private void clearHistoryData() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete all operation history logs from the database?",
                "Confirm Clear History",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            if (dbManager.clearAllOperations()) {
                loadHistoryData();
                JOptionPane.showMessageDialog(this, "History successfully cleared.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to clear history from database.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
