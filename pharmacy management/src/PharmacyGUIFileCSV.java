import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

class Medicine {
    int id;
    String name;
    double price;
    int quantity;
    String category;

    Medicine(int id, String name, double price, int quantity, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
    }

    @Override
    public String toString() {
        return id + "," + name + "," + price + "," + quantity + "," + category;
    }

    static Medicine fromString(String line) {
        String[] parts = line.split(",");
        return new Medicine(
                Integer.parseInt(parts[0]),
                parts[1],
                Double.parseDouble(parts[2]),
                Integer.parseInt(parts[3]),
                parts.length > 4 ? parts[4] : "Other"
        );
    }
}

public class PharmacyGUIFileCSV {
    private static final String FILE_NAME = "medicines.csv";
    private static List<Medicine> medicines = new ArrayList<>();
    private static DefaultTableModel tableModel;

    private static JPanel totalCard, lowStockCard, categoryCard;
    private static JLabel totalValueLabel, lowStockValueLabel, categoryValueLabel;
    private static JTable table;

    public static void main(String[] args) {
        loadFromFile();

        JFrame frame = new JFrame("💊 Pharmacy Management System");
        frame.setSize(1000, 650);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(240, 248, 255));

        //  Dashboard
        totalCard = createCard("Total Medicines", String.valueOf(medicines.size()), new Color(70, 130, 180));
        lowStockCard = createCard("Low Stock (<10)", String.valueOf(countLowStock()), new Color(255, 99, 71));
        categoryCard = createCard("Categories", String.valueOf(countCategories()), new Color(60, 179, 113));

        JPanel dashboardPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        dashboardPanel.setBackground(new Color(230, 240, 250));
        dashboardPanel.add(totalCard);
        dashboardPanel.add(lowStockCard);
        dashboardPanel.add(categoryCard);
        frame.add(dashboardPanel, BorderLayout.NORTH);

