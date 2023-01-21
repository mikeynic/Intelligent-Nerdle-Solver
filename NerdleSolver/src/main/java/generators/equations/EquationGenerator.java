package generators.equations;

import com.fathzer.soft.javaluator.DoubleEvaluator;
import org.apache.commons.lang3.StringUtils;
import settings.Settings;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class EquationGenerator implements EquationGeneratorInterface {

    // LOGGING
    public int triedEquations = 0;
    public int numberOfEquations = 0;

    public EquationGenerator () {}

    @Override
    public List<String> retrieveAllEquations () {
        return retrieveAllEquations(Settings.FILE_NAME);
    }

    @Override
    public List<String> retrieveAllEquations(String filename) {
        File f = new File(filename);
        if (f.exists() && !f.isDirectory()) {
            try (FileInputStream fis = new FileInputStream(filename);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                return (LinkedList<String>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("ERROR RETRIEVING OBJECT: " + filename);
                e.printStackTrace();
            }
        }
        else { // create the file
            return generateAndSaveAllEquations();
        }
        return null;
    }

    @Override
    public List<String> generateAndSaveAllEquations () {
        List<String> equations = generateAllEquations();
        try (FileOutputStream fos = new FileOutputStream(Settings.FILE_NAME);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(equations);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return equations;
    }

    @Override
    public List<String> generateAllEquations () {
        List<String> equations = new LinkedList<>();
        for (String s: Settings.SKELETON_GENERATOR.genSkeletons()) {
            equations.addAll(enumerateForSkeleton(s));
        }
        return equations;
    }

    private List<String> enumerateForSkeleton(String s) {
        String[] sSplit = s.split("=");
        String s0 = sSplit[0];
        List<String> equations = new LinkedList<>();
        int len0 = StringUtils.countMatches(s0, "x");
        int lowerBound = 0; // createLowerBound(len0);
        int upperBound = Integer.parseInt("9".repeat(len0));
        // Originally:
        // for (int i=lowerBound; i<upperBound; i++)
        for (int i=lowerBound; i<=upperBound; i++) {
            char[] sChars = s0.toCharArray();
            char[] intChars = padWithZeros(i, len0).toCharArray();
            String sCopy = s0;
            // for each x in s, replace with int char
            for (int j=0; j<len0; j++) {
                sChars[sCopy.indexOf('x')] = intChars[j];
                sCopy = sCopy.replaceFirst("x", intChars[j] + "");
            }
            // validate equation and add to the list...
            String validated = validEquation(String.valueOf(sChars));
            if (validated != null) {
                equations.add(validated);
                // Logging
                numberOfEquations++;
            }
            // Logging
            triedEquations++;
        }
        return equations;
    }

    private String padWithZeros(int num, int len){
        String intAsString = Integer.toString(num);
        if(intAsString.length() < len){
            // TODO: validate the number of repeated zeros is correct
            return "0".repeat(len- intAsString.length()) + intAsString;
        }
        return intAsString;
    }

    private String validEquation(String equation) {
        double ans = new DoubleEvaluator().evaluate(equation);
        String fullEq = equation + "=" + (int) ans;

        if (equation.toCharArray()[0] == '-') {
            return null;
        }
        if (hasLoneOrLeadingZeros(equation)) {
            return null;
        }
        if (ans < 0) {
            return null;
        }
        if(ans - (int) ans != 0){
            return null;
        }
        if(fullEq.length() != Settings.COLS){
            return null;
        }
        return fullEq;
    }

    private boolean hasLoneOrLeadingZeros(String equation) {
        char[] chars = equation.toCharArray();
        String number = "0123456789";

        for (int i=0; i<chars.length; i++) {
            if (i == 0 && chars[0] == '0') {
                // since it will be either a leading/lone zero
                return true;
            }
            else if (chars[i] == '0'
                    && !number.contains(chars[i-1] + "")) {
                // if char before is not a number, then the char at index i is either a lone/leading 0
                return true;
            }
        }
        return false;
    }
}
