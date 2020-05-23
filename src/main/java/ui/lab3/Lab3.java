package ui.lab3;


import ui.utils.Constant;
import ui.utils.ID3ElementList;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static ui.utils.RegexOperator.*;


public class Lab3 {
    //    private static final LinkedHashMap<String, LinkedList<String>> lineToVbElements = new LinkedHashMap<>();
//    private static LinkedList<String> vElements, firstColumn, secondColumn, thirdColumn, forthColumn, labelColumn;

    private static final LinkedHashMap<Integer, LinkedList<String>> columnListWithElements = new LinkedHashMap<>();
    private static final LinkedHashMap<Integer, String> feautureAndLabelList = new LinkedHashMap<>();
    private static final LinkedHashMap<String, LinkedList<String>> columnWithVariables = new LinkedHashMap<>();

    private static final LinkedHashMap<String, LinkedHashMap<String, Integer>> volleyBallMatchMap = new LinkedHashMap<>();
    private static final LinkedList<ID3ElementList> id3Elements = new LinkedList<>();
    private static String labelColumnName;

    private static final LinkedHashMap<String, Integer> countElements = new LinkedHashMap<>();
    private static final LinkedList<String> getID3 = new LinkedList<>();
    private static String modeHyper;
    private static String modelHyper;
    private static double depthHyper;
    private static double nrTreesHyper;
    private static double featRatioHyper;
    private static double exRatioHyper;

    private static final AtomicReference<Double> nrOfLabelYes = new AtomicReference<>((double) 0);
    private static final AtomicReference<Double> nrOfLabelNo = new AtomicReference<>((double) 0);

    private static final LinkedHashMap<String, LinkedList<Double>> labelRelativeFreq = new LinkedHashMap<>();
    private static final LinkedHashMap<String, Double> countPerSetComb = new LinkedHashMap<>();
    private static final LinkedHashMap<String, Double> likelihoodPerSetComb = new LinkedHashMap<>();
    private static LinkedList<Double> valueWithProbability;
    private static final LinkedList<String> mapHypothesisConsoleResults = new LinkedList<>();
    private static final LinkedList<Double> mapHypothesisResults = new LinkedList<>();

    public static void main(String[] args) throws FileNotFoundException {
        getID3();

//        retrieveFileData(new File(Constant.volleyball));
        retrieveFileData(new File(Constant.titanic_train_categorical));

        getNrOfElementsForEachValuePerColumn();

        getNrByLabelCol();

        for (int i = 1; i < id3Elements.size(); i++) {
            ID3ElementList element = id3Elements.get(i);
            String labelCol = element.getId3FedElements().get(element.getId3FedElements().size() - 1);
            for (int j = 0; j < element.getId3FedElements().size() - 1; j++) {
                String currentCol = element.getId3FedElements().get(j);
                String currentColLikelihood = currentCol.concat("|" + labelCol);
                getPlayCountPerSetCombination(currentColLikelihood);

            }
        }

        retrieveLikelihoodOfElements();

        likelihoodAndOutput();
    }

    public static void retrieveFileData(File retrieveFile) throws FileNotFoundException {
        Scanner interactive = new Scanner(retrieveFile);

        while (interactive.hasNext()) {
            String knowledge = interactive.nextLine();

            String[] elems = knowledge.split(",");
            LinkedList<String> vElements = new LinkedList<>(Arrays.asList(elems));
            ID3ElementList element = new ID3ElementList(vElements);
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
            feautureAndLabelList.putIfAbsent(i, column);
            columnWithVariables.putIfAbsent(column, new LinkedList<>());
        }

        for (int i = 1; i < id3Elements.size(); i++) {
            ID3ElementList elementList = id3Elements.get(i);
            LinkedList<String> values = elementList.getId3FedElements();
            for (int j = 0; j < feautureAndLabelList.size(); j++) {
                String column = values.get(j);
                columnListWithElements.computeIfAbsent(i - 1, k -> new LinkedList<>()).add(column);

            }
        }

        LinkedList<String> featureList = new LinkedList<>(feautureAndLabelList.values());
        for (String featureOrLabel : featureList) {
            int currentColumnIndex = featureList.indexOf(featureOrLabel);

            for (LinkedList<String> currentColumnValue : columnListWithElements.values()) {
                String currentValue = currentColumnValue.get(currentColumnIndex);
                columnWithVariables.computeIfAbsent(featureOrLabel, k -> new LinkedList<>()).add(currentValue);
            }

        }

//        String[] arrayKeys = columnWithVariables.keySet().toArray(new String[0]);
//        LinkedList<String> arrK = new LinkedList<>(Arrays.asList(arrayKeys));

        LinkedList<String> arrK = new LinkedList<>(Arrays.asList(columnWithVariables.keySet().toArray(new String[0])));

        columnWithVariables.forEach((fOrLKey, fOrLValue) -> {
            for (String featureOrLabel : fOrLValue) {
                retrieveNrOfDistinctElementsPerColumn(count[0], columnListToTest, featureOrLabel, fOrLKey);
            }

            LinkedHashMap<String, Integer> localElements = new LinkedHashMap<>(countElements);
            volleyBallMatchMap.put(fOrLKey, localElements);
            count[0] = new AtomicInteger(1);
            if (fOrLKey.equals(arrK.get(arrK.size() - 1))) {
                deriveLabelColumnProbabilities(localElements);
            }
            countElements.clear();
        });

    }

