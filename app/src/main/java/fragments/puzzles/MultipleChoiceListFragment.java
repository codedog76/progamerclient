package fragments.puzzles;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.progamer.R;

import java.util.ArrayList;
import java.util.List;

import activities.PuzzleActivity;
import puzzle.JavaInterpreter;
import puzzle.PuzzleCodeBuilder;

public class MultipleChoiceListFragment extends Fragment {

    private String mClassName = getClass().toString();
    private ListView mListView;
    private ArrayAdapter<String> mArrayAdapter;
    private PuzzleActivity mParentPuzzleActivity;
    private PuzzleCodeBuilder mCurrentPuzzleCodeBuilder;
    private List<Pair<String, String>> mCodePairList;

    public MultipleChoiceListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_multiple_choice_list, container, false);
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
            mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            mListView.setAdapter(mArrayAdapter);
        } else {
            mParentPuzzleActivity.finish();
        }
    }

    public Boolean checkIfCorrect() {
        List<String> codeToRun = new ArrayList<>();
        List<String> checkedItems = new ArrayList<>();
        SparseBooleanArray checked = mListView.getCheckedItemPositions();
        for (int i = 0; i < checked.size(); i++) {
            if (checked.valueAt(i)) {
                checkedItems.add(mListView.getItemAtPosition(checked.keyAt(i)).toString());
            }
        }
        if (mCurrentPuzzleCodeBuilder.getProcessCodeSelected()) {
            for (Pair<String, String> pair : mCodePairList) {
                if (pair.first.equals(""))
                    codeToRun.add(pair.second);
                else {
                    if (checkIfChecked(pair.first, checkedItems)) {
                        codeToRun.add(pair.second);
                    }
                }
            }
        } else {
            for (Pair<String, String> pair : mCodePairList) {
                if (pair.first.equals(""))
                    codeToRun.add(pair.second);
                else if (!checkIfChecked(pair.first, checkedItems)) {
                    codeToRun.add(pair.second);
                }
            }
        }
        mParentPuzzleActivity.setCompiledCode(codeToRun);
        JavaInterpreter javaInterpreter = new JavaInterpreter();
        List<Object> compiledAnswer = javaInterpreter.compileCSharpCode(codeToRun);
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

    private boolean checkIfChecked(String first, List<String> checkedItems) {
        for (String checkedItem : checkedItems) {
            if (first.equals(checkedItem)) {
                return true;
            }
        }
        return false;
    }

    private void assignViews(View view) {
        mListView = (ListView) view.findViewById(R.id.list_view);
    }
}
