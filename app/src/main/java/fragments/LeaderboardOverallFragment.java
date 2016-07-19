package fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.progamer.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import adapters.LeaderboardAdapter;
import de.hdodenhof.circleimageview.CircleImageView;
import singletons.DatabaseHandlerSingleton;
import models.User;
import singletons.NetworkManagerSingleton;

public class LeaderboardOverallFragment extends Fragment implements LeaderboardAdapter.clickListener {

    private RecyclerView mRecyclerView;
    private ArrayList<User> userList;
    private LeaderboardAdapter leaderboardAdapter;
    private RelativeLayout leaderboardProgressBarLayout;
    private LinearLayout overallTopLinearLayout, overallBottomLinearLayout;
    private InterfaceListener interfaceListener;
    private DatabaseHandlerSingleton databaseHandlerSingleton;
    private TextView overallTopNameTextView, overallTopScoreTextView, overallBottomNameTextView, overallBottomScoreTextView, overallTopRankTextView, overallBottomRankTextView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NetworkManagerSingleton networkManagerSingleton;
    private CircleImageView overallTopRankCircleImageView, overallBottomRankCircleImageView;
    private int currentUserPos;
    private User currentUser;

    public LeaderboardOverallFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_leaderboard_overall, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        assignSingletons();
        assignViews(view);
        assignFonts();
        assignAdapter();
        assignListeners();
        loadData();
        ((LeaderboardFragment) getParentFragment()).syncLeaderboardData("overall");
    }

    private void assignFonts() {
        Typeface Roboto_Medium = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Medium.ttf");
        Typeface Roboto_Regular = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Regular.ttf");
        overallTopNameTextView.setTypeface(Roboto_Regular);
        overallTopScoreTextView.setTypeface(Roboto_Regular);
        overallBottomNameTextView.setTypeface(Roboto_Regular);
        overallBottomScoreTextView.setTypeface(Roboto_Regular);
        overallTopRankTextView.setTypeface(Roboto_Regular);
        overallBottomRankTextView.setTypeface(Roboto_Regular);
    }

    private void loadData() {
        addList(((LeaderboardFragment)getParentFragment()).getLeaderboardList());
        if(leaderboardAdapter.getItemCount()>1) {
            overallTopNameTextView.setText(currentUser.getUser_nickname());
            overallTopScoreTextView.setText(String.valueOf(currentUser.getUser_overall_score()));
            overallBottomNameTextView.setText(currentUser.getUser_nickname());
            overallBottomScoreTextView.setText(String.valueOf(currentUser.getUser_overall_score()));
            if (currentUserPos <= 2) {
                overallTopRankTextView.setVisibility(View.GONE);
                overallBottomRankTextView.setVisibility(View.GONE);
                overallTopRankCircleImageView.setVisibility(View.VISIBLE);
                overallBottomRankCircleImageView.setVisibility(View.VISIBLE);
                if (currentUserPos == 0) {
                    overallTopRankCircleImageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.trophy_gold));
                    overallBottomRankCircleImageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.trophy_gold));
                }
                if (currentUserPos == 1) {
                    overallTopRankCircleImageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.trophy_silver));
                    overallBottomRankCircleImageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.trophy_silver));
                }
                if (currentUserPos == 2) {
                    overallTopRankCircleImageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.trophy_bronze));
                    overallBottomRankCircleImageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.trophy_bronze));
                }
            } else {
                overallTopRankTextView.setText(String.valueOf(currentUserPos + 1));
                overallBottomRankTextView.setText(String.valueOf(currentUserPos + 1));
                overallTopRankTextView.setVisibility(View.VISIBLE);
                overallBottomRankTextView.setVisibility(View.VISIBLE);
                overallTopRankCircleImageView.setVisibility(View.GONE);
                overallBottomRankCircleImageView.setVisibility(View.GONE);
            }
        } else {
            overallTopLinearLayout.setVisibility(View.GONE);
            overallBottomLinearLayout.setVisibility(View.GONE);
        }
        leaderboardProgressBarLayout.setVisibility(View.GONE);

    }

    public void assignSingletons() {
        databaseHandlerSingleton = DatabaseHandlerSingleton.getInstance(getActivity());
        networkManagerSingleton = NetworkManagerSingleton.getInstance(getActivity());
    }

    private void assignViews(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        overallTopLinearLayout = (LinearLayout) view.findViewById(R.id.overallTopLinearLayout);
        overallBottomLinearLayout = (LinearLayout) view.findViewById(R.id.overallBottomLinearLayout);
        leaderboardProgressBarLayout = (RelativeLayout) view.findViewById(R.id.leaderboardProgressBarLayout);
        overallTopNameTextView = (TextView) view.findViewById(R.id.overallTopNameTextView);
        overallTopScoreTextView = (TextView) view.findViewById(R.id.overallTopScoreTextView);
        overallBottomNameTextView = (TextView) view.findViewById(R.id.overallBottomNameTextView);
        overallBottomScoreTextView = (TextView) view.findViewById(R.id.overallBottomScoreTextView);
        overallTopRankTextView = (TextView) view.findViewById(R.id.overallTopRankTextView);
        overallBottomRankTextView = (TextView) view.findViewById(R.id.overallBottomRankTextView);
        overallTopRankCircleImageView = (CircleImageView) view.findViewById(R.id.overallTopRankCircleImageView);
        overallBottomRankCircleImageView = (CircleImageView) view.findViewById(R.id.overallBottomRankCircleImageView);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
    }

    private void assignAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        leaderboardAdapter = new LeaderboardAdapter(getActivity());
        mRecyclerView.setAdapter(leaderboardAdapter);
        leaderboardAdapter.setListener(this);
    }

    private void assignListeners() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager LM = (LinearLayoutManager) recyclerView.getLayoutManager();
                int posTop = LM.findFirstCompletelyVisibleItemPosition();
                int posBottom = LM.findLastCompletelyVisibleItemPosition();
                if (posTop > currentUserPos) {
                    overallTopLinearLayout.setVisibility(View.VISIBLE);
                } else {
                    overallTopLinearLayout.setVisibility(View.GONE);
                }
                if (posBottom < currentUserPos) {
                    overallBottomLinearLayout.setVisibility(View.VISIBLE);
                } else {
                    overallBottomLinearLayout.setVisibility(View.GONE);
                }
            }
        });

        overallTopLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.smoothScrollToPosition(currentUserPos);
            }
        });

        overallBottomLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.smoothScrollToPosition(currentUserPos);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                interfaceListener.startRefreshData();
            }
        });
        ((LeaderboardFragment) getParentFragment()).setListener(new LeaderboardFragment.OverallFragmentInterface() {
            @Override
            public void stopRefreshing() {
                swipeRefreshLayout.setRefreshing(false);
            }
            @Override
            public void startRefreshing() {
                swipeRefreshLayout.setRefreshing(true);
            }
            @Override
            public void reloadData() {
                loadData();
            }
        });
    }

    public int findPosition() {
        for (User user : userList) {
            if (user.getUser_student_number_id().equals(databaseHandlerSingleton.getLoggedUser().getUser_student_number_id())) {
                currentUser = user;
                return userList.indexOf(user);
            }
        }
        return -1;
    }

    public void sortList(ArrayList<User> list) { //Todo: deprecated
        Collections.sort(list, new Comparator<User>() {
            @Override
            public int compare(User lhs, User rhs) {
                if (lhs.getUser_overall_score() > rhs.getUser_overall_score())
                    return -1;
                if (lhs.getUser_overall_score() < rhs.getUser_overall_score())
                    return 1;
                return 0;
            }
        });
    }

    public void addList(ArrayList<User> list) {
        if (!list.isEmpty()) {
            sortList(list);
            userList = new ArrayList<>();
            userList.addAll(list);
            leaderboardAdapter.setUserList(userList, "overall");
            currentUserPos = findPosition();
        } else {
            leaderboardAdapter.clearUserList();
        }
    }

    public void setListener(InterfaceListener interfaceListener) {
        this.interfaceListener = interfaceListener;
    }

    @Override
    public void itemClicked(int position, String user_student_number) {
        interfaceListener.itemClicked(position, user_student_number);
    }

    public interface InterfaceListener {
        void itemClicked(int position, String user_student_number);

        void startRefreshData();
    }
}
