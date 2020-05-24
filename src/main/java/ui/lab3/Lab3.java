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
    //    private static final LinkedHashMap<String, LinkedList<String>> lineToVbElements = new LinkedHashMap<>();
//    private static LinkedList<String> vElements, firstColumn, secondColumn, thirdColumn, forthColumn, labelColumn;

    private static final LinkedHashMap<Integer, LinkedList<String>> columnListWithElements = new LinkedHashMap<>();
    private static final LinkedHashMap<Integer, String> featureAndLabelList = new LinkedHashMap<>();
    private static final LinkedHashMap<String, LinkedList<String>> columnWithVariables = new LinkedHashMap<>();

    private static final LinkedHashMap<String, LinkedHashMap<String, Integer>> fileDatasetMap = new LinkedHashMap<>();
    private static final LinkedList<ID3Element> id3Elements = new LinkedList<>();
    private static DecimalFormat newFormat = new DecimalFormat("#.###");
    private static double labelEntropy;
    private static double currentLabelEntropy;

    private static final LinkedHashMap<String, Integer> countElements = new LinkedHashMap<>();
    private static LinkedList<String> getID3Data = new LinkedList<>();

    private static final AtomicReference<Double> nrOfLabelYes = new AtomicReference<>((double) 0);
    private static final AtomicReference<Double> nrOfLabelNo = new AtomicReference<>((double) 0);

    private static final LinkedHashMap<String, LinkedList<Double>> labelRelativeFreq = new LinkedHashMap<>();
    private static final LinkedHashMap<String, Double> countPerSetComb = new LinkedHashMap<>();
    private static LinkedHashMap<String, Double> fullCountPerSetComb = new LinkedHashMap<>();
    //includes values from normalCount-but with "nonexistent-0"
    private static final LinkedHashMap<String, Double> getNrOfCountPerSetComb = new LinkedHashMap<>();
    private static final LinkedHashMap<String, Double> likelihoodPerSetComb = new LinkedHashMap<>();
    private static LinkedList<Double> valueWithProbability;
    private static final LinkedList<String> mapHypothesisConsoleResults = new LinkedList<>();
    private static final LinkedList<Double> mapHypothesisResults = new LinkedList<>();


    public static void main(String[] args) throws FileNotFoundException {
        getID3Data = getGetID3();

//        retrieveFileData(new File(Constant.volleyball));
        retrieveFileData(new File(Constant.titanic_train_categorical));

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

        setupFullCountPerSetComb();

        set0ProbabilityForEmptySetElements();
    }

    private static void set0ProbabilityForEmptySetElements() {
        fullCountPerSetComb = countPerSetComb;

        int fullSize = fullCountPerSetComb.size();

        //maybe do a break each time it finds one (TBDetermined)
        getNrOfCountPerSetComb.forEach((existingKey, existingValue) -> {
            if (existingValue == 1) {
                for (Map.Entry<String, Double> entry : fullCountPerSetComb.entrySet()) {
                    String fullCKey = entry.getKey();
                    String[] entryKeySet = fullCKey.split("\\|");
                    String label = entryKeySet[1];
                    String endResult;
                    if (label.equals(labelColYes)) {
                        endResult = fullCKey.replace("|"+label, "|"+labelColNo);
                    } else {
                        endResult = fullCKey.replace("|"+label, "|"+labelColYes);
                    }
                    fullCountPerSetComb.putIfAbsent(endResult, 0.0);
                    int sizeToTest = fullCountPerSetComb.size();
                    if (fullSize < sizeToTest) {
                        break;
                    }
                }
            }
        });
    }

    private static void setupFullCountPerSetComb() {
        fileDatasetMap.forEach((fileKey, fileValue) -> {
            for (Map.Entry<String, Integer> entry : fileValue.entrySet()) {
                String entryKey = entry.getKey();

                AtomicInteger count = new AtomicInteger(1);
                countPerSetComb.forEach((existingKey, existingValue) -> {
                    String[] entryKeySet = existingKey.split("\\|");
                    String columnNameOfCountSet = entryKeySet[0];
                    String labelNameOfCountSet = entryKeySet[1];

                    if (entryKey.equals(columnNameOfCountSet)) {
                        getNrOfCountPerSetComb.put(columnNameOfCountSet, (double) count.getAndIncrement());
                    }
                });
            }
        });
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
        AtomicInteger currentColumnCount = new AtomicInteger(1);

        LinkedList<String> columnListToTest = new LinkedList<>();

        LinkedList<String> columnNames = id3Elements.get(0).getId3FedElements();
        for (int i = 0; i < columnNames.size(); i++) {
            String column = columnNames.get(i);
            featureAndLabelList.putIfAbsent(i, column);
            columnWithVariables.putIfAbsent(column, new LinkedList<>());
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
                columnWithVariables.computeIfAbsent(featureOrLabel, k -> new LinkedList<>()).add(currentValue);
            }

        }

        LinkedList<String> arrK = new LinkedList<>(Arrays.asList(columnWithVariables.keySet().toArray(new String[0])));

        columnWithVariables.forEach((fOrLKey, fOrLValue) -> {
            for (String featureOrLabel : fOrLValue) {
                retrieveNrOfDistinctElementsPerColumn(count[0], columnListToTest, featureOrLabel, fOrLKey);
            }

            LinkedHashMap<String, Integer> localElements = new LinkedHashMap<>(countElements);
            fileDatasetMap.put(fOrLKey, localElements);
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
        double total = positiveColValue + negativeColValue;
        double entropy = entropyOfLabelDataset(positiveColValue, negativeColValue, total);
        return Double.parseDouble(newFormat.format(entropy));
    }

    public void usefulThings() {
//        int indexOfKey = new LinkedList<>(countPerSetComb.keySet()).indexOf(existingKey);
//        int lastPos = new LinkedList<>(countPerSetComb.keySet()).indexOf(lastCountPerSetCombKeyElement);
//        double countPerKey = fullCountPerSetComb.get(entryKey);
//
//        LinkedList<String> countPerSetCombKeySet = new LinkedList<>(countPerSetComb.keySet());
//        String lastCountPerSetCombKeyElement = countPerSetCombKeySet.getLast();

    }
}
