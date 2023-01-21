package nerdle.solver;

import nerdle.sampling.SimulationSampling;
import org.apache.commons.lang3.StringUtils;
import settings.Settings;

import java.util.*;

public class Solver extends SolverFoundation{

    public Solver(List<String> equations) {
        super(equations);
    }

    @Override
    public String nextGuess (String feedback) {
        String guess;
        if (StringUtils.countMatches(feedback, "g") == Settings.COLS-2
                && StringUtils.countMatches(feedback, "b") == 2
                // && triesLeft < workingEquations.size()
                && triesLeft >= 2
        ) {
            guess = findAnsWith(findDiffChars(feedback));
        }
        else {
            // Using the best guess out of all "most information guesses"
            if (workingEquations.size() < Settings.SIMULATION_THRESHOLD) {
                guess = new SimulationSampling(workingEquations).bestGuess();
            }
            else
                guess = findStringWithMostInfo();
        }
        triesLeft--;
        return guess;
    }
}
