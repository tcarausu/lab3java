package ui;

import ui.model.ColumnValue;
import ui.model.ID3Element;
import ui.model.LabelForColumn;
import ui.model.LeafColValue;
import ui.utils.Constant;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import static ui.lab3.Lab3.*;

public class Solution {
    private static StringBuilder consoleDisplay = new StringBuilder();

    private static double generalEntropy;
    private static double depth = 0;
    private static double nrOfPositiveLabels;
    private static double nrOfNegativeLabels;
    private static double totalNrOfLabelEntropy;
    private static String positiveLabel;
    private static String negativeLabel;

    private static double columnIG;
    private static ColumnValue columnForSubsequentUse;
    private static LeafColValue leafColValueWith0OrLowestEntropy;
    private static LinkedList<ID3Element> id3Elements = new LinkedList<>();
    private static LinkedList<ID3Element> listToTest = new LinkedList<>();
    private static final DecimalFormat newFormat = new DecimalFormat("#.###");

    public static void main(String... args) throws FileNotFoundException {
        id3Elements = retrieveFileData(new File(Constant.volleyball));
//        retrieveFileData(new File(Constant.titanic_train_categorical));

        setupModelDataTable();
        startTraining();

        System.out.println(consoleDisplay);
    }

    private static void startTraining() {
        generalEntropy = getLabelEntropy();
        positiveLabel = getPositiveLabel();
        negativeLabel = getNegativeLabel();
        nrOfPositiveLabels = getNrOfPositiveLabel();
        nrOfNegativeLabels = getNrOfNegativeLabel();
        totalNrOfLabelEntropy = getTotalNrOfLabelEntropy();

        //ReduceHashmapToUsableModels
        LinkedList<ColumnValue> tree = setID3ElementsTree();
        setColumnIGForEachColumn(tree);

        LinkedList<ColumnValue> subsequentDataset = new LinkedList<>();
        LinkedHashMap<String, Double> columnWithValue = new LinkedHashMap<>();

        for (LeafColValue leafToTest : columnForSubsequentUse.getLeafValues()) {
            if(leafToTest.getLabelValues().getFirst().getEntropyOfColumn()!=0){
                for (int i = 1; i < id3Elements.size(); i++) {
                    ID3Element element = id3Elements.get(i);
                    if (element.getId3FedElements().contains(leafToTest.getColumnValue())) {
                        listToTest.add(element);
                    }
                }
                setElementsToColumnWithValue(columnWithValue, leafToTest, tree, subsequentDataset);

                //moving Onto Next Leaf
                listToTest = new LinkedList<>();
                columnWithValue = new LinkedHashMap<>();
                subsequentDataset = new LinkedList<>();
            }else{
                //leaf that has the 0 as entropy of testing Main Column
                String s ="s";
            }

        }

    }

    private static void retrieveAndSetCurrentColumnIG(LeafColValue leafToTest, ColumnValue currentColumn) {
        double testingLeafEntropy = leafToTest.getLabelValues().getLast().getEntropyOfColumn();

        for (LeafColValue leafForIG : currentColumn.getLeafValues()) {
            LabelForColumn firsVal = leafForIG.getLabelValues().getFirst();
            double firsValNrOfEl = firsVal.getNrOfSimilarElements();
            double leafEntropy;
            double leafToTestTotalEntropy = leafToTest.getLabelValues().getFirst().getNrOfSimilarElements()
                    + leafToTest.getLabelValues().getLast().getNrOfSimilarElements();
            if (leafForIG.getLabelValues().size() > 1) {
                LabelForColumn secondVal = leafForIG.getLabelValues().getLast();
                double secondValNrOfEl = secondVal.getNrOfSimilarElements();
                double total = secondValNrOfEl + firsValNrOfEl;
                if (firsVal.getLabel().equals(positiveLabel)) {
                    leafEntropy = entropyOfLabelDataset(firsValNrOfEl, secondValNrOfEl, total);
                } else {
                    leafEntropy = entropyOfLabelDataset(secondValNrOfEl, firsValNrOfEl, total);
                }
                columnIG = informationGainIG(testingLeafEntropy, firsValNrOfEl, secondValNrOfEl, leafEntropy, leafToTestTotalEntropy);
                testingLeafEntropy = columnIG;
            } else if (leafForIG.getLabelValues().size() == 1) {
                leafEntropy = entropyOfLabelDataset(firsValNrOfEl, 0, firsValNrOfEl);
                columnIG = informationGainIG(testingLeafEntropy, firsValNrOfEl, 0, leafEntropy, leafToTestTotalEntropy);
                testingLeafEntropy = columnIG;
            }
        }
    }

