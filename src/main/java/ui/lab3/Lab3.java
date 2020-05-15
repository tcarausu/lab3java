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

        String s = "s";


    }
}
