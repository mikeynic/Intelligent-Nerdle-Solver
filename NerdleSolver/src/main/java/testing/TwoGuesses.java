package testing;

import nerdle.game.Game;
import nerdle.solver.Solver;
import settings.Settings;

import java.util.ArrayList;
import java.util.List;

public class TwoGuesses {

    static int counter = 1;

    public static void main(String[] args) {
        // Find the set of pairs of equations that compliment each other
        List<Pair> l = getPairsOfUniqueEqs();
        for (Pair p : l) {
            System.out.println(p.left + " " + p.right);
        }
        System.out.println(l.size());
    }

    private static List<String> getSetOfUniqueEqs () {
        List<String> l = Settings.EQUATION_GENERATOR.retrieveAllEquations();
        List<String> out = new ArrayList<>();
        for (String s: l) {
            if (s.chars().distinct().count() == Settings.COLS) {
                out.add(s);
            }
        }
        return out;
    }

    private static List<Pair> getPairsOfUniqueEqs () {
        List<String> eqs = getSetOfUniqueEqs();
        List<Pair> out = new ArrayList<>();
        for (String e : eqs) {
            for (String x : eqs) {
                String woutEquals = e.replace("=", "");
                boolean isFine = true;
                for (int i=0; i<x.length(); i++) {
                    // if x contains no chars from e apart from equals, then proceed
                    if (woutEquals.contains(x.toCharArray()[i] + "")) {
                        isFine = false;
                    }
                }
                if (isFine)
                    out.add(new Pair(e, x));
            }
        }
        return out;
    }

    private static PairLogObj batchTest (Pair equation) {

        int totalGuesses = 0;
        int success = 0;

        List<String> equations = Settings.EQUATION_GENERATOR.retrieveAllEquations();

        for (String answer : equations) {
            Solver solver = new Solver(equations);
            Game game = new Game(answer);

            String feedback;
            String guess;

            // Custom setup for first guess
            guess = equation.left;
            feedback = game.processGuess(guess);
            solver.triesLeft--;
            solver.processFeedback(feedback, guess);

            // Custom setup for second guess
            guess = equation.right;
            feedback = game.processGuess(guess);
            solver.triesLeft--;
            solver.processFeedback(feedback, guess);

            for (int tries=3; tries<=Settings.TRIES; tries++) {
                // if statement for first guess.
                guess = solver.nextGuess(feedback);
                feedback = game.processGuess(guess);
                solver.processFeedback(feedback, guess);

                if (feedback.equals("g".repeat(Settings.COLS))) {
                    totalGuesses += tries;
                    success++;
                    break;
                }
                else if (tries == Settings.TRIES) {
                    System.out.println("tries: " + tries + "\neq: " + answer);
                }
            }
        }
        float avgGuess = (float) totalGuesses / (float) equations.size();
        System.out.println(counter++);
        return new PairLogObj(equation.left, equation.right, avgGuess, success);
    }
}