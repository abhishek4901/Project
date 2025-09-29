package com.employee.app;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class EmployeeTableModel extends AbstractTableModel {
    private final String[] columns = {"ID", "Name", "Email", "Department"};
    private final List<Employee> data = new ArrayList<>();

    public void setEmployees(List<Employee> employees) {
        data.clear();
        data.addAll(employees);
        fireTableDataChanged();
    }

    public Employee getEmployeeAt(int row) {
        if (row < 0 || row >= data.size()) return null;
        return data.get(row);
    }

    @Override
    public int getRowCount() { return data.size(); }

    @Override
    public int getColumnCount() { return columns.length; }

    @Override
    public String getColumnName(int column) { return columns[column]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Employee e = data.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> e.getId();
            case 1 -> e.getName();
            case 2 -> e.getEmail();
            case 3 -> e.getDepartment();
            default -> null;
        };
    }
}
