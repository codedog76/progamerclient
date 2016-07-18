package activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.progamer.R;

import java.util.ArrayList;
import java.util.List;

import fragments.LoginFragment;
import fragments.RegisterFragment;

public class LoginActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TextView logoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        assignViews();
        assignViews();
        assignFonts();
    }

    private void assignViews() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        if (tabLayout != null)
            tabLayout.setupWithViewPager(viewPager);
        logoTextView = (TextView)findViewById(R.id.logoTextView);
    }

    private void assignFonts(){
        Typeface Roboto_Medium = Typeface.createFromAsset(getAssets(), "Roboto-Medium.ttf");
        logoTextView.setTypeface(Roboto_Medium);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new LoginFragment(), "Log In");
        adapter.addFragment(new RegisterFragment(), "Register");
        viewPager.setAdapter(adapter);
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
