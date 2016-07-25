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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import activities.PuzzleActivity;
import interpreter.JavaInterpreter;
import models.Puzzle;

public class MultipleChoiceListFragment extends Fragment {

    private ListView multipleSelectionListView;
    private ArrayAdapter<String> mArrayAdapter;
    private Puzzle mCurrentPuzzle;

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
        loadData();
    }

    private void loadData() {
        PuzzleActivity puzzleActivity = (PuzzleActivity) getActivity();
        mCurrentPuzzle = puzzleActivity.getSelectedPuzzle();
        List<String> puzzleData = mCurrentPuzzle.getPuzzleData();
        mArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_multiple_choice, puzzleData);
        multipleSelectionListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        multipleSelectionListView.setAdapter(mArrayAdapter);
    }

    public boolean checkIfCorrect() {
        List<String> cSharpCode = new ArrayList<>();
        SparseBooleanArray checked = multipleSelectionListView.getCheckedItemPositions();
        for (int i = 0; i < checked.size(); i++) {
            if (checked.valueAt(i)) {
                String tag = multipleSelectionListView.getItemAtPosition(checked.keyAt(i)).toString();
                cSharpCode.add(tag);
            }
        }
        JavaInterpreter javaInterpreter = new JavaInterpreter();
        List compiledAnswer = javaInterpreter.compileCSharpCode(cSharpCode);
        Log.e("ASD", compiledAnswer.toString());
        if (compiledAnswer == null) {
            return false;
        }
        List<String> puzzleAnswer = mCurrentPuzzle.getPuzzleAnswers();
        if (puzzleAnswer == null) {
            return false;
        }
        for (int x = 0; x < compiledAnswer.size(); x++) {
            if (!String.valueOf(compiledAnswer.get(x)).equals(String.valueOf(puzzleAnswer.get(x)))) {
                return false;
            }
        }
        return true;
    }

    private ArrayList<String> getCheckList() {
        ArrayList<String> outgoing_list = new ArrayList<>();
        SparseBooleanArray checked = multipleSelectionListView.getCheckedItemPositions();
        for (int i = 0; i < checked.size(); i++) {
            if (checked.valueAt(i)) {
                String tag = multipleSelectionListView.getItemAtPosition(checked.keyAt(i)).toString();
                outgoing_list.add(tag);
            }
        }
        return outgoing_list;
    }

    public void toggleTouch() {
        if (multipleSelectionListView.isEnabled()) {
            multipleSelectionListView.setEnabled(false);
            multipleSelectionListView.setClickable(false);
            multipleSelectionListView.setFocusable(false);
            multipleSelectionListView.setFocusableInTouchMode(false);
        } else {
            multipleSelectionListView.setEnabled(true);
            multipleSelectionListView.setClickable(true);
            multipleSelectionListView.setFocusable(true);
            multipleSelectionListView.setFocusableInTouchMode(true);
        }

    }

    private void assignViews(View view) {
        multipleSelectionListView = (ListView) view.findViewById(R.id.multipleSelectionListView);
    }

}
