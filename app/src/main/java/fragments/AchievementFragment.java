package fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.progamer.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import adapters.AchievementAdapter;
import models.UserAchievement;
import singletons.DatabaseHandlerSingleton;

/**
 * A simple {@link Fragment} subclass.
 */
public class AchievementFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ArrayList<UserAchievement> mAchievementList;
    private AchievementAdapter mAchievementAdapter;
    private DatabaseHandlerSingleton mDatabaseHandlerSingleton;
    private String[] mAchievementListSort = new String[]{"Sort by date completed", "Sort by title", "Sort by completed"};
    private String[] mAchievementListFilter = new String[]{"All", "Completed", "Incomplete"};
    private Spinner mSpinnerAchievementSort, mSpinnerAchievementFilter;

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
        assignViews(view);
        assignListeners();
        addList(mDatabaseHandlerSingleton.getLoggedUserAchievements());
    }

    public void sortListDate(List<UserAchievement> list) { //Todo: deprecated
        Collections.sort(list, new Comparator<UserAchievement>() {
            @Override
            public int compare(UserAchievement obj1, UserAchievement obj2) {

                if (obj1.getUserachievement_date_completed() == null) {
                    return -1;
                }
                if (obj2.getUserachievement_date_completed() == null) {
                    return 1;
                }
                if (obj1.getUserachievement_date_completed().equals(obj2.getUserachievement_date_completed())) {
                    return 0;
                }
                return obj2.getUserachievement_date_completed().compareTo(obj1.getUserachievement_date_completed());
            }
        });
    }

    public void sortListTitle(List<UserAchievement> list) { //Todo: deprecated
        Collections.sort(list, new Comparator<UserAchievement>() {
            @Override
            public int compare(UserAchievement obj1, UserAchievement obj2) {

                if (obj1.getAchievement_title() == null) {
                    return -1;
                }
                if (obj2.getAchievement_title() == null) {
                    return 1;
                }
                if (obj1.getAchievement_title().equals(obj2.getAchievement_title())) {
                    return 0;
                }
                return obj1.getAchievement_title().compareTo(obj2.getAchievement_title());
            }
        });
    }

    public void sortListComplete(List<UserAchievement> list) { //Todo: deprecated
        Collections.sort(list, new Comparator<UserAchievement>() {
            @Override
            public int compare(UserAchievement obj1, UserAchievement obj2) {

                if (obj1.getUserachievement_completed() > obj2.getUserachievement_completed())
                    return -1;
                if (obj1.getUserachievement_completed() < obj2.getUserachievement_completed())
                    return 1;
                else
                    return 0;
            }
        });
    }

    private void assignListeners() {
        mSpinnerAchievementSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sortSelected = mAchievementListSort[position];
                ArrayList<UserAchievement> userAchievementList = new ArrayList<>(mAchievementList);
                if(sortSelected.equals("Sort by date")) {
                    sortListDate(userAchievementList);
                    addList(userAchievementList);
                }
                if(sortSelected.equals("Sort by title")) {
                    sortListTitle(userAchievementList);
                    addList(userAchievementList);
                }
                if(sortSelected.equals("Sort by complete")) {
                    sortListComplete(userAchievementList);
                    addList(userAchievementList);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void assignViews(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mSpinnerAchievementSort = (Spinner) view.findViewById(R.id.spinner_achievement_sort);
        mSpinnerAchievementFilter = (Spinner) view.findViewById(R.id.spinner_achievement_filter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAchievementAdapter = new AchievementAdapter(getActivity());
        mRecyclerView.setAdapter(mAchievementAdapter);

        assignSortSpinnerView(view);
        assignFilerSpinnerView(view);
    }

    private void assignFilerSpinnerView(View view) {
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item,
                mAchievementListFilter);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerAchievementFilter.setAdapter(spinnerArrayAdapter);
        mSpinnerAchievementFilter.setSelection(0);
    }

    private void assignSortSpinnerView(View view) {
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item,
                mAchievementListSort);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerAchievementSort.setAdapter(spinnerArrayAdapter);
        mSpinnerAchievementSort.setSelection(0);
    }

    private void assignSingletons() {
        mDatabaseHandlerSingleton = DatabaseHandlerSingleton.getInstance(getActivity());
    }

    public void addList(List<UserAchievement> list) {
        if (!list.isEmpty()) {
            mAchievementList = new ArrayList<>();
            mAchievementList.addAll(list);
            mAchievementAdapter.setAchievementList(mAchievementList);
        } else {
            mAchievementAdapter.clearAchievementList();
        }
    }

}
