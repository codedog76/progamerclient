package fragments.puzzles;


import android.net.ParseException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.progamer.R;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import activities.PuzzleActivity;
import puzzle.JavaInterpreter;
import puzzle.PuzzleCodeBuilder;

/**
 * A simple {@link Fragment} subclass.
 */
public class SingleChoiceListFragment extends Fragment {

    private String mClassName = getClass().toString();
    private ListView mListView;
    private ArrayAdapter<String> mArrayAdapter;
    private PuzzleActivity mParentPuzzleActivity;
    private PuzzleCodeBuilder mCurrentPuzzleCodeBuilder;
    private List<Pair<String, String>> mCodePairList;

    public SingleChoiceListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_single_choice_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assignViews(view);
        mParentPuzzleActivity = (PuzzleActivity) getActivity();
        mCurrentPuzzleCodeBuilder = mParentPuzzleActivity.getCurrentPuzzleCodeBuilder();
        loadData();
    }

    private void loadData() {
        mCodePairList = mCurrentPuzzleCodeBuilder.getCSharpCodeToDisplayPuzzle();
        if (mCodePairList != null && mCodePairList.size() > 0) {
            List<String> toDisplayList = new ArrayList<>();
            for (Pair<String, String> pair : mCodePairList) {
                if (pair.first != null && !pair.first.equals(""))
                    toDisplayList.add(pair.first);
            }
            mArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.item_checkbox, toDisplayList);
            mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            mListView.setAdapter(mArrayAdapter);
        } else {
            mParentPuzzleActivity.finish();
        }
    }

    public Boolean checkIfCorrect() {
        if (mCurrentPuzzleCodeBuilder.getPuzzleExpectedOutputType().equals("<code>")) {
            String checkedItem = "";
            SparseBooleanArray checked = mListView.getCheckedItemPositions();
            if(checked.size()==0) {
                mParentPuzzleActivity.showNoSelectionAlert();
                return null;
            }
            for (int i = 0; i < checked.size(); i++) {
                if (checked.valueAt(i)) {
                    checkedItem = mListView.getItemAtPosition(checked.keyAt(i)).toString();
                }
            }
            if (checkedItem.equals("")) return false;
            List<String> names = mCurrentPuzzleCodeBuilder.getCSharpCodeToRun();
            mParentPuzzleActivity.setCompiledCode(names);
            List<Object> expectedAnswer = mCurrentPuzzleCodeBuilder.getCSharpCodeToRunAnswer();
            mParentPuzzleActivity.setCompiledResult(expectedAnswer);
            String answer = "";
            String floatChecked = "";
            try {
                float floatValue = Float.parseFloat(expectedAnswer.get(0).toString());
                answer = String.valueOf(round(floatValue, 2));
                floatValue = Float.parseFloat(checkedItem);
                floatChecked = String.valueOf(floatValue);
            } catch (NumberFormatException e) {
                Log.e("ParseException", expectedAnswer.get(0).toString());
            }
            return floatChecked.toLowerCase().trim().equals(answer.toLowerCase().trim());
        } else {
            List<String> codeToRun = new ArrayList<>();
            String checkedItem = "";
            SparseBooleanArray checked = mListView.getCheckedItemPositions();
            if(checked.size()==0) {
                mParentPuzzleActivity.showNoSelectionAlert();
                return null;
            }
            for (int i = 0; i < checked.size(); i++) {
                if (checked.valueAt(i)) {
                    checkedItem = mListView.getItemAtPosition(checked.keyAt(i)).toString();
                }
            }
            if (checkedItem.equals("")) return false;
            String[] splited = checkedItem.split("\\s+");
            if (mCurrentPuzzleCodeBuilder.getProcessCodeSelected()) {
                for (Pair<String, String> pair : mCodePairList) {
                    if (pair.first.equals(checkedItem) || pair.first.equals("")) {
                        if (pair.second.contains("Console.WriteLine")) {
                            codeToRun.add("Console.WriteLine(" + splited[1] + ");");
                        } else {
                            codeToRun.add(pair.second);
                        }
                    }
                }
            } else {
                for (Pair<String, String> pair : mCodePairList) {
                    if (!pair.first.equals(checkedItem)) {
                        codeToRun.add(pair.second);
                    }
                }
            }
            mParentPuzzleActivity.setCompiledCode(codeToRun);
            JavaInterpreter javaInterpreter = new JavaInterpreter();
            List<Object> compiledAnswer = javaInterpreter.compileCSharpCode(codeToRun);
            mParentPuzzleActivity.setCompiledResult(compiledAnswer);
            if (compiledAnswer == null || compiledAnswer.size() == 0) {
                return false;
            }
            List<Object> expectedAnswer = mCurrentPuzzleCodeBuilder.getCSharpCodeToRunAnswer();
            for (int x = 0; x < compiledAnswer.size(); x++) {
                if (!String.valueOf(compiledAnswer.get(x)).equals(String.valueOf(expectedAnswer.get(x)))) {
                    return false;
                }
            }
            return true;
        }
    }

    private float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    private void assignViews(View view) {
        mListView = (ListView) view.findViewById(R.id.list_view);
    }

}
