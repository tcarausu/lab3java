package ui.lab3;

import ui.model.ColumnValue;
import ui.model.ID3Element;
import ui.model.LabelForColumn;
import ui.model.LeafColValue;
import ui.utils.Constant;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static ui.utils.Lab3Utils.log2;
import static ui.utils.RegexOperator.*;

public class Lab3 {
    private static final DecimalFormat newFormat = new DecimalFormat("#.###");

    private static final LinkedHashMap<Integer, LinkedList<String>> columnListWithElements = new LinkedHashMap<>();
    private static final LinkedHashMap<Integer, String> featureAndLabelList = new LinkedHashMap<>();
    private static final LinkedHashMap<String, LinkedList<String>> columnWithAllVariables = new LinkedHashMap<>();
    private static final LinkedHashMap<String, LinkedHashMap<String, Integer>> columnWithUniqueElements = new LinkedHashMap<>();

    private static final LinkedHashMap<String, Integer> countElements = new LinkedHashMap<>();
    private static final LinkedHashMap<String, Double> countPerSetComb = new LinkedHashMap<>();
    private static final LinkedHashMap<String, Double> getNrOfCountPerSetComb = new LinkedHashMap<>();
    private static LinkedHashMap<String, Double> fullCountPerSetComb = new LinkedHashMap<>();
    private static final LinkedHashMap<String, Double> likelihoodPerSetComb = new LinkedHashMap<>();
    private static LinkedList<Double> valueWithProbability;

    private static LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>> columnWithCurrentColEntropy = new LinkedHashMap<>();
    private static LinkedHashMap<String, LinkedList<Double>> valueWithEntropy = new LinkedHashMap<>();

    private static final LinkedList<ID3Element> id3Elements = new LinkedList<>();
    private static double labelEntropy;
    private static double totalNrOfLabelEntropy;
    private static double currentColEntropy;


    private static final AtomicReference<Double> nrOfLabelYes = new AtomicReference<>((double) 0);
    private static final AtomicReference<Double> nrOfLabelNo = new AtomicReference<>((double) 0);
    private static final LinkedHashMap<String, LinkedList<Double>> labelRelativeFreq = new LinkedHashMap<>();

    private static LinkedList<LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>>> listOfMapsPerColumn = new LinkedList<>();
    private static LinkedHashMap<String, LinkedList<LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>>>> tableDataSetPerColumn = new LinkedHashMap<>();

    private static AtomicReference<Double> totalEnt;

    private static String positiveLabel;
    private static String negativeLabel;

    private static LinkedList<ColumnValue> tree = new LinkedList<>();
    private static ColumnValue currentColV;
    private static LeafColValue currentLeafColValue;
    private static LabelForColumn currentLabel;
    private static LinkedList<LabelForColumn> labelValues = new LinkedList<>();
    private static LinkedList<LeafColValue> leafColValues = new LinkedList<>();

    public static void main(String[] args) throws FileNotFoundException {
        retrieveFileData(new File(Constant.volleyball));
//        retrieveFileData(new File(Constant.titanic_train_categorical));

        setupModelDataTable();

        //ReduceHashmapToUsableModels
        setID3ElementsTree();
        consoleDisplay();
    }

    public static void setupModelDataTable() {
        getNrOfElementsForEachValuePerColumn();

        getNrByLabelCol();

        for (int i = 1; i < id3Elements.size(); i++) {
            ID3Element element = id3Elements.get(i);
            String labelCol = element.getId3FedElements().get(element.getId3FedElements().size() - 1);
            for (int j = 0; j < element.getId3FedElements().size() - 1; j++) {
                String currentCol = element.getId3FedElements().get(j);
                String currentColLikelihood = currentCol.concat("|" + labelCol);
                getPlayCountPerSetCombination(currentColLikelihood);
            }
        }

        retrieveLikelihoodOfElements();

        labelEntropy = getLabelEntropy(nrOfLabelYes.get(), nrOfLabelNo.get());
        totalNrOfLabelEntropy = nrOfLabelYes.get() + nrOfLabelNo.get();
        totalEnt = new AtomicReference<>(labelEntropy);
        setupFullCountPerSetComb();

        set0ProbabilityForEmptySetElements();

        draftDatasetTable();
    }


