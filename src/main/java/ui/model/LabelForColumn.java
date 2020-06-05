package ui.model;

public class LabelForColumn {
    double nrOfSimilarElements ;
    double entropyOfColumn ;
    String  label ;

    public LabelForColumn() {
    }

    public LabelForColumn(double nrOfSimilarElements, double entropyOfColumn, String label) {
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

