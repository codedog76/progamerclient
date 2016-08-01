package puzzle;

import android.support.v4.util.Pair;
import android.util.Log;
import android.view.View;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PuzzleCodeBuilder {

    private String[] mStrings = new String[]{"\"phase\"", "\"annual\"", "\"astronaut\"", "\"astounding\"", "\"apparently\"", "\"puppet\"", "\"pneumatic\"", "\"bottle\"", "\"carnival\"", "\"sawdust\""};
    private String[] mCharacters = new String[]{"'i'", "'j'", "'k'", "'l'", "'m'", "'n'", "'o'", "'p'", "'q'", "'r'", "'s'", "'t'", "'u'", "'v'", "'w'"};
    private String[] mVariableNames = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "x", "y", "z"};
    private String[] mExcludedVariableNames = new String[]{"i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w"};
    private String[] mPrimitives = new String[]{"int", "double", "String", "char", "Boolean"};
    private String[] mArithmeticOperators = new String[]{"+", "-", "*", "/", "%"};
    private String[] mArithmeticOperatorsVary = new String[]{"++", "--"};
    private String[] mRelationalOperators = new String[]{"==", "!=", ">", "<", ">=", "<="};
    private String[] mLogicalOperators = new String[]{"&&", "||"};
    private String mLogicalOperatorNot = "!";
    private String[] mAssignmentOperators = new String[]{"=", "+=", "-=", "*=", "/=", "&=",};
    private List<String> mUnusedVariableNames = new ArrayList<>(Arrays.asList(mVariableNames));
    private List<String> mUsedVariableNames = new ArrayList<>();

    private List<String> mCSharpCodeToRun;
    private List<Object> mCSharpCodeToRunAnswer;

    private List<Pair<String, String>> mCSharpCodeToDisplayPuzzle;
    private String mCSharpCodeToDisplayExpectedOutput;

    private String mPuzzleFragmentType;
    private String mPuzzleExpectedOutputType;
    private boolean mProcessCodeSelected;
    private boolean mCodeCanRun;

    private JavaInterpreter mJavaInterpreter;

    public PuzzleCodeBuilder() {
        mJavaInterpreter = new JavaInterpreter();
    }

    public String getPuzzleExpectedOutputType() {
        return mPuzzleExpectedOutputType;
    }

    public String getCSharpCodeToDisplayExpectedOutput() {
        return mCSharpCodeToDisplayExpectedOutput;
    }

    public List<Pair<String, String>> getCSharpCodeToDisplayPuzzle() {
        return mCSharpCodeToDisplayPuzzle;
    }

    public String getPuzzleFragmentType() {
        return mPuzzleFragmentType;
    }

    public boolean getProcessCodeSelected() {
        return mProcessCodeSelected;
    }

    public List<Object> getCSharpCodeToRunAnswer() {
        return mCSharpCodeToRunAnswer;
    }

    /*
     *
     */
    public void processCSharpCode(String cSharpCode) {
        List<String> cSharpCodeAfterComments = removeComments(cSharpCode);
        List<String> cSharpCodeAfterHeaderTags = processHeaderTags(cSharpCodeAfterComments);
        List<String> cSharpCodeAfterTags = processCodeTags(cSharpCodeAfterHeaderTags);
        mCSharpCodeToRun = processTagsForRun(cSharpCodeAfterTags);
        mCSharpCodeToDisplayPuzzle = processTagsForDisplay(cSharpCodeAfterTags);
        mJavaInterpreter.compileCSharpCode(mCSharpCodeToRun);
        mCSharpCodeToRunAnswer = mJavaInterpreter.compileCSharpCode(mCSharpCodeToRun);
        processCodeToRunAnswer();
        processCodeToDisplayExpectedOutput();
        processCodeToDisplayPuzzle();
    }

    private void processCodeToDisplayPuzzle() {
        if (!mPuzzleFragmentType.equals("<truefalse>") && mPuzzleExpectedOutputType.equals("<code>")) {
            mCSharpCodeToDisplayPuzzle = new ArrayList<>();
            for (Object obj : mCSharpCodeToRunAnswer) {
                mCSharpCodeToDisplayPuzzle.add(new Pair<>(obj.toString(), ""));
            }
            for (int x = 0; x <= (6 - mCSharpCodeToRunAnswer.size()); x++) {
                if (isInteger(mCSharpCodeToDisplayPuzzle.get(0).first)) {
                    mCSharpCodeToDisplayPuzzle.add(new Pair<>(generateRandomInteger(), ""));
                } else if (isDouble(mCSharpCodeToDisplayPuzzle.get(0).first)) {
                    double toReplace = Double.parseDouble(mCSharpCodeToDisplayPuzzle.get(0).first);
                    mCSharpCodeToDisplayPuzzle.remove(0);
                    mCSharpCodeToDisplayPuzzle.add(new Pair<>(Double.toString(round(toReplace, 2)), ""));
                    mCSharpCodeToDisplayPuzzle.add(new Pair<>(generateRandomDouble(), ""));
                }
            }
            Collections.shuffle(mCSharpCodeToDisplayPuzzle);
        }
    }

    private boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    //Splits input string based on comment tag "//" and removes those tags
    //Returns a string array of split string
    private List<String> removeComments(String cSharpCode) {
        List<String> codeStringList = new ArrayList<>(Arrays.asList(cSharpCode.split("//")));
        codeStringList.remove(0);
        return codeStringList;
    }

    //Processes header tags, which determines the type of fragment for the code and what will be displayed in the expected output block of the puzzle
    //Returns a string list with header string object removed
    private List<String> processHeaderTags(List<String> cSharpCodeList) {
        String header = cSharpCodeList.get(0);
        if (header.contains("<single>") || header.contains("<multiple>") || header.contains("<rearrange>") || header.contains("<truefalse>") || header.contains("<result>") || header.contains("<code>")) {
            if (header.contains("<single>")) {
                mPuzzleFragmentType = "<single>";
                mProcessCodeSelected = !header.contains("<exclude>");
            } else if (header.contains("<multiple>")) {
                mPuzzleFragmentType = "<multiple>";
                mProcessCodeSelected = !header.contains("<exclude>");
            } else if (header.contains("<rearrange>"))
                mPuzzleFragmentType = "<rearrange>";
            else if (header.contains("<truefalse>")) {
                mPuzzleFragmentType = "<truefalse>";
                mPuzzleExpectedOutputType = "<code>";
                cSharpCodeList.remove(0);
                return cSharpCodeList;
            } else
                mPuzzleFragmentType = "<rearrange>";
            if (header.contains("<result>"))
                mPuzzleExpectedOutputType = "<result>";
            else if (header.contains("<code>") && (mPuzzleFragmentType.equals("<single>") || mPuzzleFragmentType.equals("<truefalse>")))
                mPuzzleExpectedOutputType = "<code>";
            else if (header.contains("<none>"))
                mPuzzleExpectedOutputType = "<none>";
            else
                mPuzzleExpectedOutputType = "<result>";
            cSharpCodeList.remove(0);
        } else {
            mPuzzleFragmentType = "<rearrange>";
            mPuzzleExpectedOutputType = "<result>";
        }
        return cSharpCodeList;
    }

    //Processes all code tags for each line of code in string list
    //Returns string list with tags processed
    private List<String> processCodeTags(List<String> cSharpCodeList) {
        List<String> cSharpCodeAfterTags = new ArrayList<>();
        for (int x = 0; x < cSharpCodeList.size(); x++) {
            String cSharpCodeLine = cSharpCodeList.get(x);

            cSharpCodeLine = checkForForLoop(cSharpCodeLine);
            cSharpCodeLine = checkForIfElseStatement(cSharpCodeLine);

            cSharpCodeLine = checkForFixedPrimitives(cSharpCodeLine);
            cSharpCodeLine = checkForRandomPrimitives(cSharpCodeLine);

            cSharpCodeLine = checkForFixedVariableNames(cSharpCodeLine);
            cSharpCodeLine = checkForRandomUnusedVariableNames(cSharpCodeLine);
            cSharpCodeLine = checkForRandomExcludedVariableNames(cSharpCodeLine);
            cSharpCodeLine = checkForRandomUsedVariableNames(cSharpCodeLine);

            cSharpCodeLine = checkForFixedArithmeticOperators(cSharpCodeLine);
            cSharpCodeLine = checkForRandomArithmeticOperators(cSharpCodeLine);

            cSharpCodeLine = checkForFixedArithmeticOperatorsVary(cSharpCodeLine);
            cSharpCodeLine = checkForRandomArithmeticOperatorsVary(cSharpCodeLine);

            cSharpCodeLine = checkForFixedRelationalOperators(cSharpCodeLine);
            cSharpCodeLine = checkForRandomRelationalOperators(cSharpCodeLine);

            cSharpCodeLine = checkForFixedLogicalOperators(cSharpCodeLine);
            cSharpCodeLine = checkForRandomLogicalOperators(cSharpCodeLine);

            cSharpCodeLine = checkForFixedAssignmentOperators(cSharpCodeLine);
            cSharpCodeLine = checkForRandomAssignmentOperators(cSharpCodeLine);

            cSharpCodeLine = checkForFixedValues(cSharpCodeLine);
            cSharpCodeLine = checkForRandomValues(cSharpCodeLine);
            cSharpCodeAfterTags.add(cSharpCodeLine);
        }
        return cSharpCodeAfterTags;
    }

    private List<Pair<String, String>> processTagsForDisplay(List<String> cSharpCodeList) {
        List<Pair<String, String>> cSharpCodeAfterHideTags = new ArrayList<>();
        for (int x = 0; x < cSharpCodeList.size(); x++) {
            String cSharpCodeLineDisplay = cSharpCodeList.get(x);
            cSharpCodeLineDisplay = checkForIncorrectValues(cSharpCodeLineDisplay);
            String cSharpCodeLineCode = cSharpCodeList.get(x);
            cSharpCodeLineCode = cSharpCodeLineCode.replaceAll("<hide>", "");
            cSharpCodeLineCode = cSharpCodeLineCode.replaceAll("</hide>", "");
            cSharpCodeLineDisplay = cSharpCodeLineDisplay.replaceAll("(?s)<hide>.*?</hide>", "");
            cSharpCodeAfterHideTags.add(new Pair<>(cSharpCodeLineDisplay, cSharpCodeLineCode));
        }
        return cSharpCodeAfterHideTags;
    }

    private List<String> processTagsForRun(List<String> cSharpCodeList) {
        List<String> cSharpCodeAfterHideTags = new ArrayList<>();
        for (int x = 0; x < cSharpCodeList.size(); x++) {
            String cSharpCodeLine = cSharpCodeList.get(x);
            cSharpCodeLine = cSharpCodeLine.replaceAll("<hide>", "");
            cSharpCodeLine = cSharpCodeLine.replaceAll("</hide>", "");
            if (!cSharpCodeLine.contains("<rwv>") && !cSharpCodeLine.contains("<wv>") && !cSharpCodeLine.contains("</wv>"))
                cSharpCodeAfterHideTags.add(cSharpCodeLine);
        }
        return cSharpCodeAfterHideTags;
    }

    private void processCodeToRunAnswer() {
        if (mPuzzleFragmentType.equals("<truefalse>")) {
            if (mCSharpCodeToRunAnswer != null) {
                mCSharpCodeToRunAnswer = new ArrayList<>();
                mCSharpCodeToRunAnswer.add("True");
            } else {
                mCSharpCodeToRunAnswer = new ArrayList<>();
                mCSharpCodeToRunAnswer.add("False");
            }
        }
    }

    private void processCodeToDisplayExpectedOutput() {
        mCSharpCodeToDisplayExpectedOutput = "";
        switch (mPuzzleExpectedOutputType) {
            case "<none>":
                mCSharpCodeToDisplayExpectedOutput = "";
                break;
            case "<code>":
                for (Pair<String, String> output : mCSharpCodeToDisplayPuzzle) {
                    String expected_output_line = output.first;
                    if (!expected_output_line.equals(""))
                        if (mCSharpCodeToRunAnswer.indexOf(output) == mCSharpCodeToRunAnswer.size() - 1)
                            mCSharpCodeToDisplayExpectedOutput = mCSharpCodeToDisplayExpectedOutput + expected_output_line;
                        else
                            mCSharpCodeToDisplayExpectedOutput = mCSharpCodeToDisplayExpectedOutput + expected_output_line + "\n";
                }
                break;
            case "<result>":
                for (Object output : mCSharpCodeToRunAnswer) {
                    String expected_output_line = output.toString();
                    if (mCSharpCodeToRunAnswer.indexOf(output) == mCSharpCodeToRunAnswer.size() - 1)
                        mCSharpCodeToDisplayExpectedOutput = mCSharpCodeToDisplayExpectedOutput + expected_output_line;
                    else
                        mCSharpCodeToDisplayExpectedOutput = mCSharpCodeToDisplayExpectedOutput + expected_output_line + "\n";
                }
                break;
            default:
                for (Object output : mCSharpCodeToRunAnswer) {
                    String expected_output_line = output.toString();
                    if (mCSharpCodeToRunAnswer.indexOf(output) == mCSharpCodeToRunAnswer.size() - 1)
                        mCSharpCodeToDisplayExpectedOutput = mCSharpCodeToDisplayExpectedOutput + expected_output_line;
                    else
                        mCSharpCodeToDisplayExpectedOutput = mCSharpCodeToDisplayExpectedOutput + expected_output_line + "\n";
                }
                break;
        }
    }

    private String checkForForLoop(String cSharpCodeLine) {
        if (cSharpCodeLine.contains("<for>") && cSharpCodeLine.contains("</for>")) {
            String variableName, value1, relationalOperator, value2, arithmeticOperatorVary;
            if (cSharpCodeLine.contains("<vn>") && cSharpCodeLine.contains("</vn>")) {
                String vn = getSubstringBetweenTags("<vn>", "</vn>", cSharpCodeLine);
                variableName = checkForFixedVariableNames(vn);
            } else {
                variableName = generateRandomUnusedVariableName();
            }
            if (cSharpCodeLine.contains("<aov>") && cSharpCodeLine.contains("</aov>")) {
                String aov = getSubstringBetweenTags("<aov>", "</aov>", cSharpCodeLine);
                arithmeticOperatorVary = checkForFixedValues(aov);
            } else {
                arithmeticOperatorVary = generateRandomArithmeticOperatorVary();
            }
            if (cSharpCodeLine.contains("<v>") && cSharpCodeLine.contains("</v>")) {
                String v = getSubstringBetweenTags("<v>", "</v>", cSharpCodeLine);
                value1 = checkForFixedValues(v);
            } else {
                value1 = generateRandomInteger();

            }
            if (arithmeticOperatorVary.equals("++")) {
                relationalOperator = "<";
                value2 = String.valueOf(Integer.parseInt(value1) + 3);
            } else {
                relationalOperator = ">";
                value2 = String.valueOf(Integer.parseInt(value1) - 3);
            }
            cSharpCodeLine = cSharpCodeLine.replaceAll("<for>.*<\\/for>", "for(int " + variableName + " = " + value1 + "; " + variableName + " " + relationalOperator + " " + value2 + "; " + variableName + arithmeticOperatorVary + ")");
        }
        return cSharpCodeLine;
    }

    private String checkForIfElseStatement(String cSharpCodeLine) {
        if (cSharpCodeLine.contains("<if>") && cSharpCodeLine.contains("</if>")) {
            String variableName1 = null, variableName2 = null, relationalOperator = null;
            if ((cSharpCodeLine.contains("<ro>") && cSharpCodeLine.contains("</ro>")) || cSharpCodeLine.contains("<rro>")) {
                if (cSharpCodeLine.contains("<ro>") && cSharpCodeLine.contains("</ro>")) {
                    String ro = getSubstringBetweenTags("<ro>", "</ro>", cSharpCodeLine);
                    relationalOperator = checkForFixedRelationalOperators(ro);
                } else {
                    relationalOperator = generateRandomRelationalOperator();
                }
                if (cSharpCodeLine.contains("<vn>") && cSharpCodeLine.contains("</vn>")) {
                    String vn = getSubstringBetweenTags("<vn>", "</vn>", cSharpCodeLine);
                    variableName1 = checkForFixedVariableNames(vn);
                    cSharpCodeLine = cSharpCodeLine.replaceFirst("(?s)<vn>.*?</vn>", "");
                }
                if (cSharpCodeLine.contains("<vn>") && cSharpCodeLine.contains("</vn>")) {
                    String vn = getSubstringBetweenTags("<vn>", "</vn>", cSharpCodeLine);
                    variableName2 = checkForFixedVariableNames(vn);
                    cSharpCodeLine = cSharpCodeLine.replaceFirst("(?s)<vn>.*?</vn>", "");
                }
                if (variableName1 == null) {
                    variableName1 = generateRandomUsedVariableName();
                }
                if (variableName2 == null) {
                    variableName2 = generateRandomUsedVariableName();
                }
                cSharpCodeLine = cSharpCodeLine.replaceAll("<if>.*<\\/if>", "if(" + variableName1 + relationalOperator + variableName2 + ")");
            } else {
                if (cSharpCodeLine.contains("<vn>") && cSharpCodeLine.contains("</vn>")) {
                    String vn = getSubstringBetweenTags("<vn>", "</vn>", cSharpCodeLine);
                    variableName1 = checkForFixedVariableNames(vn);
                    cSharpCodeLine = cSharpCodeLine.replaceFirst("(?s)<vn>.*?</vn>", "");
                } else if (cSharpCodeLine.contains("<rvn>")) {
                    variableName1 = generateRandomUsedVariableName();
                    cSharpCodeLine = cSharpCodeLine.replaceFirst("<rvn>", "");
                } else {
                    variableName1 = generateRandomUsedVariableName();
                }
                if (cSharpCodeLine.contains("<vn>") && cSharpCodeLine.contains("</vn>")) {
                    String vn = getSubstringBetweenTags("<vn>", "</vn>", cSharpCodeLine);
                    variableName2 = checkForFixedVariableNames(vn);
                    cSharpCodeLine = cSharpCodeLine.replaceFirst("(?s)<vn>.*?</vn>", "");
                }
                if (cSharpCodeLine.contains("<rvn>")) {
                    variableName2 = generateRandomUsedVariableName();
                    cSharpCodeLine = cSharpCodeLine.replaceFirst("<rvn>", "");
                }
                if (variableName2 != null) {
                    if (cSharpCodeLine.contains("<ro>") && cSharpCodeLine.contains("</ro>")) {
                        String ro = getSubstringBetweenTags("<ro>", "</ro>", cSharpCodeLine);
                        relationalOperator = checkForFixedRelationalOperators(ro);
                    } else {
                        relationalOperator = generateRandomRelationalOperator();
                    }
                }
                if (variableName2 == null) {
                    cSharpCodeLine = cSharpCodeLine.replaceAll("<if>.*<\\/if>", "if(" + variableName1 + ")");
                } else {
                    cSharpCodeLine = cSharpCodeLine.replaceAll("<if>.*<\\/if>", "if(" + variableName1 + relationalOperator + variableName2 + ")");
                }
            }
        }
        return cSharpCodeLine;
    }

    private String checkForFixedPrimitives(String cSharpCodeLine) {
        return cSharpCodeLine.replaceAll("<p>(.*)</p>", "$1");
    }

    private String checkForRandomPrimitives(String cSharpCodeLine) {
        return cSharpCodeLine.replaceAll("<rp>", generateRandomPrimitive());
    }

    private String checkForFixedVariableNames(String cSharpCodeLine) { //temp
        Pattern vn = Pattern.compile("<vn>(.*?)<\\/vn>");
        Matcher matcher = vn.matcher(cSharpCodeLine);
        while (matcher.find()) {
            mUsedVariableNames.add(moveVariableFromUnusedToUsed(matcher.group(1)));
        }
        return cSharpCodeLine.replaceAll("<vn>(.*)</vn>", "$1");
    }

    private String checkForRandomUnusedVariableNames(String cSharpCodeLine) {
        if (cSharpCodeLine.contains("<rvn>"))
            return cSharpCodeLine.replaceAll("<rvn>", generateRandomUnusedVariableName());
        else
            return cSharpCodeLine;
    }

    private String checkForRandomUsedVariableNames(String cSharpCodeLine) {
        if (cSharpCodeLine.contains("<ruvn>"))
            return cSharpCodeLine.replaceAll("<ruvn>", generateRandomUsedVariableName());
        else
            return cSharpCodeLine;
    }

    private String checkForRandomExcludedVariableNames(String cSharpCodeLine) {
        return cSharpCodeLine.replaceAll("<revn>", generateRandomExcludedVariableName());
    }

    private String checkForFixedArithmeticOperators(String cSharpCodeLine) {
        return cSharpCodeLine.replaceAll("<ao>(.*)</ao>", "$1");
    }

    private String checkForRandomArithmeticOperators(String cSharpCodeLine) {
        return cSharpCodeLine.replaceAll("<rao>", generateRandomArithmeticOperator());
    }

    private String checkForFixedArithmeticOperatorsVary(String cSharpCodeLine) {
        return cSharpCodeLine.replaceAll("<aov>(.*)</aov>", "$1");
    }

    private String checkForRandomArithmeticOperatorsVary(String cSharpCodeLine) {
        return cSharpCodeLine.replaceAll("<raov>", generateRandomArithmeticOperatorVary());
    }

    private String checkForFixedRelationalOperators(String cSharpCodeLine) {
        return cSharpCodeLine.replaceAll("<ro>(.*)</ro>", "$1");
    }

    private String checkForRandomRelationalOperators(String cSharpCodeLine) {
        return cSharpCodeLine.replaceAll("<rro>", generateRandomRelationalOperator());
    }

    private String checkForFixedLogicalOperators(String cSharpCodeLine) {
        return cSharpCodeLine.replaceAll("<lo>(.*)</lo>", "$1");
    }

    private String checkForRandomLogicalOperators(String cSharpCodeLine) {
        return cSharpCodeLine.replaceAll("<rlo>", generateRandomLogicalOperator());
    }

    private String checkForFixedAssignmentOperators(String cSharpCodeLine) {
        return cSharpCodeLine.replaceAll("<aso>(.*)</aso>", "$1");
    }

    private String checkForRandomAssignmentOperators(String cSharpCodeLine) {
        return cSharpCodeLine.replaceAll("<raso>", generateRandomAssignmentOperator());
    }

    private String checkForFixedValues(String cSharpCodeLine) { //done
        cSharpCodeLine = cSharpCodeLine.replaceAll("<v>(.*)</v>", "$1");
        cSharpCodeLine = cSharpCodeLine.replaceAll("<iv>(.*)</iv>", "$1");
        cSharpCodeLine = cSharpCodeLine.replaceAll("<dv>(.*)</dv>", "$1");
        cSharpCodeLine = cSharpCodeLine.replaceAll("<sv>(.*)</sv>", "$1");
        cSharpCodeLine = cSharpCodeLine.replaceAll("<cv>(.*)</cv>", "$1");
        cSharpCodeLine = cSharpCodeLine.replaceAll("<bv>(.*)</bv>", "$1");
        return cSharpCodeLine;
    }

    private String checkForIncorrectValues(String cSharpCodeLine) {
        cSharpCodeLine = cSharpCodeLine.replaceAll("<wv>(.*)</wv>", "$1");
        if (cSharpCodeLine.contains("int"))
            cSharpCodeLine = cSharpCodeLine.replaceAll("<rwv>", generateRandomString());
        if (cSharpCodeLine.contains("double"))
            cSharpCodeLine = cSharpCodeLine.replaceAll("<rwv>", generateRandomBoolean());
        if (cSharpCodeLine.contains("String"))
            cSharpCodeLine = cSharpCodeLine.replaceAll("<rwv>", generateRandomBoolean());
        if (cSharpCodeLine.contains("char"))
            cSharpCodeLine = cSharpCodeLine.replaceAll("<rwv>", generateRandomDouble());
        if (cSharpCodeLine.contains("Boolean"))
            cSharpCodeLine = cSharpCodeLine.replaceAll("<rwv>", generateRandomInteger());
        return cSharpCodeLine;
    }

    private String checkForRandomValues(String cSharpCodeLine) {
        while (cSharpCodeLine.contains("<riv>")) {
            cSharpCodeLine = cSharpCodeLine.replaceFirst("<riv>", generateRandomInteger());
        }
        while (cSharpCodeLine.contains("<rdv>")) {
            cSharpCodeLine = cSharpCodeLine.replaceFirst("<rdv>", generateRandomDouble());
        }
        while (cSharpCodeLine.contains("<rsv>")) {
            cSharpCodeLine = cSharpCodeLine.replaceFirst("<rsv>", generateRandomString());
        }
        while (cSharpCodeLine.contains("<rcv>")) {
            cSharpCodeLine = cSharpCodeLine.replaceFirst("<rcv>", generateRandomCharacter());
        }
        while (cSharpCodeLine.contains("<rbv>")) {
            cSharpCodeLine = cSharpCodeLine.replaceFirst("<rbv>", generateRandomBoolean());
        }
        if (cSharpCodeLine.contains("int"))
            cSharpCodeLine = cSharpCodeLine.replaceAll("<rv>", generateRandomInteger());
        if (cSharpCodeLine.contains("double"))
            cSharpCodeLine = cSharpCodeLine.replaceAll("<rv>", generateRandomDouble());
        if (cSharpCodeLine.contains("String"))
            cSharpCodeLine = cSharpCodeLine.replaceAll("<rv>", generateRandomString());
        if (cSharpCodeLine.contains("char"))
            cSharpCodeLine = cSharpCodeLine.replaceAll("<rv>", generateRandomCharacter());
        if (cSharpCodeLine.contains("Boolean"))
            cSharpCodeLine = cSharpCodeLine.replaceAll("<rv>", generateRandomBoolean());
        return cSharpCodeLine;
    }

    private int substringCount(String substring, String cSharpCodeLine) {
        int lastIndex = 0;
        int count = 0;
        while (lastIndex != -1) {
            lastIndex = cSharpCodeLine.indexOf(substring, lastIndex);

            if (lastIndex != -1) {
                count++;
                lastIndex += substring.length();
            }
        }
        return count;
    }

    private String moveVariableFromUnusedToUsed(String variableName) { //TODO: requires fix, check if not already in used variable names
        for (Iterator<String> unusedVariableNames = mUnusedVariableNames.listIterator(); unusedVariableNames.hasNext(); ) {
            String unusedVariableName = unusedVariableNames.next();
            if (unusedVariableName.equals(variableName)) {
                mUsedVariableNames.add(variableName);
                unusedVariableNames.remove();
            }
        }
        return variableName;
    }

    private String getSubstringBetweenTags(String tagOne, String tagTwo, String cSharpCodeLine) {
        return cSharpCodeLine.substring(cSharpCodeLine.indexOf(tagOne), cSharpCodeLine.indexOf(tagTwo) + tagTwo.length());
    }

    private String generateRandomUnusedVariableName() {
        if (mUnusedVariableNames.size() == 0) {
            return generateRandomUsedVariableName();
        } else {
            int random = generateRandomInt(0, mUnusedVariableNames.size() - 1);
            String randomUnusedVariableName = mUnusedVariableNames.get(random);
            mUsedVariableNames.add(randomUnusedVariableName);
            mUnusedVariableNames.remove(random);
            return randomUnusedVariableName;
        }
    }

    private String generateRandomUsedVariableName() {
        if (mUsedVariableNames.size() == 0) {
            return generateRandomUnusedVariableName();
        } else {
            int random = generateRandomInt(0, mUsedVariableNames.size() - 1);
            return mUsedVariableNames.get(random);
        }
    }

    private String generateRandomExcludedVariableName() {
        return mExcludedVariableNames[generateRandomInt(0, mExcludedVariableNames.length - 1)];
    }

    private String generateRandomInteger() {
        Random random = new Random();
        return Integer.toString(random.nextInt(20 - 1 + 1) + 1);
    }

    private String generateRandomDouble() {
        Random random = new Random();
        return Double.toString(Math.round((1 + (20 - 1) * random.nextDouble()) * 100.0) / 100.0);
    }

    private String generateRandomString() {
        return mStrings[generateRandomInt(0, mStrings.length - 1)];
    }

    private String generateRandomCharacter() {
        return mCharacters[generateRandomInt(0, mCharacters.length - 1)];
    }

    private String generateRandomBoolean() {
        return Boolean.toString(generateRandomInt(0, 1) == 1);
    }

    private String generateRandomPrimitive() {
        return mPrimitives[generateRandomInt(0, mPrimitives.length - 1)];
    }

    private String generateRandomArithmeticOperator() {
        return mArithmeticOperators[generateRandomInt(0, mArithmeticOperators.length - 1)];
    }

    private String generateRandomArithmeticOperatorVary() {
        return mArithmeticOperatorsVary[generateRandomInt(0, mArithmeticOperatorsVary.length - 1)];
    }

    private String generateRandomRelationalOperator() {
        return mRelationalOperators[generateRandomInt(0, mRelationalOperators.length - 1)];
    }

    private String generateRandomLogicalOperator() {
        return mLogicalOperators[generateRandomInt(0, mLogicalOperators.length - 1)];
    }

    private String generateRandomAssignmentOperator() {
        return mAssignmentOperators[generateRandomInt(0, mAssignmentOperators.length - 1)];
    }

    private int generateRandomInt(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }
}
