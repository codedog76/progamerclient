package adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.progamer.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import models.User;
import singletons.DatabaseHandlerSingleton;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    private Context mContext;
    private ClickListener mClickListener;
    private LayoutInflater mLayoutInflater;
    private List<User> mUserList = new ArrayList<>();
    private String mLeaderboardType;

    public LeaderboardAdapter(Context context) {
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_leaderboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User current_user = mUserList.get(position);
        holder.mTextRank.setText(mContext.getString(R.string.string_rank, position + 1));
        int id = mContext.getResources().getIdentifier("avatar_" +
                String.valueOf(current_user.getUser_avatar()), "drawable", mContext.getPackageName());
        Drawable drawable = ContextCompat.getDrawable(mContext, id);
        holder.mCircleImageAvatar.setImageDrawable(drawable);
        if (position <= 2) {
            holder.mCircleImageAvatar.setBorderWidth(5);
            if (position == 0) {
                holder.mCircleImageAvatar.setBorderColor(ContextCompat.getColor(mContext, R.color.gold));
            }
            if (position == 1) {
                holder.mCircleImageAvatar.setBorderColor(ContextCompat.getColor(mContext, R.color.silver));
            }
            if (position == 2) {
                holder.mCircleImageAvatar.setBorderColor(ContextCompat.getColor(mContext, R.color.bronze));
            }
        } else {
            holder.mCircleImageAvatar.setBorderWidth(0);
            holder.mCircleImageAvatar.setBorderColor(ContextCompat.getColor(mContext, R.color.grey_50));
        }
        holder.mTextNickname.setText(current_user.getUser_nickname());
        if (mLeaderboardType.equals("score")) {
            holder.mTextScore.setText(String.valueOf(current_user.getUser_overall_score()));
        }
        if (mLeaderboardType.equals("attempts")) {
            holder.mTextScore.setText(String.valueOf(current_user.getUser_overall_attempts()));
        }
        if (mLeaderboardType.equals("time")) {
            holder.mTextScore.setText(String.valueOf(current_user.getUser_overall_time()));
        }
        if (current_user.getUser_student_number_id().equals(DatabaseHandlerSingleton.getInstance(mContext)
                .getLoggedUserStudentNumber())) {
            holder.mLinearContainer.setBackgroundColor(ContextCompat.getColor(mContext, R.color.green_100));
        } else {
            holder.mLinearContainer.setBackgroundColor(0);
        }
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public void setListener(ClickListener listener) {
        this.mClickListener = listener;
    }

    public void setUserList(ArrayList<User> userList, String leaderboardType) {
        this.mUserList = userList;
        this.mLeaderboardType = leaderboardType;
        notifyDataSetChanged();
    }

    public void clearUserList() {
        this.mUserList.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTextRank, mTextNickname, mTextScore;
        private CircleImageView mCircleImageAvatar;
        private LinearLayout mLinearContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextRank = (TextView) itemView.findViewById(R.id.text_rank);
            mTextNickname = (TextView) itemView.findViewById(R.id.text_nickname);
            mTextScore = (TextView) itemView.findViewById(R.id.text_score);
            mCircleImageAvatar = (CircleImageView) itemView.findViewById(R.id.circle_image_avatar);
            mLinearContainer = (LinearLayout) itemView.findViewById(R.id.linear_container);
            Typeface Roboto_Regular = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Regular.ttf");
            mTextRank.setTypeface(Roboto_Regular);
            mTextNickname.setTypeface(Roboto_Regular);
            mTextScore.setTypeface(Roboto_Regular);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null) {
                mClickListener.itemClicked(getAdapterPosition(),
                        mUserList.get(getAdapterPosition()).getUser_student_number_id());
            }
        }
    }

    public interface ClickListener {
        void itemClicked(int position, String user_student_number);
    }
}