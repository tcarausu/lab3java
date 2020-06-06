package ui.model;

import java.util.LinkedList;

public class ColumnValue {
    String columnName;
    LinkedList<LeafColValue> leafValues = new LinkedList<>();
    double columnInformationGain;
    ColumnValue parentColumn;
    public ColumnValue() {
    }

    public ColumnValue(String columnName, LinkedList<LeafColValue> leafValues) {
        this.columnName = columnName;
        this.leafValues = leafValues;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public LinkedList<LeafColValue> getLeafValues() {
        return leafValues;
    }

    public void setLeafValues(LinkedList<LeafColValue> leafValues) {
        this.leafValues = leafValues;
    }

    public double getColumnInformationGain() {
        return columnInformationGain;
    }

    public void setColumnInformationGain(double columnInformationGain) {
        this.columnInformationGain = columnInformationGain;
    }

    public ColumnValue getParentColumn() {
        return parentColumn;
    }

    public void setParentColumn(ColumnValue parentColumn) {
        this.parentColumn = parentColumn;
    }
}