    private static void getPlayCountPerSetCombination(String likelihood) {
        if (!countPerSetComb.containsKey(likelihood)) {
            countPerSetComb.put(likelihood, 1.0);
        } else {
            double currentLikelihood = countPerSetComb.get(likelihood);
            countPerSetComb.put(likelihood, currentLikelihood + 1);
        }
    }

    private static void getNrByLabelCol() {
        labelRelativeFreq.forEach((key, value) -> {
            if (key.equals(positiveLabel)) {
                double totalY = value.get(0); //we saved it as 1st element
                nrOfLabelYes.set(totalY);
            } else if (key.equals(negativeLabel)) {
                double totalY = value.get(0); //we saved it as 1st element
                nrOfLabelNo.set(totalY);
            }
        });
    }

    private static void retrieveLikelihoodOfElements() {
        countPerSetComb.forEach((key, value) -> {
            double likelihoodOfLabel = 0;
            if (key.contains(positiveLabel)) {
                likelihoodOfLabel = nrOfLabelYes.get();
            } else if (key.contains(negativeLabel)) {
                likelihoodOfLabel = nrOfLabelNo.get();
            }
            double likelihoodOfSubset = getLikelihoodByPlayCount(value, likelihoodOfLabel);
            likelihoodPerSetComb.put(key, likelihoodOfSubset);
        });
    }

    private static double getLikelihoodByPlayCount(double nrToTest, double play) {
        return nrToTest / play;
    }

    private static void retrieveNrOfDistinctElementsPerColumn(AtomicInteger count, LinkedList<String> weatherCount,
                                                              String weather, String colName) {
        if (!weatherCount.contains(weather)) {
            if (!weather.equals(colName)) {
                weatherCount.add(weather);
                countElements.put(weather, count.get());
            }
        } else if (weatherCount.contains(weather)) {
            if (!countElements.containsKey(weather)) {
                countElements.put(weather, count.getAndIncrement());
            } else {
                count = new AtomicInteger(countElements.get(weather));
                count.getAndIncrement();
                countElements.put(weather, count.get());
            }
        }
    }

    private static void deriveLabelColumnProbabilities(LinkedHashMap<String, Integer> elements) {
        elements.forEach((key, value) -> {
            if (key.equals(positiveLabel)) {
                double probability = getPriorProbabilityByRelativeFrequency(value);
                valueWithProbability = new LinkedList<>();
                valueWithProbability.add(Double.valueOf(value));
                valueWithProbability.add(probability);
                labelRelativeFreq.put(key, valueWithProbability);

            } else if (key.equals(negativeLabel)) {
                double probability = getPriorProbabilityByRelativeFrequency(value);
                valueWithProbability = new LinkedList<>();
                valueWithProbability.add(Double.valueOf(value));
                valueWithProbability.add(probability);
                labelRelativeFreq.put(key, valueWithProbability);

            }
        });
    }

    private static double getPriorProbabilityByRelativeFrequency(double nrToTest) {
        //Subtracting the first line (because it contains the name of the columns)
        double nrOfElementsInTotal = id3Elements.size() - 1;
        return nrToTest / nrOfElementsInTotal;
    }

    private static void setupFullCountPerSetComb() {
        columnWithUniqueElements.forEach((fileKey, fileValue) -> {
            for (Map.Entry<String, Integer> entry : fileValue.entrySet()) {
                String entryKey = entry.getKey();

                AtomicInteger count = new AtomicInteger(1);
                countPerSetComb.forEach((existingKey, existingValue) -> {
                    String[] entryKeySet = existingKey.split("\\|");
                    String columnNameOfCountSet = entryKeySet[0];

                    if (entryKey.equals(columnNameOfCountSet)) {
                        getNrOfCountPerSetComb.put(columnNameOfCountSet, (double) count.getAndIncrement());
                    }
                });
            }
        });
    }

