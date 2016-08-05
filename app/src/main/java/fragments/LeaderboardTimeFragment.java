package fragments;


import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
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
import models.User;
import singletons.DatabaseHandlerSingleton;

public class LeaderboardTimeFragment extends Fragment implements LeaderboardAdapter.ClickListener {

    private RecyclerView mRecyclerView;
    private ArrayList<User> mUserList;
    private LeaderboardAdapter mLeaderboardAdapter;
    private LinearLayout mLinearTop, mLinearBottom;
    private RelativeLayout mRelativeProgress;
    private InterfaceListener mInterfaceListener;
    private TextView mTextTopNickname, mTextTopScore, mTextBottomNickname, mTextBottomScore, mTextTopRank,
            mTextBottomRank;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private DatabaseHandlerSingleton mDatabaseHandlerSingleton;
    private CircleImageView mCircleImageAvatarTop, mCircleImageAvatarBottom;
    private int mCurrentUserPos;
    private User mCurrentUser;

    public LeaderboardTimeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_leaderboard_time, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        assignSingletons();
        assignViews(view);
        assignFonts();
        assignAdapter();
        assignListeners();
        assignSingletons();
        loadData();
    }

    @Override
    public void itemClicked(int position, String user_student_number) {
        mInterfaceListener.itemClicked(position, user_student_number);
    }

    private void assignSingletons() {
        mDatabaseHandlerSingleton = DatabaseHandlerSingleton.getInstance(getActivity());
    }

    private void loadData() {
        addList(((LeaderboardFragment) getParentFragment()).getLeaderboardList());
        if (mLeaderboardAdapter.getItemCount() > 1) {
            mTextTopNickname.setText(mCurrentUser.getUser_nickname());
            mTextTopScore.setText(String.valueOf(mCurrentUser.getUser_overall_time()));
            mTextBottomNickname.setText(mCurrentUser.getUser_nickname());
            mTextBottomScore.setText(String.valueOf(mCurrentUser.getUser_overall_time()));
            mTextTopRank.setText(getString(R.string.string_rank, mCurrentUserPos + 1));
            mTextBottomRank.setText(getString(R.string.string_rank, mCurrentUserPos + 1));
            int id = getContext().getResources().getIdentifier("avatar_" + String.valueOf(mCurrentUser.getUser_avatar()),
                    "drawable", getContext().getPackageName());
            Drawable drawable = ContextCompat.getDrawable(getContext(), id);
            mCircleImageAvatarTop.setImageDrawable(drawable);
            mCircleImageAvatarBottom.setImageDrawable(drawable);
            if (mCurrentUserPos <= 2) {
                mCircleImageAvatarTop.setBorderWidth(5);
                mCircleImageAvatarBottom.setBorderWidth(5);
                if (mCurrentUserPos == 0) {
                    mCircleImageAvatarTop.setBorderColor(ContextCompat.getColor(getContext(), R.color.gold));
                    mCircleImageAvatarBottom.setBorderColor(ContextCompat.getColor(getContext(), R.color.gold));
                }
                if (mCurrentUserPos == 1) {
                    mCircleImageAvatarTop.setBorderColor(ContextCompat.getColor(getContext(), R.color.silver));
                    mCircleImageAvatarBottom.setBorderColor(ContextCompat.getColor(getContext(), R.color.silver));
                }
                if (mCurrentUserPos == 2) {
                    mCircleImageAvatarTop.setBorderColor(ContextCompat.getColor(getContext(), R.color.bronze));
                    mCircleImageAvatarBottom.setBorderColor(ContextCompat.getColor(getContext(), R.color.bronze));
                }
            } else {
                mCircleImageAvatarTop.setBorderWidth(0);
                mCircleImageAvatarBottom.setBorderWidth(0);
                mCircleImageAvatarTop.setBorderColor(ContextCompat.getColor(getContext(), R.color.grey_50));
                mCircleImageAvatarBottom.setBorderColor(ContextCompat.getColor(getContext(), R.color.grey_50));
            }
        } else {
            mLinearTop.setVisibility(View.GONE);
            mLinearBottom.setVisibility(View.GONE);
        }
        mRelativeProgress.setVisibility(View.GONE);
    }

    private void assignFonts() {
        Typeface Roboto_Medium = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Medium.ttf");
        Typeface Roboto_Regular = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Regular.ttf");
        mTextTopNickname.setTypeface(Roboto_Regular);
        mTextTopScore.setTypeface(Roboto_Regular);
        mTextTopRank.setTypeface(Roboto_Regular);
        mTextBottomNickname.setTypeface(Roboto_Regular);
        mTextBottomScore.setTypeface(Roboto_Regular);
        mTextBottomRank.setTypeface(Roboto_Regular);
    }

    private void assignViews(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mLinearTop = (LinearLayout) view.findViewById(R.id.linear_top);
        mLinearBottom = (LinearLayout) view.findViewById(R.id.linear_bottom);
        mRelativeProgress = (RelativeLayout) view.findViewById(R.id.relative_progress);
        mTextTopNickname = (TextView) view.findViewById(R.id.text_top_nickame);
        mTextTopScore = (TextView) view.findViewById(R.id.text_top_score);
        mTextTopRank = (TextView) view.findViewById(R.id.text_top_rank);
        mTextBottomNickname = (TextView) view.findViewById(R.id.text_bottom_nickname);
        mTextBottomScore = (TextView) view.findViewById(R.id.text_bottom_score);
        mTextBottomRank = (TextView) view.findViewById(R.id.text_bottom_rank);
        mCircleImageAvatarTop = (CircleImageView) view.findViewById(R.id.circle_image_avatar_top);
        mCircleImageAvatarBottom = (CircleImageView) view.findViewById(R.id.circle_image_avatar_bottom);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
    }

    private void assignAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mLeaderboardAdapter = new LeaderboardAdapter(getActivity());
        mLeaderboardAdapter.setListener(this);
        mRecyclerView.setAdapter(mLeaderboardAdapter);
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
                if (posTop > mCurrentUserPos) {
                    mLinearTop.setVisibility(View.VISIBLE);
                } else {
                    mLinearTop.setVisibility(View.GONE);
                }
                if (posBottom < mCurrentUserPos) {
                    mLinearBottom.setVisibility(View.VISIBLE);
                } else {
                    mLinearBottom.setVisibility(View.GONE);
                }
            }
        });

        mLinearTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.smoothScrollToPosition(mCurrentUserPos);
            }
        });

        mLinearBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.smoothScrollToPosition(mCurrentUserPos);
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mInterfaceListener.startRefreshData();
            }
        });
        ((LeaderboardFragment) getParentFragment()).setListener(new LeaderboardFragment.TimeFragmentInterface() {
            @Override
            public void stopRefreshing() {
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void startRefreshing() {
                mSwipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void reloadData() {
                loadData();
            }
        });
    }

    private int findPosition() {
        for (User user : mUserList) {
            if (user.getUser_student_number_id().equals(mDatabaseHandlerSingleton.getLoggedUser().getUser_student_number_id())) {
                mCurrentUser = user;
                return mUserList.indexOf(user);
            }
        }
        return -1;
    }

    public void sortList(ArrayList<User> list) {
        Collections.sort(list, new Comparator<User>() {
            @Override
            public int compare(User lhs, User rhs) {
                if (lhs.getUser_overall_time() > rhs.getUser_overall_time())
                    return 1;
                if (lhs.getUser_overall_time() < rhs.getUser_overall_time())
                    return -1;
                return 0;
            }
        });
    }

    public void addList(ArrayList<User> list) {
        if (!list.isEmpty()) {
            sortList(list);
            mUserList = new ArrayList<>();
            mUserList.addAll(list);
            mLeaderboardAdapter.setUserList(mUserList, "time");
            mCurrentUserPos = findPosition();
        } else {
            mLeaderboardAdapter.clearUserList();
        }
    }

    public void setListener(InterfaceListener interfaceListener) {
        this.mInterfaceListener = interfaceListener;
    }

    public interface InterfaceListener {
        void itemClicked(int position, String user_student_number);

        void startRefreshData();
    }
}
