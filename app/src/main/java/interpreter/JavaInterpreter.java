package interpreter;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bsh.EvalError;
import bsh.Interpreter;
import models.Puzzle;

public class JavaInterpreter {

    private String mClassName = getClass().toString();

    public JavaInterpreter() {

    }

    public List compileCSharpCode(List<String> cSharpCode) {
        String javaCode = "import java.util.List; import java.util.ArrayList; List list = new ArrayList(); ";
        for (String cSharpLine : cSharpCode) {
            String javaLine = convertCSharpToJava(cSharpLine);
            if (javaLine != null) {
                javaCode = javaCode + javaLine;
            }
        }
        try {
            Interpreter interpreter = new Interpreter();
            Log.e(mClassName, javaCode);
            interpreter.eval(javaCode);
            return (List) interpreter.get("result");
        } catch (EvalError evalError) {
            Log.e(mClassName, evalError.getMessage());
            return null;
        }
    }

    private String convertCSharpToJava(String line) {
        line = line.trim();
        if (line.equals("Console.ReadLine();"))
            return "result = list;";
        if (line.contains("Console.WriteLine")) {
            int startIndex = line.indexOf("(");
            int endIndex = line.indexOf(")");
            line = "list.add(" + line.substring(startIndex + 1, endIndex) + ");";
        }
        return line + " ";
    }
}
