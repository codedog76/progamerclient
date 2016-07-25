package fragments.puzzles;


import android.nfc.Tag;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import activities.PuzzleActivity;
import models.Puzzle;

public class MultipleChoiceListFragment extends Fragment {

    private ListView multipleSelectionListView;
    private ArrayAdapter<String> arrayAdapter;
    String[] mPuzzleAnswers;

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
        Puzzle selectedPuzzle = puzzleActivity.getSelectedPuzzle();
        String[] mPuzzleData = selectedPuzzle.getPuzzle_data().split("//");
        mPuzzleAnswers = selectedPuzzle.getPuzzle_answer().split("//");
        ArrayList<String> puzzleItems = new ArrayList<>(Arrays.asList(mPuzzleData));
        puzzleItems.remove(0);
        Collections.shuffle(puzzleItems);
        arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_multiple_choice, puzzleItems);
        multipleSelectionListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        multipleSelectionListView.setAdapter(arrayAdapter);
    }

    public boolean checkIfCorrect() {
        List<String> answerItems = new ArrayList<>(Arrays.asList(mPuzzleAnswers));
        answerItems.remove(0);
        Collections.sort(answerItems);
        List<String> checkedItems = getCheckList();
        Collections.sort(checkedItems);
        for(int x = 0; x < answerItems.size(); x++){
            if(!answerItems.get(x).trim().equals(checkedItems.get(x).trim()))
                return false;
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

    public void disableSelection() {
        multipleSelectionListView.setClickable(false);
    }

    private void assignViews(View view) {
        multipleSelectionListView = (ListView) view.findViewById(R.id.multipleSelectionListView);
    }

}
