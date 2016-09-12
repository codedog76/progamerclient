package puzzle;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import bsh.EvalError;
import bsh.Interpreter;

public class JavaInterpreter {

    private String mClassName = getClass().toString();
    private boolean mIsValidCode = false;
    private List<String> mCodeRun = new ArrayList<>();

    public JavaInterpreter() {
    }

    public boolean getIsValidCode() {
        return mIsValidCode;
    }

    public List<Object> compileCSharpCode(List<String> cSharpCode) {
        List<Object> compiledCodeList = new ArrayList<>();

        //mCodeRun.add("debug();\n");
        mCodeRun.add("import java.util.List;\n");
        mCodeRun.add("import java.util.ArrayList;\n");
        mCodeRun.add("List CW = new ArrayList();\n");
        mCodeRun.add("List CR = new ArrayList();\n");
        for (String cSharpLine : cSharpCode) {
            cSharpLine = cSharpLine.trim();
            if (cSharpLine.equals("Console.ReadLine();"))
                cSharpLine = "CR.addAll(CW);";
            if (cSharpLine.contains("Console.WriteLine")) {
                int startIndex = cSharpLine.indexOf("(");
                int endIndex = cSharpLine.indexOf(")");
                cSharpLine = "CW.add(" + cSharpLine.substring(startIndex + 1, endIndex) + ");";
            }
            mCodeRun.add(cSharpLine + "\n");
        }
        mCodeRun.add("result = CR;");
        String javaCode = "";
        for (String line : mCodeRun) {
            javaCode = javaCode + line;
        }
        Interpreter interpreter = new Interpreter();
        try {
            interpreter.eval(javaCode);
            compiledCodeList = (List) interpreter.get("result");
            mIsValidCode = true;
        } catch (EvalError evalError) {
            Log.e(mClassName, javaCode);
            Log.e(mClassName, evalError.getMessage());
            compiledCodeList.add(mCodeRun.get(evalError.getErrorLineNumber() - 1));
            compiledCodeList.add(evaluateEvalError(mCodeRun.get(evalError.getErrorLineNumber() - 1), evalError.getMessage()));
            mIsValidCode = false;
        }
        return compiledCodeList;
    }

    //a very basic error builder. Not perfect but should be fine in this case
    private String evaluateEvalError(String errorText, String message) {
        String dataType = "";
        if (errorText.contains("String ")) dataType = "String";
        if (errorText.contains("Boolean ")) dataType = "Boolean";
        if (errorText.contains("int ")) dataType = "int";
        if (errorText.contains("float ")) dataType = "float";
        if (errorText.contains("char ")) dataType = "char";
        String[] splitError = message.split(". . . ''");
        switch (splitError[1]) {
            case " : Typed variable declaration":
                if (dataType.equals("String"))
                    return "The value assigned to a string should be surrounded by double quotes\n(i.e. String a = \"fun\";)";
                if (dataType.equals("float"))
                    return "Only real numbers can be assigned to a float\n(i.e. float a = 3.14159;)";
                if (dataType.equals("int"))
                    return "Only whole numbers can be assigned to an int\n(i.e. int a = -1;)";
                if (dataType.equals("char"))
                    return "A char should only contain a single character surrounded by a single quotes\n(i.e. char = 'z';)";
                break;
            case " internal Error: error in wrapper cast":
                if (dataType.equals("Boolean"))
                    return "Only true and false can be assigned to a Boolean\n(i.e. Boolean = true;)";
                break;
            case " : Typed variable declaration : Void initializer.":
                String[] splitAssignment = errorText.split("=");
                if (splitAssignment[0].toLowerCase().trim().equals(splitAssignment[1].toLowerCase().trim()))
                    return "Cannot assign variable to itself\n(i.e. int a = a;)";
                else
                    return "Variable name '" + splitAssignment[1].trim() + "' has not been assigned";
            case " : Typed variable declaration : illegal use of undefined variable, class, or 'void' literal":
                return "Cannot reference to an undefined variable";
            default:
                return splitError[1];
        }
        return splitError[1];
    }
}
