package nerdle.solver;

import org.apache.commons.lang3.StringUtils;
import settings.Settings;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class SolverFoundation {

    private final List<String> equations;
    public List<String> workingEquations;

    // public for testing
    public int triesLeft;

    public SolverFoundation (List<String> equations) {
        this.equations = equations;
        this.workingEquations = equations;
        this.triesLeft = Settings.TRIES;
    }

    public abstract String nextGuess (String feedback);

    public void processFeedback(String feedback, String guess) {
        int cols = Settings.COLS;
        char[] fChars = feedback.toCharArray();
        char[] gChars = guess.toCharArray();
        List<String> newWorkingEquations = new LinkedList<>(workingEquations);
        // Win condition
        if (feedback.equals("g".repeat(cols))) {
            return;
        }
        for (String eq: workingEquations) {
            boolean gSat, pSat, bSat;
            char[] eqChars = eq.toCharArray();
            for (int i=0; i<cols; i++) {
                gSat = gSatisfied(fChars[i], gChars[i], eqChars[i]);
                pSat = pSatisfied(i, eq, feedback, guess);
                bSat = bSatisfied(i, eq, feedback, guess);
                if(!(gSat && pSat && bSat)) {
                    newWorkingEquations.remove(eq);
                    break;
                }
            }
        }
        workingEquations = newWorkingEquations;
    }

    //==========HELPER METHODS==================================================
    String findAnsWith(HashMap<Character, Integer> charOccurrence) {
        String bestEq = null;
        int mostOcc = 0;
        int maxOcc = charOccurrence.size()-1;
        for (String s : equations) {
            int currOcc = 0;
            for (Map.Entry<Character, Integer> e: charOccurrence.entrySet()) {
                char c = e.getKey();
                int occ = e.getValue();
                if (StringUtils.countMatches(s, c) >= occ) {
                    currOcc++;
                }
            }
            if (currOcc > mostOcc) {
                mostOcc = currOcc;
                bestEq = s;
            }
            if (currOcc == maxOcc) {
                return s;
            }
        }
        return bestEq;
    }

    HashMap<Character, Integer> findDiffChars(String feedback) {
        int firstIdx = feedback.indexOf('b');
        int secondIdx = feedback.indexOf('b', firstIdx + 1);
        HashMap<Character, Integer> map = new HashMap<>();
        for (String s: workingEquations) {
            StringBuilder sb = new StringBuilder(s);
            sb.setCharAt(firstIdx, 'X');
            sb.setCharAt(secondIdx, 'X');
            String processed = sb.toString();
            processed = processed.replace("X", "");
            // +1 so value gives us the max. frequency of the given character
            int countFirst = StringUtils.countMatches(processed, s.charAt(firstIdx)) + 1;
            map.put(s.charAt(firstIdx), countFirst);
        }
        return map;
    }

    String findStringWithMostInfo() {
        int uniqueScore = 0;
        int uniqueIdx = -1;
        for (String eq: workingEquations) {
            int currUniqueScore = (int) eq.chars().distinct().count();
            if (currUniqueScore == Settings.COLS) {
                return eq;
            }
            if (currUniqueScore > uniqueScore) {
                uniqueIdx = workingEquations.indexOf(eq);
            }
        }
        if (uniqueIdx != -1) {
            return workingEquations.get(uniqueIdx);
        }
        // This will only happen if the equation entered by the user is wrong
        return null;
    }

    //==========SATISFIABILITY=============================================
    private boolean gSatisfied(char fChar, char gChar, char eqChar) {
        boolean gSat = true;
        if (fChar == 'g' && gChar != eqChar) {
            gSat = false;
        }
        return gSat;
    }

    public boolean pSatisfied (int idx, String eq, String feedback, String guess) {
        char eqChar = eq.charAt(idx);
        char fChar = feedback.charAt(idx);
        char gChar = guess.charAt(idx);
        if (fChar == 'p') {
            if (eqChar == gChar) {
                return false;
            }
            if (!eq.contains(gChar + "")) {
                return false;
            }

            int numP = 0;
            int numB = 0;
            int numG = 0;
            for (int i=0; i<eq.length(); i++) {
                if (guess.charAt(i) == gChar) {
                    if (feedback.charAt(i) == 'p') {
                        numP++;
                    }
                    if (feedback.charAt(i) == 'b') {
                        numB++;
                    }
                    if (feedback.charAt(i) == 'g') {
                        numG++;
                    }
                }
            }
            // the symbol's max freq == min freq == numP
            if (numB > 0) {
                if (StringUtils.countMatches(eq, gChar) != numP + numG) {
                    return false;
                }
            }
            // min freq == numP, max freq not known
            else {
                if (StringUtils.countMatches(eq, gChar) < numP + numG) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean bSatisfied (int idx, String eq, String feedback, String guess) {
        char eqChar = eq.charAt(idx);
        char fChar = feedback.charAt(idx);
        char gChar = guess.charAt(idx);
        if (fChar == 'b') {
            int freq = StringUtils.countMatches(guess, gChar);
            if (freq > 1) {
                if (eqChar == gChar) {
                    return false;
                }
                if (StringUtils.countMatches(eq, gChar)
                        >= StringUtils.countMatches(guess, gChar)) {
                    return false;
                }
            }
            if (freq == 1) {
                return !eq.contains(gChar + "");
            }
        }
        return true;
    }
}
