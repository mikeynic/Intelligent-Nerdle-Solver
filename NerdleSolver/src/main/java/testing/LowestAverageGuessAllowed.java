package testing;

import nerdle.game.Game;
import nerdle.solver.Solver;
import settings.Settings;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class LowestAverageGuessAllowed {

    private static int counter=1;

    public static void main (String[] args) throws InterruptedException, ExecutionException {
        System.out.println(Runtime.getRuntime().availableProcessors());
        List<String> valid = Settings.EQUATION_GENERATOR.retrieveAllEquations("equationsValid8.dat");
        List<String> allowed = Settings.EQUATION_GENERATOR.retrieveAllEquations("equationsAllowed8.dat");

        valid = getEquationsUniqueChars(valid);
        allowed = getEquationsUniqueChars(allowed);

        System.out.println(valid.size());
        System.out.println(allowed.size());

        List<String> uniqueEqs = findDifference(allowed, valid);
        for (String s: uniqueEqs)
            System.out.println(s);
        System.out.println(uniqueEqs.size());

        //==========SET THESE==========
        int min=0; int max=678;
        //=============================
        List<LogObj> logObjList = new ArrayList<>();
        List<Callable<LogObj>> callableTasks = new ArrayList<>();

        for (String s: getSublist(uniqueEqs, min, max)) {
            callableTasks.add(() -> batchTest(s));
        }

        ExecutorService executor = Executors.newFixedThreadPool(6);
        List<Future<LogObj>> futures = executor.invokeAll(callableTasks);
        executor.shutdown();

        boolean allDone = false;
        while(!allDone) {
            allDone = true;
            for (Future<LogObj> future : futures)
                allDone &= future.isDone();
            Thread.sleep(1000);
        }

        for (Future<LogObj> future : futures) {
            logObjList.add(future.get());
        }
        saveLogObj(logObjList, min, max);
    }

    private static List<String> findDifference(List<String> allowed, List<String> valid) {
        List<String> diffs = new ArrayList<>();
        for (String s: allowed) {
            if (!valid.contains(s))
                diffs.add(s);
        }
        return diffs;
    }

    private static void printLogObj (LogObj lo) {
        System.out.println(lo.eq);
        System.out.println(lo.avg);
        System.out.println(lo.successRate + "/" + Settings.EQUATION_GENERATOR.retrieveAllEquations().size());
    }

    private static LogObj batchTest (String equation) {

        int totalGuesses = 0;
        int success = 0;

        List<String> equations = Settings.EQUATION_GENERATOR.retrieveAllEquations();

        for (String answer : equations) {
            Solver solver = new Solver(equations);
            Game game = new Game(answer);

            String feedback;
            String guess;

            // Custom setup for first guess
            guess = equation;
            feedback = game.processGuess(guess);
            solver.triesLeft--;
            solver.processFeedback(feedback, guess);

            for (int tries=2; tries<=Settings.TRIES; tries++) {
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
        return new LogObj(equation, avgGuess, success);
    }

    private static List<String> getEquationsUniqueChars(List<String>eqs) {
        List<String> uniqueEqs = new ArrayList<>();
        for (String s : eqs) {
            if(s.chars().distinct().count() == Settings.COLS) {
                uniqueEqs.add(s);
            }
        }
        return uniqueEqs;
    }

    private static List<String> get3Eqs() {
        List<String> l = new ArrayList<>();
        List<String> eqs = getEquationsUniqueChars(Settings.EQUATION_GENERATOR.retrieveAllEquations());
        l.add(eqs.get(eqs.indexOf("86-39=47")));
        l.add(eqs.get(eqs.indexOf("12+37=49")));
        l.add(eqs.get(eqs.indexOf("102-4=98")));
        l.add(eqs.get(eqs.indexOf("105-7=98")));
        return l;
    }

    private static List<String> getSublist (List<String> l, int min, int max) {
        List<String> newList = new ArrayList<>();
        for (int i=min; i<max; i++){
            newList.add(l.get(i));
        }
        return newList;
    }

    private static void saveLogObj (List<LogObj> l, int startIdx, int endIdx) {
        String filename = "Log" + startIdx + "-" + endIdx + ".csv";
        try (Writer writer =
                     new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), StandardCharsets.UTF_8))) {
            for (LogObj lo: l) {
                String toWrite = lo.eq + " , " + lo.avg + " , " + lo.successRate;
                writer.write(toWrite + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
