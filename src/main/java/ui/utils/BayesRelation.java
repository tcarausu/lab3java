package ui.utils;

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

}
