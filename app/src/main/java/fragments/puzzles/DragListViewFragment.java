package fragments.puzzles;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.progamer.R;
import com.woxthebox.draglistview.DragItem;
import com.woxthebox.draglistview.DragListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import activities.PuzzleActivity;
import adapters.ItemAdapter;
import models.Puzzle;

public class DragListViewFragment extends Fragment {

    private DragListView mDragListView;
    private ArrayList<Pair<Long, String>> mItemArray;
    private ItemAdapter mItemAdapter;

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
        loadData();
    }

    private void loadData() {
        PuzzleActivity puzzleActivity = (PuzzleActivity) getActivity();
        Puzzle selectedPuzzle = puzzleActivity.getSelectedPuzzle();
        String[] mPuzzleData = selectedPuzzle.getPuzzle_data().split("//");
        mItemArray = new ArrayList<>();
        for (int i = 1; i < mPuzzleData.length; i++) {
            mItemArray.add(new Pair<>((long) i, mPuzzleData[i]));
        }
        setupDragView();
    }

    private void assignViews(View view) {
        mDragListView = (DragListView) view.findViewById(R.id.dragListView);
    }

    private void setupDragView() {
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

    public void toggleTouch() {
        if(mDragListView.isDragEnabled())
            mDragListView.setDragEnabled(false);
        else
            mDragListView.setDragEnabled(true);
    }

    public boolean checkIfCorrect() {
        List<Pair<Long, String>> adapterList = mItemAdapter.getItemList();
        for (int x = 0; x < mItemArray.size(); x++) {
            if (!adapterList.get(x).second.equals(mItemArray.get(x).second)) {
                return false;
            }
        }
        return true;
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