    private static void set0ProbabilityForEmptySetElements() {
        fullCountPerSetComb = countPerSetComb;
        int initialSize = fullCountPerSetComb.size();

        //maybe do a break each time it finds one (TBDetermined)
        getNrOfCountPerSetComb.forEach((existingKey, existingValue) -> {
            if (existingValue == 1) {
                for (Map.Entry<String, Double> entry : fullCountPerSetComb.entrySet()) {
                    String fullCKey = entry.getKey();
                    String[] entryKeySet = fullCKey.split("\\|");
                    String label = entryKeySet[1];
                    String endResult;
                    if (label.equals(positiveLabel)) {
                        endResult = fullCKey.replace("|" + label, "|" + negativeLabel);
                    } else {
                        endResult = fullCKey.replace("|" + label, "|" + positiveLabel);
                    }
                    fullCountPerSetComb.putIfAbsent(endResult, 0.0);
                    int sizeToTest = fullCountPerSetComb.size();
                    if (initialSize < sizeToTest) {
                        break;
                    }
                }
            }
        });
    }

    private static void getNrOfElementsForEachValuePerColumn() {
        final AtomicInteger[] count = {new AtomicInteger(1)};
        LinkedList<String> columnListToTest = new LinkedList<>();

        LinkedList<String> columnNames = id3Elements.get(0).getId3FedElements();
        for (int i = 0; i < columnNames.size(); i++) {
            String column = columnNames.get(i);
            featureAndLabelList.putIfAbsent(i, column);
            columnWithAllVariables.putIfAbsent(column, new LinkedList<>());
        }

        for (int i = 1; i < id3Elements.size(); i++) {
            ID3Element elementList = id3Elements.get(i);
            LinkedList<String> values = elementList.getId3FedElements();
            for (int j = 0; j < featureAndLabelList.size(); j++) {
                String column = values.get(j);
                columnListWithElements.computeIfAbsent(i - 1, k -> new LinkedList<>()).add(column);
            }
        }

        LinkedList<String> featureList = new LinkedList<>(featureAndLabelList.values());
        for (String featureOrLabel : featureList) {
            int currentColumnIndex = featureList.indexOf(featureOrLabel);

            for (LinkedList<String> currentColumnValue : columnListWithElements.values()) {
                String currentValue = currentColumnValue.get(currentColumnIndex);
                columnWithAllVariables.computeIfAbsent(featureOrLabel, k -> new LinkedList<>()).add(currentValue);
            }
        }

        LinkedList<String> arrK = new LinkedList<>(Arrays.asList(columnWithAllVariables.keySet().toArray(new String[0])));

        columnWithAllVariables.forEach((fOrLKey, fOrLValue) -> {
            for (String featureOrLabel : fOrLValue) {
                retrieveNrOfDistinctElementsPerColumn(count[0], columnListToTest, featureOrLabel, fOrLKey);
            }

            LinkedHashMap<String, Integer> localElements = new LinkedHashMap<>(countElements);
            columnWithUniqueElements.put(fOrLKey, localElements);
            count[0] = new AtomicInteger(1);
            if (fOrLKey.equals(arrK.get(arrK.size() - 1))) {
                deriveLabelColumnProbabilities(localElements);
            }
            countElements.clear();
        });

    }

