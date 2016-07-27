package other;

import android.support.v4.util.Pair;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Lucien on 7/26/2016.
 */
public class PuzzleGenerator {

    private JavaInterpreter mJavaInterpreter;
    private Character[] mLetters = new Character[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'x', 'y', 'z'};
    private String[] mWords = new String[]{"phase", "annual", "astronaut", "astounding", "apparently", "puppet", "pneumatic", "bottle", "carnival", "sawdust"};
    private List<Character> mUnusedLetters = new ArrayList<>(Arrays.asList(mLetters));
    private List<Character> mUsedLetters = new ArrayList<>();
    private List<String> mUnusedWords = new ArrayList<>(Arrays.asList(mWords));
    private List<String> mUsedWords = new ArrayList<>();
    private List<DataType> mDataTypeCodeList = new ArrayList<>();

    public PuzzleGenerator() {
        mJavaInterpreter = new JavaInterpreter();
    }

    public List<String> getExpectedAnswer() {
        List<Object> compiledAnswerList = mJavaInterpreter.compileCSharpCode(getFinalCodeList());
        List<String> mCurrentPuzzleCorrectAnswer = new ArrayList<>();
        for (Object s : compiledAnswerList) {
            Log.e("Puzzlegenerator", s.toString());
            mCurrentPuzzleCorrectAnswer.add(s.toString());
        }
        return mCurrentPuzzleCorrectAnswer;
    }

    public List<String> getFinalCodeList() {
        List<String> codeList = new ArrayList<>();
        for (DataType dataType : mDataTypeCodeList) {
            Log.e("getFinalCodeList", dataType.getCodeLine());
            codeList.add(dataType.getCodeLine());
        }
        return codeList;
    }

    public void generatePuzzle(String level_title) {
        if(level_title.equals("Introduction")) {
            generateBasicPuzzle();
        }
        if(level_title.equals("The if-else Statement")) {
            generateIfElseStatementPuzzle();
        }
        if(level_title.equals("The for Loop")) {
            generateForLoopPuzzle();
        }
    }

    private void generateBasicPuzzle() {
        mDataTypeCodeList.add(generateRandomIntegerOperations(generateRandomIntegerCode()));
        mDataTypeCodeList.add(generateRandomIntegerOperations(generateRandomIntegerCode()));
        mDataTypeCodeList.add(generateRandomIntegerOperations(generateRandomIntegerCode()));
        mDataTypeCodeList.add(generateRandomDataTypeCode());
        mDataTypeCodeList.add(generateRandomDataTypeCode());
        mDataTypeCodeList.add(new DataType("WriteLine", "", generateRandomUsedLetter() + " + " + generateRandomUsedLetter()));
        mDataTypeCodeList.add(new DataType("ReadLine", "", ""));
    }

    private void generateForLoopPuzzle() {
        mDataTypeCodeList.add(generateRandomIntegerOperations(generateRandomIntegerCode()));
        mDataTypeCodeList.add(generateRandomIntegerOperations(generateRandomIntegerCode()));
        char forLoopChar = generateRandomUnusedLetter();
        int forLoopStartCount = generateRandomIntValue(0, 20);
        if (generateRandomBoolean()) {
            mDataTypeCodeList.add(new DataType("for", Character.toString(forLoopChar), "int " + forLoopChar + " = " + forLoopStartCount + "; " + forLoopChar + " <= " + (forLoopStartCount + 4) + "; " + forLoopChar + "++"));
        } else {
            mDataTypeCodeList.add(new DataType("for", Character.toString(forLoopChar), "int " + forLoopChar + " = " + forLoopStartCount + "; " + forLoopChar + " >= " + (forLoopStartCount - 4) + "; " + forLoopChar + "--"));
        }
        mDataTypeCodeList.add(generateRandomIntegerOperations(generateRandomIntegerCode()));
        mDataTypeCodeList.add(new DataType("WriteLine", "", Character.toString(generateRandomUsedLetter())));
        mDataTypeCodeList.add(new DataType("bracket", "", "}"));
        mDataTypeCodeList.add(new DataType("ReadLine", "", ""));
    }

