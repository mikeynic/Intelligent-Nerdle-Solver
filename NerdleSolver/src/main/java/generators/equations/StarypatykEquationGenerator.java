package generators.equations;

import com.fathzer.soft.javaluator.DoubleEvaluator;
import settings.Settings;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;

public class StarypatykEquationGenerator implements EquationGeneratorInterface{
    private final String digits0 = "0123456789";
    private final String opers = "+-*/";
    private String digitsOpers;
    private int NUM_COLS = 8; //default 8
    private DoubleEvaluator evaluator;

    private LinkedList<String> equations;

    // Constructors---------------------------------------------------------------------------------------------------------
    public StarypatykEquationGenerator() {
        initialiseClass();
    }

    public StarypatykEquationGenerator(int numberOfCols) {
        this.NUM_COLS = numberOfCols;
        initialiseClass();
    }

    // Class methods (private)----------------------------------------------------------------------------------------------
    private void initialiseClass () {
        evaluator = new DoubleEvaluator();
        digitsOpers = digits0 + opers;
    }

    private void verifyPerm (String perm, int ansMin, int ansMax) {
        double val = evaluator.evaluate(perm);
        if (ansMin <= val && val < ansMax && val == (int) val) {
            equations.add(perm + "=" + (int) val);
        }
    }

    private void generatePerms (int level, int maxLevel, String prevPerm, int ansMin, int ansMax) {

        String digits1 = "123456789";
        boolean lastCharIsOper;

        if (prevPerm.length() == 0)
            lastCharIsOper = true;
        else
            lastCharIsOper = opers.contains(prevPerm.toCharArray()[prevPerm.length() - 1] + "");

        if (level == 0)
            for (char c : digits1.toCharArray())
                generatePerms(1, maxLevel, prevPerm + c, ansMin, ansMax);

        else if (level == maxLevel)
            if (lastCharIsOper) // non-zero digit after operator
                for (char c : digits1.toCharArray())
                    verifyPerm(prevPerm + c, ansMin, ansMax);

            else // any digit after digit
                for (char c : digits0.toCharArray())
                    verifyPerm(prevPerm+c, ansMin, ansMax);
        else
        if (lastCharIsOper)
            for (char c : digits1.toCharArray())
                generatePerms(level+1, maxLevel, prevPerm + c, ansMin, ansMax);

        else
            for (char c : digitsOpers.toCharArray())
                generatePerms(level+1, maxLevel, prevPerm + c, ansMin, ansMax);
    }

    @Override
    public List<String> retrieveAllEquations() {
        return generateAllEquations();
    }

    @Override
    public List<String> retrieveAllEquations(String filename) {
        System.out.println("This method does not support filenames...");
        return null;
    }

    @Override
    public List<String> generateAndSaveAllEquations() {
        List<String> eqs = this.retrieveAllEquations();
        try (FileOutputStream fos = new FileOutputStream(Settings.FILE_NAME);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(eqs);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return eqs;
    }

    @Override
    public List<String> generateAllEquations() {
        // TODO: make it so that the NUM_COLS is used to enumerate however many equations...
        equations = new LinkedList<>();
        generatePerms(0, 3, "", 100, 1000);
        generatePerms(0, 4, "", 10, 100);
        generatePerms(0, 5, "", 0, 10);
        return equations;
    }
}
