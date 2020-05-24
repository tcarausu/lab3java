package ui.utils;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static ui.utils.RegexOperator.labelColNo;
import static ui.utils.RegexOperator.labelColYes;

public class BayesRelation {


    public static void ruleDenominator(double x, double y, double X) {
//        p(x) = p(x&X) = p(x&(y||~y)) = p((x&y) ||(x&~y)) =
//                p(x) = p(x&y) + p(x&~y)
        double H, E;
//        p(H | E) = (p(E | H) * p(H)) /
//                p(E);
//
//        p(E) = (p(E | H) * p(H)) + (p(E | !H) * p(!H))

//                p(H | E) = (p(E | H) * p(H)) /
//        (p(E | H) * p(H)) + (p(E | !H) * p(!H))

/*

        p(H) = 0.2 //Ivan has a flu
        p(!H) = 0.8 //Ivan doesn't a flu

        p(H) + p(!H) = ALWAYS = 1
        p(E | H) + p(!E|H) = ALWAYS = 1
        p(E | H) + p(E|!H) = doesn't need to be != 1


        p(E | H) = 0.75
        p(E | !H) = 0.2 //doesn't need to be !=1 (0.95)
        p(E) = (0.75)  _p(E | H)_  * (0.2) _p(H)_ + (0.2) _p(E | !H)_ *(0.8) _p(!H)_ =0.31

        p(H|E)=(0.75)  _p(E | H)_  * (0.2) _p(H)_  / (0.31) _p(E)_ = 0.48387
        p(H|E)=(1 - 0.75)  _p(!E | H)_  * (0.2) _p(H)_  / (1- 0.31) _p(!E)_ = 0.07246

*/

        /*
       p ( Hi ∣ E )=  (p(E1 E2 E3 En ∣ Hi) * p (Hi))/p(E1 E2 E3 En)

        */
    }

    public static void p(double H, double E) {

    }

    public static void p(double val) {

    }
//
//
//    private static void likelihoodAndOutput() {
//        computeMapHypothesis(likelihoodPerSetComb);
//
//        StringBuilder maxOutput = new StringBuilder();
//        for (String maxHypo : mapHypothesisConsoleResults) {
//            String[] label = maxHypo.split("\\|");
//            LinkedList<String> arr = new LinkedList<>(Arrays.asList(label));
//            maxOutput.append(arr.getLast()).append(" ");
//        }
//        System.out.println(maxOutput);
//        String s = "s";
//    }
//    private static void computeMapHypothesis(LinkedHashMap<String, Double> computedSetComb) {
//
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
//
//    }
//
//    private static void multiplyAllColumnsToLabelColumn(AtomicReference<Double> labelMap, AtomicReference<Double> oppositeLabelMap,
//                                                        String key, LinkedList<Double> value, String labelColNo) {
//        double playFrequency = value.get(1); //we saved it as 2nd element
//        labelMap.set(labelMap.get() * playFrequency);
//
//        //get oppositeLike
//        String oppositeKey = key.replace(key, labelColNo);
//        double likelihoodOfLabel = getLikelihoodByLabel(labelRelativeFreq, oppositeKey);
//        oppositeLabelMap.set(oppositeLabelMap.get() * likelihoodOfLabel);
//        String s = "s";
//    }
//
//    private static void retrieveMapYesAndNo(AtomicReference<Double> mapYes, AtomicReference<Double> mapNo,
//                                            String key, Double value, LinkedList<String> likelihoodOfElements) {
//
//        if (key.contains(labelColYes)) {
//            mapYes.set(mapYes.get() * value);
//            retrieveDataByOppositeMapType(mapNo, key, value, labelColYes, labelColNo);
//        } else if (key.contains(labelColNo)) {
//            mapNo.set(mapNo.get() * value);
//            retrieveDataByOppositeMapType(mapYes, key, value, labelColNo, labelColYes);
//
//        }
//    }
//

//    public static double getLikelihoodByLabel(LinkedHashMap<String, LinkedList<Double>> labelRelativeFreq, String label) {
//        LinkedList<Map.Entry<String, LinkedList<Double>>> entryList = new LinkedList<>(labelRelativeFreq.entrySet());
//        int indexOfLabel = new ArrayList<>(labelRelativeFreq.keySet()).indexOf(label);
//
//        String key = entryList.get(indexOfLabel).getKey();
//        LinkedList<Double> value = entryList.get(indexOfLabel).getValue();
//
//
//        return value.get(value.size() - 1);
//    }
//
//    private static void retrieveDataByOppositeMapType(AtomicReference<Double> currentMap, String key, Double value,
//                                                      String currentLabel, String replaceableLabel) {
//        String oppositeLabel = key.replace(currentLabel, replaceableLabel);
//        if (likelihoodPerSetComb.containsKey(oppositeLabel)) {
//            double getOppositeValue = likelihoodPerSetComb.get(oppositeLabel);
//            currentMap.set(currentMap.get() * getOppositeValue);
//
//        } else if (!likelihoodPerSetComb.containsKey(oppositeLabel)) {
//            currentMap.set(currentMap.get() * value);
//
//        }
//    }
public void usefulThings() {
//        int indexOfKey = new LinkedList<>(countPerSetComb.keySet()).indexOf(existingKey);
//        int lastPos = new LinkedList<>(countPerSetComb.keySet()).indexOf(lastCountPerSetCombKeyElement);
//        double countPerKey = fullCountPerSetComb.get(entryKey);
//
//        LinkedList<String> countPerSetCombKeySet = new LinkedList<>(countPerSetComb.keySet());
//        String lastCountPerSetCombKeyElement = countPerSetCombKeySet.getLast();

}

}
