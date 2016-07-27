package other;

import android.util.Log;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bsh.EvalError;
import bsh.Interpreter;

public class JavaInterpreter {

    private String mClassName = getClass().toString();

    public JavaInterpreter() {

    }

    public List compileCSharpCode(List<String> cSharpCode) {
        String javaCodePrefix = "import java.util.List; import java.util.ArrayList; List CW = new ArrayList(); List CR = new ArrayList(); ";
        String javaCodeSuffix = "result = CR;";
        for (String cSharpLine : cSharpCode) {
            String javaLine = convertCSharpToJava(cSharpLine);
            if (javaLine != null) {
                javaCodePrefix = javaCodePrefix + javaLine + " ";
            }
        }
        String javaCode = javaCodePrefix + javaCodeSuffix;
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
            return "CR.addAll(CW); ";
        if (line.contains("Console.WriteLine")) {
            Log.e("asd", line);
            Matcher for0Matcher = (Pattern.compile("\\((.*?)\\)")).matcher(line);
            String substring = "";
            if (for0Matcher.find()) {
                substring = for0Matcher.group(1);
            }
            int startIndex = line.indexOf("(");
            int endIndex = line.indexOf(")");
            Log.e("asd", substring);
            return "CW.add(" + line.substring(startIndex + 1, endIndex) + "); ";
        }
        return line;
    }


}