        // 🧾 Table setup
        String[] columns = {"ID", "Name", "Price", "Quantity", "Category"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                int qty = Integer.parseInt(getValueAt(row, 3).toString());
                c.setBackground(qty < 10 ? new Color(255, 228, 225) : Color.WHITE);
                return c;
            }
        };

        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        //  Input Panel
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.setBackground(new Color(245, 250, 255));

        JTextField idField = new JTextField(10);
        JTextField nameField = new JTextField(10);
        JTextField priceField = new JTextField(10);
        JTextField qtyField = new JTextField(10);

        String[] categories = {"Tablet", "Capsule", "Syrup", "Injection", "Ointment", "Drops", "Powder", "Gel", "Spray", "Other"};
        JComboBox<String> categoryBox = new JComboBox<>(categories);

        JButton addBtn = styledButton("Add");
        JButton updateBtn = styledButton("Update");
        JButton deleteBtn = styledButton("Delete");
        JButton billingBtn = styledButton("Billing");

        inputPanel.add(new JLabel("ID:"));
        inputPanel.add(idField);
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Price:"));
        inputPanel.add(priceField);
        inputPanel.add(new JLabel("Quantity:"));
        inputPanel.add(qtyField);
        inputPanel.add(new JLabel("Category:"));
        inputPanel.add(categoryBox);

        inputPanel.add(addBtn);
        inputPanel.add(updateBtn);
        inputPanel.add(deleteBtn);
        inputPanel.add(billingBtn);

        frame.add(inputPanel, BorderLayout.SOUTH);

        //  Search & Filter Panel
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.setBackground(new Color(235, 245, 255));
        JTextField searchField = new JTextField(15);
        JButton resetBtn = styledButton("Reset");
        JComboBox<String> filterBox = new JComboBox<>(new String[]{"All", "Tablet", "Capsule", "Syrup", "Injection", "Ointment", "Other"});

        searchPanel.add(new JLabel("Search (ID/Name):"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Filter by Category:"));
        searchPanel.add(filterBox);
        searchPanel.add(resetBtn);

        frame.add(searchPanel, BorderLayout.BEFORE_FIRST_LINE);

        //  Load Table Data
        refreshTable();

        //  Add
        addBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText());
                String name = nameField.getText();
                double price = Double.parseDouble(priceField.getText());
                int qty = Integer.parseInt(qtyField.getText());
                String category = categoryBox.getSelectedItem().toString();

                Medicine med = new Medicine(id, name, price, qty, category);
                medicines.add(med);
                saveToFile();
                refreshTable();
                JOptionPane.showMessageDialog(frame, "Medicine Added Successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Invalid Input!");
            }
        });

        //  Update
        updateBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(frame, "Select a medicine to update!");
                return;
            }
            try {
                int id = Integer.parseInt(idField.getText());
                String name = nameField.getText();
                double price = Double.parseDouble(priceField.getText());
                int qty = Integer.parseInt(qtyField.getText());
                String category = categoryBox.getSelectedItem().toString();

                medicines.set(selectedRow, new Medicine(id, name, price, qty, category));
                saveToFile();
                refreshTable();
                JOptionPane.showMessageDialog(frame, "Medicine Updated!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Invalid Input!");
            }
        });

        //  Delete
        deleteBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(frame, "Select a medicine to delete!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this medicine?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                medicines.remove(selectedRow);
                saveToFile();
                refreshTable();
                JOptionPane.showMessageDialog(frame, "Medicine Deleted!");
            }
        });

        //  Billing
        billingBtn.addActionListener(e -> {
            BillingWindow.open(frame, medicines, () -> {
                saveToFile();
                refreshTable();
            });
        });

        // Fill fields when selecting row
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    idField.setText(tableModel.getValueAt(row, 0).toString());
                    nameField.setText(tableModel.getValueAt(row, 1).toString());
                    priceField.setText(tableModel.getValueAt(row, 2).toString());
                    qtyField.setText(tableModel.getValueAt(row, 3).toString());
                    categoryBox.setSelectedItem(tableModel.getValueAt(row, 4).toString());
                }
            }
        });

        //  LIVE SEARCH (Real-time filtering)
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void updateFilter() {
                String query = searchField.getText().trim();
                if (query.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
                }
            }

            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateFilter(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateFilter(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateFilter(); }
        });

        // Filter
        filterBox.addActionListener(e -> {
            String selectedCategory = filterBox.getSelectedItem().toString();
            if (selectedCategory.equals("All")) sorter.setRowFilter(null);
            else sorter.setRowFilter(RowFilter.regexFilter(selectedCategory, 4));
        });

        // Reset
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            filterBox.setSelectedIndex(0);
            sorter.setRowFilter(null);
        });

        frame.setVisible(true);
    }

    //  Dashboard helpers
    private static int countLowStock() {
        return (int) medicines.stream().filter(m -> m.quantity < 10).count();
    }

    private static long countCategories() {
        return medicines.stream().map(m -> m.category).distinct().count();
    }

    private static void refreshDashboard() {
        totalValueLabel.setText(String.valueOf(medicines.size()));
        lowStockValueLabel.setText(String.valueOf(countLowStock()));
        categoryValueLabel.setText(String.valueOf(countCategories()));
    }

    private static void refreshTable() {
        medicines.clear();
        loadFromFile();
        tableModel.setRowCount(0);
        for (Medicine m : medicines) {
            tableModel.addRow(new Object[]{m.id, m.name, m.price, m.quantity, m.category});
        }
        refreshDashboard();
    }

    //  Dashboard cards
    private static JPanel createCard(String title, String value, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(200, 80));
        panel.setBackground(color);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.SOUTH);

        if (title.contains("Total")) totalValueLabel = valueLabel;
        else if (title.contains("Low")) lowStockValueLabel = valueLabel;
        else if (title.contains("Categories")) categoryValueLabel = valueLabel;

        return panel;
    }

    private static JButton styledButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(70, 130, 180));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    //  File operations
    private static void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Medicine med : medicines) {
                writer.write(med.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                medicines.add(Medicine.fromString(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}