    private static void computeMapHypothesis(LinkedHashMap<String, Double> computedSetComb) {

//        for (ID3ElementV element : ID_3_ELEMENT_VS) {
//            String weather = element.getWeather();
//            String temp = element.getTemp();
//            String hum = element.getHum();
//            String wind = element.getWind();
//            String play = element.getPlay();
//
//            String weatherLikelihood = weather.concat("|" + play);
//            String tempLikelihood = temp.concat("|" + play);
//            String humLikelihood = hum.concat("|" + play);
//            String windLikelihood = wind.concat("|" + play);
//
//            LinkedList<String> likelihoodOfElements = new LinkedList<>();
//            likelihoodOfElements.add(weatherLikelihood);
//            likelihoodOfElements.add(tempLikelihood);
//            likelihoodOfElements.add(humLikelihood);
//            likelihoodOfElements.add(windLikelihood);
//
//            AtomicReference<Double> mapYes = new AtomicReference<>((double) 1);
//            AtomicReference<Double> mapNo = new AtomicReference<>((double) 1);
//
//
//            computedSetComb.forEach((key, value) -> {
//                if (key.equals(weatherLikelihood)) {
//                    retrieveMapYesAndNo(mapYes, mapNo, key, value, likelihoodOfElements);
//                }
//                String s = "s";
//            });
//            computedSetComb.forEach((key, value) -> {
//                if (key.equals(tempLikelihood)) {
//                    retrieveMapYesAndNo(mapYes, mapNo, key, value, likelihoodOfElements);
//                }
//                String s = "s";
//            });
//            computedSetComb.forEach((key, value) -> {
//                if (key.equals(humLikelihood)) {
//                    retrieveMapYesAndNo(mapYes, mapNo, key, value, likelihoodOfElements);
//                }
//                String s = "s";
//
//            });
//            computedSetComb.forEach((key, value) -> {
//                if (key.equals(windLikelihood)) {
//                    retrieveMapYesAndNo(mapYes, mapNo, key, value, likelihoodOfElements);
//                }
//                String s = "s";
//            });
//
//            for (Map.Entry<String, LinkedList<Double>> label : labelRelativeFreq.entrySet()) {
//                String key = label.getKey();
//                LinkedList<Double> value = label.getValue();
//
//                if (key.equals(labelColYes)) {
//                    multiplyAllColumnsToLabelColumn(mapYes, mapNo, key, value, labelColNo);
//                } else if (key.equals(labelColNo)) {
//                    multiplyAllColumnsToLabelColumn(mapNo, mapYes, key, value, labelColYes);
//                }
//
//                DecimalFormat newFormat = new DecimalFormat("#.####");
//                double maxHypothesis = Math.max(mapYes.get(), mapNo.get());
//                if (maxHypothesis == mapYes.get()) {
//                    double twoDecimal = Double.parseDouble(newFormat.format(mapYes.get()));
//
//                    if (!mapHypothesisResults.contains(twoDecimal)) {
//                        mapHypothesisResults.add(twoDecimal);
//                        String consoleResult = twoDecimal + "|" + labelColYes;
//                        mapHypothesisConsoleResults.add(consoleResult);
//                    }
//                } else {
//                    double twoDecimal = Double.parseDouble(newFormat.format(mapNo.get()));
//
//                    if (!mapHypothesisResults.contains(twoDecimal)) {
//                        mapHypothesisResults.add(twoDecimal);
//                        String consoleResult = twoDecimal + "|" + labelColNo;
//                        mapHypothesisConsoleResults.add(consoleResult);
//                    }
//                }
//                String s = "s";
//            }
//            labelRelativeFreq.forEach((key, value) -> {
//                if (key.equals(labelColYes)) {
//                    String oppositeLabel = key.replace(labelColYes, labelColNo);
//
//                    double playFrequency = value.get(1); //we saved it as 2nd element
//                    mapYes.set(mapYes.get() * playFrequency);
//                    String s = "s";
//
//                } else if (key.equals(labelColNo)) {
//                    String oppositeLabel = key.replace(labelColNo, labelColYes);
//
//                    double playFrequency = value.get(1); //we saved it as 2nd element
//                    mapNo.set(mapNo.get() * playFrequency);
//                    String s = "s";
//
//                }
//
//            });
//        }

    }

