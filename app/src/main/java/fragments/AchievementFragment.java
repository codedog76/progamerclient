package fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.progamer.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import adapters.AchievementAdapter;
import adapters.LeaderboardAdapter;
import models.Achievement;
import models.User;
import models.UserAchievement;
import singletons.DatabaseHandlerSingleton;

/**
 * A simple {@link Fragment} subclass.
 */
public class AchievementFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ArrayList<UserAchievement> achievementList;
    private AchievementAdapter achievementAdapter;
    private DatabaseHandlerSingleton mDatabaseHandlerSingleton;

    public AchievementFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_achievement, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        assignSingletons();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        achievementAdapter = new AchievementAdapter(getActivity());
        mRecyclerView.setAdapter(achievementAdapter);
        ArrayList<UserAchievement> temp = mDatabaseHandlerSingleton.getLoggedUserAchievements();
        Log.e("userachivementsize", temp.size()+"");
        addList(temp);
    }

    private void assignSingletons() {
        mDatabaseHandlerSingleton = DatabaseHandlerSingleton.getInstance(getActivity());
    }

    public void addList(ArrayList<UserAchievement> list) {
        if (!list.isEmpty()) {
            achievementList = new ArrayList<>();
            achievementList.addAll(list);
            achievementAdapter.setAchievementList(achievementList);
        } else {
            achievementAdapter.clearAchievementList();
        }
    }

}
