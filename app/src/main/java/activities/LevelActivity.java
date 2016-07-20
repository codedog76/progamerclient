package activities;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.progamer.R;

import java.util.ArrayList;
import java.util.List;

import database.DatabaseHandler;
import models.Level;
import models.Puzzle;
import singletons.DatabaseHandlerSingleton;

public class LevelActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Level selectedLevel;
    private DatabaseHandlerSingleton mDatabaseHandlerSingleton;
    private TextView activityLevelTitleTextView;
    private Button levelContinueButton;
    private String mClassName = getClass().toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        assignViews();
        assignSingletons();
        assignListeners();
        assignActionBar();
        getBundle();
    }

    private void assignListeners() {
        levelContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PuzzleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("current_level", selectedLevel);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void assignSingletons() {
        mDatabaseHandlerSingleton = DatabaseHandlerSingleton.getInstance(this);
    }

    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            selectedLevel = (Level) bundle.getSerializable("selected_level");
            if (selectedLevel != null) {
                getSupportActionBar().setTitle("Level " + selectedLevel.getLevel_number());
                activityLevelTitleTextView.setText(selectedLevel.getLevel_title());
            } else {
                Log.e(mClassName, "Level data missing");
                finish();
            }
        }
    }

    private void assignViews() {
        toolbar = (Toolbar) findViewById(R.id.app_actionbar);
        activityLevelTitleTextView = (TextView) findViewById(R.id.activityLevelTitleTextView);
        levelContinueButton = (Button) findViewById(R.id.levelContinueButton);
    }

    private void assignActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        finish();
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

    private class ViewPagerAdapter extends FragmentPagerAdapter {
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
