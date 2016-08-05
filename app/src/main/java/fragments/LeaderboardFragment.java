package fragments;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.progamer.R;

import java.util.ArrayList;
import java.util.List;

import activities.UserProfileActivity;
import models.User;
import singletons.DatabaseHandlerSingleton;
import singletons.NetworkManagerSingleton;


/**
 * A simple {@link Fragment} subclass.
 */
public class LeaderboardFragment extends Fragment implements LeaderboardScoreFragment.InterfaceListener, LeaderboardAttemptsFragment.InterfaceListener, LeaderboardTimeFragment.InterfaceListener {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private LeaderboardScoreFragment mLeaderboardScoreFragment;
    private LeaderboardAttemptsFragment mLeaderboardAttemptsFragment;
    private LeaderboardTimeFragment mLeaderboardTimeFragment;
    private TextView mTextLoading, mTextTryAgain;
    private RelativeLayout mRelativeLayout;
    private LinearLayout mLinearProgressBar, mLinearTryAgain;
    private ViewPagerAdapter mViewPagerAdapter;
    private NetworkManagerSingleton mNetworkManagerSingleton;
    private DatabaseHandlerSingleton mDatabaseHandlerSingleton;
    private ScoreFragmentInterface mScoreFragmentInterface;
    private AttemptsFragmentInterface mAttemptsFragmentInterface;
    private TimeFragmentInterface mTimeFragmentInterface;
    private Button mButtonTryAgain;
    private ArrayList<User> mLeaderboardList = new ArrayList<>();

    public LeaderboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_leaderboard, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        assignSingletons();
        assignViews(view);
        assignFonts();
        assingListeners();
        initializeData();
    }

    @Override
    public void itemClicked(int position, String user_student_number) {
        Intent intent = new Intent(getContext(), UserProfileActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("selected_user", user_student_number);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void startRefreshData() {
        refreshData();
    }

    @Override
    public void onStop() {
        super.onStop();
        mNetworkManagerSingleton.cancelJSONRequest("getLeaderboardJsonRequest");
    }

    private void initializeData() {
        mRelativeLayout.setVisibility(View.VISIBLE);
        mLinearProgressBar.setVisibility(View.VISIBLE);
        mLinearTryAgain.setVisibility(View.GONE);

        mNetworkManagerSingleton.putUserJsonRequest(mDatabaseHandlerSingleton.getLoggedUser(), new NetworkManagerSingleton.BooleanResponseListener() {
            @Override
            public void getResult(Boolean response, String message) {
                if(response) {
                    mNetworkManagerSingleton.getLeaderboardJsonRequest(new NetworkManagerSingleton.ObjectResponseListener<ArrayList<User>>() {
                        @Override
                        public void getResult(ArrayList<User> object, Boolean response, String message) {
                            if (response) {
                                mLeaderboardList.clear();
                                mLeaderboardList = object;
                                assignViewPager();
                                mRelativeLayout.setVisibility(View.GONE);
                                mLinearProgressBar.setVisibility(View.GONE);
                                mLinearTryAgain.setVisibility(View.GONE);
                            } else {
                                mLinearProgressBar.setVisibility(View.GONE);
                                mLinearTryAgain.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                } else {
                    mLinearProgressBar.setVisibility(View.GONE);
                    mLinearTryAgain.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void refreshData() {
        mScoreFragmentInterface.startRefreshing();
        mAttemptsFragmentInterface.startRefreshing();
        mTimeFragmentInterface.startRefreshing();
        mNetworkManagerSingleton.getLeaderboardJsonRequest(new NetworkManagerSingleton.ObjectResponseListener<ArrayList<User>>() {
            @Override
            public void getResult(ArrayList<User> object, Boolean response, String message) {
                if (response) {
                    mLeaderboardList.clear();
                    mLeaderboardList = object;
                    mScoreFragmentInterface.reloadData();
                    mAttemptsFragmentInterface.reloadData();
                    mTimeFragmentInterface.reloadData();
                }
                mScoreFragmentInterface.stopRefreshing();
                mAttemptsFragmentInterface.stopRefreshing();
                mTimeFragmentInterface.stopRefreshing();
            }
        });
    }

    private void assignSingletons() {
        mNetworkManagerSingleton = NetworkManagerSingleton.getInstance(getActivity());
        mDatabaseHandlerSingleton = DatabaseHandlerSingleton.getInstance(getActivity());
    }

    private void assignViews(View view) {
        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
        mTabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        mRelativeLayout = (RelativeLayout) view.findViewById(R.id.relative_layout);
        mLinearProgressBar = (LinearLayout) view.findViewById(R.id.linear_progress_bar);
        mLinearTryAgain = (LinearLayout) view.findViewById(R.id.linear_try_again);
        mButtonTryAgain = (Button) view.findViewById(R.id.button_try_again);
        mTextLoading = (TextView) view.findViewById(R.id.text_loading);
        mTextTryAgain = (TextView) view.findViewById(R.id.text_try_again);
    }

    private void assignFonts() {
        Typeface Roboto_Medium = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Medium.ttf");
        Typeface Roboto_Regular = Typeface.createFromAsset(getContext().getAssets(), "Roboto-Regular.ttf");
        mButtonTryAgain.setTypeface(Roboto_Medium);
        mTextLoading.setTypeface(Roboto_Regular);
        mTextTryAgain.setTypeface(Roboto_Regular);
    }

    private void assingListeners() {
        mButtonTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeData();
            }
        });
    }

    private void assignViewPager() {
        setupViewPager(mViewPager);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        viewPager.setOffscreenPageLimit(3);
        mViewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        mLeaderboardScoreFragment = new LeaderboardScoreFragment();
        mLeaderboardScoreFragment.setListener(this);
        mViewPagerAdapter.addFragment(mLeaderboardScoreFragment, "Score");
        mLeaderboardAttemptsFragment = new LeaderboardAttemptsFragment();
        mLeaderboardAttemptsFragment.setListener(this);
        mViewPagerAdapter.addFragment(mLeaderboardAttemptsFragment, "Attempts");
        mLeaderboardTimeFragment = new LeaderboardTimeFragment();
        mLeaderboardTimeFragment.setListener(this);
        mViewPagerAdapter.addFragment(mLeaderboardTimeFragment, "Time");
        viewPager.setAdapter(mViewPagerAdapter);
    }

    public ArrayList<User> getLeaderboardList() {
        return mLeaderboardList;
    }

    public interface ScoreFragmentInterface {
        void stopRefreshing();

        void startRefreshing();

        void reloadData();
    }

    public interface AttemptsFragmentInterface {
        void stopRefreshing();

        void startRefreshing();

        void reloadData();
    }

    public interface TimeFragmentInterface {
        void stopRefreshing();

        void startRefreshing();

        void reloadData();
    }

    public void setListener(ScoreFragmentInterface scoreFragmentInterface) {
        this.mScoreFragmentInterface = scoreFragmentInterface;
    }

    public void setListener(AttemptsFragmentInterface attemptsFragmentInterface) {
        this.mAttemptsFragmentInterface = attemptsFragmentInterface;
    }

    public void setListener(TimeFragmentInterface timeFragmentInterface) {
        this.mTimeFragmentInterface = timeFragmentInterface;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
