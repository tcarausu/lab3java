package ui.lab3;


import ui.utils.Constant;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import static ui.utils.RegexOperator.*;


public class Lab3 {

    private static LinkedHashMap<String, LinkedList<String>> volleyballElements = new LinkedHashMap<>();
    private static LinkedHashMap<Integer, LinkedList<String>> columnsPos = new LinkedHashMap<>();
    private static LinkedList<String> vElements, firstColumn, secondColumn, thirdColumn, forthColumn, labelColumn;

    private static LinkedHashMap<String, LinkedHashMap<String, Integer>> weatherMap = new LinkedHashMap<>();
    private static LinkedHashMap<String, LinkedHashMap<String, Integer>> tempMap = new LinkedHashMap<>();
    private static LinkedHashMap<String, LinkedHashMap<String, Integer>> humMap = new LinkedHashMap<>();
    private static LinkedHashMap<String, LinkedHashMap<String, Integer>> windMap = new LinkedHashMap<>();
    private static LinkedHashMap<String, LinkedHashMap<String, Integer>> playMap = new LinkedHashMap<>();

    static LinkedHashMap<String, Integer> countElems = new LinkedHashMap<>();
    private static String currentElementsFromColumn;
    private static String modeHyper;
    private static String modelHyper;
    private static double depthHyper;
    private static double nrTreesHyper;
    private static double featRatioHyper;
    private static double exRatioHyper;

    public static void main(String[] args) throws FileNotFoundException {

        setColumnPosition();


        getVolleyball();
        getID3();
    }

    private static void setColumnPosition() {
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

    private static void getVolleyball() throws FileNotFoundException {
        Scanner interactive = new Scanner(new File(Constant.volleyball));

        while (interactive.hasNext()) {
            String knowledge = interactive.nextLine();

            String[] elems = knowledge.split(",");
            vElements = new LinkedList<>(Arrays.asList(elems));

            volleyballElements.put(knowledge, vElements);
            String s = "s";
        }

//        Iterator<String> iteratorK = volleyballElements.keySet().iterator();
//        String headerKey = iteratorK.next();

//        Iterator<LinkedList<String>> iteratorV = volleyballElements.values().iterator();
//        LinkedList<String> headerValue = iteratorV.next();
//        String classLabel = headerValue.getLast();

        volleyballElements.forEach((key, value) -> {
//            String currKey = key;
            LinkedList<String> currValues = value;

            firstColumn.add(currValues.getFirst());
            labelColumn.add(currValues.getLast());

            for (int i = currValues.size() - 2; i > 0; i--) {
                String currentEl = currValues.get(i);
                int finalI = i;
                columnsPos.forEach((keyPos, valuePos) -> {
                    if (finalI == keyPos) {
                        valuePos.add(currentEl);
                    }
                });
            }
        });


        AtomicInteger count = new AtomicInteger(1);

        LinkedList<String> weatherCount = new LinkedList<>();

        for (String weather : firstColumn) {
            retrieveNrOfDistinctElementsPerColumn(count, weatherCount, weather, col1Name);
        }

        LinkedHashMap<String, Integer> localElems = new LinkedHashMap<>(countElems);
        weatherMap.put(col1Name, localElems);
        countElems.clear();
        count = new AtomicInteger(1);

        for (String temp : secondColumn) {
            retrieveNrOfDistinctElementsPerColumn(count, weatherCount, temp, col2Name);
        }

        localElems = new LinkedHashMap<>(countElems);
        weatherMap.put(col2Name, localElems);
        countElems.clear();
        count = new AtomicInteger(1);

        for (String hum : thirdColumn) {
            retrieveNrOfDistinctElementsPerColumn(count, weatherCount, hum, col3Name);
        }

        localElems = new LinkedHashMap<>(countElems);
        weatherMap.put(col3Name, localElems);
        countElems.clear();
        count = new AtomicInteger(1);

        for (String wind : forthColumn) {
            retrieveNrOfDistinctElementsPerColumn(count, weatherCount, wind, col4Name);

        }

        localElems = new LinkedHashMap<>(countElems);
        weatherMap.put(col4Name, localElems);
        countElems.clear();
        count = new AtomicInteger(1);

        for (String play : labelColumn) {
            retrieveNrOfDistinctElementsPerColumn(count, weatherCount, play, labelColName);

        }

        localElems = new LinkedHashMap<>(countElems);
        weatherMap.put(labelColName, localElems);
        countElems.clear();

        //   setSunnyDay(); was thinking of setting some values not sure is even needed

        String s = "s";
    }

    private static void retrieveNrOfDistinctElementsPerColumn(AtomicInteger count, LinkedList<String> weatherCount, String weather, String colName) {
        if (!weatherCount.contains(weather)) {
            if (!weather.equals(colName)) {
                weatherCount.add(weather);
                countElems.put(weather, count.get());
            }
        } else if (weatherCount.contains(weather)) {
            if (!countElems.containsKey(weather)) {
                countElems.put(weather, count.getAndIncrement());
            } else {
                count = new AtomicInteger(countElems.get(weather));
                count.getAndIncrement();
                countElems.put(weather, count.get());
            }
        }
    }

    private static void getID3() throws FileNotFoundException {
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

    private static double concentrationVery(double x) {
        return Math.pow(x, 2); // Modifier Very is "variable" to the Power of 2
    }

    private static double dilatationMoreOrLess(double x) {
        return Math.pow(x, 0.5); // Modifier MoreOrLess is "variable" to the Power of 1/2
    }

    private static double unionOfSetsOr(double membershipAofX, double membershipBofX) {
        return Math.max(membershipAofX, membershipBofX); // OrOperator of MembershipA and MembershipB
    }

    private static double intersectionOfSetsAnd(double membershipAofX, double membershipBofX) {
        return Math.min(membershipAofX, membershipBofX); // AndOperator of MembershipA and MembershipB
    }

    private static double negationOfSetA(double x) {
        return 1 - x; // AndOperator of MembershipA and MembershipB
    }

    private static double approxEqualsMemberWithSet(double membershipAofV, LinkedList<Double> setVW) {
        if (setVW.size() == 2) {
            // approxEqualsMemberWithSet Math.min(membershipAofV,setVW)
            return Math.max(Math.min(membershipAofV, setVW.getFirst()), Math.min(membershipAofV, setVW.getLast()));
        } else {
            return -1;
        }
    }

}
