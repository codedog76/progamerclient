package adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.progamer.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import models.Level;
import singletons.DatabaseHandlerSingleton;

public class LevelAdapter extends RecyclerView.Adapter<LevelAdapter.ViewHolder> {

    private Context context;
    private clickListener clickListener;
    private LayoutInflater layoutInflater;
    private List<Level> levelList = Collections.emptyList();
    private String mClassName = getClass().toString();

    public LevelAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setListener(clickListener listener) {
        this.clickListener = listener;
    }

    public void setLevelList(ArrayList<Level> levelList) {
        this.levelList = levelList;
        notifyDataSetChanged();
    }

    public void clearLevelList() {
        this.levelList.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.level_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Level current_level = levelList.get(position);
        holder.levelRowLevelNumber.setText("Level " + current_level.getLevel_number());
        holder.levelRowLevelName.setText(current_level.getLevel_title());
        holder.levelRowNumericProgressTextView.setText(current_level.getLevel_puzzles_completed() + "/" + current_level.getLevel_puzzles_count());
        if (current_level.getLevel_score() != 0) {
            holder.levelRowScore.setText(String.valueOf(current_level.getLevel_score()));
            holder.levelRowScore.setVisibility(View.VISIBLE);
        } else
            holder.levelRowScore.setVisibility(View.GONE);
        String levelTrophy = current_level.getLevel_trophy();
        int id = context.getResources().getIdentifier(levelTrophy, "drawable", context.getPackageName());
        Drawable drawable = ContextCompat.getDrawable(context, id);
        holder.levelRowBadgeImageView.setImageDrawable(drawable);
        if (current_level.getLevel_puzzles_count() != 0) {
            double percentage = 100.0 * current_level.getLevel_puzzles_completed() / current_level.getLevel_puzzles_count();
            holder.levelRowProgressBar.setProgress((int) percentage);
        } else {
            holder.levelRowProgressBar.setProgress(0);
        }
        Typeface Roboto_Regular = Typeface.createFromAsset(context.getAssets(), "Roboto-Regular.ttf");
        Typeface Roboto_Medium = Typeface.createFromAsset(context.getAssets(), "Roboto-Medium.ttf");
        holder.levelRowLevelNumber.setTypeface(Roboto_Regular);
        holder.levelRowLevelName.setTypeface(Roboto_Regular);
        if (position == 0 || (levelList.get(position - 1).getLevel_completed() == 1)) {
            holder.selectableLayout.setFocusable(true);
            holder.selectableLayout.setClickable(true);
            holder.levelRowLevelNumber.setAlpha(0.85f);
            holder.levelRowLevelName.setAlpha(0.7f);
            holder.levelRowNumericProgressTextView.setAlpha(0.7f);
            holder.levelRowProgressBar.setAlpha(0.85f);
            holder.levelRowDivider.setAlpha(0.12f);
            holder.levelRowBadgeImageView.setAlpha(1.0f);
            holder.levelRowLevelNumber.setTypeface(Roboto_Medium);
        } else {
            holder.selectableLayout.setFocusable(false);
            holder.selectableLayout.setClickable(false);
            holder.levelRowBadgeImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.lock_104));
            holder.levelRowLevelNumber.setAlpha(0.4f);
            holder.levelRowLevelName.setAlpha(0.4f);
            holder.levelRowNumericProgressTextView.setAlpha(0.4f);
            holder.levelRowProgressBar.setAlpha(0.4f);
            holder.levelRowDivider.setAlpha(0.08f);
            holder.levelRowBadgeImageView.setAlpha(0.2f);
            holder.levelRowLevelNumber.setTypeface(Roboto_Regular);
        }
    }

    @Override
    public int getItemCount() {
        return levelList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView levelRowLevelNumber, levelRowLevelName, levelRowNumericProgressTextView, levelRowScore;
        private ImageView levelRowBadgeImageView;
        private ProgressBar levelRowProgressBar;
        private LinearLayout selectableLayout;
        private View levelRowDivider;

        public ViewHolder(View itemView) {
            super(itemView);
            selectableLayout = (LinearLayout) itemView.findViewById(R.id.levelRowLayout);
            levelRowLevelNumber = (TextView) itemView.findViewById(R.id.levelRowLevelNumber);
            levelRowLevelName = (TextView) itemView.findViewById(R.id.levelRowLevelName);
            levelRowNumericProgressTextView = (TextView) itemView.findViewById(R.id.levelRowNumericProgressTextView);
            levelRowScore = (TextView) itemView.findViewById(R.id.levelRowScore);
            levelRowBadgeImageView = (ImageView) itemView.findViewById(R.id.levelRowBadgeImageView);
            levelRowProgressBar = (ProgressBar) itemView.findViewById(R.id.levelRowProgressBar);
            levelRowDivider = itemView.findViewById(R.id.levelRowDivider);
            selectableLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.itemClicked(getAdapterPosition(), levelList.get(getAdapterPosition()));
            }
        }
    }

    public interface clickListener {
        void itemClicked(int position, Level selected_level);
    }
}
