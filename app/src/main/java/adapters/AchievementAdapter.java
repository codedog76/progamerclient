package adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.progamer.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import models.UserAchievement;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.ViewHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<UserAchievement> mAchievementList = Collections.emptyList();

    public AchievementAdapter(Context context) {
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setAchievementList(ArrayList<UserAchievement> achievementList) {
        this.mAchievementList = achievementList;
        notifyDataSetChanged();
    }

    public void clearAchievementList() {
        this.mAchievementList.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_achievement, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserAchievement achievement = mAchievementList.get(position);
        holder.setIsRecyclable(false);
        holder.mTextTitle.setText(achievement.getAchievement_title());
        holder.mTextDescription.setText(achievement.getAchievement_description());
        holder.mProgressBar.setProgress(achievement.getUserachievement_progress());
        holder.mProgressBar.setMax(achievement.getAchievement_total());
        holder.mTextProgressNumeric.setText(mContext.getString(R.string.string_out_of,
                achievement.getUserachievement_progress(), achievement.getAchievement_total()));
    }

    @Override
    public int getItemCount() {
        return mAchievementList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextTitle, mTextDescription, mTextProgressNumeric;
        private ImageView mImageIcon;
        private ProgressBar mProgressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextTitle = (TextView) itemView.findViewById(R.id.text_title);
            mTextDescription = (TextView) itemView.findViewById(R.id.text_description);
            mTextProgressNumeric = (TextView) itemView.findViewById(R.id.text_progress_numeric);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
            mImageIcon = (ImageView) itemView.findViewById(R.id.image_icon);
            Typeface Roboto_Regular = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Regular.ttf");
            mTextTitle.setTypeface(Roboto_Regular);
            mTextDescription.setTypeface(Roboto_Regular);
            mTextProgressNumeric.setTypeface(Roboto_Regular);
        }
    }
}
