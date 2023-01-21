import nerdle.game.Game;
import nerdle.solver.Solver;
import settings.Settings;

import java.util.*;

public class Main {
    private final static  List<String> equations
            = Settings.EQUATION_GENERATOR.retrieveAllEquations();

    public static void main (String[] args) {
        if (Objects.equals(args[0], "PLAY")) {
            playGame();
        }
        if (Objects.equals(args[0], "SIMULATE")) {
            if (args.length >= 2) {
                simulateGame(args[1], true);
            }
            else {
                simulateRandomGame();
            }
        }
        if (Objects.equals(args[0], "BATCH")) {
            batchTest();
        }
    }

    private static void playGame () {
        Scanner scanner = new Scanner(System.in);
        List<String> equations = Settings.EQUATION_GENERATOR.retrieveAllEquations();
        Solver solver = new Solver(equations);
        int tries = Settings.TRIES;
        String guess;
        String feedback;

        if (Settings.COLS == 8) {
            // used precomputed first guess
            guess =  "64-29=35";
        }
        else {
            guess = solver.nextGuess("");
        }

        System.out.println("Turn 1...\nNext Guess: " + guess);
        System.out.println("Enter Feedback: ");
        feedback = scanner.nextLine();
        solver.processFeedback(feedback, guess);
        if (feedback.equals("g".repeat(Settings.COLS))) {
            System.out.println("\nY0U WON!\nThe hidden equation was: " + guess);
            scanner.close();
            return;
        }

        for (int turn=2; turn<tries; turn++) {
            System.out.println("Turn " + turn + "...");
            guess = solver.nextGuess(feedback);
            System.out.println("Next guess: " + guess);
            System.out.println("Enter Feedback: ");
            feedback = scanner.nextLine();
            solver.processFeedback(feedback, guess);
            if (feedback.equals("g".repeat(Settings.COLS))) {
                System.out.println("You won!\nThe hidden equation was: " + guess);
                scanner.close();
                return;
            }
        }
        System.out.println("You lost ...");
    }

    // Returns number of turns needed.
    private static int simulateGame (String hiddenEq, boolean printOn) {
        Solver solver = new Solver(equations);
        Game game = new Game(hiddenEq);
        int tries = Settings.TRIES;
        String guess;
        String feedback;

        if (Settings.COLS == 8) {
            // use precomputed first guess
            guess = "64-29=35";
        }
        else {
            guess = solver.nextGuess("");
        }
        if (printOn) System.out.println("Turn 1...\nNext Guess: " + guess);
        feedback = game.processGuess(guess);
        if (printOn) System.out.println("Feedback:   " + feedback);
        solver.processFeedback(feedback, guess);
        if (feedback.equals("g".repeat(Settings.COLS))) {
            if(printOn) System.out.println("\nY0U WON!\nThe hidden equation was: " + guess);
            return 1;
        }

        for (int turn=2; turn<=tries; turn++) {
            if (printOn) System.out.println("Turn " + turn + "...");
            guess = solver.nextGuess(feedback);
            if (printOn) System.out.println("Next guess: " + guess);
            feedback = game.processGuess(guess);
            solver.processFeedback(feedback, guess);
            if (printOn) System.out.println("Feedback:   " + feedback);
            if (feedback.equals("g".repeat(Settings.COLS))) {
                if(printOn) System.out.println("\nY0U WON!\nThe hidden equation was: " + guess);
                return turn;
            }
        }
        // System.out.println("You Lost!\nThe hidden equation was: " + hiddenEq);
        return Integer.MAX_VALUE;
    }

    private static void simulateRandomGame () {
        String hiddenEq = equations.get(new Random().nextInt(0, equations.size()));
        simulateGame(hiddenEq, true);
    }

    private static void batchTest () {
        int[] numList = new int[Settings.TRIES+1];
        int totalGuesses = 0;
        int success = 0;
        System.out.println("Size of dictionary: " + equations.size());

        for (String answer: equations) {
            int triesTaken = simulateGame(answer, false);
            if (triesTaken <= Settings.TRIES) {
                totalGuesses += triesTaken;
                success++;
                numList[triesTaken]++;
            }
            else{
                System.out.println("Failed for hidden equation: " + answer);
            }
        }
        System.out.println("Tries Frequencies:");
        for (int i=1; i< numList.length; i++) {
            System.out.println(i + ": " + numList[i]);
        }
        System.out.println("Completed equations: " + success + " / " + equations.size());
        System.out.println((float) totalGuesses / (float) equations.size());
    }
}
