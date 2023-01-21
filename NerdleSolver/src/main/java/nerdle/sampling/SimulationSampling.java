package nerdle.sampling;

import nerdle.solver.DumbSolver;
import nerdle.game.Game;
import settings.Settings;

import java.util.ArrayList;
import java.util.List;

public class SimulationSampling {
    private final List<String> workingEquations;

    public SimulationSampling(List<String> workingEquations) {
        this.workingEquations = workingEquations;
    }

    public String bestGuess () {
        float lowest = Float.POSITIVE_INFINITY;
        String lowestEq = "";
        for (String s: getMostUniqueEqs(getHighestUniqueCount())) {
            float avg = averageNumberOfTurnsFor(s);
            if (avg < lowest) {
                lowest = avg;
                lowestEq = s;
            }
        }
        return lowestEq;
    }

    public int getHighestUniqueCount () {
        int highest = 0;
        for (String s: workingEquations) {
            int curr = (int) s.chars().distinct().count();
            if(curr > highest)
                highest = curr;
        }
        return highest;
    }

    public List<String> getMostUniqueEqs (int uniqueCount) {
        List<String> uniqueList = new ArrayList<>();
        for (String s: workingEquations)
            if (s.chars().distinct().count() == uniqueCount)
                uniqueList.add(s);
        return uniqueList;
    }

    public float averageNumberOfTurnsFor (String startingEq) {
        int totalGuesses = 0;
        for (String answer: workingEquations) {

            String feedback;
            String guess;

            DumbSolver solver = new DumbSolver(workingEquations);
            Game game = new Game(answer);

            // Custom setup for first guess
            guess = startingEq;
            feedback = game.processGuess(guess);
            solver.triesLeft--;
            solver.processFeedback(feedback, guess);

            for (int tries=1; tries<=Settings.TRIES; tries++) {
                guess = solver.nextGuess(feedback);
                feedback = game.processGuess(guess);
                solver.processFeedback(feedback, guess);
                if (feedback.equals("g".repeat(Settings.COLS))) {
                    totalGuesses += tries;
                    break;
                }
            }
        }
        return (float) totalGuesses/ (float) workingEquations.size();
    }
}


















