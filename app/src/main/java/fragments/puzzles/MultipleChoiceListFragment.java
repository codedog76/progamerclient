package fragments.puzzles;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import models.Level;
import other.JavaInterpreter;
import models.Puzzle;
import other.PuzzleGenerator;

public class MultipleChoiceListFragment extends Fragment {

    private String mClassName = getClass().toString();
    private ListView multipleSelectionListView;
    private ArrayAdapter<String> mArrayAdapter;
    private PuzzleGenerator mPuzzleGenerator;
    private List<String> mExpectedAnswer;
    private PuzzleActivity mParentPuzzleActivity;
    private Level mCurrentLevel;
    private List<String> mPuzzleData;

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
        mCurrentLevel = mParentPuzzleActivity.getCurrentLevel();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                loadData();
                setupMultiChoiceListView();
            }
        });
    }

    private void loadData() {
        mPuzzleGenerator.generatePuzzle(mCurrentLevel.getLevel_title());
        mExpectedAnswer = mPuzzleGenerator.getExpectedAnswer();
        mParentPuzzleActivity.setExpectedOutput(mExpectedAnswer);
        mPuzzleData = mPuzzleGenerator.getFinalCodeList();
    }

    private void setupMultiChoiceListView() {
        mArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_multiple_choice, mPuzzleData);
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
        if (compiledAnswer == null) {
            return false;
        }
        for (int x = 0; x < compiledAnswer.size(); x++) {
            if (!String.valueOf(compiledAnswer.get(x)).equals(String.valueOf(mExpectedAnswer.get(x)))) {
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