    private static void generateValuesForTable(
            LinkedList<LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>>> listOfMaps) {
        columnWithCurrentColEntropy = new LinkedHashMap<>();
        valueWithEntropy = new LinkedHashMap<>();

        columnWithUniqueElements.forEach((colUnK, colUnV) -> {
            colUnV.forEach((currentCol, nrOfThem) -> {
                fullCountPerSetComb.forEach((existingKey, existingValue) -> {
                    String[] keyParts = existingKey.split("\\|");
                    String colName = keyParts[0];
                    String label = keyParts[1];
                    if (colName.equals(currentCol)) {
                        fullCountPerSetComb.forEach((findSameKey, findDiffValue) -> {
                            String[] sameKeyParts = findSameKey.split("\\|");
                            String sameColName = sameKeyParts[0];
                            String diffLabel = sameKeyParts[1];
                            if (sameColName.equals(colName)) {
                                if (label.equals(diffLabel)) {
                                    valueWithEntropy.computeIfAbsent(diffLabel, k -> new LinkedList<>()).add(existingValue);
                                } else {
                                    valueWithEntropy.computeIfAbsent(diffLabel, k -> new LinkedList<>()).add(findDiffValue);
                                }
                            }
                        });

                        Iterator<Map.Entry<String, LinkedList<Double>>> iterator = valueWithEntropy.entrySet().iterator();
                        double firstValueForEd = iterator.next().getValue().getFirst();
                        Map.Entry<String, LinkedList<Double>> lastElement = null;
                        while (iterator.hasNext()) {
                            lastElement = iterator.next();
                        }
                        double secondValueForEd = Objects.requireNonNull(lastElement).getValue().getFirst();
                        currentColEntropy = getLabelEntropy(firstValueForEd, secondValueForEd);
                        valueWithEntropy.computeIfAbsent(negativeLabel, k -> new LinkedList<>()).add(currentColEntropy);
                        currentColEntropy = getLabelEntropy(secondValueForEd, firstValueForEd);
                        valueWithEntropy.computeIfAbsent(positiveLabel, k -> new LinkedList<>()).add(currentColEntropy);
                        columnWithCurrentColEntropy.putIfAbsent(currentCol, valueWithEntropy);

                        if (!listOfMaps.contains(columnWithCurrentColEntropy)) {
                            listOfMaps.add(columnWithCurrentColEntropy);
                        }
                        valueWithEntropy = new LinkedHashMap<>();
                        columnWithCurrentColEntropy = new LinkedHashMap<>();
                    }
                });
            });
        });
    }

    private static void draftDatasetTable() {
        LinkedList<LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>>> listOfMaps = new LinkedList<>();
        generateValuesForTable(listOfMaps);

        columnWithUniqueElements.forEach((uniqueKey, uniqueValue) -> {
            for (Map.Entry<String, Integer> entry : uniqueValue.entrySet()) {
                String valueOfColumn = entry.getKey();
                colBreak:
                for (LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>> map : listOfMaps) {
                    for (Map.Entry<String, LinkedHashMap<String, LinkedList<Double>>> mapEntry : map.entrySet()) {
                        String columnName = mapEntry.getKey();
                        if (valueOfColumn.equals(columnName)) {
                            listOfMapsPerColumn.add(map);
                            break colBreak;
                        }
                    }
                }
                if (listOfMapsPerColumn.size() == uniqueValue.size()) {
                    tableDataSetPerColumn.putIfAbsent(uniqueKey, listOfMapsPerColumn);
                    listOfMapsPerColumn = new LinkedList<>();
                }
            }
        });
    }

    private static void consoleDisplay() {
        tableDataSetPerColumn.forEach((tableCol, tableVal) -> {

            for (LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>> tableV : tableVal) {
                tableV.forEach((tableValueName, labelValues) -> {
                    for (Map.Entry<String, LinkedList<Double>> entry : labelValues.entrySet()) {
                        String label = entry.getKey();
                        LinkedList<Double> values = entry.getValue();
                        double depth = values.getFirst();
                        if (depth == 0) {
                            System.out.println(0 + ":" + tableCol);
                            break;
                        }
                    }
                });
            }
        });
    }

