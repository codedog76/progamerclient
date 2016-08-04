package activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
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

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private Toolbar mToolbar;
    private TextView mTextNickname, mTextStudentNumber, mTextTotalScore, mTextTotalAttempts,
            mTextTotalTime;
    private LinearLayout mLinearTopContainer;
    private ProgressBar mProgressBar;
    private CircleImageView mCircleImageAvatar;
    private DatabaseHandlerSingleton mDatabaseHandlerSingleton;
    private NetworkManagerSingleton mNetworkManagerSingleton;
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
        mDatabaseHandlerSingleton = DatabaseHandlerSingleton.getInstance(this);
        mNetworkManagerSingleton = NetworkManagerSingleton.getInstance(this);
    }

    private void assignActionBar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setHomeButtonEnabled(true);
        else Log.e(mClassName, "getSupportActionBar null");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
    }

    private void assignViews() {
        mViewPager = (ViewPager) findViewById(R.id.userProfileViewPager);
        setupViewPager(mViewPager);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        if (mTabLayout != null) mTabLayout.setupWithViewPager(mViewPager);
        else Log.e(mClassName, "mTabLayout null");
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mLinearTopContainer = (LinearLayout) findViewById(R.id.linear_top_container);
        mTextNickname = (TextView) findViewById(R.id.text_nickname);
        mTextStudentNumber = (TextView) findViewById(R.id.text_student_number);
        mTextTotalScore = (TextView) findViewById(R.id.text_total_score);
        mTextTotalAttempts = (TextView) findViewById(R.id.text_total_attempts);
        mTextTotalTime = (TextView) findViewById(R.id.text_total_time);
        mCircleImageAvatar = (CircleImageView) findViewById(R.id.circle_image_avatar);
    }

    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mLinearTopContainer.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
            String user_student_number = bundle.getString("user_student_number", null);
            if (user_student_number != null && mDatabaseHandlerSingleton.getLoggedUserStudentNumber()
                    .equals(user_student_number)) {
                User currentUser = mDatabaseHandlerSingleton.getLoggedUser();
                mTextNickname.setText(currentUser.getUser_nickname());
                mTextStudentNumber.setText(currentUser.getUser_student_number_id());
                mTextTotalScore.setText(String.valueOf(currentUser.getUser_overall_score_rank()));
                mTextTotalAttempts.setText(String.valueOf(currentUser.getUser_overall_attempts_rank()));
                mTextTotalTime.setText(String.valueOf(currentUser.getUser_overall_time_rank()));
                int id = getResources().getIdentifier("avatar_" + String.valueOf(currentUser.getUser_avatar()),
                        "drawable", getPackageName());
                mCircleImageAvatar.setImageDrawable(ContextCompat.getDrawable(this, id));
                mProgressBar.setVisibility(View.GONE);
                mLinearTopContainer.setVisibility(View.VISIBLE);
                String achievement = bundle.getString("achievement", null);
                if (achievement != null && achievement.equals("achievement")) {
                    TabLayout.Tab tab = mTabLayout.getTabAt(2);
                    if (tab != null)
                        tab.select();
                }
            } else {
                User user = new User();
                user.setUser_student_number_id(user_student_number);
                mNetworkManagerSingleton.getUserJsonRequest(user, new NetworkManagerSingleton.ObjectResponseListener<User>() {
                    @Override
                    public void getResult(User object, Boolean response, String message) {
                        if (response) {
                            mTextNickname.setText(object.getUser_nickname());
                            mTextStudentNumber.setText(object.getUser_student_number_id());
                            mTextTotalScore.setText(String.valueOf(object.getUser_overall_score_rank()));
                            mTextTotalAttempts.setText(String.valueOf(object.getUser_overall_attempts_rank()));
                            mTextTotalTime.setText(String.valueOf(object.getUser_overall_time_rank()));
                            int id = getResources().getIdentifier("avatar_" + String.valueOf(object.getUser_avatar()),
                                    "drawable", getPackageName());
                            mCircleImageAvatar.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), id));
                            mProgressBar.setVisibility(View.GONE);
                            mLinearTopContainer.setVisibility(View.VISIBLE);
                        } else {
                            Log.e(mClassName, message);
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            finish();
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        }
                    }
                });
            }
        } else {
            Log.e(mClassName, "Bundle null");
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
