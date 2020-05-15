package ui.lab3;


import ui.utils.Constant;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static ui.utils.Lab3Utils.setSunnyDay;


public class Lab3 {

    private static LinkedHashMap<String, LinkedList<String>> volleyballElements = new LinkedHashMap<>();
    private static LinkedList<String> vElements;
    private static String firstLine;
    private static LinkedList<String> firstLineSet;


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
