package ui.utils;

import java.util.LinkedList;

public class Bayes_PseudoM {

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
