package settings;

import generators.equations.EquationGenerator;
import generators.equations.EquationGeneratorInterface;
import generators.skeletons.AbstractSkeletonGenerator;
import generators.skeletons.ValidSkeletonGenerator;

public class Settings {

    public static final int COLS = 8;
    public static final int TRIES = 6;
    public static final int SIMULATION_THRESHOLD = 500;
    public static final String FILE_NAME = "equations8.dat";
    public static final AbstractSkeletonGenerator SKELETON_GENERATOR =
            new ValidSkeletonGenerator();
    public static final EquationGeneratorInterface EQUATION_GENERATOR =
        new EquationGenerator();
}
