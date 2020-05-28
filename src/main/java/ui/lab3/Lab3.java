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
    private static LinkedHashMap<String, LinkedList<LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>>>> subsequentMapToTest = new LinkedHashMap<>();
    private static LinkedList<Double> listOfEntropy = new LinkedList<>();

    private static AtomicReference<Double> totalEnt;
    private static AtomicInteger depth;
    private static double currentEntropy;
    private static String usingLeaf;
    private static LinkedHashMap<String, Double> colNameAndTotalEntropy = new LinkedHashMap<>();
    private static LinkedList<LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>>> columnsToUse = new LinkedList<>();
    private static LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>> columnToUse = new LinkedHashMap<>();
    private static LinkedList<String> currentPath;
    private static LinkedList<LinkedList<Double>> columnEntropy;
    private static LinkedList<String> formerPath;
    private static LinkedHashMap<String, Double> columnWithLeafAndIG;
    private static double finalEntropyPerLeafColumn;

    public static void main(String[] args) throws FileNotFoundException {
        getID3Data = getGetID3();

        retrieveFileData(new File(Constant.volleyball));
//        retrieveFileData(new File(Constant.titanic_train_categorical));

        setupModelDataTable();

        LinkedHashMap<String, LinkedList<Double>> colNameAndValuesWithEntropy = new LinkedHashMap<>();

        setColNameAndValuesWithEntropy(colNameAndValuesWithEntropy);

        retrieveInformationGainForMainColumns(colNameAndValuesWithEntropy);

        subsequentMapToTest = goIGMoreInDepth(tableDataSetPerColumn);
        LinkedHashMap<String, LinkedList<Double>> colNameOnFirstLeaf = new LinkedHashMap<>();

        AtomicReference<Double> positiveColValue = new AtomicReference<>((double) 0);
        AtomicReference<Double> negativeColValue = new AtomicReference<>((double) 0);
        AtomicReference<Double> total = new AtomicReference<>((double) 0);
        AtomicReference<Double> leafPos = new AtomicReference<>((double) 0);
        AtomicReference<Double> leafNeg = new AtomicReference<>((double) 0);
        AtomicReference<Double> leafEntropy = new AtomicReference<>((double) 0);
        setFirstDepthDataForFirstTableToTest(colNameOnFirstLeaf, positiveColValue, negativeColValue, total, leafPos, leafNeg, leafEntropy);


        formerPath = new LinkedList<>();
        currentPath = new LinkedList<>();
        columnEntropy = new LinkedList<>();
        columnWithLeafAndIG = new LinkedHashMap<>();
        AtomicReference<Double> doneThroughColumn = new AtomicReference<>((double) 0);
        finalEntropyPerLeafColumn = leafEntropy.get();
        LinkedList<String> testingCol = new LinkedList<>(colNameOnFirstLeaf.keySet());
        colNameOnFirstLeaf.forEach((dataToProcess, entropy) -> {
            String[] dataElements = dataToProcess.split("-");
            currentPath.addAll(Arrays.asList(dataElements));
            if (formerPath.equals(currentPath) && formerPath.size() != 0) {
                doneThroughColumn.getAndSet(doneThroughColumn.get() + 1);
            } else if (!formerPath.equals(currentPath) && formerPath.size() != 0) {
                for (int i = 0; i < formerPath.size(); i++) {
                    currentPath.removeFirst();
                }
                if (formerPath.get(1).equals(currentPath.get(1))) {
                    String s = "s";
                    doneThroughColumn.getAndSet(doneThroughColumn.get() + 1);
                } else {
                    String testingCurVal;
                    String similarSubsequentVal;
                    for (int i = 0; i < columnEntropy.size(); i++) {
                        LinkedList<Double> elements = columnEntropy.get(i);
                        String colData = testingCol.get(i);
                        double totalE = leafNeg.get() + leafPos.get();
                        //if the 2nd - entropy is 0 then just stick to it
                        double entropyL = elements.getLast();
                        if (elements.getLast() == 0.0) {
                            finalEntropyPerLeafColumn = finalEntropyPerLeafColumn - entropyL;
                        } else {
                            //case where the entropy for currentV isn't 0 (2nd value in the element list)

                            for (int j = i; j < testingCol.size(); j++) {
                                testingCurVal = testingCol.get(j);
                                String[] similarColValue1El = testingCurVal.split("-");
                                LinkedList<String> first = new LinkedList<>(Arrays.asList(similarColValue1El));
                                similarSubsequentVal = testingCol.get(j + 1);
                                String[] similarColValue2El = similarSubsequentVal.split("-");
                                LinkedList<String> subsequent = new LinkedList<>(Arrays.asList(similarColValue2El));
                                if ((first.get(1).equals(subsequent.get(1))) &&
                                        (first.get(2).equals(subsequent.get(2))) &&
                                        (!first.get(3).equals(subsequent.get(3)))) {
                                    ///SOMETHING HERE
                                    LinkedList<Double> subsequentElements = columnEntropy.get(j + 1);
                                    double testingNr = elements.getFirst() + subsequentElements.getFirst();
                                    double valueToDeduce = testingNr / totalE * entropyL;
                                    finalEntropyPerLeafColumn -= valueToDeduce;
                                    break;
                                }
                            }
                            i++;
                        }
                        if (i == columnEntropy.size()) {
                            String[] similarColValue1El = colData.split("-");
//                      colNameAndValuesWithEntropy.putIfAbsent();
                            String s = "s";
                        }
                    }
                }
            }

            if (formerPath.size() == 0) {
                formerPath.addAll(currentPath);
            }
            if (formerPath.get(1).equals(currentPath.get(1))) {
                columnEntropy.add(entropy);
            }
            if (!formerPath.get(1).equals(currentPath.get(1))) {
                columnEntropy = new LinkedList<>();
                columnEntropy.add(entropy);
            }
        });


        consoleDisplay();

        String s = "s";
    }

    private static void setFirstDepthDataForFirstTableToTest(LinkedHashMap<String, LinkedList<Double>> colNameOnFirstLeaf,
                                                             AtomicReference<Double> positiveColValue,
                                                             AtomicReference<Double> negativeColValue, AtomicReference<Double> total,
                                                             AtomicReference<Double> leafPos, AtomicReference<Double> leafNeg, AtomicReference<Double> leafEntropy) {
        for (Map.Entry<String,
                LinkedList<LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>>>> entry : subsequentMapToTest.entrySet()) {
            String colN = entry.getKey();
            LinkedList<LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>>> colList = entry.getValue();
            for (LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>> columnToTest : colList) {
                if (columnToTest.keySet().iterator().next().equals(usingLeaf)) {
                    columnToTest.forEach((column, labelWithEntropy) -> {
                        if (labelWithEntropy.size() > 1) {
                            labelWithEntropy.forEach((label, listOfElements) -> {
                                if (label.equals(labelColYes)) {
                                    leafPos.set(listOfElements.getFirst());
                                }
                                if (label.equals(labelColNo)) {
                                    leafNeg.set(listOfElements.getFirst());
                                }
                                leafEntropy.set(listOfElements.getLast());
                            });
                        }
                    });
                    continue;
                }
                columnToTest.forEach((column, labelWithEntropy) -> {
                    if (labelWithEntropy.size() > 1) {
                        labelWithEntropy.forEach((label, listOfElements) -> {
                            if (label.equals(labelColYes)) {
                                positiveColValue.set(listOfElements.getFirst());
                            }
                            if (label.equals(labelColNo)) {
                                negativeColValue.set(listOfElements.getFirst());
                            }
                        });
                        total.set(positiveColValue.get() + negativeColValue.get());
                        currentEntropy = entropyOfLabelDataset(positiveColValue.get(), negativeColValue.get(), total.get());
                        labelWithEntropy.forEach((label, listOfElements) -> {
                            listOfElements.add(currentEntropy);
                            LinkedList<Double> currentV = new LinkedList<>(listOfElements);
                            colNameOnFirstLeaf.putIfAbsent(usingLeaf + "-" + colN + "-" + column + "-" + label, currentV);
                        });
                    } else {
                        labelWithEntropy.forEach((label, listOfElements) -> {
                            currentEntropy = 0;
                            LinkedList<Double> currentV = new LinkedList<>(listOfElements);
                            currentV.add(currentEntropy);
                            colNameOnFirstLeaf.putIfAbsent(usingLeaf + "-" + colN + "-" + column + "-" + label, currentV);
                        });
                    }
                });
            }
        }
    }

    private static void setupModelDataTable() {
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
        if (negativeEx == 0.0) {
            return 0;
        }
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
        for (int i = 0; i < listOfKeys.size() - 1; i++) {
            String keyName = listOfKeys.get(i);
            double valueInTotalEnt = totalInformationGainIG.get(i);
            colNameAndTotalEntropy.putIfAbsent(keyName, valueInTotalEnt);
        }
        depth = new AtomicInteger(0);
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

    private static boolean retrieveInitialElementMap(LinkedHashMap<String, LinkedList<LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>>>> tableDataSetPerColumn,
                                                     LinkedHashMap<String, LinkedList<LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>>>> mapToTest,
                                                     String colToTest, String label, LinkedList<Double> nrOfElWithEntropy) {
        int count = 1;
        for (Map.Entry<String, LinkedList<LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>>>> e : tableDataSetPerColumn.entrySet()) {
            String col = e.getKey();
            LinkedList<LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>>> listV = e.getValue();
            labelT:
            for (LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>> v : listV) {
                LinkedHashMap<String, LinkedList<Double>> values = v.entrySet().iterator().next().getValue();
                LinkedHashMap<String, LinkedList<Double>> valuesToAdd = new LinkedHashMap<>();
                for (String key : v.keySet()) {
                    if (key.equals(colToTest)) {
                        for (Map.Entry<String, LinkedList<Double>> entry : values.entrySet()) {
                            String labelV = entry.getKey();
                            LinkedList<Double> valuesL = entry.getValue();
                            if (labelV.equals(label)) {
                                nrOfElWithEntropy.add((double) count);
                                nrOfElWithEntropy.add(valuesL.getLast());//entropy
                                valuesToAdd.putIfAbsent(label, nrOfElWithEntropy);
                                break;
                            }
                        }
                        columnToUse.putIfAbsent(key, valuesToAdd);
                        columnsToUse.add(columnToUse);
                        mapToTest.put(col, columnsToUse);
                        columnToUse = new LinkedHashMap<>();
                        columnsToUse = new LinkedList<>();
                        break labelT;
                    }
                }
            }
        }
        return mapToTest.size() == tableDataSetPerColumn.size();
    }

    private static LinkedHashMap<String, LinkedList<LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>>>>
    goIGMoreInDepth(LinkedHashMap<String, LinkedList<LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>>>> tableDataSetPerColumn) {

        LinkedHashMap<String, LinkedList<LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>>>> mapWitOnly1ColLabel = new LinkedHashMap<>();
        LinkedHashMap<String, LinkedList<LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>>>> mapToTest = new LinkedHashMap<>();

        tableDataSetPerColumn.forEach((tableCol, tableVal) -> {
            for (LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>> tableV : tableVal) {
                tableV.forEach((tableValueName, labelValues) -> {
                    for (Map.Entry<String, LinkedList<Double>> entry : labelValues.entrySet()) {
                        LinkedList<Double> values = entry.getValue();
                        double depth = values.getFirst();
                        if (depth == 0) {
                            mapWitOnly1ColLabel.putIfAbsent(tableCol, tableVal);
                            break;
                        }
                    }
                });
            }
        });

        LinkedHashMap<String, Double> valuesOfTable = new LinkedHashMap<>();
        LinkedList<LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>>> listToTest = mapWitOnly1ColLabel.get(mapWitOnly1ColLabel.keySet().iterator().next());
        for (LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>> item : listToTest) {
            item.forEach((columnValue, labelColumnWithVal) -> {
                labelColumnWithVal.forEach((labYorNo, valueToTest) -> {
                    double entropy = valueToTest.getLast();
                    valuesOfTable.putIfAbsent(columnValue, entropy);
                });
            });
        }
        LinkedList<Double> list = new LinkedList<>(valuesOfTable.values());
        double findMaxFeatureEntropy = 0;
        for (int i = 0; i < list.size() - 1; i++) {
            double currentEl = list.get(i);
            double nexEl = list.get(i + 1);
            findMaxFeatureEntropy = Math.max(currentEl, nexEl);
        }

        usingLeaf = "";
        for (Map.Entry<String, Double> entry : valuesOfTable.entrySet()) {
            String name = entry.getKey();
            Double entropy = entry.getValue();
            if (entropy.equals(findMaxFeatureEntropy)) {
                usingLeaf = name;
                break;
            }
        }

        LinkedList<ID3Element> id3ElemWithSameLeafComponent = new LinkedList<>();
        for (ID3Element element : id3Elements) {
            for (String colValue : element.getId3FedElements()) {
                if (colValue.equals(usingLeaf)) {
                    id3ElemWithSameLeafComponent.add(element);
                }
            }
        }

        ///WORKING ON IT
        for (ID3Element element : id3ElemWithSameLeafComponent) {
            double valuesTested = 0.0;
            ssB:
            for (int i = 0; i < element.getId3FedElements().size() - 1; i++) {
                String colToTest = element.getId3FedElements().get(i);
                String label = element.getId3FedElements().get(element.getId3FedElements().size() - 1); // last element in ID3
                if (checkIfCurrentColumnIsTheLeaf(mapWitOnly1ColLabel, mapToTest, usingLeaf, colToTest)) continue;

                LinkedList<Double> nrOfElWithEntropy = new LinkedList<>();
                if (mapToTest.size() < tableDataSetPerColumn.size()) {
                    if (retrieveInitialElementMap(tableDataSetPerColumn, mapToTest, colToTest, label, nrOfElWithEntropy)) {
                        break; // found the first Elements data now move onto the next ID3
                    }
                }
                if (mapToTest.size() == tableDataSetPerColumn.size()) {
                    Double count;
                    LinkedList<String> colNames = new LinkedList<>();
                    LinkedList<String> testingColNames = findAddTheKeysThenRemoveTheLeaf(mapToTest, usingLeaf, colNames);

                    nextItem:
                    for (Map.Entry<String, LinkedList<LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>>>> e : mapToTest.entrySet()) {
                        LinkedList<LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>>> listV = e.getValue();
                        labelT:
                        for (LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>> v : listV) {
                            LinkedHashMap<String, LinkedList<Double>> values = v.entrySet().iterator().next().getValue();
                            LinkedHashMap<String, LinkedList<Double>> valuesToAdd = new LinkedHashMap<>();
                            for (String key : v.keySet()) {
                                if (key.equals(usingLeaf)) continue;
                                //contains
                                if (testingColNames.contains(colToTest)) {
                                    if (key.equals(colToTest)) {
                                        for (Map.Entry<String, LinkedList<Double>> entry : values.entrySet()) {
                                            String labelL = entry.getKey();
                                            LinkedList<Double> valuesL = entry.getValue();
                                            if (label.equals(labelL)) {
                                                count = valuesL.getFirst();
                                                count++;
                                                nrOfElWithEntropy.add(count);
                                                valuesToAdd.putIfAbsent(labelL, nrOfElWithEntropy);
                                                columnToUse.putIfAbsent(key, valuesToAdd);
                                                columnsToUse.add(columnToUse);
                                                listV.remove(v);
                                                listV.add(columnToUse);
                                                columnToUse = new LinkedHashMap<>();
                                                columnsToUse = new LinkedList<>();
                                                valuesTested++;
                                                //not taking in consideration first and label col
                                                if (i < element.getId3FedElements().size() - 2) {
                                                    break nextItem;
                                                } else if (i == element.getId3FedElements().size() - 2) {
                                                    break ssB;
                                                }
                                            }
                                            //different label
                                            else {
                                                if (values.keySet().iterator().next().equals(label)) {
                                                    count = valuesL.getFirst();
                                                    count++;
                                                    nrOfElWithEntropy.add(count);
                                                    valuesToAdd.putIfAbsent(label, nrOfElWithEntropy);
                                                } else {
                                                    nrOfElWithEntropy.add(1.0);
                                                    values.putIfAbsent(label, nrOfElWithEntropy);
                                                    columnToUse = new LinkedHashMap<>();
                                                    columnsToUse = new LinkedList<>();
                                                }
                                                valuesTested++;
                                                if (valuesTested == testingColNames.size()) {
                                                    break ssB;
                                                }
                                                //not taking into count first(tested elem) and label
                                                if (valuesTested == element.getId3FedElements().size() - 2) {
                                                    break ssB;
                                                }
                                            }
                                            break nextItem;
                                        }
                                    }
                                    //key is different
                                    else {
                                        int indexOfKey = testingColNames.indexOf(key);
                                        if (!testingColNames.contains(colToTest)) {
                                            if (i - 1 == indexOfKey) {
                                                setAppropriateNrForValuesWithEntropy(colToTest, label, nrOfElWithEntropy, values, valuesToAdd, listV);
                                                break ssB;
                                            } else {
                                                //index not yet same
                                                break labelT;
                                            }
                                        }
                                        //  contains the element already
                                        else {
                                            if (testingColNames.contains(colToTest)) {
                                                if (i == valuesTested) {
                                                    for (Map.Entry<String, LinkedList<Double>> entry : values.entrySet()) {
                                                        String labelL = entry.getKey();
                                                        LinkedList<Double> valuesL = entry.getValue();
                                                        if (label.equals(labelL)) {
                                                            count = valuesL.getFirst();
                                                            count++;
                                                            nrOfElWithEntropy.add(count);
                                                            valuesToAdd.putIfAbsent(labelL, nrOfElWithEntropy);
                                                        } else {
                                                            nrOfElWithEntropy.add(1.0);
                                                            valuesToAdd.putIfAbsent(label, nrOfElWithEntropy);
                                                        }
                                                        break;
                                                    }
                                                    columnToUse.putIfAbsent(colToTest, valuesToAdd);
                                                    columnsToUse.add(columnToUse);
                                                    valuesTested++;
                                                    listV.add(columnToUse);
                                                    columnToUse = new LinkedHashMap<>();
                                                    columnsToUse = new LinkedList<>();
                                                    break nextItem;
                                                }
                                            } else {
                                                break labelT;
                                            }
                                        }
                                    }
                                }

                                //doesn't yet contain
                                else {
                                    if (!testingColNames.contains(colToTest)) {
                                        int countN = 0;
                                        for (Map.Entry<String, LinkedList<LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>>>> mapEntry : mapToTest.entrySet()) {
                                            LinkedList<LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>>> valuesC = mapEntry.getValue();
                                            if (i == 1) {
                                                valuesTested = getValuesTested(valuesTested, colToTest, label, nrOfElWithEntropy, values, valuesToAdd, listV);
                                                break nextItem;
                                            }
                                            if (countN == i) {
                                                valuesTested = getValuesTested(valuesTested, colToTest, label, nrOfElWithEntropy, values, valuesToAdd, valuesC);
                                                if (valuesTested == testingColNames.size()) {
                                                    break ssB;
                                                }
                                                break nextItem;
                                            } else {
                                                countN++;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        depth.getAndIncrement();
        return mapToTest;
    }

    private static LinkedList<String> findAddTheKeysThenRemoveTheLeaf(LinkedHashMap<String, LinkedList<LinkedHashMap<String,
            LinkedHashMap<String, LinkedList<Double>>>>> mapToTest, String usingLeaf, LinkedList<String> colNames) {
        for (Map.Entry<String, LinkedList<LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>>>> e : mapToTest.entrySet()) {
            LinkedList<LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>>> listV = e.getValue();
            for (LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>> v : listV) {
                colNames.addAll(v.keySet());
            }
        }
        LinkedList<String> testingColNames = new LinkedList<>(colNames);
        if (colNames.getFirst().equals(usingLeaf)) {
            testingColNames.remove(colNames.getFirst());
        }
        return testingColNames;
    }

    private static boolean checkIfCurrentColumnIsTheLeaf(LinkedHashMap<String, LinkedList<LinkedHashMap<String, LinkedHashMap<String,
            LinkedList<Double>>>>> mapWitOnly1ColLabel, LinkedHashMap<String, LinkedList<LinkedHashMap<String, LinkedHashMap<String,
            LinkedList<Double>>>>> mapToTest, String usingLeaf, String colToTest) {
        if (colToTest.equals(usingLeaf)) {
            String finalUsingLeaf = usingLeaf;
            mapWitOnly1ColLabel.forEach((col, listV) -> {
                for (LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>> v : listV) {
                    LinkedHashMap<String, LinkedList<Double>> values = v.entrySet().iterator().next().getValue();
                    for (String key : v.keySet()) {
                        if (key.equals(finalUsingLeaf)) {
                            columnToUse.putIfAbsent(key, values);
                            columnsToUse.add(columnToUse);
                            mapToTest.putIfAbsent(col, columnsToUse);
                        }
                    }
                }
            });
            columnsToUse = new LinkedList<>();
            columnToUse = new LinkedHashMap<>();
            return true;
        }
        return false;
    }

    private static double getValuesTested(double valuesTested, String colToTest, String label, LinkedList<Double> nrOfElWithEntropy,
                                          LinkedHashMap<String, LinkedList<Double>> values, LinkedHashMap<String, LinkedList<Double>> valuesToAdd,
                                          LinkedList<LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>>> valuesForColumnWithLabels) {
        setAppropriateNrForValuesWithEntropy(colToTest, label, nrOfElWithEntropy, values, valuesToAdd, valuesForColumnWithLabels);
        valuesTested++;
        return valuesTested;
    }

    private static void setAppropriateNrForValuesWithEntropy(String colToTest, String label, LinkedList<Double> nrOfElWithEntropy,
                                                             LinkedHashMap<String, LinkedList<Double>> values, LinkedHashMap<String, LinkedList<Double>> valuesToAdd,
                                                             LinkedList<LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>>> valuesForColumnWithLabels) {
        for (Map.Entry<String, LinkedList<Double>> entry : values.entrySet()) {
            LinkedList<Double> valuesL = entry.getValue();
            nrOfElWithEntropy.add(1.0);
            valuesToAdd.putIfAbsent(label, nrOfElWithEntropy);
            break;
        }
        columnToUse.putIfAbsent(colToTest, valuesToAdd);
        columnsToUse.add(columnToUse);
        valuesForColumnWithLabels.add(columnToUse);
        columnToUse = new LinkedHashMap<>();
        columnsToUse = new LinkedList<>();
    }

}
