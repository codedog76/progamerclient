package activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.progamer.R;

import java.util.ArrayList;

import adapters.LevelAdapter;
import adapters.PuzzleAdapter;
import models.Level;
import models.Puzzle;
import singletons.DatabaseHandlerSingleton;
import singletons.NetworkManagerSingleton;

public class AdminPuzzlesActivity extends AppCompatActivity implements PuzzleAdapter.ClickListener {

    private RecyclerView mRecyclerView;
    private NetworkManagerSingleton mNetworkManagerSingleton;
    private DatabaseHandlerSingleton mDatabaseHandlerSingleton;
    private RelativeLayout mRelativeLayout;
    private LinearLayout mLinearProgressBar, mLinearTryAgain;
    private TextView mTextTryAgain;
    private Button mButtonTryAgain;
    private ArrayList<Puzzle> mPuzzleList;
    private PuzzleAdapter mPuzzleAdapter;
    private int mLevelId;
    private String mClassName = getClass().toString();
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_puzzles);
        assignSingletons();
        assignViews();
        assignActionBar();
        assignAdapter();
        getBundle();
        loadPuzzles();
        setTitle("Puzzles");
    }

    @Override
    protected void onStop() {
        super.onStop();
        mNetworkManagerSingleton.cancelJSONRequest("getAdminPuzzlesJsonRequest");
    }

    @Override
    public void itemClicked(int position, int puzzle_id) {

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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

    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mLevelId = bundle.getInt("level_id", -1);
            if (mLevelId == -1) finish();
        }
    }

    private void assignAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mPuzzleAdapter = new PuzzleAdapter(this);
        mPuzzleAdapter.setListener(this);
        mRecyclerView.setAdapter(mPuzzleAdapter);
    }

    private void loadPuzzles() {
        mRecyclerView.setVisibility(View.GONE);
        mRelativeLayout.setVisibility(View.VISIBLE);
        mLinearTryAgain.setVisibility(View.GONE);
        mLinearProgressBar.setVisibility(View.VISIBLE);
        mNetworkManagerSingleton.getAdminPuzzlesJsonRequest(mLevelId, new NetworkManagerSingleton.ObjectResponseListener<ArrayList<Puzzle>>() {
            @Override
            public void getResult(ArrayList<Puzzle> object, Boolean response, String message) {
                if(response) {
                    mLinearProgressBar.setVisibility(View.GONE);
                    mRelativeLayout.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    addList(object);
                } else {
                    mTextTryAgain.setText(message);
                    mLinearProgressBar.setVisibility(View.GONE);
                    mLinearTryAgain.setVisibility(View.VISIBLE);
                    mButtonTryAgain.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loadPuzzles();
                        }
                    });
                }
            }
        });
    }

    public void addList(ArrayList<Puzzle> list) {
        if (!list.isEmpty()) {
            mPuzzleList = new ArrayList<>();
            mPuzzleList.addAll(list);
            mPuzzleAdapter.setPuzzleList(mPuzzleList);
        } else {
            mPuzzleAdapter.clearPuzzleList();
        }
    }

    private void assignViews() {
        mToolbar = (Toolbar) findViewById(R.id.app_actionbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relative_layout);
        mLinearProgressBar = (LinearLayout) findViewById(R.id.linear_progress_bar);
        mLinearTryAgain = (LinearLayout) findViewById(R.id.linear_try_again);
        mTextTryAgain = (TextView) findViewById(R.id.text_try_again);
        mButtonTryAgain = (Button) findViewById(R.id.button_try_again);
    }

    private void assignSingletons() {
        mDatabaseHandlerSingleton = DatabaseHandlerSingleton.getInstance(this);
        mNetworkManagerSingleton = NetworkManagerSingleton.getInstance(this);
    }

    private void assignActionBar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        else {
            Log.e(mClassName, "getSupportActionBar null");
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }
}
