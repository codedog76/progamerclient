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

/**
 * A simple {@link Fragment} subclass.
 */
public class AchievementFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ArrayList<Achievement> achievementList;
    private AchievementAdapter achievementAdapter;

    public AchievementFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_achievement, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        achievementAdapter = new AchievementAdapter(getActivity());
        mRecyclerView.setAdapter(achievementAdapter);

        ArrayList<Achievement> temp = new ArrayList<>();
        temp.add(new Achievement("Total Achievement", "Achieve all achievement", 5, 3));
        temp.add(new Achievement("Total Achievement", "Achieve all achievement", 5, 3));
        temp.add(new Achievement("Total Achievement", "Achieve all achievement", 5, 3));
        temp.add(new Achievement("Total Achievement", "Achieve all achievement", 5, 3));
        temp.add(new Achievement("Total Achievement", "Achieve all achievement", 5, 3));
        temp.add(new Achievement("Total Achievement", "Achieve all achievement", 5, 3));
        temp.add(new Achievement("Total Achievement", "Achieve all achievement", 5, 3));
        temp.add(new Achievement("Total Achievement", "Achieve all achievement", 5, 3));

        addList(temp);
    }


    public void addList(ArrayList<Achievement> list) {
        if (!list.isEmpty()) {
            achievementList = new ArrayList<>();
            achievementList.addAll(list);
            achievementAdapter.setAchievementList(achievementList);
        } else {
            achievementAdapter.clearAchievementList();
        }
    }

}
