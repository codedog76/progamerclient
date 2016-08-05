package puzzle;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import bsh.EvalError;
import bsh.Interpreter;

public class JavaInterpreter {

    private String mClassName = getClass().toString();
    private boolean mIsValidCode = false;

    public JavaInterpreter() {
    }

    public boolean getIsValidCode() {
        return mIsValidCode;
    }

    public List<Object> compileCSharpCode(List<String> cSharpCode) {
        List<Object> compiledCodeList = new ArrayList<>();
        String javaCodePrefix = "import java.util.List; import java.util.ArrayList; List CW = new ArrayList(); List CR = new ArrayList();";
        String javaCodeSuffix = "result = CR;";
        for (String cSharpLine : cSharpCode) {
            cSharpLine = cSharpLine.trim();
            if (cSharpLine.equals("Console.ReadLine();"))
                cSharpLine = "CR.addAll(CW);";
            if (cSharpLine.contains("Console.WriteLine")) {
                int startIndex = cSharpLine.indexOf("(");
                int endIndex = cSharpLine.indexOf(")");
                cSharpLine = "CW.add(" + cSharpLine.substring(startIndex + 1, endIndex) + ");";
            }
            javaCodePrefix = javaCodePrefix + cSharpLine + " ";
        }
        String javaCode = javaCodePrefix + javaCodeSuffix;
        try {
            Interpreter interpreter = new Interpreter();
            Log.e(mClassName, javaCode);
            interpreter.eval(javaCode);
            compiledCodeList = (List) interpreter.get("result");
            mIsValidCode = true;
        } catch (EvalError evalError) {
            Log.e(mClassName, evalError.getMessage());
            compiledCodeList.add(evaluateEvalError(evalError.getMessage()));
            mIsValidCode = false;
        }
        return compiledCodeList;
    }

    private String evaluateEvalError(String evalError) {
        String[] splitError = evalError.split(". . . '' :");
        if (splitError.length == 0)
            return evalError;
        if (splitError[1].equals(" Typed variable declaration")) {
            return "Invalid primitive assignment";
        }
        return splitError[1];
    }
}
