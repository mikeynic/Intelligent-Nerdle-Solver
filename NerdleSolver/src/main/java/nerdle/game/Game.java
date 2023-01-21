package nerdle.game;

import settings.Settings;

import java.util.HashMap;

public class Game {
    /*
     * THINGS TO DO:
     * TODO: implement an equation validity checker on the users guess
     *
     * */

    /*
     * Main class for the game:
     * This should spit out the colours given for each guess at the solution
     * */
    private final String solution;

    public Game(String solution) {
        this.solution = solution;
    }

    public String processGuess(String guess){
        HashMap<Character, Integer> solHS = occurrencesHashSet(solution);
        HashMap<Character, Integer> guessHS = occurrencesHashSet(guess);
        char[] outChars = new char[Settings.COLS];

        for (char symbol : guessHS.keySet()) {
            int quota;
            if (solHS.get(symbol) == null) {
                quota = 0;
            }
            else {
                quota = solHS.get(symbol);
            }
            markPGB(outChars, guess.toCharArray(), solution.toCharArray(), symbol, quota);
        }
        return new String(outChars);
    }

    private void markPGB(char[] outChars, char[] guessChars,
                         char[] solChars, char target, int quota) {

        for (int i=0; i<Settings.COLS; i++) {
            if (quota == 0) {
                break;
            }
            // mark greens, if any
            if (guessChars[i] == solChars[i]
                    && solChars[i] == target) {
                outChars[i] = 'g';
                quota--;
            }
        }

        for (int i=0; i<Settings.COLS; i++) {
            if (quota == 0){
                break;
            }
            // mark purples, if any left
            if (guessChars[i] == target
                    && outChars[i] != 'g'
                    && outChars[i] != 'b') {
                outChars[i] = 'p';
                quota--;
            }
        }

        for (int i=0; i<Settings.COLS; i++) {
            // Quota may be >0 if number of occurrences of a symbol in solution is larger
            //  than the guessed number of occurrences.
            // mark black
            if (guessChars[i] == target && outChars[i] == 0) {
                outChars[i] = 'b';
            }
        }
    }

    private HashMap<Character, Integer> occurrencesHashSet (String equations) {
        HashMap<Character, Integer> hashMap = new HashMap<>();
        for (char c : equations.toCharArray()) {
            hashMap.merge(c, 1, Integer::sum);
        }
        return hashMap;
    }
}