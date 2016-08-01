package adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.progamer.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import models.UserAchievement;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<UserAchievement> achievementList = Collections.emptyList();

    public AchievementAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setAchievementList(ArrayList<UserAchievement> achievementList) {
        this.achievementList = achievementList;
        notifyDataSetChanged();
    }

    public void clearAchievementList() {
        this.achievementList.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_achievement, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserAchievement achievement = achievementList.get(position);
        holder.achievementRowTitleTextView.setText(achievement.getAchievement_title());
        holder.achievementRowDescriptionTextView.setText(achievement.getAchievement_description());
    }

    @Override
    public int getItemCount() {
        return achievementList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView achievementRowTitleTextView, achievementRowDescriptionTextView, achievementRowNumericTextView;
        private ProgressBar achievementRowProgressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            achievementRowTitleTextView = (TextView) itemView.findViewById(R.id.achievementRowTitleTextView);
            achievementRowDescriptionTextView = (TextView) itemView.findViewById(R.id.achievementRowDescriptionTextView);
            achievementRowNumericTextView = (TextView) itemView.findViewById(R.id.achievementRowNumericTextView);
            achievementRowProgressBar = (ProgressBar) itemView.findViewById(R.id.achievementRowProgressBar);

            Typeface Roboto_Regular = Typeface.createFromAsset(context.getAssets(), "Roboto-Regular.ttf");
            achievementRowTitleTextView.setTypeface(Roboto_Regular);
            achievementRowDescriptionTextView.setTypeface(Roboto_Regular);
            achievementRowNumericTextView.setTypeface(Roboto_Regular);
        }
    }
}
