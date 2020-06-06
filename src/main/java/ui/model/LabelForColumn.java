package ui.model;

public class LabelForColumn {
    String label;
    double nrOfSimilarElements;
    double entropyOfColumn;

    public LabelForColumn() {
    }

    public LabelForColumn(String label, double nrOfSimilarElements, double entropyOfColumn) {
        this.nrOfSimilarElements = nrOfSimilarElements;
        this.entropyOfColumn = entropyOfColumn;
        this.label = label;
    }

    public double getNrOfSimilarElements() {
        return nrOfSimilarElements;
    }

    public void setNrOfSimilarElements(double nrOfSimilarElements) {
        this.nrOfSimilarElements = nrOfSimilarElements;
    }

    public double getEntropyOfColumn() {
        return entropyOfColumn;
    }

    public void setEntropyOfColumn(double entropyOfColumn) {
        this.entropyOfColumn = entropyOfColumn;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}

