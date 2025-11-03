package com.employee.app;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class EmployeeManagementApp extends JFrame {
    private final JTextField nameField = new JTextField(20);
    private final JTextField emailField = new JTextField(20);
    private final JTextField deptField = new JTextField(20);
    private final JButton addButton = new JButton("Add Employee");
    private final JButton showButton = new JButton("Show Employees");

    private final EmployeeDAO dao = new EmployeeDAO();

    // --- STYLE CONSTANTS ---
    private static final Color PRIMARY_BG = new Color(245, 245, 245); // Light Gray
    private static final Color FORM_BG = Color.WHITE; // White card background
    private static final Color ACCENT_COLOR = new Color(52, 152, 219); // Blue for secondary action
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96); // Green for primary action
    private static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 14);
    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 18);
    // -----------------------

    public EmployeeManagementApp() {
        super("Employee Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Sets the frame to full screen (maximized) upon launch
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Modern look and main frame styling
        try { UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); } catch (Exception ignored) {}
        getContentPane().setBackground(PRIMARY_BG); // Set the main background color

        // Form panel - Styling as a 'card'
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 15, 15)); // Increased gap
        formPanel.setBackground(FORM_BG);
        
        // Add a styled TitledBorder with internal padding
        formPanel.setBorder(
            BorderFactory.createCompoundBorder(
                new TitledBorder(null, "Employee Details", TitledBorder.LEFT, TitledBorder.TOP, TITLE_FONT),
                new EmptyBorder(20, 30, 20, 30) // Internal Padding
            )
        );
        
        // Add components and apply font styles to labels
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(LABEL_FONT);
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(LABEL_FONT);
        formPanel.add(emailLabel);
        formPanel.add(emailField);
        
        JLabel deptLabel = new JLabel("Department:");
        deptLabel.setFont(LABEL_FONT);
        formPanel.add(deptLabel);
        formPanel.add(deptField);

        // Buttons panel - Styling buttons and panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(PRIMARY_BG);
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Add padding

        // Style the "Add Employee" button (Primary Action)
        addButton.setBackground(SUCCESS_COLOR);
        addButton.setForeground(Color.WHITE);
        addButton.setFont(BUTTON_FONT);
        addButton.setPreferredSize(new Dimension(150, 40));

        // Style the "Show Employees" button (Secondary Action)
        showButton.setBackground(ACCENT_COLOR);
        showButton.setForeground(Color.WHITE);
        showButton.setFont(BUTTON_FONT);
        showButton.setPreferredSize(new Dimension(150, 40));
        
        buttonPanel.add(addButton);
        buttonPanel.add(showButton);

        // Layout
        setLayout(new BorderLayout(20, 20)); // Add gap between panels
        add(formPanel, BorderLayout.NORTH); // Keep form at the top
        add(buttonPanel, BorderLayout.CENTER); // Center the buttons below the form
        
        // Add main padding around the entire content
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));


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

    // Show Employees Dialog (Minor styling applied here as well for consistency)
    private void showEmployeesDialog() {
        JDialog dialog = new JDialog(this, "Employees", true);
        EmployeeTableModel model = new EmployeeTableModel();
        JTable table = new JTable(model);

        JButton refreshBtn = new JButton("Refresh");
        JButton deleteBtn = new JButton("Delete Selected");
        
        // Button styling in dialog
        refreshBtn.setBackground(ACCENT_COLOR); refreshBtn.setForeground(Color.WHITE);
        deleteBtn.setBackground(Color.RED); deleteBtn.setForeground(Color.WHITE);
        
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

        dialog.setSize(800, 500); // Increased dialog size
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EmployeeManagementApp().setVisible(true));
    }
}
