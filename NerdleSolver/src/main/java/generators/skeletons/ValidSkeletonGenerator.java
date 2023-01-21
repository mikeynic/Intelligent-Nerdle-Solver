package generators.skeletons;

import com.fathzer.soft.javaluator.DoubleEvaluator;
import org.apache.commons.lang3.StringUtils;
import settings.Settings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Collections2.orderedPermutations;
import static com.google.common.primitives.Chars.asList;

public class ValidSkeletonGenerator extends AbstractSkeletonGenerator{

    /*
     * Generates all possible permutations of number-operator equations
     *  where an operator can never be followed by another operator.
     * Max number of operators for column num = 8 is three: XOXOX=Xx
     *
     * Implementation of StaryPatyk generation.
     * */
    @Override
    public ArrayList<String> genNumOpEquations() {
        // Generates all possible permutations of number-operator equations
        int cols = Settings.COLS;
        int halfWay = cols/2;
        ArrayList<String> numOpEquation = new ArrayList<>();
        // all equals positions
        for (int beforeEquals=halfWay; beforeEquals<cols-1; beforeEquals++) {
            // all amounts of operators
            for (int numOps = 0; numOps<beforeEquals; numOps++) {
                String startingState = "x".repeat(beforeEquals-numOps) + "o".repeat(numOps);
                String postfix = "=" + "x".repeat(cols-beforeEquals-1);

                Collection<List<Character>> perms =
                        orderedPermutations(new ArrayList<>(asList(startingState.toCharArray())));

                for (List<Character> l : perms) {
                    String prefix = StringUtils.join(l, null);
                    if (prefix.contains("oo")) {
                        continue;
                    }
                    numOpEquation.add(prefix + postfix);
                }
            }
        }
        return numOpEquation;
    }
}
