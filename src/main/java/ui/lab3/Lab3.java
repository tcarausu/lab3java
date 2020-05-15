package ui.lab3;


import ui.utils.Constant;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;



public class Lab3 {

    private static LinkedHashMap<String, LinkedList<String>> volleyballElements = new LinkedHashMap<>();
    private static LinkedList<String> vElements;
    private static String firstLine;
    private static LinkedList<String> firstLineSet;

    public static LinkedList<Double> sunnyDay = new LinkedList<>();


    public static void main(String[] args) throws FileNotFoundException {

        Scanner interactive = new Scanner(new File(Constant.volleyball));
//            Scanner interactive = new Scanner(new File(Constant.id3));
        LinkedList<String> inputsToTest = new LinkedList<>();

        while (interactive.hasNext()) {
            String knowledge = interactive.nextLine();

            String[] elems = knowledge.split(",");
            vElements = new LinkedList<>(Arrays.asList(elems));

            inputsToTest.add(knowledge);
            volleyballElements.put(knowledge, vElements);
            String s = "s";
        }

        Iterator<String> iteratorK = volleyballElements.keySet().iterator();
        String firstKey = iteratorK.next();

        Iterator<LinkedList<String>> iteratorV = volleyballElements.values().iterator();
        LinkedList<String> firstValue = iteratorV.next();

        setSunnyDay();

        String s = "s";


    }

    private static double concentrationVery (double x){
        return Math.pow(x,2); // Modifier Very is "variable" to the Power of 2
    }

    private static double dilatationMoreOrLess (double x){
        return Math.pow(x,0.5); // Modifier MoreOrLess is "variable" to the Power of 1/2
    }

     private static double unionOfSetsOr (double membershipAofX,double membershipBofX){
        return Math.max(membershipAofX,membershipBofX); // OrOperator of MembershipA and MembershipB
    }

     private static double intersectionOfSetsAnd (double membershipAofX,double membershipBofX){
        return Math.min(membershipAofX,membershipBofX); // AndOperator of MembershipA and MembershipB
    }

     private static double negationOfSetA (double x){
        return 1-x; // AndOperator of MembershipA and MembershipB
    }


    private static void setSunnyDay() {
        double clearSun = 1; // is TRUE!
        double closeToSun = 0.8;
        double decentSun = 0.6;
        double halfHalf = 0.5; // still true with the degree of 0.5
        double somewhatCloudy = 0.3;
        double closeToCloudy = 0.1;
        double totalCloudy = 0; // is False!

        sunnyDay.add(clearSun);
        sunnyDay.add(closeToSun);
        sunnyDay.add(halfHalf);
        sunnyDay.add(decentSun);
        sunnyDay.add(somewhatCloudy);
        sunnyDay.add(closeToCloudy);
        sunnyDay.add(totalCloudy);
    }
}
