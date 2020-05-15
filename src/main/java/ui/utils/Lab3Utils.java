package ui.utils;

import java.util.LinkedList;

public class Lab3Utils {

    public static LinkedList<Double> sunnyDay = new LinkedList<>();


    public static void setSunnyDay() {
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
