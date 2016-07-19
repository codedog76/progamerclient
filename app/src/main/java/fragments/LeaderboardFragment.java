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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.progamer.R;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import activities.MainActivity;
import activities.UserProfileActivity;
import models.User;
import singletons.NetworkManagerSingleton;


/**
 * A simple {@link Fragment} subclass.
 */
public class LeaderboardFragment extends Fragment implements LeaderboardOverallFragment.InterfaceListener, LeaderboardAttemptsFragment.InterfaceListener, LeaderboardTimeFragment.InterfaceListener {

    private TabLayout leaderboardTabLayout;
    private ViewPager leaderboardViewPager;
    private LeaderboardOverallFragment leaderboardOverallFragment;
    private LeaderboardAttemptsFragment leaderboardAttemptsFragment;
    private LeaderboardTimeFragment leaderboardTimeFragment;
    private TextView leaderboardLoadingTextView, leaderboardTryAgainTextView;
    private RelativeLayout leaderboardRelativeLayout;
    private LinearLayout leaderboardProgressBarLinearLayout, leaderboardTryAgainLinearLayout;
    private ViewPagerAdapter adapter;
    private NetworkManagerSingleton networkManagerSingleton;
    private OverallFragmentInterface overallFragmentInterface;
    private AttemptsFragmentInterface attemptsFragmentInterface;
    private TimeFragmentInterface timeFragmentInterface;
    private Button leaderboardTryAgainButton;
    private ArrayList<User> leaderboardList = new ArrayList<>();

    public LeaderboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        assignSingletons();
        assignViews(view);
        assignFonts();
        assingListeners();
        initializeData();
    }

    private void initializeData() {
        leaderboardRelativeLayout.setVisibility(View.VISIBLE);
        leaderboardProgressBarLinearLayout.setVisibility(View.VISIBLE);
        leaderboardTryAgainLinearLayout.setVisibility(View.GONE);
        networkManagerSingleton.downloadLeaderboardJSONRequest(new NetworkManagerSingleton.ObjectResponseListener<ArrayList<User>>() {
            @Override
            public void getResult(ArrayList<User> object, Boolean response, String message) {
                if (response) {
                    leaderboardList.clear();
                    leaderboardList = object;
                    assignViewPager();
                    leaderboardRelativeLayout.setVisibility(View.GONE);
                    leaderboardProgressBarLinearLayout.setVisibility(View.GONE);
                    leaderboardTryAgainLinearLayout.setVisibility(View.GONE);
                } else {
                    leaderboardProgressBarLinearLayout.setVisibility(View.GONE);
                    leaderboardTryAgainLinearLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    private void refreshData() {
        overallFragmentInterface.startRefreshing();
        attemptsFragmentInterface.startRefreshing();
        timeFragmentInterface.startRefreshing();
        networkManagerSingleton.downloadLeaderboardJSONRequest(new NetworkManagerSingleton.ObjectResponseListener<ArrayList<User>>() {
            @Override
            public void getResult(ArrayList<User> object, Boolean response, String message) {
                if (response) {
                    leaderboardList.clear();
                    leaderboardList = object;
                    overallFragmentInterface.reloadData();
                    attemptsFragmentInterface.reloadData();
                    timeFragmentInterface.reloadData();
                }
                overallFragmentInterface.stopRefreshing();
                attemptsFragmentInterface.stopRefreshing();
                timeFragmentInterface.stopRefreshing();
            }
        });
    }

    public ArrayList<User> getLeaderboardList() {
        return leaderboardList;
    }

    private void assignSingletons() {
        networkManagerSingleton = NetworkManagerSingleton.getInstance(getActivity());
    }

    private void assignViews(View view) {
        leaderboardViewPager = (ViewPager) view.findViewById(R.id.leaderboardViewPager);
        leaderboardTabLayout = (TabLayout) view.findViewById(R.id.leaderboardTabLayout);
        leaderboardRelativeLayout = (RelativeLayout) view.findViewById(R.id.leaderboardRelativeLayout);
        leaderboardProgressBarLinearLayout = (LinearLayout) view.findViewById(R.id.leaderboardProgressBarLinearLayout);
        leaderboardTryAgainLinearLayout = (LinearLayout) view.findViewById(R.id.leaderboardTryAgainLinearLayout);
        leaderboardTryAgainButton = (Button) view.findViewById(R.id.leaderboardTryAgainButton);
        leaderboardLoadingTextView = (TextView) view.findViewById(R.id.leaderboardLoadingTextView);
        leaderboardTryAgainTextView = (TextView) view.findViewById(R.id.leaderboardTryAgainTextView);
    }

    private void assignFonts() {
        Typeface Roboto_Regular = Typeface.createFromAsset(getContext().getAssets(), "Roboto-Regular.ttf");
        leaderboardLoadingTextView.setTypeface(Roboto_Regular);
        leaderboardTryAgainTextView.setTypeface(Roboto_Regular);
    }

    private void assingListeners() {
        leaderboardTryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeData();
            }
        });
    }

    private void assignViewPager() {
        setupViewPager(leaderboardViewPager);
        leaderboardTabLayout.setupWithViewPager(leaderboardViewPager);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_leaderboard, container, false);
    }

    private void setupViewPager(ViewPager viewPager) {
        viewPager.setOffscreenPageLimit(3);
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        leaderboardOverallFragment = new LeaderboardOverallFragment();
        leaderboardOverallFragment.setListener(this);
        adapter.addFragment(leaderboardOverallFragment, "Overall");
        leaderboardAttemptsFragment = new LeaderboardAttemptsFragment();
        leaderboardAttemptsFragment.setListener(this);
        adapter.addFragment(leaderboardAttemptsFragment, "Attempts");
        leaderboardTimeFragment = new LeaderboardTimeFragment();
        leaderboardTimeFragment.setListener(this);
        adapter.addFragment(leaderboardTimeFragment, "Time");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void itemClicked(int position, User selected_user) {
        Intent intent = new Intent(getContext(), UserProfileActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("selected_user", selected_user);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void startRefreshData() {
        refreshData();
    }

    public interface OverallFragmentInterface {
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

    public void setListener(OverallFragmentInterface overallFragmentInterface) {
        this.overallFragmentInterface = overallFragmentInterface;
    }

    public void setListener(AttemptsFragmentInterface attemptsFragmentInterface) {
        this.attemptsFragmentInterface = attemptsFragmentInterface;
    }

    public void setListener(TimeFragmentInterface timeFragmentInterface) {
        this.timeFragmentInterface = timeFragmentInterface;
    }

    private boolean isFragmentOverallLoaded, isFragmentAttemptsLoaded, isFragmentTimeLoaded;

    public void syncLeaderboardData(String fragment) {
        if (fragment.equals("overall"))
            isFragmentOverallLoaded = true;
        if (fragment.equals("attempts"))
            isFragmentAttemptsLoaded = true;
        if (fragment.equals("time"))
            isFragmentTimeLoaded = true;
        if (isFragmentOverallLoaded && isFragmentAttemptsLoaded && isFragmentTimeLoaded) {
        }
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
