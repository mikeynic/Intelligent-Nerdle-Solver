package generators.skeletons;

import com.fathzer.soft.javaluator.DoubleEvaluator;
import org.apache.commons.lang3.StringUtils;
import settings.Settings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Collections2.orderedPermutations;
import static com.google.common.primitives.Chars.asList;

public abstract class AbstractSkeletonGenerator {
    public List<String> genSkeletons () {
        return getValidEqs(getValidSkelsWithOps());
    }

    //==========HELPER METHODS=========================
    private String aggregateEquationAndOps(char[] equation, String opString) {
        ArrayList<Character> opChars = new ArrayList<>();
        for(Character c : opString.toCharArray()) {
            opChars.add(c);
        }
        for (int i=0; i<equation.length; i++) {
            if (equation[i] == 'o') {
                equation[i] = opChars.get(0);
                opChars.remove(0);
            }
        }
        return String.valueOf(equation);
    }

    private int countOps(String s) {
        return StringUtils.countMatches(s, "o");
    }

    private List<String> getValidEqs (List<String> uncheckedEqs) {
        List<String> validEqs = new ArrayList<>();
        DoubleEvaluator evaluator = new DoubleEvaluator();
        for (String s : uncheckedEqs) {
            String[] sSplit = s.split("=");
            String sSplit0 = replaceXWith0(sSplit[0]);
            try {
                evaluator.evaluate(sSplit0);
                validEqs.add(s);
            }
            catch (IllegalArgumentException ignored) {}
        }
        return validEqs;
    }

    public abstract ArrayList<String> genNumOpEquations();

    private ArrayList<String> getValidSkelsWithOps () {
        ArrayList<String> numOpList = genNumOpEquations();
        ArrayList<String> fullExpansion = new ArrayList<>();
        // for each numOp equation, expand to include all permutations of operators
        for (String equation: numOpList) {
            int opCount = countOps(equation);
            List<String> opPerms = operatorPerms(opCount);

            for (String opString : opPerms) {
                String aggregate = aggregateEquationAndOps(equation.toCharArray(), opString);
                fullExpansion.add(aggregate);
            }
        }
        return fullExpansion;
    }

    private List<String> operatorPerms (int len) {
        char[] perm = new char[len];
        String ops = "+-/*";
        List<String> opPerms = new ArrayList<>();
        operatorPermutation(perm, 0, ops, opPerms);
        return opPerms;
    }

    // Helper method for operatorPerms
    private void operatorPermutation(char[] perm, int index, String ops, List<String> l) {
        if (index == perm.length) {
            l.add(new String(perm));
        }
        else {
            for (int i=0; i< ops.length(); i++) {
                perm[index] = ops.charAt(i);
                operatorPermutation(perm, index+1, ops, l);
            }
        }
    }

    private String replaceXWith0(String s) {
        char[] sChars = s.toCharArray();
        StringBuilder out = new StringBuilder();
        for(int i=0; i<s.length(); i++) {
            if (sChars[i] == 'x') {
                out.append('0');
            }
            else{
                out.append(sChars[i]);
            }
        }
        return out.toString();
    }
}
