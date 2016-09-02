package activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.progamer.R;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fragments.LeaderboardFragment;
import fragments.LevelsFragment;
import fragments.NavigationDrawerFragment;
import models.User;
import models.UserAchievement;
import services.SyncService;
import singletons.AchievementHandlerSingleton;
import singletons.DatabaseHandlerSingleton;
import singletons.NetworkManagerSingleton;

public class MainActivity extends AppCompatActivity implements NavigationDrawerFragment.DrawerListener {

    private LevelsFragment mLevelsFragment;
    private LeaderboardFragment mLeaderboardFragment;
    private LinearLayout mLinearNavLevels, mLinearNavLevelsBack, mLinearNavLeaderboard, mLinearAchievementPopup,
            mLinearNavLeaderboardBack, mLinearNavSettings, mLinearNavLogout, mLinearNavTop;
    private TextView mTextNavLevels, mTextNavLeaderboard, mTextNavLogout, mTextNavSettings, mTextNavNickname, mTextNavStudentNumber;
    private ImageView mImageNavLevels, mImageNavLeaderboard;
    private CircleImageView mCircleImageNavAvatar;
    private Boolean mLevelsFragmentSelected, mLeaderboardFragmentSelected;
    private ProgressDialog mProgressDialog;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Toolbar mToolbar;
    private DatabaseHandlerSingleton mDatabaseHandlerSingleton;
    private NetworkManagerSingleton mNetworkManagerSingleton;
    private String mClassName = getClass().toString();
    private final Handler mAchievementHandler = new Handler();
    private final Handler mAchievementCloseHandler = new Handler();
    private Animation mAnimLeftRight, mAnimRightLeft;
    private int mAchievementDisplayCounter = 0;
    private TextView mTextTitle, mTextDescription, mTextProgressNumeric;
    private ProgressBar mProgressBarAchievement;
    private List<UserAchievement> mUserAchievementList = new ArrayList<>();
    private boolean mIsMenuVisible = false;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

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
        mProgressDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isVisible()) mNavigationDrawerFragment.close();
        else {
            if (!mLevelsFragmentSelected) loadLevelsFragment();
            else finish();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (mDatabaseHandlerSingleton.getLoggedUser().getUser_type().equals("admin")) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            MenuItem mMenuItemDownload = menu.findItem(R.id.item_download_student_data);
            mMenuItemDownload.setVisible(mIsMenuVisible);
            mMenuItemDownload.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    verifyStoragePermissions(MainActivity.this);
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Save as:");
                    final EditText input = new EditText(MainActivity.this);
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String filename = input.getText().toString() + ".csv";
                            downloadStudentData(filename);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                    return false;
                }
            });
        }
        return true;
    }

    private void downloadStudentData(final String filename) {
        mProgressDialog.show();
        mNetworkManagerSingleton.getStudentDataJsonRequest(new NetworkManagerSingleton.ObjectResponseListener<ArrayList<User>>() {
            @Override
            public void getResult(ArrayList<User> object, Boolean response, String message) {
                if (response) {
                    try {
                        CSVWriter writer;
                        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                        File file = new File(path, filename);
                        file.createNewFile();
                        writer = new CSVWriter(new FileWriter(file.getAbsolutePath()));
                        String[] headers = {"Student number", "Student nickname", "Student levels completed",
                                "Student total score", "Student total attempts", "Student total time",
                                "Student average score", "Student average attempts", "Student average time"};
                        writer.writeNext(headers);
                        for (User user : object) {
                            String[] data = {
                                    user.getUser_student_number_id(),
                                    user.getUser_nickname(),
                                    String.valueOf(user.getUser_levels_completed()),
                                    String.valueOf(user.getUser_total_score()),
                                    String.valueOf(user.getUser_total_attempts()),
                                    String.valueOf(user.getUser_total_time()),
                                    String.valueOf(user.getUser_average_score()),
                                    String.valueOf(user.getUser_average_attempts()),
                                    String.valueOf(user.getUser_average_time())
                            };
                            writer.writeNext(data);
                        }
                        writer.close();
                        MediaScannerConnection.scanFile(getApplicationContext(),
                                new String[]{file.getAbsolutePath()}, null, null);
                    } catch (IOException e) {
                        Log.e("IOException", e.getMessage());
                    }
                    Toast.makeText(getApplicationContext(), "Student data successfully downloaded: Internal " +
                            "storage/Download/" + filename, Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("StudentDataJsonRequest", "Failed to download student data " + message);
                    Toast.makeText(getApplicationContext(), "Failed to download student data " + message, Toast.LENGTH_SHORT).show();
                }
                mProgressDialog.hide();
            }
        });
    }

    private void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    public void drawerClosed() {

    }

    @Override
    public void drawerOpened() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        AchievementHandlerSingleton achievementHandlerSingleton = AchievementHandlerSingleton.getInstance(this);
        mUserAchievementList = achievementHandlerSingleton.getUserAchievementsNotifications();
        if (mUserAchievementList.size() > 0) {
            mAchievementHandler.postDelayed(mAchievementRunnable, 1000);
        }
    }

    private Runnable mAchievementRunnable = new Runnable() {
        @Override
        public void run() {
            if (mAchievementDisplayCounter <= mUserAchievementList.size() - 1) {
                UserAchievement userAchievement = mUserAchievementList.get(mAchievementDisplayCounter);
                mTextTitle.setText(userAchievement.getAchievement_title());
                mTextDescription.setText(userAchievement.getAchievement_description());
                mTextProgressNumeric.setText(getString(R.string.string_out_of, userAchievement.getUserachievement_progress(), userAchievement.getAchievement_total()));
                mProgressBarAchievement.setMax(userAchievement.getAchievement_total());
                mProgressBarAchievement.setProgress(userAchievement.getUserachievement_progress());
                mLinearAchievementPopup.startAnimation(mAnimRightLeft);
                mLinearAchievementPopup.setVisibility(View.VISIBLE);
                mAchievementDisplayCounter++;
                mAchievementCloseHandler.postDelayed(mAchievementCloseRunnable, 5000);
                mAchievementHandler.postDelayed(this, 7000);
                mDatabaseHandlerSingleton.setUserAchievementNotified(userAchievement.getUserachievement_id());
            }
        }
    };

    private Runnable mAchievementCloseRunnable = new Runnable() {
        @Override
        public void run() {
            mLinearAchievementPopup.startAnimation(mAnimLeftRight);
            mLinearAchievementPopup.setVisibility(View.GONE);

        }
    };

    private void assignSingletons() {
        mDatabaseHandlerSingleton = DatabaseHandlerSingleton.getInstance(this);
        mNetworkManagerSingleton = NetworkManagerSingleton.getInstance(this);
    }

    private void assignProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading..");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
    }

    private void assignViews() {
        mToolbar = (Toolbar) findViewById(R.id.app_actionbar);
        mLinearNavLevels = (LinearLayout) findViewById(R.id.linear_nav_levels);
        mLinearNavLevelsBack = (LinearLayout) findViewById(R.id.linear_nav_levels_back);
        mLinearNavLeaderboard = (LinearLayout) findViewById(R.id.linear_nav_leaderboard);
        mLinearNavLeaderboardBack = (LinearLayout) findViewById(R.id.linear_nav_leaderboard_back);
        mLinearNavSettings = (LinearLayout) findViewById(R.id.linear_nav_settings);
        mLinearNavLogout = (LinearLayout) findViewById(R.id.linear_nav_logout);
        mLinearNavTop = (LinearLayout) findViewById(R.id.linear_nav_top);
        mCircleImageNavAvatar = (CircleImageView) findViewById(R.id.circle_image_nav_avatar);
        mImageNavLevels = (ImageView) findViewById(R.id.image_nav_levels);
        mImageNavLeaderboard = (ImageView) findViewById(R.id.image_nav_leaderboard);
        mTextNavLevels = (TextView) findViewById(R.id.text_nav_levels);
        mTextNavLeaderboard = (TextView) findViewById(R.id.text_nav_leaderboard);
        mTextNavSettings = (TextView) findViewById(R.id.text_nav_settings);
        mTextNavLogout = (TextView) findViewById(R.id.text_nav_logout);
        mTextNavNickname = (TextView) findViewById(R.id.text_nav_nickname);
        mTextNavStudentNumber = (TextView) findViewById(R.id.text_nav_student_number);
        mAnimLeftRight = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left_right);
        mAnimRightLeft = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_left);
        mTextTitle = (TextView) findViewById(R.id.text_title);
        mTextDescription = (TextView) findViewById(R.id.text_description);
        mProgressBarAchievement = (ProgressBar) findViewById(R.id.progress_bar_achievement);
        mTextProgressNumeric = (TextView) findViewById(R.id.text_progress_numeric);
        mLinearAchievementPopup = (LinearLayout) findViewById(R.id.linear_achievement_popup);
    }

    private void assignFonts() {
        Typeface Roboto_Medium = Typeface.createFromAsset(getAssets(), "Roboto-Medium.ttf");
        Typeface Roboto_Regular = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        mTextNavLevels.setTypeface(Roboto_Medium);
        mTextNavLeaderboard.setTypeface(Roboto_Medium);
        mTextNavSettings.setTypeface(Roboto_Medium);
        mTextNavLogout.setTypeface(Roboto_Medium);
        mTextNavNickname.setTypeface(Roboto_Medium);
        mTextNavStudentNumber.setTypeface(Roboto_Regular);
        mTextTitle.setTypeface(Roboto_Regular);
        mTextDescription.setTypeface(Roboto_Regular);
        mTextProgressNumeric.setTypeface(Roboto_Regular);
    }

    private void assignActionBar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowHomeEnabled(true);
        else {
            Log.e(mClassName, "getSupportActionBar null");
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    private void assignNavigationDrawer() {
        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_navigation_drawer);
        mNavigationDrawerFragment.setUp((DrawerLayout) findViewById(R.id.drawer_navigation), mToolbar);
        mNavigationDrawerFragment.setListener(this);
        mTextNavNickname.setText(mDatabaseHandlerSingleton.getLoggedUser().getUser_nickname());
        mTextNavStudentNumber.setText(mDatabaseHandlerSingleton.getLoggedUser().getUser_student_number_id());
        int id = getResources().getIdentifier("avatar_" +
                        String.valueOf(mDatabaseHandlerSingleton.getLoggedUser().getUser_avatar()),
                "drawable", getPackageName());
        mCircleImageNavAvatar.setImageDrawable(ContextCompat.getDrawable(this, id));
    }

    private void assignListeners() {
        mLinearNavLevels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNavigationDrawerFragment.close();
                loadLevelsFragment();
            }
        });
        mLinearNavLeaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNavigationDrawerFragment.close();
                loadLeaderboardFragment();
            }
        });
        mLinearNavSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        mLinearNavLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNavigationDrawerFragment.close();
                showLogoutDialog();
            }
        });
        mLinearNavTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("user_student_number", mDatabaseHandlerSingleton.getLoggedUser()
                        .getUser_student_number_id());
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        mLinearAchievementPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("user_student_number", mDatabaseHandlerSingleton.getLoggedUser()
                        .getUser_student_number_id());
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }

    public void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mDatabaseHandlerSingleton.logoutUser();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                })
                .setNegativeButton("No", null).show();
    }

    private void loadLevelsFragment() {
        if (mLevelsFragment == null) mLevelsFragment = new LevelsFragment();
        replaceFragment(mLevelsFragment);
        setSelectedFragment(mLinearNavLevels);
    }

    private void loadLeaderboardFragment() {
        if (mLeaderboardFragment == null) mLeaderboardFragment = new LeaderboardFragment();
        replaceFragment(mLeaderboardFragment);
        setSelectedFragment(mLinearNavLeaderboard);
    }

    private void replaceFragment(Fragment fragment) {
        String tag = fragment.getClass().toString();
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.replace(R.id.frame_container, fragment, tag);
            fragmentTransaction.addToBackStack(tag);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();
            getSupportFragmentManager().executePendingTransactions();
        } catch (Exception ex) {
            Log.e(mClassName, ex.getMessage());
        }
    }

    private void setSelectedFragment(LinearLayout layout) {
        if (layout.equals(mLinearNavLevels)) {
            mLinearNavLevelsBack.setBackgroundColor(ContextCompat.getColor(this, R.color.grey_300));
            setTitle("Levels");
            mLevelsFragmentSelected = true;
            mImageNavLevels.setAlpha(1.0f);
            mTextNavLevels.setAlpha(1.0f);
        } else {
            mLinearNavLevelsBack.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            mLevelsFragmentSelected = false;
            mImageNavLevels.setAlpha(0.54f);
            mTextNavLevels.setAlpha(0.87f);
        }
        if (layout.equals(mLinearNavLeaderboard)) {
            mIsMenuVisible = true;
            invalidateOptionsMenu();
            mLinearNavLeaderboardBack.setBackgroundColor(ContextCompat.getColor(this, R.color.grey_300));
            setTitle("Leaderboard");
            mLeaderboardFragmentSelected = true;
            mImageNavLeaderboard.setAlpha(1.0f);
            mTextNavLeaderboard.setAlpha(1.0f);
        } else {
            mIsMenuVisible = false;
            invalidateOptionsMenu();
            mLinearNavLeaderboardBack.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            mLeaderboardFragmentSelected = false;
            mImageNavLeaderboard.setAlpha(0.54f);
            mTextNavLeaderboard.setAlpha(0.87f);
        }
    }

}
