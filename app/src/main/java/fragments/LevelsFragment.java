package fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.progamer.R;

import java.util.ArrayList;

import activities.LevelActivity;
import adapters.LevelAdapter;
import models.Level;
import singletons.DatabaseHandlerSingleton;

public class LevelsFragment extends Fragment implements LevelAdapter.ClickListener {

    private RecyclerView mRecyclerView;
    private ArrayList<Level> mLevelList;
    private LevelAdapter mLevelAdapter;
    private DatabaseHandlerSingleton mDatabaseHandlerSingleton;

    public LevelsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_levels, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assignSingletons();
        assignViews(view);
        assignAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        addList(mDatabaseHandlerSingleton.getLevels());
    }

    @Override
    public void itemClicked(int position, int level_id) {
        Intent intent = new Intent(getContext(), LevelActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("level_id", level_id);
        intent.putExtras(bundle);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void assignSingletons() {
        mDatabaseHandlerSingleton = DatabaseHandlerSingleton.getInstance(getActivity());
    }

    private void assignAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mLevelAdapter = new LevelAdapter(getActivity());
        mLevelAdapter.setListener(this);
        mRecyclerView.setAdapter(mLevelAdapter);
    }

    private void assignViews(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
    }

    public void addList(ArrayList<Level> list) {
        if (!list.isEmpty()) {
            mLevelList = new ArrayList<>();
            mLevelList.addAll(list);
            mLevelAdapter.setLevelList(mLevelList);
        } else {
            mLevelAdapter.clearLevelList();
        }
    }

}
