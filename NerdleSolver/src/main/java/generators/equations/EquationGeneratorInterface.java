package generators.equations;

import java.util.List;

public interface EquationGeneratorInterface {
    public List<String> retrieveAllEquations ();
    public List<String> retrieveAllEquations (String filename);
    public List<String> generateAndSaveAllEquations();
    public List<String> generateAllEquations ();
}
