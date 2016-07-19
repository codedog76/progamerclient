package fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.progamer.R;
import java.util.ArrayList;

import activities.LevelActivity;
import activities.LoginActivity;
import activities.UserProfileActivity;
import adapters.LevelAdapter;
import models.Level;
import models.Puzzle;
import singletons.DatabaseHandlerSingleton;

public class LevelsFragment extends Fragment implements LevelAdapter.clickListener {

    private RecyclerView mRecyclerView;
    private ArrayList<Level> levelList;
    private LevelAdapter levelAdapter;
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
        addList(mDatabaseHandlerSingleton.getLevels());
    }

    private void assignSingletons() {
        mDatabaseHandlerSingleton = DatabaseHandlerSingleton.getInstance(getActivity());
    }

    private void assignAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        levelAdapter = new LevelAdapter(getActivity());
        levelAdapter.setListener(this);
        mRecyclerView.setAdapter(levelAdapter);
    }

    private void assignViews(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
    }

    public void addList(ArrayList<Level> list) {
        if (!list.isEmpty()) {
            levelList = new ArrayList<>();
            levelList.addAll(list);
            levelAdapter.setLevelList(levelList);
        } else{
            levelAdapter.clearLevelList();
        }
    }

    @Override
    public void itemClicked(int position) {
        Intent intent = new Intent(getContext(), LevelActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("level", levelList.get(position));
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
