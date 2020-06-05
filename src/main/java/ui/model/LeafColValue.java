package ui.model;

import java.util.LinkedList;

public class LeafColValue {
    String columnValue;
    LinkedList<LabelForColumn> labelValues = new LinkedList<>();

    public LeafColValue() {
    }

    public LeafColValue(String columnValue, LinkedList<LabelForColumn> labelValues) {
        this.columnValue = columnValue;
        this.labelValues = labelValues;
    }

    public String getColumnValue() {
        return columnValue;
    }

    public void setColumnValue(String columnValue) {
        this.columnValue = columnValue;
    }

    public LinkedList<LabelForColumn> getLabelValues() {
        return labelValues;
    }

    public void setLabelValues(LinkedList<LabelForColumn> labelValues) {
        this.labelValues = labelValues;
    }

}