    private static void setColumnWithValue(LinkedHashMap<String, Double> columnWithValue, LeafColValue leafToTest) {
        for (ID3Element currentValue : listToTest) {
            //ignore last element = label column
            for (int i = 0; i < currentValue.getId3FedElements().size() - 1; i++) {
                String label = currentValue.getId3FedElements().getLast();
                String columnValueToUseOrIgnore = currentValue.getId3FedElements().get(i);
                if (columnValueToUseOrIgnore.equals(leafToTest.getColumnValue())) continue;

                if (!columnWithValue.containsKey(columnValueToUseOrIgnore + "|" + label)) {
                    columnWithValue.put(columnValueToUseOrIgnore + "|" + label, 1.0);
                } else {
                    for (Map.Entry<String, Double> currentElement : columnWithValue.entrySet()) {
                        String colV = currentElement.getKey();
                        if (colV.contains(columnValueToUseOrIgnore)) {
                            double nrOfEl = currentElement.getValue();
                            nrOfEl++;
                            columnWithValue.put(columnValueToUseOrIgnore + "|" + label, nrOfEl);
                            break;
                        }
                    }
                }
            }
        }
    }

    private static void setColumnIGForEachColumn(LinkedList<ColumnValue> tree) {
        for (ColumnValue column : tree) {
            column.setColumnInformationGain(generalEntropy);
            LinkedList<LeafColValue> leaves = column.getLeafValues();
            for (LeafColValue leaf : leaves) {
                //doesn't matter because both have same "entropy"
                double columnEntropy = leaf.getLabelValues().getFirst().getEntropyOfColumn();
                if (columnEntropy == 0) {
                    setColumnForSubsequentUse(column);
                    leafColValueWith0OrLowestEntropy = leaf;
                    consoleDisplay.append(depth).append(": ").append(column.getColumnName());
                    depth++;
                }
                double firstVal = leaf.getLabelValues().getFirst().getNrOfSimilarElements();
                double secondVal = leaf.getLabelValues().getLast().getNrOfSimilarElements();
                columnIG = informationGainIG(column.getColumnInformationGain(), firstVal, secondVal, columnEntropy, totalNrOfLabelEntropy);
                columnIG = Double.parseDouble(newFormat.format(columnIG));
                column.setColumnInformationGain(columnIG);
            }
        }
    }

    private static void setColumnForSubsequentUse(ColumnValue column) {
        columnForSubsequentUse = column;
    }

    private static void setElementsToColumnWithValue(LinkedHashMap<String, Double> columnWithValue,
                                                     LeafColValue leafToTest, LinkedList<ColumnValue> tree, LinkedList<ColumnValue> subsequentDataset) {
        LinkedList<LeafColValue> subsequentDatasetLeaf = new LinkedList<>();
        LinkedList<LabelForColumn> subsequentDatasetLabelForColumn = new LinkedList<>();
        setColumnWithValue(columnWithValue, leafToTest);

//        LabelForColumn labelForColumnToAdd;
        ColumnValue currentColumn = new ColumnValue();
        ColumnValue parentColumn = new ColumnValue();

        setColumnToSubset(columnWithValue, leafToTest, tree, subsequentDataset, subsequentDatasetLeaf, subsequentDatasetLabelForColumn, currentColumn, parentColumn);
    }