    private void generateIfElseStatementPuzzle() {
        mDataTypeCodeList.add(generateRandomDataTypeCode());
        mDataTypeCodeList.add(generateRandomDataTypeCode());
        generateRandomIfCode();
        generateRandomElseCode();
        mDataTypeCodeList.add(new DataType("ReadLine", "", ""));
    }

    private void generateRandomIfCode() {
        if (mDataTypeCodeList.size() == 0) {
            mDataTypeCodeList.add(new DataType("if", "", generateRandomIntValue(0, 10) + generateRandomRelationalOperator() + generateRandomIntValue(0, 10)));
        } else {
            int random = generateRandomIntValue(0, mDataTypeCodeList.size() - 1);
            DataType randomCodeAbove = mDataTypeCodeList.get(random);
            switch (randomCodeAbove.getType()) {
                case "Boolean":
                    mDataTypeCodeList.add(new DataType("if", "", randomCodeAbove.getName()));
                    break;
                case "String":
                    mDataTypeCodeList.add(new DataType("if", "", randomCodeAbove.getName() + generateRandomEqualsOperator() + "\"" + generateRandomWord() + "\""));
                    break;
                case "char":
                    mDataTypeCodeList.add(new DataType("if", "", randomCodeAbove.getName() + generateRandomEqualsOperator() + "'" + generateRandomChar() + "'"));
                    break;
                case "int":
                    mDataTypeCodeList.add(new DataType("if", "", randomCodeAbove.getName() + generateRandomRelationalOperator() + generateRandomIntValue(0, 20)));
                    break;
                case "double":
                    mDataTypeCodeList.add(new DataType("if", "", randomCodeAbove.getName() + generateRandomRelationalOperator() + generateRandomDoubleValue(0, 10)));
                    break;
            }
        }
        mDataTypeCodeList.add(new DataType("WriteLine", "", Character.toString(generateRandomUsedLetter())));
    }

    private void generateRandomElseCode() {
        mDataTypeCodeList.add(new DataType("else", "", ""));
        mDataTypeCodeList.add(new DataType("WriteLine", "", Character.toString(generateRandomUsedLetter())));
        mDataTypeCodeList.add(new DataType("bracket", "", "}"));
    }

    private DataType generateRandomIntegerOperations(DataType dataType) {
        for (DataType type : mDataTypeCodeList) {
            if (type.getType().equals("int")) {
                char usedLetter = generateRandomUsedLetter();
                if (!dataType.getName().equals(Character.toString(usedLetter)))
                    dataType.setValue(dataType.getValue() + " " + generateRandomMathOperator() + " " + usedLetter);
            }
        }
        return dataType;
    }

    private DataType generateRandomDataTypeCode() {
        int random = generateRandomIntValue(0, 5);
        if (random == 0)
            return generateRandomIntegerCode();
        if (random == 1)
            return generateRandomDoubleCode();
        if (random == 2)
            return generateRandomCharCode();
        if (random == 3)
            return generateRandomStringCode();
        else
            return generateRandomBooleanCode();
    }

    private DataType generateRandomCharCode() {
        return new DataType("char", Character.toString(generateRandomUnusedLetter()), Character.toString(generateRandomChar()));
    }

    private DataType generateRandomDoubleCode() {
        return new DataType("double", Character.toString(generateRandomUnusedLetter()), Double.toString(generateRandomDoubleValue(0, 10)));
    }

    private DataType generateRandomStringCode() {
        return new DataType("String", Character.toString(generateRandomUnusedLetter()), generateRandomString());
    }

    private DataType generateRandomIntegerCode() {
        return new DataType("int", Character.toString(generateRandomUnusedLetter()), Integer.toString(generateRandomIntValue(0, 20)));
    }

    private DataType generateRandomBooleanCode() {
        return new DataType("Boolean", Character.toString(generateRandomUnusedLetter()), Boolean.toString(generateRandomBoolean()));
    }

