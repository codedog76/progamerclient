package activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.progamer.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import adapters.AchievementAdapter;
import de.hdodenhof.circleimageview.CircleImageView;
import models.Level;
import models.User;
import models.UserAchievement;
import singletons.DatabaseHandlerSingleton;
import singletons.NetworkManagerSingleton;

public class UserProfileActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mTextNickname, mTextStudentNumber, mTextTotalScore, mTextTotalAttempts, mTextTotalTime,
            mTextUserPerfTitle, mTextAveragePerfTitle, mTextVisitorPerfTitle,
            mTextVisitorPerfAverageScore, mTextUserPerfAverageScore, mTextAveragePerfAverageScore,
            mTextVisitorPerfAverageAttempts, mTextUserPerfAverageAttempts, mTextAveragePerfAverageAttempts,
            mTextVisitorPerfAverageTime, mTextUserPerfAverageTime, mTextAveragePerfAverageTime,
            mTextPerformanceTitle, mTextAchievementTitle, mTextAverageScorePerf, mTextAverageAttemptsPerf, mTextAverageTimePerf;
    private LinearLayout mLinearTopContainer;
    private ProgressBar mProgressBar,
            mProgressVisitorPerfAverageScore, mProgressUserPerfAverageScore, mProgressAveragePerfAverageScore,
            mProgressVisitorPerfAverageAttempts, mProgressUserPerfAverageAttempts, mProgressAveragePerfAverageAttempts,
            mProgressVisitorPerfAverageTime, mProgressUserPerfAverageTime, mProgressAveragePerfAverageTime;
    private RelativeLayout mRelativeVisitorPerfAverageScore, mRelativeUserPerfAverageScore, mRelativeAveragePerfAverageScore,
            mRelativeVisitorPerfAverageAttempts, mRelativeUserPerfAverageAttempts, mRelativeAveragePerfAverageAttempts,
            mRelativeVisitorPerfAverageTime, mRelativeUserPerfAverageTime, mRelativeAveragePerfAverageTime;
    private CircleImageView mCircleImageAvatar;
    private NetworkManagerSingleton mNetworkManagerSingleton;
    private String mClassName = getClass().toString();
    private RecyclerView mRecyclerView;
    private ArrayList<UserAchievement> mAchievementList = new ArrayList<>();
    private ArrayList<UserAchievement> mConstantAchievementList = new ArrayList<>();
    private AchievementAdapter mAchievementAdapter;
    private DatabaseHandlerSingleton mDatabaseHandlerSingleton;
    private String[] mAchievementListSort = new String[]{"Sort by date completed", "Sort by title", "Sort by completed"};
    private String[] mAchievementListFilter = new String[]{"All", "Complete", "Incomplete"};
    private Spinner mSpinnerAchievementSort, mSpinnerAchievementFilter;
    private String mCurrentSort = mAchievementListSort[0];
    private String mCurrentFilter;
    private boolean mIsVisitor = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        assignSingletons();
        assignViews();
        assignFonts();
        assignActionBar();
        assignListeners();
        getBundle();
    }

    private void assignFonts() {
        Typeface Roboto_Medium = Typeface.createFromAsset(getAssets(), "Roboto-Medium.ttf");
        Typeface Roboto_Regular = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        mTextNickname.setTypeface(Roboto_Regular);
        mTextStudentNumber.setTypeface(Roboto_Regular);
        mTextTotalScore.setTypeface(Roboto_Regular);
        mTextTotalAttempts.setTypeface(Roboto_Regular);
        mTextTotalTime.setTypeface(Roboto_Regular);
        mTextUserPerfTitle.setTypeface(Roboto_Regular);
        mTextAveragePerfTitle.setTypeface(Roboto_Regular);
        mTextVisitorPerfTitle.setTypeface(Roboto_Regular);
        mTextVisitorPerfAverageScore.setTypeface(Roboto_Regular);
        mTextUserPerfAverageScore.setTypeface(Roboto_Regular);
        mTextAveragePerfAverageScore.setTypeface(Roboto_Regular);
        mTextVisitorPerfAverageAttempts.setTypeface(Roboto_Regular);
        mTextUserPerfAverageAttempts.setTypeface(Roboto_Regular);
        mTextAveragePerfAverageAttempts.setTypeface(Roboto_Regular);
        mTextVisitorPerfAverageTime.setTypeface(Roboto_Regular);
        mTextUserPerfAverageTime.setTypeface(Roboto_Regular);
        mTextAveragePerfAverageTime.setTypeface(Roboto_Regular);
        mTextPerformanceTitle.setTypeface(Roboto_Regular);
        mTextAchievementTitle.setTypeface(Roboto_Regular);
        mTextAverageScorePerf.setTypeface(Roboto_Regular);
        mTextAverageAttemptsPerf.setTypeface(Roboto_Regular);
        mTextAverageTimePerf.setTypeface(Roboto_Regular);
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
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mProgressVisitorPerfAverageScore = (ProgressBar) findViewById(R.id.progress_visitor_perf_average_score);
        mProgressUserPerfAverageScore = (ProgressBar) findViewById(R.id.progress_user_perf_average_score);
        mProgressAveragePerfAverageScore = (ProgressBar) findViewById(R.id.progress_average_perf_average_score);
        mProgressVisitorPerfAverageAttempts = (ProgressBar) findViewById(R.id.progress_visitor_perf_average_attempts);
        mProgressUserPerfAverageAttempts = (ProgressBar) findViewById(R.id.progress_user_perf_average_attempts);
        mProgressAveragePerfAverageAttempts = (ProgressBar) findViewById(R.id.progress_average_perf_average_attempts);
        mProgressVisitorPerfAverageTime = (ProgressBar) findViewById(R.id.progress_visitor_perf_average_time);
        mProgressUserPerfAverageTime = (ProgressBar) findViewById(R.id.progress_user_perf_average_time);
        mProgressAveragePerfAverageTime = (ProgressBar) findViewById(R.id.progress_average_perf_average_time);
        mLinearTopContainer = (LinearLayout) findViewById(R.id.linear_top_container);
        mTextNickname = (TextView) findViewById(R.id.text_nickname);
        mTextStudentNumber = (TextView) findViewById(R.id.text_student_number);
        mTextTotalScore = (TextView) findViewById(R.id.text_total_score);
        mTextTotalAttempts = (TextView) findViewById(R.id.text_total_attempts);
        mTextTotalTime = (TextView) findViewById(R.id.text_total_time);
        mTextUserPerfTitle = (TextView) findViewById(R.id.text_user_perf_title);
        mTextAveragePerfTitle = (TextView) findViewById(R.id.text_average_perf_title);
        mTextVisitorPerfTitle = (TextView) findViewById(R.id.text_visitor_perf_title);
        mTextVisitorPerfAverageScore = (TextView) findViewById(R.id.text_visitor_perf_average_score);
        mTextUserPerfAverageScore = (TextView) findViewById(R.id.text_user_perf_average_score);
        mTextAveragePerfAverageScore = (TextView) findViewById(R.id.text_average_perf_average_score);
        mTextVisitorPerfAverageAttempts = (TextView) findViewById(R.id.text_visitor_perf_average_attempts);
        mTextUserPerfAverageAttempts = (TextView) findViewById(R.id.text_user_perf_average_attempts);
        mTextAveragePerfAverageAttempts = (TextView) findViewById(R.id.text_average_perf_average_attempts);
        mTextVisitorPerfAverageTime = (TextView) findViewById(R.id.text_visitor_perf_average_time);
        mTextUserPerfAverageTime = (TextView) findViewById(R.id.text_user_perf_average_time);
        mTextAveragePerfAverageTime = (TextView) findViewById(R.id.text_average_perf_average_time);
        mTextPerformanceTitle = (TextView) findViewById(R.id.text_performance_title);
        mTextAchievementTitle = (TextView) findViewById(R.id.text_achievement_title);
        mTextAverageScorePerf = (TextView) findViewById(R.id.text_average_score_perf);
        mTextAverageAttemptsPerf = (TextView) findViewById(R.id.text_average_attempts_perf);
        mTextAverageTimePerf = (TextView) findViewById(R.id.text_average_time_perf);
        mCircleImageAvatar = (CircleImageView) findViewById(R.id.circle_image_avatar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mSpinnerAchievementSort = (Spinner) findViewById(R.id.spinner_achievement_sort);
        mSpinnerAchievementFilter = (Spinner) findViewById(R.id.spinner_achievement_filter);
        mRelativeVisitorPerfAverageScore = (RelativeLayout) findViewById(R.id.relative_visitor_perf_average_score);
        mRelativeUserPerfAverageScore = (RelativeLayout) findViewById(R.id.relative_user_perf_average_score);
        mRelativeAveragePerfAverageScore = (RelativeLayout) findViewById(R.id.relative_average_perf_average_score);
        mRelativeVisitorPerfAverageAttempts = (RelativeLayout) findViewById(R.id.relative_visitor_perf_average_attempts);
        mRelativeUserPerfAverageAttempts = (RelativeLayout) findViewById(R.id.relative_user_perf_average_attempts);
        mRelativeAveragePerfAverageAttempts = (RelativeLayout) findViewById(R.id.relative_average_perf_average_attempts);
        mRelativeVisitorPerfAverageTime = (RelativeLayout) findViewById(R.id.relative_visitor_perf_average_time);
        mRelativeUserPerfAverageTime = (RelativeLayout) findViewById(R.id.relative_user_perf_average_time);
        mRelativeAveragePerfAverageTime = (RelativeLayout) findViewById(R.id.relative_average_perf_average_time);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAchievementAdapter = new AchievementAdapter(this);
        mRecyclerView.setAdapter(mAchievementAdapter);
        mRecyclerView.setNestedScrollingEnabled(false);

        assignSortSpinnerView();
        assignFilerSpinnerView();
    }

    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mLinearTopContainer.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
            String user_student_number = bundle.getString("user_student_number", null);
            if (user_student_number == null)
                finish();
            mIsVisitor = !mDatabaseHandlerSingleton.getLoggedUserStudentNumber().equals(user_student_number);
            assignPerformanceViews(user_student_number);
        } else {
            Log.e(mClassName, "Bundle null");
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    private void assignPerformanceViews(String user_student_number) {
        User currentUser = mDatabaseHandlerSingleton.getLoggedUser();
        int total_score = 0, total_attempts = 0, total_time = 0, total_count = 0;
        double average_score = 0, average_attempts = 0, average_time = 0;
        if(!currentUser.getUser_type().equals("admin")) {
            List<Level> levelList = mDatabaseHandlerSingleton.getLevels();
            for (Level level : levelList) {
                if (level.getLevel_completed() == 1) {
                    total_score += level.getLevel_score();
                    total_attempts += level.getLevel_attempts();
                    total_time += level.getLevel_time();
                    total_count += 1;
                }

            }
            average_score = total_score / total_count;
            average_attempts = total_attempts / total_count;
            average_time = total_time / total_count;
        }
        if (mIsVisitor) {
            //assign the visitor column, which will always be the logged in user.
            mTextVisitorPerfTitle.setText(currentUser.getUser_nickname());
            mTextVisitorPerfAverageScore.setText(String.valueOf(average_score));
            mTextVisitorPerfAverageAttempts.setText(String.valueOf(average_attempts));
            mTextVisitorPerfAverageTime.setText(String.valueOf(average_time));
            mProgressVisitorPerfAverageScore.setVisibility(View.GONE);
            mProgressVisitorPerfAverageAttempts.setVisibility(View.GONE);
            mProgressVisitorPerfAverageTime.setVisibility(View.GONE);
            if(!currentUser.getUser_type().equals("admin")) {
                mTextVisitorPerfAverageScore.setVisibility(View.VISIBLE);
                mTextVisitorPerfAverageAttempts.setVisibility(View.VISIBLE);
                mTextVisitorPerfAverageTime.setVisibility(View.VISIBLE);
            } else {
                mTextVisitorPerfTitle.setVisibility(View.GONE);
                mRelativeVisitorPerfAverageScore.setVisibility(View.GONE);
                mRelativeVisitorPerfAverageAttempts.setVisibility(View.GONE);
                mRelativeVisitorPerfAverageTime.setVisibility(View.GONE);
            }
            final User user = new User();
            user.setUser_student_number_id(user_student_number);
            //get data of the user being visited
            mNetworkManagerSingleton.getUserJsonRequest(user, new NetworkManagerSingleton.ObjectResponseListener<User>() {
                @Override
                public void getResult(User object, Boolean response, String message) {
                    if (response) {
                        //assign user column to visited user values
                        mTextUserPerfTitle.setText(object.getUser_nickname());
                        mTextUserPerfAverageScore.setText(String.valueOf(object.getUser_average_score()));
                        mTextUserPerfAverageAttempts.setText(String.valueOf(object.getUser_average_attempts()));
                        mTextUserPerfAverageTime.setText(String.valueOf(object.getUser_average_time()));
                        //make column visible
                        mTextUserPerfTitle.setVisibility(View.VISIBLE);
                        mTextUserPerfAverageScore.setVisibility(View.VISIBLE);
                        mProgressUserPerfAverageScore.setVisibility(View.GONE);
                        mTextUserPerfAverageAttempts.setVisibility(View.VISIBLE);
                        mProgressUserPerfAverageAttempts.setVisibility(View.GONE);
                        mTextUserPerfAverageTime.setVisibility(View.VISIBLE);
                        mProgressUserPerfAverageTime.setVisibility(View.GONE);
                        //assign profile view values
                        mTextNickname.setText(object.getUser_nickname());
                        mTextStudentNumber.setText(object.getUser_student_number_id());
                        mTextTotalScore.setText(String.valueOf(object.getUser_total_score()));
                        mTextTotalAttempts.setText(String.valueOf(object.getUser_total_attempts()));
                        mTextTotalTime.setText(String.valueOf(object.getUser_total_time()));
                        int id = getResources().getIdentifier("avatar_" + String.valueOf(object.getUser_avatar()),
                                "drawable", getPackageName());
                        mCircleImageAvatar.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), id));

                        mNetworkManagerSingleton.getUserAchievementsJSONRequest(user, new NetworkManagerSingleton.ObjectResponseListener<ArrayList<UserAchievement>>() {
                            @Override
                            public void getResult(ArrayList<UserAchievement> object, Boolean response, String message) {
                                if (response) {
                                    mConstantAchievementList = new ArrayList<>(object);
                                    addList(mConstantAchievementList);
                                    mSpinnerAchievementFilter.setSelection(1);
                                    //done loading
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
                    } else {
                        Log.e(mClassName, message);
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        finish();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                }
            });
        } else {
            if(currentUser.getUser_type().equals("admin")) {
                finish();
                return;
            }
            //if it is the logged in user there is no need for visitor column.
            mTextVisitorPerfTitle.setVisibility(View.GONE);
            mTextVisitorPerfAverageScore.setVisibility(View.GONE);
            mProgressVisitorPerfAverageScore.setVisibility(View.GONE);
            mTextVisitorPerfAverageAttempts.setVisibility(View.GONE);
            mProgressVisitorPerfAverageAttempts.setVisibility(View.GONE);
            mTextVisitorPerfAverageTime.setVisibility(View.GONE);
            mProgressVisitorPerfAverageTime.setVisibility(View.GONE);
            mRelativeVisitorPerfAverageScore.setVisibility(View.GONE);
            mRelativeVisitorPerfAverageAttempts.setVisibility(View.GONE);
            mRelativeVisitorPerfAverageTime.setVisibility(View.GONE);
            //the current user is the logged in user, therefore assign that column to the logged in user data
            mTextUserPerfTitle.setText(currentUser.getUser_nickname());
            mTextUserPerfAverageScore.setText(String.valueOf(average_score));
            mTextUserPerfAverageAttempts.setText(String.valueOf(average_attempts));
            mTextUserPerfAverageTime.setText(String.valueOf(average_time));
            //make user column visible
            mTextUserPerfTitle.setVisibility(View.VISIBLE);
            mTextUserPerfAverageScore.setVisibility(View.VISIBLE);
            mProgressUserPerfAverageScore.setVisibility(View.GONE);
            mTextUserPerfAverageAttempts.setVisibility(View.VISIBLE);
            mProgressUserPerfAverageAttempts.setVisibility(View.GONE);
            mTextUserPerfAverageTime.setVisibility(View.VISIBLE);
            mProgressUserPerfAverageTime.setVisibility(View.GONE);

            mConstantAchievementList = new ArrayList<>(mDatabaseHandlerSingleton.getLoggedUserAchievements());
            addList(mConstantAchievementList);
            mSpinnerAchievementFilter.setSelection(1);
            mTextNickname.setText(currentUser.getUser_nickname());
            mTextStudentNumber.setText(currentUser.getUser_student_number_id());
            mTextTotalScore.setText(String.valueOf(total_score));
            mTextTotalAttempts.setText(String.valueOf(total_attempts));
            mTextTotalTime.setText(String.valueOf(total_time));

            int id = getResources().getIdentifier("avatar_" + String.valueOf(currentUser.getUser_avatar()),
                    "drawable", getPackageName());
            mCircleImageAvatar.setImageDrawable(ContextCompat.getDrawable(this, id));

            mProgressBar.setVisibility(View.GONE);
            mLinearTopContainer.setVisibility(View.VISIBLE);
        }
        mNetworkManagerSingleton.getAverageUserDataJsonRequest(new NetworkManagerSingleton.ObjectResponseListener<User>() {
            @Override
            public void getResult(User object, Boolean response, String message) {
                if (response) {
                    //if get average user data success, set average column
                    mTextAveragePerfAverageScore.setText(String.valueOf(object.getUser_average_score()));
                    mTextAveragePerfAverageAttempts.setText(String.valueOf(object.getUser_average_attempts()));
                    mTextAveragePerfAverageTime.setText(String.valueOf(object.getUser_average_time()));
                    mTextAveragePerfTitle.setVisibility(View.VISIBLE);
                    mTextAveragePerfAverageScore.setVisibility(View.VISIBLE);
                    mTextAveragePerfAverageAttempts.setVisibility(View.VISIBLE);
                    mTextAveragePerfAverageTime.setVisibility(View.VISIBLE);
                } else {
                    mTextAveragePerfAverageScore.setVisibility(View.VISIBLE);
                    mTextAveragePerfAverageAttempts.setVisibility(View.VISIBLE);
                    mTextAveragePerfAverageTime.setVisibility(View.VISIBLE);
                    mTextAveragePerfAverageScore.setText("N/A");
                    mTextAveragePerfAverageAttempts.setText("N/A");
                    mTextAveragePerfAverageTime.setText("N/A");
                }
                mProgressAveragePerfAverageScore.setVisibility(View.GONE);
                mProgressAveragePerfAverageAttempts.setVisibility(View.GONE);
                mProgressAveragePerfAverageTime.setVisibility(View.GONE);
            }
        });
    }

    private void sortList(String toSortBy) {
        mCurrentSort = toSortBy;
        switch (toSortBy) {
            case "Sort by date completed":
                sortListDate();
                addList(mAchievementList);
                mSpinnerAchievementSort.setSelection(0);
                break;
            case "Sort by title":
                sortListTitle();
                addList(mAchievementList);
                mSpinnerAchievementSort.setSelection(1);
                break;
            case "Sort by completed":
                sortListComplete();
                addList(mAchievementList);
                mSpinnerAchievementSort.setSelection(2);
                break;
        }
        mCurrentSort = toSortBy;
    }

    private void filterList(String toFilterBy) {
        switch (toFilterBy) {
            case "All":
                sortList(mCurrentSort);
                addList(mConstantAchievementList);
                break;
            case "Complete":
                filterListComplete();
                addList(mAchievementList);
                mSpinnerAchievementFilter.setSelection(1);
                break;
            case "Incomplete":
                filterListIncomplete();
                addList(mAchievementList);
                mSpinnerAchievementFilter.setSelection(2);
                break;
        }
        mCurrentFilter = toFilterBy;
    }

    private void assignListeners() {
        mSpinnerAchievementSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Log.e("sort", "called " + mAchievementListSort[position]);
                sortList(mAchievementListSort[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpinnerAchievementFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Log.e("filter", "called "+mAchievementListFilter[position]);
                filterList(mAchievementListFilter[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void sortListDate() {
        Collections.sort(mAchievementList, new Comparator<UserAchievement>() {
            @Override
            public int compare(UserAchievement obj1, UserAchievement obj2) {

                if (obj1.getUserachievement_date_completed() == null) {
                    return -1;
                }
                if (obj2.getUserachievement_date_completed() == null) {
                    return 1;
                }
                if (obj1.getUserachievement_date_completed().equals(obj2.getUserachievement_date_completed())) {
                    return 0;
                }
                return obj2.getUserachievement_date_completed().compareTo(obj1.getUserachievement_date_completed());
            }
        });
    }

    public void sortListTitle() {
        Collections.sort(mAchievementList, new Comparator<UserAchievement>() {
            @Override
            public int compare(UserAchievement obj1, UserAchievement obj2) {
                if (obj1.getAchievement_title().equals(obj2.getAchievement_title())) {
                    return 0;
                }
                if (obj1.getAchievement_title() == null) {
                    return -1;
                }
                if (obj2.getAchievement_title() == null) {
                    return 1;
                }
                return obj1.getAchievement_title().compareTo(obj2.getAchievement_title());
            }
        });
    }

    public void sortListComplete() {
        Collections.sort(mAchievementList, new Comparator<UserAchievement>() {
            @Override
            public int compare(UserAchievement obj1, UserAchievement obj2) {
                if (obj1.getUserachievement_completed() > obj2.getUserachievement_completed())
                    return -1;
                if (obj1.getUserachievement_completed() < obj2.getUserachievement_completed())
                    return 1;
                else
                    return 0;
            }
        });
    }

    private void filterListComplete() {
        List<UserAchievement> tempToReplace = new ArrayList<>();
        for (UserAchievement userAchievement : mConstantAchievementList) {
            if (userAchievement.getUserachievement_completed() == 1) {
                tempToReplace.add(userAchievement);
            }
        }
        mAchievementList = new ArrayList<>(tempToReplace);
        sortList(mCurrentSort);
    }

    private void filterListIncomplete() {
        List<UserAchievement> tempToReplace = new ArrayList<>();
        for (UserAchievement userAchievement : mConstantAchievementList) {
            if (userAchievement.getUserachievement_completed() != 1) {
                tempToReplace.add(userAchievement);
            }
        }
        mAchievementList = new ArrayList<>(tempToReplace);
        sortList(mCurrentSort);
    }

    private void assignFilerSpinnerView() {
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item,
                mAchievementListFilter);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerAchievementFilter.setAdapter(spinnerArrayAdapter);
    }

    private void assignSortSpinnerView() {
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item,
                mAchievementListSort);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerAchievementSort.setAdapter(spinnerArrayAdapter);
    }

    public void addList(List<UserAchievement> list) {
        if (!list.isEmpty()) {
            mAchievementList = new ArrayList<>();
            mAchievementList.addAll(list);
            mAchievementAdapter.setAchievementList(mAchievementList);
        } else {
            mAchievementAdapter.clearAchievementList();
        }
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
}
