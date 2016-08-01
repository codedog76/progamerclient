package fragments.puzzles;


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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import activities.PuzzleActivity;
import models.Level;
import puzzle.JavaInterpreter;
import puzzle.PuzzleCodeBuilder;

/**
 * A simple {@link Fragment} subclass.
 */
public class SingleChoiceListFragment extends Fragment {

    private String mClassName = getClass().toString();
    private ListView mSingleSelectionListView;
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
            mArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.multiple_choice_row, toDisplayList);
            mSingleSelectionListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            mSingleSelectionListView.setAdapter(mArrayAdapter);
        } else {
            mParentPuzzleActivity.finish();
        }
    }

    public Boolean checkIfCorrect() {
        if (mCurrentPuzzleCodeBuilder.getPuzzleExpectedOutputType().equals("<code>")) {
            String checkedItem = "";
            SparseBooleanArray checked = mSingleSelectionListView.getCheckedItemPositions();
            for (int i = 0; i < checked.size(); i++) {
                if (checked.valueAt(i)) {
                    checkedItem = mSingleSelectionListView.getItemAtPosition(checked.keyAt(i)).toString();
                }
            }
            List<Object> expectedAnswer = mCurrentPuzzleCodeBuilder.getCSharpCodeToRunAnswer();
            return checkedItem.equals(expectedAnswer.get(0).toString());
        } else {
            List<String> codeToRun = new ArrayList<>();
            String checkedItem = "";
            SparseBooleanArray checked = mSingleSelectionListView.getCheckedItemPositions();
            for (int i = 0; i < checked.size(); i++) {
                if (checked.valueAt(i)) {
                    checkedItem = mSingleSelectionListView.getItemAtPosition(checked.keyAt(i)).toString();
                }
            }
            String[] splited = checkedItem.split("\\s+");
            Log.e("split", splited[1]);
            if (mCurrentPuzzleCodeBuilder.getProcessCodeSelected()) {
                for (Pair<String, String> pair : mCodePairList) {
                    if (pair.first.equals(checkedItem) || pair.first.equals("")) {
                        if(pair.second.contains("Console.WriteLine")) {
                            codeToRun.add("Console.WriteLine("+splited[1]+");");
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
    }

    private void assignViews(View view) {
        mSingleSelectionListView = (ListView) view.findViewById(R.id.singleSelectionListView);
    }

}