    private static void setColumnToSubset(LinkedHashMap<String, Double> columnWithValue,
                                          LeafColValue leafToTest, LinkedList<ColumnValue> tree,
                                          LinkedList<ColumnValue> subsequentDataset, LinkedList<LeafColValue> subsequentDatasetLeaf,
                                          LinkedList<LabelForColumn> subsequentDatasetLabelForColumn, ColumnValue currentColumn, ColumnValue parentColumn) {
        LabelForColumn labelForColumnToAdd;
        for (ColumnValue treeColumn : tree) {
            if (treeColumn.equals(columnForSubsequentUse)) {
                parentColumn = columnForSubsequentUse;
                continue;
            }
            if (!treeColumn.equals(columnForSubsequentUse)) {
                currentColumn.setColumnName(treeColumn.getColumnName());
                currentColumn.setParentColumn(parentColumn);
                for (LeafColValue columnToAdd : treeColumn.getLeafValues()) {
                    for (Map.Entry<String, Double> entry : columnWithValue.entrySet()) {
                        if (entry.getKey().contains(columnToAdd.getColumnValue())) {
                            String[] colAndLabel = entry.getKey().split("\\|");
                            LeafColValue leafToAdd = new LeafColValue();

                            nextEntry:
                            for (Map.Entry<String, Double> entryToCompareTo : columnWithValue.entrySet()) {
                                if (entryToCompareTo.getKey().equals(entry.getKey())) {
                                    String[] colAndLabelTested = entry.getKey().split("\\|");
                                    if (colAndLabel[0].equals(colAndLabelTested[0])) {
                                        if (colAndLabel[1].equals(colAndLabelTested[1])) {
                                            //set Entropy 0 until Updated Properly
                                            labelForColumnToAdd = new LabelForColumn(colAndLabel[1], entry.getValue(), 0);
                                            subsequentDatasetLabelForColumn.add(labelForColumnToAdd);

                                            leafToAdd.setColumnValue(colAndLabel[0]);
                                            leafToAdd.setLabelValues(subsequentDatasetLabelForColumn);
                                            subsequentDatasetLeaf.add(leafToAdd);

                                            if (currentColumn.getLeafValues().size() == 0) {
                                                currentColumn.setLeafValues(subsequentDatasetLeaf);
                                            } else {
                                                if (!currentColumn.getLeafValues().contains(subsequentDatasetLeaf.getFirst())) {
                                                    for (LeafColValue leaf : currentColumn.getLeafValues()) {
                                                        String subC = subsequentDatasetLeaf.getFirst().getColumnValue();
                                                        if (leaf.getColumnValue().equals(subC)) {
                                                            LinkedList<LabelForColumn> similarColumns = currentColumn.getLeafValues().getFirst().getLabelValues();
                                                            similarColumns.addAll(subsequentDatasetLabelForColumn);
                                                            currentColumn.getLeafValues().getFirst().setLabelValues(similarColumns);
                                                            subsequentDatasetLeaf = new LinkedList<>();
                                                            subsequentDatasetLabelForColumn = new LinkedList<>();
                                                        } else {
                                                            currentColumn.getLeafValues().addAll(subsequentDatasetLeaf);
                                                        }
                                                        break nextEntry;
                                                    }
                                                }
                                            }
                                            subsequentDatasetLeaf = new LinkedList<>();
                                            subsequentDatasetLabelForColumn = new LinkedList<>();
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                retrieveAndSetCurrentColumnIG(leafToTest, currentColumn);

                currentColumn.setColumnInformationGain(columnIG);
                subsequentDataset.add(currentColumn);
                currentColumn = new ColumnValue();
                subsequentDatasetLeaf = new LinkedList<>();
                subsequentDatasetLabelForColumn = new LinkedList<>();
            }
        }
    }

}
