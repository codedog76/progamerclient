package fragments.puzzles;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
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
import puzzle.JavaInterpreter;
import puzzle.PuzzleCodeBuilder;

public class DragListViewFragment extends Fragment {

    private String mClassName = getClass().toString();
    private DragListView mDragListView;
    private ItemAdapter mItemAdapter;
    private PuzzleActivity mParentPuzzleActivity;
    private PuzzleCodeBuilder mCurrentPuzzleCodeBuilder;
    private List<Pair<String, String>> mCodePairList;

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
            ArrayList<Pair<Long, String>> mItemArray = new ArrayList<>();
            for (int i = 0; i < toDisplayList.size(); i++) {
                mItemArray.add(new Pair<>((long) i, toDisplayList.get(i)));
            }
            mDragListView.getRecyclerView().setVerticalScrollBarEnabled(true);
            mDragListView.setLayoutManager(new LinearLayoutManager(getContext()));
            ArrayList<Pair<Long, String>> listToSend = new ArrayList<>();
            listToSend.addAll(mItemArray);
            Collections.shuffle(listToSend);
            mItemAdapter = new ItemAdapter(listToSend, R.layout.item_drag, R.id.text, false, getContext());
            mDragListView.setAdapter(mItemAdapter, true);
            mDragListView.setCanDragHorizontally(false);
            mDragListView.setCustomDragItem(new MyDragItem(getContext(), R.layout.item_drag));
        } else {
            mParentPuzzleActivity.finish();
        }
    }

    public Boolean checkIfCorrect() {
        List<Pair<Long, String>> adapterList = mItemAdapter.getItemList();
        List<String> codeToRun = new ArrayList<>();
        for (Pair<Long, String> cSharpLine : adapterList) {
            codeToRun.add(cSharpLine.second); //todo: find all in correct list
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

    private void assignViews(View view) {
        mDragListView = (DragListView) view.findViewById(R.id.drag_list_view);
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
