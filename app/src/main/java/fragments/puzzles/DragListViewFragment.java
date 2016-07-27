package fragments.puzzles;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.progamer.R;
import com.woxthebox.draglistview.DragItem;
import com.woxthebox.draglistview.DragListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import activities.PuzzleActivity;
import adapters.ItemAdapter;
import models.Level;
import other.JavaInterpreter;
import models.Puzzle;
import other.PuzzleGenerator;

public class DragListViewFragment extends Fragment {

    private String mClassName = getClass().toString();
    private DragListView mDragListView;
    private ItemAdapter mItemAdapter;
    private PuzzleGenerator mPuzzleGenerator;
    private List<String> mExpectedAnswer;
    private PuzzleActivity mParentPuzzleActivity;
    private Level mCurrentLevel;
    private List<String> mPuzzleData;

    public DragListViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_drag_list_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assignViews(view);
        mParentPuzzleActivity = (PuzzleActivity) getActivity();
        mCurrentLevel = mParentPuzzleActivity.getCurrentLevel();
        mPuzzleGenerator = new PuzzleGenerator();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                loadData();
                setupDragListView();
            }
        });
    }

    private void loadData() {
        mPuzzleGenerator.generatePuzzle(mCurrentLevel.getLevel_title());
        mExpectedAnswer = mPuzzleGenerator.getExpectedAnswer();
        mParentPuzzleActivity.setExpectedOutput(mExpectedAnswer);
        mPuzzleData = mPuzzleGenerator.getFinalCodeList();
    }

    private void setupDragListView() {
        ArrayList<Pair<Long, String>> mItemArray = new ArrayList<>();
        for (int i = 0; i < mPuzzleData.size(); i++) {
            mItemArray.add(new Pair<>((long) i, mPuzzleData.get(i)));
        }
        mDragListView.getRecyclerView().setVerticalScrollBarEnabled(true);
        mDragListView.setLayoutManager(new LinearLayoutManager(getContext()));
        ArrayList<Pair<Long, String>> listToSend = new ArrayList<>();
        listToSend.addAll(mItemArray);
        Collections.shuffle(listToSend);
        mItemAdapter = new ItemAdapter(listToSend, R.layout.drag_row, R.id.text, false, getContext());
        mDragListView.setAdapter(mItemAdapter, true);
        mDragListView.setCanDragHorizontally(false);
        mDragListView.setCustomDragItem(new MyDragItem(getContext(), R.layout.drag_row));
    }

    public boolean checkIfCorrect() {
        List<Pair<Long, String>> adapterList = mItemAdapter.getItemList();
        List<String> cSharpCode = new ArrayList<>();
        for (Pair<Long, String> cSharpLine : adapterList) {
            cSharpCode.add(cSharpLine.second);
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

    private void assignViews(View view) {
        mDragListView = (DragListView) view.findViewById(R.id.dragListView);
    }

    public void toggleTouch() {
        if (mDragListView.isDragEnabled()) {
            mDragListView.setEnabled(false);
            mDragListView.setDragEnabled(false);
            mDragListView.setClickable(false);
            mDragListView.setFocusable(false);
            mDragListView.setFocusableInTouchMode(false);
        } else {
            mDragListView.setEnabled(true);
            mDragListView.setDragEnabled(true);
            mDragListView.setClickable(true);
            mDragListView.setFocusable(true);
            mDragListView.setFocusableInTouchMode(true);
        }
    }

    private static class MyDragItem extends DragItem {

        public MyDragItem(Context context, int layoutId) {
            super(context, layoutId);
        }

        @Override
        public void onBindDragView(View clickedView, View dragView) {
            CharSequence text = ((TextView) clickedView.findViewById(R.id.text)).getText();
            ((TextView) dragView.findViewById(R.id.text)).setText(text);
            dragView.setBackgroundColor(dragView.getResources().getColor(R.color.amber_50));
        }
    }
}
