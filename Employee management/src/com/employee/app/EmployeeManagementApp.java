package com.employee.app;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class EmployeeManagementApp extends JFrame {
    private final JTextField nameField = new JTextField(20);
    private final JTextField emailField = new JTextField(20);
    private final JTextField deptField = new JTextField(20);
    private final JButton addButton = new JButton("Add Employee");
    private final JButton showButton = new JButton("Show Employees");

    private final EmployeeDAO dao = new EmployeeDAO();

    public EmployeeManagementApp() {
        super("Employee Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 250);
        setLocationRelativeTo(null);

        // Modern look
        try { UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); } catch (Exception ignored) {}

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(new TitledBorder("Employee Details"));
        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Department:"));
        formPanel.add(deptField);

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(showButton);

        // Layout
        setLayout(new BorderLayout(10, 10));
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Actions
        addButton.addActionListener(this::onAdd);
        showButton.addActionListener(e -> showEmployeesDialog());
    }

    // Add Employee
    private void onAdd(ActionEvent e) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String dept = deptField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || dept.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields required!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Invalid email!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Employee emp = new Employee(name, email, dept);
            int id = dao.add(emp);
            nameField.setText(""); emailField.setText(""); deptField.setText("");
            JOptionPane.showMessageDialog(this, "Employee added with ID: " + id);
        } catch (SQLException ex) {
            showError("Failed to add employee", ex);
        }
    }

    // Validate Email
    private boolean isValidEmail(String email) {
        return Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", email);
    }

    // Show Error
    private void showError(String msg, Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Show Employees Dialog
    private void showEmployeesDialog() {
        JDialog dialog = new JDialog(this, "Employees", true);
        EmployeeTableModel model = new EmployeeTableModel();
        JTable table = new JTable(model);

        JButton refreshBtn = new JButton("Refresh");
        JButton deleteBtn = new JButton("Delete Selected");

        JPanel btns = new JPanel();
        btns.add(refreshBtn); btns.add(deleteBtn);

        dialog.setLayout(new BorderLayout(10, 10));
        dialog.add(new JScrollPane(table), BorderLayout.CENTER);
        dialog.add(btns, BorderLayout.SOUTH);

        // Reload data
        Runnable reload = () -> {
            try { model.setEmployees(dao.listAll()); }
            catch (SQLException ex) { showError("Failed to load employees", ex); }
        };
        reload.run();

        // Refresh button
        refreshBtn.addActionListener(ev -> reload.run());

        // Delete button
        deleteBtn.addActionListener(ev -> {
            int row = table.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(dialog, "Select a row first!"); return; }
            Employee emp = model.getEmployeeAt(row);
            int confirm = JOptionPane.showConfirmDialog(dialog, "Delete employee ID " + emp.getId() + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    if (dao.deleteById(emp.getId())) { reload.run(); JOptionPane.showMessageDialog(dialog, "Deleted!"); }
                } catch (SQLException ex) { showError("Delete failed", ex); }
            }
        });

        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EmployeeManagementApp().setVisible(true));
    }
}