    private static void multiplyAllColumnsToLabelColumn(AtomicReference<Double> labelMap, AtomicReference<Double> oppositeLabelMap,
                                                        String key, LinkedList<Double> value, String labelColNo) {
        double playFrequency = value.get(1); //we saved it as 2nd element
        labelMap.set(labelMap.get() * playFrequency);

        //get oppositeLike
        String oppositeKey = key.replace(key, labelColNo);
        double likelihoodOfLabel = getLikelihoodByLabel(labelRelativeFreq, oppositeKey);
        oppositeLabelMap.set(oppositeLabelMap.get() * likelihoodOfLabel);
        String s = "s";
    }

    public static double getLikelihoodByLabel(LinkedHashMap<String, LinkedList<Double>> labelRelativeFreq, String label) {
        LinkedList<Map.Entry<String, LinkedList<Double>>> entryList = new LinkedList<>(labelRelativeFreq.entrySet());
        int indexOfLabel = new ArrayList<>(labelRelativeFreq.keySet()).indexOf(label);

        String key = entryList.get(indexOfLabel).getKey();
        LinkedList<Double> value = entryList.get(indexOfLabel).getValue();

        String s = "s";
        return value.get(value.size() - 1);
    }

    private static void retrieveMapYesAndNo(AtomicReference<Double> mapYes, AtomicReference<Double> mapNo,
                                            String key, Double value, LinkedList<String> likelihoodOfElements) {

        if (key.contains(labelColYes)) {
            mapYes.set(mapYes.get() * value);
            retrieveDataByOppositeMapType(mapNo, key, value, labelColYes, labelColNo);
        } else if (key.contains(labelColNo)) {
            mapNo.set(mapNo.get() * value);
            retrieveDataByOppositeMapType(mapYes, key, value, labelColNo, labelColYes);

        }
    }

    private static void retrieveDataByOppositeMapType(AtomicReference<Double> currentMap, String key, Double value,
                                                      String currentLabel, String replaceableLabel) {
        String oppositeLabel = key.replace(currentLabel, replaceableLabel);
        if (likelihoodPerSetComb.containsKey(oppositeLabel)) {
            double getOppositeValue = likelihoodPerSetComb.get(oppositeLabel);
            currentMap.set(currentMap.get() * getOppositeValue);
            String s = "s";
        } else if (!likelihoodPerSetComb.containsKey(oppositeLabel)) {
            currentMap.set(currentMap.get() * value);
            String s = "s";
        }
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

                String s = "s";
            } else if (key.equals(labelColNo)) {
                double probability = getPriorProbabilityByRelativeFrequency(value);
                valueWithProbability = new LinkedList<>();
                valueWithProbability.add(Double.valueOf(value));
                valueWithProbability.add(probability);
                labelRelativeFreq.put(key, valueWithProbability);

                String s = "s";
            }
        });
    }

    private static String retrieveHyperParam(String knowledge) {
        String[] elems = knowledge.split("=");
        LinkedList<String> knowElements = new LinkedList<>(Arrays.asList(elems));
        if (knowElements.getFirst().equals(modeH)) {
            return knowElements.getLast();
        } else if (knowElements.getFirst().equals(modelH)) {
            return knowElements.getLast();
        }
        return null;
    }

    private static double retrieveHyperDoubleParam(String knowledge) {
        String[] elems = knowledge.split("=");
        LinkedList<String> knowElements = new LinkedList<>(Arrays.asList(elems));
        return Double.parseDouble(knowElements.getLast());
    }


    private static double getPriorProbabilityByRelativeFrequency(double nrToTest) {
        //Subtracting the first line (because it contains the name of the columns)
        double nrOfElementsInTotal = id3Elements.size() - 1;
        return nrToTest / nrOfElementsInTotal;
    }

    private static void likelihoodAndOutput() {
        computeMapHypothesis(likelihoodPerSetComb);

        StringBuilder maxOutput = new StringBuilder();
        for (String maxHypo : mapHypothesisConsoleResults) {
            String[] label = maxHypo.split("\\|");
            LinkedList<String> arr = new LinkedList<>(Arrays.asList(label));
            maxOutput.append(arr.getLast()).append(" ");
        }
        System.out.println(maxOutput);
        String s = "s";
    }

    public static void getID3() throws FileNotFoundException {
        Scanner interactive = new Scanner(new File(Constant.id3));

        while (interactive.hasNext()) {
            String knowledge = interactive.nextLine();

            if (knowledge.contains(modeH) && modeHyper == null) {
                modeHyper = retrieveHyperParam(knowledge);
            }

            if (knowledge.contains(modelH)) {
                modelHyper = retrieveHyperParam(knowledge);
            }

            if (knowledge.contains(max_depth)) {
                depthHyper = retrieveHyperDoubleParam(knowledge);
            }

            if (knowledge.contains(num_trees)) {
                nrTreesHyper = retrieveHyperDoubleParam(knowledge);
            }

            if (knowledge.contains(feature_ratio)) {
                featRatioHyper = retrieveHyperDoubleParam(knowledge);
            }

            if (knowledge.contains(example_ratio)) {
                exRatioHyper = retrieveHyperDoubleParam(knowledge);
            }

            getID3.add(knowledge);
        }

        String s = "s";

    }

}
