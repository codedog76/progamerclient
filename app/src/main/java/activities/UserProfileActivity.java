package activities;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.progamer.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fragments.AchievementFragment;
import fragments.OverviewFragment;
import fragments.PerformanceFragment;
import models.User;
import singletons.DatabaseHandlerSingleton;
import singletons.NetworkManagerSingleton;

public class UserProfileActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private TextView userProfileStudentNameTextView, userProfileStudentNumberTextView, userProfileOverallRank, userProfileAttemptsRank, userProfileTimeRank;
    private LinearLayout activityUserProfileTopContainer;
    private ProgressBar activityUserProfileProgressBar;
    private CircleImageView leaderboardCircleImageView;
    private DatabaseHandlerSingleton databaseHandlerSingleton;
    private NetworkManagerSingleton networkManagerSingleton;
    private String mClassName = getClass().toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        assignSingletons();
        assignViews();
        assignActionBar();
        getBundle();
    }

    public void assignSingletons() {
        databaseHandlerSingleton = DatabaseHandlerSingleton.getInstance(this);
        networkManagerSingleton = NetworkManagerSingleton.getInstance(this);
    }

    private void assignActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
    }

    private void assignViews() {
        viewPager = (ViewPager) findViewById(R.id.userProfileViewPager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        activityUserProfileProgressBar = (ProgressBar) findViewById(R.id.activityUserProfileProgressBar);
        activityUserProfileTopContainer = (LinearLayout) findViewById(R.id.activityUserProfileTopContainer);
        userProfileStudentNameTextView = (TextView) findViewById(R.id.userProfileStudentNameTextView);
        userProfileStudentNumberTextView = (TextView) findViewById(R.id.userProfileStudentNumberTextView);
        userProfileOverallRank = (TextView) findViewById(R.id.userProfileOverallRank);
        userProfileAttemptsRank = (TextView) findViewById(R.id.userProfileAttemptsRank);
        userProfileTimeRank = (TextView) findViewById(R.id.userProfileTimeRank);
        leaderboardCircleImageView = (CircleImageView) findViewById(R.id.leaderboardCircleImageView);
    }

    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            activityUserProfileTopContainer.setVisibility(View.INVISIBLE);
            activityUserProfileProgressBar.setVisibility(View.VISIBLE);
            if (bundle.getBoolean("is_logged_user", false)) {
                userProfileStudentNameTextView.setText(databaseHandlerSingleton.getLoggedUser().getUser_nickname());
                userProfileStudentNumberTextView.setText(databaseHandlerSingleton.getLoggedUser().getUser_student_number_id());
                userProfileOverallRank.setText(String.valueOf(databaseHandlerSingleton.getLoggedUser().getUser_overall_score_rank()));
                userProfileAttemptsRank.setText(String.valueOf(databaseHandlerSingleton.getLoggedUser().getUser_overall_attempts_rank()));
                userProfileTimeRank.setText(String.valueOf(databaseHandlerSingleton.getLoggedUser().getUser_overall_time_rank()));
                int id = getResources().getIdentifier("avatar_" + String.valueOf(databaseHandlerSingleton.getLoggedUser().getUser_avatar()), "drawable", getPackageName());
                leaderboardCircleImageView.setImageDrawable(ContextCompat.getDrawable(this, id));
                activityUserProfileProgressBar.setVisibility(View.GONE);
                activityUserProfileTopContainer.setVisibility(View.VISIBLE);
            } else {
                User selected_user = (User)bundle.getSerializable("selected_user");
                if (selected_user != null) {
                    networkManagerSingleton.downloadUserJSONRequest(selected_user, new NetworkManagerSingleton.ObjectResponseListener<User>() {
                        @Override
                        public void getResult(User object, Boolean response, String message) {
                            if (response) {
                                userProfileStudentNameTextView.setText(object.getUser_nickname());
                                userProfileStudentNumberTextView.setText(object.getUser_student_number_id());
                                userProfileOverallRank.setText(String.valueOf(object.getUser_overall_score_rank()));
                                userProfileAttemptsRank.setText(String.valueOf(object.getUser_overall_attempts_rank()));
                                userProfileTimeRank.setText(String.valueOf(object.getUser_overall_time_rank()));
                                int id = getResources().getIdentifier("avatar_" + String.valueOf(object.getUser_avatar()), "drawable", getPackageName());
                                leaderboardCircleImageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), id));
                                activityUserProfileProgressBar.setVisibility(View.GONE);
                                activityUserProfileTopContainer.setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });

                } else {
                    finish();
                }
            }
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new OverviewFragment(), "Overview");
        adapter.addFragment(new PerformanceFragment(), "Performance");
        adapter.addFragment(new AchievementFragment(), "Achievements");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
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
