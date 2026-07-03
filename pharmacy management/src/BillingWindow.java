import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class BillingWindow {

    public static void open(JFrame parentFrame, List<Medicine> medicines, Runnable onStockUpdated) {
        JFrame billFrame = new JFrame("🧾 Pharmacy Billing System");
        billFrame.setSize(700, 500);
        billFrame.setLocationRelativeTo(parentFrame);
        billFrame.getContentPane().setBackground(new Color(245, 250, 255));

        //  Table setup
        String[] billCols = {"Medicine", "Quantity", "Price", "Total"};
        DefaultTableModel billModel = new DefaultTableModel(billCols, 0);
        JTable billTable = new JTable(billModel);
        JScrollPane billScroll = new JScrollPane(billTable);

        //  Top panel (Add to Bill)
        JPanel topPanel = new JPanel(new FlowLayout());

        //  Empty combo box initially (no default selection)
        JComboBox<String> medBox = new JComboBox<>();
        medBox.setEditable(true);






        
        JTextField editor = (JTextField) medBox.getEditor().getEditorComponent();
        JTextField qtyField = new JTextField(5);
        JButton addToBillBtn = new JButton("Add to Bill");
        styleButton(addToBillBtn);

        topPanel.add(new JLabel("Select Medicine:"));
        topPanel.add(medBox);
        topPanel.add(new JLabel("Quantity:"));
        topPanel.add(qtyField);
        topPanel.add(addToBillBtn);

        //  Bottom panel (Save Bill)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel totalLabel = new JLabel("Grand Total: ₹0.0");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JButton saveBillBtn = new JButton("Save Bill");
        styleButton(saveBillBtn);
        bottomPanel.add(totalLabel);
        bottomPanel.add(saveBillBtn);

        billFrame.add(topPanel, BorderLayout.NORTH);
        billFrame.add(billScroll, BorderLayout.CENTER);
        billFrame.add(bottomPanel, BorderLayout.SOUTH);

        final double[] grandTotal = {0.0};

        //  Smart autocomplete feature (with no default item)
        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = editor.getText().trim().toLowerCase();
                medBox.hidePopup();
                medBox.removeAllItems();

                if (text.isEmpty()) {
                    return; // Leave empty if user hasn't typed anything
                }

                // Filter matching medicine names
                List<String> filtered = medicines.stream()
                        .map(m -> m.name)
                        .filter(name -> name.toLowerCase().contains(text))
                        .collect(Collectors.toList());

                if (filtered.isEmpty()) return;

                for (String s : filtered) {
                    medBox.addItem(s);
                }

                editor.setText(text);
                medBox.showPopup();
            }
        });

        //  Add to Bill logic
        addToBillBtn.addActionListener(e -> {
            try {
                String medName = editor.getText().trim();
                if (medName.isEmpty()) {
                    JOptionPane.showMessageDialog(billFrame, "Please enter or select a medicine!");
                    return;
                }

                int qty = Integer.parseInt(qtyField.getText());
                Medicine selectedMed = medicines.stream()
                        .filter(m -> m.name.equalsIgnoreCase(medName))
                        .findFirst()
                        .orElse(null);

                if (selectedMed == null) {
                    JOptionPane.showMessageDialog(billFrame, "Medicine not found!");
                    return;
                }
                if (qty > selectedMed.quantity) {
                    JOptionPane.showMessageDialog(billFrame, "Not enough stock!");
                    return;
                }

                double total = qty * selectedMed.price;
                billModel.addRow(new Object[]{medName, qty, selectedMed.price, total});
                grandTotal[0] += total;
                totalLabel.setText("Grand Total: ₹" + grandTotal[0]);

                //  Update stock immediately
                selectedMed.quantity -= qty;
                saveToFile(medicines);
                if (onStockUpdated != null) onStockUpdated.run();

                qtyField.setText("");
                editor.setText("");
                medBox.removeAllItems();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(billFrame, "Invalid quantity!");
            }
        });

        //  Save Bill logic
        saveBillBtn.addActionListener(e -> {
            if (billModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(billFrame, "No items in bill!");
                return;
            }
            try {
                String billFile = "Bill_" + System.currentTimeMillis() + ".txt";
                BufferedWriter writer = new BufferedWriter(new FileWriter(billFile));
                writer.write("========= Pharmacy Bill =========\n");
                writer.write(String.format("%-20s %-10s %-10s %-10s\n", "Medicine", "Qty", "Price", "Total"));
                writer.write("--------------------------------------------\n");

                for (int i = 0; i < billModel.getRowCount(); i++) {
                    writer.write(String.format("%-20s %-10s %-10s %-10s\n",
                            billModel.getValueAt(i, 0),
                            billModel.getValueAt(i, 1),
                            billModel.getValueAt(i, 2),
                            billModel.getValueAt(i, 3)));
                }

                writer.write("--------------------------------------------\n");
                writer.write("Grand Total: ₹" + grandTotal[0] + "\n");
                writer.write("Date: " + new java.util.Date() + "\n");
                writer.close();

                saveToFile(medicines);
                if (onStockUpdated != null) onStockUpdated.run();

                JOptionPane.showMessageDialog(billFrame, "Bill saved as " + billFile);
                billModel.setRowCount(0);
                totalLabel.setText("Grand Total: ₹0.0");
                grandTotal[0] = 0.0;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        billFrame.setVisible(true);
    }

    //  Button styling helper
    private static void styleButton(JButton btn) {
        btn.setBackground(new Color(70, 130, 180));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    //  Save updated stock to file
    private static void saveToFile(List<Medicine> medicines) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("medicines.csv"))) {
            for (Medicine med : medicines) {
                writer.write(med.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}