    //AuxiliaryMethods
    private static void setPositiveAndNegativeLabelNames(ID3Element element) {
        String label = element.getId3FedElements().getLast();
        if (label.equals(labelColYes) || label.equals(labelColNo)) {
            positiveLabel = labelColYes;
            negativeLabel = labelColNo;
        } else if (label.equals(labelColTrue) || label.equals(labelColFalse)) {
            positiveLabel = labelColTrue;
            negativeLabel = labelColFalse;
        }
    }

    public static double entropyOfLabelDataset(double positiveEx, double negativeEx, double total) {
        if (negativeEx == 0.0) {
            return 0;
        }else if(positiveEx == 0.0){
            return 0;
        }
        double posLabel = log2(positiveEx / total);
        double negLabel = log2(negativeEx / total);
        return -posLabel * (positiveEx / total) - negLabel * negativeEx / total;
    }

    public static double informationGainIG(double totalEnt, double firstVal, double secondVal, double columnEntropy,double totalNrOfElements) {
        totalEnt = totalEnt - ((firstVal + secondVal) / totalNrOfElements) * columnEntropy;
        return Double.parseDouble(newFormat.format(totalEnt));
    }

    private static double getLabelEntropy(double positiveColValue, double negativeColValue) {
        if (positiveColValue != 0 && negativeColValue != 0) {
            double total = positiveColValue + negativeColValue;
            double entropy = entropyOfLabelDataset(positiveColValue, negativeColValue, total);
            return Double.parseDouble(newFormat.format(entropy));
        } else {
            return 0;
        }
    }

    public static double getLabelEntropy() {
        return labelEntropy;
    }

    public static double getNrOfPositiveLabel() {
        return nrOfLabelYes.get();
    }

    public static double getNrOfNegativeLabel() {
        return nrOfLabelNo.get();
    }

    public static String getPositiveLabel() {
        return positiveLabel;
    }

    public static String getNegativeLabel() {
        return negativeLabel;
    }

    public static double getTotalNrOfLabelEntropy() {
        return totalNrOfLabelEntropy;
    }

    public static LinkedList<ID3Element> retrieveFileData(File retrieveFile) throws FileNotFoundException {
        Scanner interactive = new Scanner(retrieveFile);

        while (interactive.hasNext()) {
            String knowledge = interactive.nextLine();

            String[] elems = knowledge.split(",");
            LinkedList<String> vElements = new LinkedList<>(Arrays.asList(elems));
            ID3Element element = new ID3Element(vElements);
            id3Elements.add(element);
        }
        ID3Element firstTestingElement = id3Elements.get(1);
        setPositiveAndNegativeLabelNames(firstTestingElement);
        return id3Elements;
    }

    /**
     * ReduceHashmapToSimpleModels
     * @return
     */
    public static LinkedList<ColumnValue> setID3ElementsTree() {
        currentColV = new ColumnValue();
        currentLeafColValue = new LeafColValue();
        currentLabel = new LabelForColumn();

        tableDataSetPerColumn.forEach((column, values) -> {
            currentColV.setColumnName(column);

            for (LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>> leafValues : values) {
                leafValues.forEach((currentLeaf, labelValuesOfLeaf) -> {
                    currentLeafColValue.setColumnValue(currentLeaf);
                    labelValuesOfLeaf.forEach((label, listWithEntropy) -> {
                        currentLabel.setLabel(label);
                        currentLabel.setNrOfSimilarElements(listWithEntropy.getFirst());
                        currentLabel.setEntropyOfColumn(listWithEntropy.getLast());
                        labelValues.add(currentLabel);

                        currentLeafColValue.setLabelValues(labelValues);
                        currentLabel = new LabelForColumn();
                    });
                    leafColValues.add(currentLeafColValue);
                    currentColV.setLeafValues(leafColValues);
                    currentLeafColValue = new LeafColValue();
                    labelValues = new LinkedList<>();
                });
            }
            tree.add(currentColV);
            currentColV = new ColumnValue();
            leafColValues = new LinkedList<>();
        });
        return tree;
    }
}
