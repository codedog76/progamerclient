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
    private ArrayList<UserAchievement> mConstantAchievementList;
    private AchievementAdapter mAchievementAdapter;
    private DatabaseHandlerSingleton mDatabaseHandlerSingleton;
    private String[] mAchievementListSort = new String[]{"Sort by date completed", "Sort by title", "Sort by completed"};
    private String[] mAchievementListFilter = new String[]{"All", "Complete", "Incomplete"};
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
        mConstantAchievementList = new ArrayList<>(mDatabaseHandlerSingleton.getLoggedUserAchievements());
        assignViews(view);
        assignListeners();
        addList(mConstantAchievementList);
    }

    public void sortListDate() {
        Collections.sort(mAchievementList, new Comparator<UserAchievement>() {
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

    public void sortListTitle() {
        Collections.sort(mAchievementList, new Comparator<UserAchievement>() {
            @Override
            public int compare(UserAchievement obj1, UserAchievement obj2) {
                if (obj1.getAchievement_title().equals(obj2.getAchievement_title())) {
                    return 0;
                }
                if (obj1.getAchievement_title() == null) {
                    return -1;
                }
                if (obj2.getAchievement_title() == null) {
                    return 1;
                }
                return obj1.getAchievement_title().compareTo(obj2.getAchievement_title());
            }
        });
    }

    public void sortListComplete() {
        Collections.sort(mAchievementList, new Comparator<UserAchievement>() {
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

    private String mCurrentSort = mAchievementListSort[0];

    private void sortList(String toSortBy) {
        mCurrentSort = toSortBy;
        if (toSortBy.equals("Sort by date completed")) {
            sortListDate();
            addList(mAchievementList);
        }
        if (toSortBy.equals("Sort by title")) {
            sortListTitle();
            addList(mAchievementList);
        }
        if (toSortBy.equals("Sort by completed")) {
            sortListComplete();
            addList(mAchievementList);
        }
    }

    private void assignListeners() {
        mSpinnerAchievementSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sortSelected = mAchievementListSort[position];
                sortList(sortSelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpinnerAchievementFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String filterSelected = mAchievementListFilter[position];
                if (filterSelected.equals("All")) {
                    sortList(mCurrentSort);
                    addList(mConstantAchievementList);
                }
                if (filterSelected.equals("Complete")) {
                    filterListComplete();
                    addList(mAchievementList);
                }
                if (filterSelected.equals("Incomplete")) {
                    filterListIncomplete();
                    addList(mAchievementList);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void filterListComplete() {
        List<UserAchievement> tempToReplace = new ArrayList<>();
        for (UserAchievement userAchievement : mConstantAchievementList) {
            if (userAchievement.getUserachievement_completed() == 1) {
                tempToReplace.add(userAchievement);
            }
        }
        mAchievementList = new ArrayList<>(tempToReplace);
        sortList(mCurrentSort);
    }

    private void filterListIncomplete() {
        List<UserAchievement> tempToReplace = new ArrayList<>();
        for (UserAchievement userAchievement : mConstantAchievementList) {
            if (userAchievement.getUserachievement_completed() != 1) {
                tempToReplace.add(userAchievement);
            }
        }
        mAchievementList = new ArrayList<>(tempToReplace);
        sortList(mCurrentSort);
    }

    private void assignViews(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mSpinnerAchievementSort = (Spinner) view.findViewById(R.id.spinner_achievement_sort);
        mSpinnerAchievementFilter = (Spinner) view.findViewById(R.id.spinner_achievement_filter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAchievementAdapter = new AchievementAdapter(getActivity());
        mRecyclerView.setAdapter(mAchievementAdapter);

        assignSortSpinnerView();
        assignFilerSpinnerView();
    }

    private void assignFilerSpinnerView() {
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item,
                mAchievementListFilter);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerAchievementFilter.setAdapter(spinnerArrayAdapter);
        mSpinnerAchievementFilter.setSelection(1);
    }

    private void assignSortSpinnerView() {
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
