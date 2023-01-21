package nerdle.solver;

import org.apache.commons.lang3.StringUtils;
import settings.Settings;

import java.util.List;

public class DumbSolver extends SolverFoundation{
    public DumbSolver(List<String> equations) {
        super(equations);
    }

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
            guess = findStringWithMostInfo();
        }
        triesLeft--;
        return guess;
    }
}