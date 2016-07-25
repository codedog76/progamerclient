package activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.progamer.R;

import fragments.LeaderboardFragment;
import fragments.LevelsFragment;
import fragments.NavigationDrawerFragment;
import services.SyncService;
import singletons.DatabaseHandlerSingleton;
import singletons.NetworkManagerSingleton;

public class MainActivity extends AppCompatActivity implements NavigationDrawerFragment.drawerListener {

    private LevelsFragment levelsFragment;
    private LeaderboardFragment leaderboardFragment;
    private LinearLayout navLevelsLayout, navLevelsLayoutBack, navLeaderboardLayout, navLeaderboardLayoutBack, navSettingsLayout, navLogoutLayout, topNavBarLayout;
    private TextView navLevelsTextView, navLeaderboardTextView, navLogoutTextView, navSettingsTextView, navNicknameTextView, navStudentNumberTextView;
    private ImageView navLevelsImageView, navLeaderboardImageView, navProfileImageView;
    private Boolean levelsFragmentSelected, leaderboardFragmentSelected;
    private ProgressDialog progressDialog;
    private NavigationDrawerFragment drawerFragment;
    private Toolbar toolbar;
    private DatabaseHandlerSingleton databaseHandlerSingleton;
    private NetworkManagerSingleton networkManagerSingleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        assignSingletons();
        assignViews();
        assignFonts();
        assignActionBar();
        assignNavigationDrawer();
        assignListeners();
        loadLevelsFragment();
        startService(new Intent(this, SyncService.class));
        assignProgressDialog();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
    }

    public void assignSingletons() {
        databaseHandlerSingleton = DatabaseHandlerSingleton.getInstance(this);
        networkManagerSingleton = NetworkManagerSingleton.getInstance(this);
    }

    private void assignProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading..");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private void assignViews() {
        toolbar = (Toolbar) findViewById(R.id.app_actionbar);
        navLevelsLayout = (LinearLayout) findViewById(R.id.navLevelsLayout);
        navLevelsLayoutBack = (LinearLayout) findViewById(R.id.navLevelsLayoutBack);
        navLeaderboardLayout = (LinearLayout) findViewById(R.id.navLeaderboardLayout);
        navLeaderboardLayoutBack = (LinearLayout) findViewById(R.id.navLeaderboardLayoutBack);
        navSettingsLayout = (LinearLayout) findViewById(R.id.navSettingsLayout);
        navLogoutLayout = (LinearLayout) findViewById(R.id.navLogoutLayout);
        topNavBarLayout = (LinearLayout) findViewById(R.id.topNavBarLayout);
        navLevelsImageView = (ImageView) findViewById(R.id.navLevelsImageView);
        navProfileImageView = (ImageView) findViewById(R.id.navProfileImageView);
        navLeaderboardImageView = (ImageView) findViewById(R.id.navLeaderboardImageView);
        navLevelsTextView = (TextView) findViewById(R.id.navLevelsTextView);
        navLeaderboardTextView = (TextView) findViewById(R.id.navLeaderboardTextView);
        navSettingsTextView = (TextView) findViewById(R.id.navSettingsTextView);
        navLogoutTextView = (TextView) findViewById(R.id.navLogoutTextView);
        navNicknameTextView = (TextView) findViewById(R.id.navNicknameTextView);
        navStudentNumberTextView = (TextView) findViewById(R.id.navStudentNumberTextView);
    }

    private void assignFonts() {
        Typeface Roboto_Medium = Typeface.createFromAsset(getAssets(), "Roboto-Medium.ttf");
        Typeface Roboto_Regular = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        navLevelsTextView.setTypeface(Roboto_Medium);
        navLeaderboardTextView.setTypeface(Roboto_Medium);
        navLogoutTextView.setTypeface(Roboto_Medium);
        navNicknameTextView.setTypeface(Roboto_Medium);
        navSettingsTextView.setTypeface(Roboto_Medium);
        navStudentNumberTextView.setTypeface(Roboto_Regular);
    }

    private void assignActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void assignNavigationDrawer() {
        drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp((DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        drawerFragment.setListener(this);
        navNicknameTextView.setText(databaseHandlerSingleton.getLoggedUser().getUser_nickname());
        navStudentNumberTextView.setText(databaseHandlerSingleton.getLoggedUser().getUser_student_number_id());
        int id = getResources().getIdentifier("avatar_"+String.valueOf(databaseHandlerSingleton.getLoggedUser().getUser_avatar()), "drawable", getPackageName());
        navProfileImageView.setImageDrawable(ContextCompat.getDrawable(this, id));
    }

    private void assignListeners() {
        navLevelsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerFragment.close();
                loadLevelsFragment();
            }
        });
        navLeaderboardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerFragment.close();
                loadLeaderboardFragment();
            }
        });
        navSettingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });
        navLogoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerFragment.close();
                showLogoutDialog();
            }
        });
        topNavBarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("is_logged_user", true);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to log out?")
                //.setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        databaseHandlerSingleton.logoutUser();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }
                })
                .setNegativeButton("No", null).show();
    }

    @Override
    public void drawerClosed() {

    }

    @Override
    public void drawerOpened() {

    }

    private void loadLevelsFragment() {
        if (levelsFragment == null)
            levelsFragment = new LevelsFragment();
        replaceFragment(levelsFragment);
        setSelectedFragment(navLevelsLayout);
    }

    private void loadLeaderboardFragment() {
        if (leaderboardFragment == null)
            leaderboardFragment = new LeaderboardFragment();
        replaceFragment(leaderboardFragment);
        setSelectedFragment(navLeaderboardLayout);
    }

    private void replaceFragment(Fragment fragment) {
        String tag = fragment.getClass().toString();
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment, tag);
            fragmentTransaction.addToBackStack(tag);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();
            getSupportFragmentManager().executePendingTransactions();
        } catch (Exception ex) {
            Log.e("FragmentException", ex.getMessage());
        }
    }

    private void setSelectedFragment(LinearLayout layout) {
        if (layout.equals(navLevelsLayout)) {
            navLevelsLayoutBack.setBackgroundColor(ContextCompat.getColor(this, R.color.grey_300));
            setTitle("Levels");
            levelsFragmentSelected = true;
            navLevelsImageView.setAlpha(1.0f);
            navLevelsTextView.setAlpha(1.0f);
        } else {
            navLevelsLayoutBack.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            levelsFragmentSelected = false;
            navLevelsImageView.setAlpha(0.54f);
            navLevelsTextView.setAlpha(0.87f);
        }
        if (layout.equals(navLeaderboardLayout)) {
            navLeaderboardLayoutBack.setBackgroundColor(ContextCompat.getColor(this, R.color.grey_300));
            setTitle("Leaderboard");
            leaderboardFragmentSelected = true;
            navLeaderboardImageView.setAlpha(1.0f);
            navLeaderboardTextView.setAlpha(1.0f);
        } else {
            navLeaderboardLayoutBack.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            leaderboardFragmentSelected = false;
            navLeaderboardImageView.setAlpha(0.54f);
            navLeaderboardTextView.setAlpha(0.87f);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerFragment.isVisible()) {
            drawerFragment.close();
        } else {
            if (!levelsFragmentSelected) {
                loadLevelsFragment();
            } else {
                finish();
            }
        }
    }
}
