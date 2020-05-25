package ui.lab3;


import ui.utils.Constant;
import ui.utils.ID3Element;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static ui.utils.Lab3Utils.getGetID3;
import static ui.utils.Lab3Utils.log2;
import static ui.utils.RegexOperator.labelColNo;
import static ui.utils.RegexOperator.labelColYes;


public class Lab3 {
    private static final DecimalFormat newFormat = new DecimalFormat("#.###");
    private static LinkedList<String> getID3Data = new LinkedList<>();

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
    private static LinkedList<Double> listOfEntropy = new LinkedList<>();

    private static AtomicReference<Double> totalEnt;

    public static void main(String[] args) throws FileNotFoundException {
        getID3Data = getGetID3();

        retrieveFileData(new File(Constant.volleyball));
//        retrieveFileData(new File(Constant.titanic_train_categorical));

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

        LinkedHashMap<String, LinkedList<Double>> colNameAndValuesWithEntropy = new LinkedHashMap<>();

        setColNameAndValuesWithEntropy(colNameAndValuesWithEntropy);

        retrieveInformationGainForMainColumns(colNameAndValuesWithEntropy);

    }

    private static double informationGainIG(double totalEnt, double firstVal, double secondVal, double columnEntropy) {
        totalEnt = totalEnt - ((firstVal + secondVal) / totalNrOfLabelEntropy) * columnEntropy;
        return totalEnt;

    }
    public static void retrieveFileData(File retrieveFile) throws FileNotFoundException {
        Scanner interactive = new Scanner(retrieveFile);

        while (interactive.hasNext()) {
            String knowledge = interactive.nextLine();

            String[] elems = knowledge.split(",");
            LinkedList<String> vElements = new LinkedList<>(Arrays.asList(elems));
            ID3Element element = new ID3Element(vElements);
            id3Elements.add(element);
        }
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
            if (key.equals(labelColYes)) {
                double totalY = value.get(0); //we saved it as 1st element
                nrOfLabelYes.set(totalY);
            } else if (key.equals(labelColNo)) {
                double totalY = value.get(0); //we saved it as 1st element
                nrOfLabelNo.set(totalY);
            }
        });
    }

    private static void retrieveLikelihoodOfElements() {
        countPerSetComb.forEach((key, value) -> {
            double likelihoodOfLabel = 0;
            if (key.contains(labelColYes)) {
                likelihoodOfLabel = nrOfLabelYes.get();
            } else if (key.contains(labelColNo)) {
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
            if (key.equals(labelColYes)) {
                double probability = getPriorProbabilityByRelativeFrequency(value);
                valueWithProbability = new LinkedList<>();
                valueWithProbability.add(Double.valueOf(value));
                valueWithProbability.add(probability);
                labelRelativeFreq.put(key, valueWithProbability);

            } else if (key.equals(labelColNo)) {
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


    private static double entropyOfLabelDataset(double positiveEx, double negativeEx, double total) {
        double posLabel = log2(positiveEx / total);
        double negLabel = log2(negativeEx / total);
        return -posLabel * (positiveEx / total) - negLabel * negativeEx / total;
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
                    if (label.equals(labelColYes)) {
                        endResult = fullCKey.replace("|" + label, "|" + labelColNo);
                    } else {
                        endResult = fullCKey.replace("|" + label, "|" + labelColYes);
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

    private static void generateValuesForTable(LinkedList<LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>>> listOfMaps) {
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
                        valueWithEntropy.computeIfAbsent(labelColNo, k -> new LinkedList<>()).add(currentColEntropy);

                        currentColEntropy = getLabelEntropy(secondValueForEd, firstValueForEd);
                        valueWithEntropy.computeIfAbsent(labelColYes, k -> new LinkedList<>()).add(currentColEntropy);

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

    private static void setColNameAndValuesWithEntropy(LinkedHashMap<String, LinkedList<Double>> colNameAndValuesWithEntropy) {
        columnWithUniqueElements.forEach((columnName, columnValues) -> {
            String col = columnName;
            columnValues.forEach((colVal, nrOfElem) -> {
                tableDataSetPerColumn.forEach((tableColName, tableColValue) -> {
                    if (tableColName.equals(columnName)) {
                        for (LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>> columnValue : tableColValue) {
                            String entryValueName = columnValue.keySet().iterator().next();
                            if (entryValueName.equals(colVal)) {
                                //Gets the iterator of the entryValue (sunny and subsequent elem for ex)
                                Iterator<Map.Entry<String, LinkedList<Double>>> it = columnValue.entrySet().iterator().next().getValue().entrySet().iterator();
                                double retrieveEntropyFromColumn = columnValue.entrySet().iterator().next().getValue().entrySet().iterator().next().getValue().getLast();

                                double label1Count = it.next().getValue().getFirst(); //gets first label Value
                                double label2Count = it.next().getValue().getFirst(); //gets 2nd label Value

                                listOfEntropy.add(label1Count);
                                listOfEntropy.add(label2Count);
                                listOfEntropy.add(retrieveEntropyFromColumn);
                                colNameAndValuesWithEntropy.putIfAbsent(columnName.concat("-" + entryValueName), listOfEntropy);
                                listOfEntropy = new LinkedList<>();
                            }

                        }
                    }

                });
            });

        });
    }

    private static void retrieveInformationGainForMainColumns(LinkedHashMap<String, LinkedList<Double>> colNameAndValuesWithEntropy) {
        LinkedHashMap<String, Double> colNameAndTotalEntropy = new LinkedHashMap<>();
        LinkedList<Double> totalInformationGainIG = new LinkedList<>();

        AtomicReference<Double> counter = new AtomicReference<>((double) 0);
        AtomicReference<Double> counterOfSimilarColName = new AtomicReference<>((double) 0);

        columnWithUniqueElements.forEach((colUniqueName, nrOfElems) -> {
            colNameAndValuesWithEntropy.forEach((colNameAndV, listOfEntropyValues) -> {
                String[] colNameV = colNameAndV.split("-");
                String colName = colNameV[0];
                String colVal = colNameV[1];
                if (colUniqueName.equals(colName)) {
                    colNameAndValuesWithEntropy.forEach((innerColNameAndV, innerListOfEntropyValues) -> {
                        String[] innerColNameV = innerColNameAndV.split("-");
                        String innerColName = innerColNameV[0];
                        String innerColVal = innerColNameV[1];
                        if (colName.equals(innerColName) && colVal.equals(innerColVal)) {
                            counterOfSimilarColName.getAndSet(counterOfSimilarColName.get() + 1);
                            double firstVal = innerListOfEntropyValues.getFirst();
                            double secondVal = innerListOfEntropyValues.get(1);
                            double columnEntropy = innerListOfEntropyValues.getLast();
                            counter.getAndSet(counter.get() + 1);
                            totalEnt.set(informationGainIG(totalEnt.get(), firstVal, secondVal, columnEntropy));
                        }
                    });
                    if (counter.get() == nrOfElems.size()) {
                        totalInformationGainIG.add(Double.parseDouble(newFormat.format(totalEnt.get())));
                        totalEnt = new AtomicReference<>(labelEntropy);
                        counter.set(0.0);

                    }
                }

            });
        });

        LinkedList<String> listOfKeys = new LinkedList<>(columnWithUniqueElements.keySet());
        for (int i = 0; i < listOfKeys.size()-1; i++) {
            String keyName = listOfKeys.get(i);
            double valueInTotalEnt = totalInformationGainIG.get(i);
            colNameAndTotalEntropy.putIfAbsent(keyName,valueInTotalEnt);
        }
    }


}
