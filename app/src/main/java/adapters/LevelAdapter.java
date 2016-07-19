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

import models.Level;

public class LevelAdapter extends RecyclerView.Adapter<LevelAdapter.ViewHolder> {

    private Context context;
    private clickListener clickListener;
    private LayoutInflater layoutInflater;
    private List<Level> levelList = Collections.emptyList();

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
        Level level = levelList.get(position);

        holder.levelRowLevelNumber.setText("Level " + level.getLevel_id());
        holder.levelRowLevelName.setText(level.getLevel_title());
        holder.levelRowNumericProgressTextView.setText("1/1");
        holder.levelRowProgressBar.setProgress((int)(Math.random() * 99));
        //String levelTrophy = level.getLevelTrophy();
        //int id = context.getResources().getIdentifier(levelTrophy, "drawable", context.getPackageName());
        //Drawable drawable = ContextCompat.getDrawable(context, id);
        //holder.levelRowBadgeImageView.setImageDrawable(drawable);
    }

    @Override
    public int getItemCount() {
        return levelList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView levelRowLevelNumber, levelRowLevelName, levelRowNumericProgressTextView;
        private ImageView levelRowBadgeImageView;
        private ProgressBar levelRowProgressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            levelRowLevelNumber = (TextView) itemView.findViewById(R.id.levelRowLevelNumber);
            levelRowLevelName = (TextView) itemView.findViewById(R.id.levelRowLevelName);
            levelRowNumericProgressTextView = (TextView) itemView.findViewById(R.id.levelRowNumericProgressTextView);
            levelRowBadgeImageView = (ImageView) itemView.findViewById(R.id.levelRowBadgeImageView);
            levelRowProgressBar = (ProgressBar) itemView.findViewById(R.id.levelRowProgressBar);

            Typeface Roboto_Regular = Typeface.createFromAsset(context.getAssets(), "Roboto-Regular.ttf");
            levelRowLevelNumber.setTypeface(Roboto_Regular);
            levelRowLevelName.setTypeface(Roboto_Regular);

            itemView.setOnClickListener(this);
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
