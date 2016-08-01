package puzzle;

import android.util.Log;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bsh.EvalError;
import bsh.Interpreter;
import singletons.NetworkManagerSingleton;

public class JavaInterpreter {

    private String mClassName = getClass().toString();

    public JavaInterpreter() {
    }

    public List<Object> compileCSharpCode(List<String> cSharpCode) {
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
            Log.e("javaCode", javaCode);
            interpreter.eval(javaCode);
            return (List) interpreter.get("result");
        } catch (EvalError evalError) {
            Log.e(mClassName, evalError.getMessage());
        }
        return null;
    }
}