    private String generateRandomRelationalOperator() {
        int random = generateRandomIntValue(0, 5);
        if (random == 0)
            return "==";
        if (random == 1)
            return "!=";
        if (random == 2)
            return ">";
        if (random == 3)
            return "<";
        if (random == 4)
            return ">=";
        else
            return "<=";
    }

    private String generateRandomMathOperator() {
        int random = generateRandomIntValue(0, 5);
        if (random == 0)
            return "+";
        if (random == 1)
            return "-";
        if (random == 2)
            return "/";
        else
            return "*";
    }

    private String generateRandomEqualsOperator() {
        int random = generateRandomIntValue(0, 1);
        if (random == 0)
            return "==";
        else
            return "!=";
    }

    private String generateRandomWord() {
        int random = generateRandomIntValue(0, 1);
        if (random == 0) {
            return generateRandomUsedWord();
        } else {
            return generateRandomUnusedWord();
        }
    }

    private String generateRandomUsedWord() {
        if (mUsedWords.size() == 0) {
            return generateRandomUnusedWord();
        } else {
            int random = generateRandomIntValue(0, mUsedWords.size());
            return mUsedWords.get(random);
        }
    }

    private String generateRandomUnusedWord() { //return random word from list
        if (mUnusedWords.size() == 0) {
            return generateRandomUsedWord();
        } else {
            int random = generateRandomIntValue(0, mUnusedWords.size());
            String randomWord = mUnusedWords.get(random);
            mUsedWords.add(randomWord);
            mUnusedWords.remove(random);
            return randomWord;
        }
    }

    private char generateRandomLetter() { //random return used or unused letter
        int random = generateRandomIntValue(0, 1);
        if (random == 0) {
            return generateRandomUsedLetter();
        } else {
            return generateRandomUnusedLetter();
        }
    }

    private char generateRandomUnusedLetter() { //return random letter from unused list
        if (mUnusedLetters.size() == 0) {
            return generateRandomUsedLetter();
        } else {
            int random = generateRandomIntValue(0, mUnusedLetters.size() - 1);
            char randomLetter = mUnusedLetters.get(random);
            mUsedLetters.add(randomLetter);
            mUnusedLetters.remove(random);
            return randomLetter;
        }
    }

    private char generateRandomUsedLetter() { //return random letter from used list
        if (mUsedLetters.size() == 0) {
            return generateRandomUnusedLetter();
        } else {
            int random = generateRandomIntValue(0, mUsedLetters.size() - 1);
            return mUsedLetters.get(random);
        }
    }

    private char generateRandomChar() {
        return mLetters[generateRandomIntValue(0, mLetters.length - 1)];
    }

    private String generateRandomString() {
        return mWords[generateRandomIntValue(0, mWords.length - 1)];
    }

    private boolean generateRandomBoolean() { //random return true or false
        return generateRandomIntValue(0, 1) == 1;
    }

    private int generateRandomIntValue(int min, int max) { //generates random value between min and max
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    private double generateRandomDoubleValue(double min, double max) {
        Random random = new Random();
        return Math.round((min + (max - min) * random.nextDouble()) * 100.0) / 100.0;
    }

    public class DataType {
        private String type, name, value;

        public DataType(String type, String name, String value) {
            this.type = type;
            this.name = name;
            this.value = value;
        }

        public String getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getCodeLine() {
            switch (type) {
                case "ReadLine":
                    return "Console.ReadLine();";
                case "WriteLine":
                    return "Console.WriteLine(" + value + ");";
                case "bracket":
                    return value;
                case "for":
                    return "for(" + value + ") { ";
                case "if":
                    return "if(" + value + ") { ";
                case "else":
                    return "} else {";
                case "char":
                    return type + " " + name + " = '" + value + "';";
                case "String":
                    return type + " " + name + " = \"" + value + "\";";
                case "int":
                    return type + " " + name + " = " + value + ";";
                case "double":
                    return type + " " + name + " = " + value + ";";
                case "Boolean":
                    return type + " " + name + " = " + value + ";";
                default:
                    return "";
            }
        }
    }
}
