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
import java.util.List;

import activities.PuzzleActivity;
import puzzle.PuzzleCodeBuilder;

public class TrueFalseFragment extends Fragment {

    private String mClassName = getClass().toString();
    private ListView mTrueFalseListView;
    private ArrayAdapter<String> mArrayAdapter;
    private PuzzleActivity mParentPuzzleActivity;
    private PuzzleCodeBuilder mCurrentPuzzleCodeBuilder;
    private List<Pair<String, String>> mCodePairList;

    public TrueFalseFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_true_false, container, false);
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
        List<String> toDisplayList = new ArrayList<>();
        toDisplayList.add("True");
        toDisplayList.add("False");
        mArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.item_checkbox, toDisplayList);
        mTrueFalseListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mTrueFalseListView.setAdapter(mArrayAdapter);
    }

    public Boolean checkIfCorrect() {
        String checkedItem = "";
        SparseBooleanArray checked = mTrueFalseListView.getCheckedItemPositions();
        for (int i = 0; i < checked.size(); i++) {
            if (checked.valueAt(i)) {
                checkedItem = mTrueFalseListView.getItemAtPosition(checked.keyAt(i)).toString();
            }
        }
        String expectedAnswer = mCurrentPuzzleCodeBuilder.getCSharpCodeToRunAnswer().get(0).toString();
        Log.e("expectedAnswer", expectedAnswer);
        return expectedAnswer.equals(checkedItem);
    }

    private void assignViews(View view) {
        mTrueFalseListView = (ListView) view.findViewById(R.id.trueFalseListView);
    }
}
