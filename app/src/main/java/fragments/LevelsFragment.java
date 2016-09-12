package fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.progamer.R;

import java.util.ArrayList;

import activities.AdminPuzzlesActivity;
import activities.LevelActivity;
import adapters.LevelAdapter;
import models.Level;
import models.User;
import singletons.DatabaseHandlerSingleton;
import singletons.NetworkManagerSingleton;

public class LevelsFragment extends Fragment implements LevelAdapter.ClickListener {

    private RecyclerView mRecyclerView;
    private ArrayList<Level> mLevelList;
    private LevelAdapter mLevelAdapter;
    private DatabaseHandlerSingleton mDatabaseHandlerSingleton;
    private NetworkManagerSingleton mNetworkManagerSingleton;
    private RelativeLayout mRelativeLayout;
    private LinearLayout mLinearProgressBar, mLinearTryAgain;
    private TextView mTextTryAgain;
    private Button mButtonTryAgain;
    private User mCurrentUser;

    public LevelsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_levels, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assignSingletons();
        mCurrentUser = mDatabaseHandlerSingleton.getLoggedUser();
        assignViews(view);
        assignAdapter();
        if (mCurrentUser.getUser_type().equals("admin")) loadAdminLevelList();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mCurrentUser.getUser_type().equals("admin"))
            addList(mDatabaseHandlerSingleton.getLevels());
    }

    @Override
    public void itemClicked(int position, int level_id) {
        if (mCurrentUser.getUser_type().equals("admin")) {
            Intent intent = new Intent(getContext(), AdminPuzzlesActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("level_id", level_id);
            intent.putExtras(bundle);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else {
            Intent intent = new Intent(getContext(), LevelActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("level_id", level_id);
            intent.putExtras(bundle);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }

    }

    private void loadAdminLevelList() {
        mRecyclerView.setVisibility(View.GONE);
        mLinearTryAgain.setVisibility(View.GONE);
        mRelativeLayout.setVisibility(View.VISIBLE);
        mLinearProgressBar.setVisibility(View.VISIBLE);
        mNetworkManagerSingleton.getAdminLevelsJsonRequest(mCurrentUser, new NetworkManagerSingleton.ObjectResponseListener<ArrayList<Level>>() {
            @Override
            public void getResult(ArrayList<Level> object, Boolean response, String message) {
                if (response) {
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
                            loadAdminLevelList();
                        }
                    });
                }
            }
        });
    }

    private void assignSingletons() {
        mDatabaseHandlerSingleton = DatabaseHandlerSingleton.getInstance(getActivity());
        mNetworkManagerSingleton = NetworkManagerSingleton.getInstance(getActivity());
    }

    private void assignAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mLevelAdapter = new LevelAdapter(getActivity(), mDatabaseHandlerSingleton.getLoggedUser().getUser_type());
        mLevelAdapter.setListener(this);
        mRecyclerView.setAdapter(mLevelAdapter);
    }

    private void assignViews(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mTextTryAgain = (TextView) view.findViewById(R.id.text_try_again);
        mRelativeLayout = (RelativeLayout) view.findViewById(R.id.relative_layout);
        mLinearProgressBar = (LinearLayout) view.findViewById(R.id.linear_progress_bar);
        mLinearTryAgain = (LinearLayout) view.findViewById(R.id.linear_try_again);
        mButtonTryAgain = (Button) view.findViewById(R.id.button_try_again);
    }

    public void addList(ArrayList<Level> list) {
        if (!list.isEmpty()) {
            mLevelList = new ArrayList<>();
            mLevelList.addAll(list);
            mLevelAdapter.setLevelList(mLevelList);
        } else {
            mLevelAdapter.clearLevelList();
        }
    }

}
