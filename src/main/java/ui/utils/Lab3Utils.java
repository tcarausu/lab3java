package ui.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

import static ui.utils.RegexOperator.*;
import static ui.utils.RegexOperator.example_ratio;

public class Lab3Utils {
    private static final LinkedList<String> getID3 = new LinkedList<>();
    private static String modeHyper;
    private static String modelHyper;
    private static double depthHyper;
    private static double nrTreesHyper;
    private static double featRatioHyper;
    private static double exRatioHyper;

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

    public static LinkedList<String> getGetID3() {
        try {
            getID3();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return getID3;
    }

    public static double log(double x, double b)
    {
        return  (Math.log(x) / Math.log(b));
    }
    public static double log2(double x)
    {
        return  (Math.log(x) / Math.log(2));
    }
}
