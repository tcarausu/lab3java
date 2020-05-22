package ui.lab3;


import ui.utils.Constant;
import ui.utils.ID3Element;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static ui.utils.RegexOperator.*;


public class Lab3 {
    private static final LinkedHashMap<String, LinkedList<String>> lineToVbElements = new LinkedHashMap<>();
    private static final LinkedHashMap<Integer, LinkedList<String>> columnsPos = new LinkedHashMap<>();
    private static LinkedList<String> vElements, firstColumn, secondColumn, thirdColumn, forthColumn, labelColumn;

    private static final LinkedHashMap<String, LinkedHashMap<String, Integer>> volleyBallMatchMap = new LinkedHashMap<>();
    private static final LinkedList<ID3Element> id3elements = new LinkedList<>();

    private static LinkedHashMap<String, Integer> countElements = new LinkedHashMap<>();
    private static String currentElementsFromColumn;
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


    public static void main(String[] args) throws FileNotFoundException {

        setColumnPosition();


        getVolleyball();
        getID3();
    }

    public static void setColumnPosition() {
        firstColumn = new LinkedList<>();
        secondColumn = new LinkedList<>();
        thirdColumn = new LinkedList<>();
        forthColumn = new LinkedList<>();
        labelColumn = new LinkedList<>();

        columnsPos.put(0, firstColumn);
        columnsPos.put(1, secondColumn);
        columnsPos.put(2, thirdColumn);
        columnsPos.put(3, forthColumn);
        columnsPos.put(4, labelColumn);
    }

    public static void getVolleyball() throws FileNotFoundException {
        Scanner interactive = new Scanner(new File(Constant.volleyball));

        while (interactive.hasNext()) {
            String knowledge = interactive.nextLine();

            String[] elems = knowledge.split(",");
            vElements = new LinkedList<>(Arrays.asList(elems));

            lineToVbElements.put(knowledge, vElements);
            String s = "s";
        }

        lineToVbElements.forEach((key, value) -> {
            firstColumn.add(value.getFirst());
            labelColumn.add(value.getLast());

            for (int i = value.size() - 2; i > 0; i--) {
                String currentEl = value.get(i);
                int finalI = i;
                columnsPos.forEach((keyPos, valuePos) -> {
                    if (finalI == keyPos) {
                        valuePos.add(currentEl);
                    }
                });
            }
        });


        for (int i = 1; i < lineToVbElements.size(); i++) {
            String weather = firstColumn.get(i);
            String temperature = secondColumn.get(i);
            String humidity = thirdColumn.get(i);
            String wind = forthColumn.get(i);
            String play = labelColumn.get(i);
            ID3Element element = new ID3Element(weather, temperature, humidity, wind, play);
            id3elements.add(element);
        }

        getNrOfElementsForEachValuePerColumn();

        getNrByLabelCol();

        for (ID3Element element : id3elements) {
            String weather = element.getWeather();
            String temp = element.getTemp();
            String hum = element.getHum();
            String wind = element.getWind();
            String play = element.getPlay();

            String weatherLikelihood = weather.concat("|" + play);
            String tempLikelihood = temp.concat("|" + play);
            String humLikelihood = hum.concat("|" + play);
            String windLikelihood = wind.concat("|" + play);

            getPlayCountPerSetCombination(weatherLikelihood);

            getPlayCountPerSetCombination(tempLikelihood);

            getPlayCountPerSetCombination(humLikelihood);

            getPlayCountPerSetCombination(windLikelihood);

            String s = "s";

        }

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


        String s = "s";
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

    private static void getNrOfElementsForEachValuePerColumn() {
        AtomicInteger count = new AtomicInteger(1);

        LinkedList<String> weatherCount = new LinkedList<>();

        for (String weather : firstColumn) {
            retrieveNrOfDistinctElementsPerColumn(count, weatherCount, weather, col1Name);
        }

        LinkedHashMap<String, Integer> localElems = new LinkedHashMap<>(countElements);
        volleyBallMatchMap.put(col1Name, localElems);
        countElements.clear();
        count = new AtomicInteger(1);

        for (String temp : secondColumn) {
            retrieveNrOfDistinctElementsPerColumn(count, weatherCount, temp, col2Name);
        }

        localElems = new LinkedHashMap<>(countElements);
        volleyBallMatchMap.put(col2Name, localElems);
        countElements.clear();
        count = new AtomicInteger(1);

        for (String hum : thirdColumn) {
            retrieveNrOfDistinctElementsPerColumn(count, weatherCount, hum, col3Name);
        }

        localElems = new LinkedHashMap<>(countElements);
        volleyBallMatchMap.put(col3Name, localElems);
        countElements.clear();
        count = new AtomicInteger(1);


        for (String wind : forthColumn) {
            retrieveNrOfDistinctElementsPerColumn(count, weatherCount, wind, col4Name);
        }

        localElems = new LinkedHashMap<>(countElements);
        volleyBallMatchMap.put(col4Name, localElems);
        countElements.clear();
        count = new AtomicInteger(1);


        for (String play : labelColumn) {
            retrieveNrOfDistinctElementsPerColumn(count, weatherCount, play, labelColName);
        }

        localElems = new LinkedHashMap<>(countElements);
        volleyBallMatchMap.put(labelColName, localElems);
        deriveLabelColumnProbabilities(localElems);
        countElements.clear();

    }

    private static double getPriorProbabilityByRelativeFrequency(double nrToTest) {
        //Subtracting the first line (because it contains the name of the columns)
        double nrOfElementsInTotal = lineToVbElements.size() - 1;
        return nrToTest / nrOfElementsInTotal;
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

    public static void getID3() throws FileNotFoundException {
        Scanner interactive = new Scanner(new File(Constant.id3));
        LinkedList<String> getID3 = new LinkedList<>();

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
