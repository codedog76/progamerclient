package adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
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

public class LevelAdapter extends RecyclerView.Adapter<LevelAdapter.ViewHolder> {

    private Context mContext;
    private ClickListener mClickListener;
    private LayoutInflater mLayoutInflater;
    private List<Level> mLevelList = Collections.emptyList();
    private String mClassName = getClass().toString();
    private String user_type;

    public LevelAdapter(Context context, String user_type) {
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.user_type = user_type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_level, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mLevelList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Level current_level = mLevelList.get(position);
        holder.mTextNumber.setText(mContext.getString(R.string.string_level_number,
                current_level.getLevel_number()));
        holder.mTextTitle.setText(current_level.getLevel_title());
        if (user_type.equals("admin")) {
            holder.mProgressBar.setVisibility(View.GONE);
            holder.mTextProgressNumeric.setVisibility(View.GONE);
            holder.mImageIcon.setVisibility(View.GONE);
            holder.mTextScore.setVisibility(View.GONE);
            holder.mTextId.setVisibility(View.VISIBLE);
            holder.mTextId.setText(mContext.getString(R.string.string_level_id, current_level.getLevel_id()));
        } else {
            holder.mTextId.setVisibility(View.GONE);
            holder.mProgressBar.setVisibility(View.VISIBLE);
            holder.mTextProgressNumeric.setText(mContext.getString(R.string.string_out_of,
                    current_level.getLevel_puzzles_completed(), current_level.getLevel_puzzles_count()));
            String levelTrophy = current_level.getLevel_trophy();
            int levelScore = current_level.getLevel_score();
            if (levelScore != 0) {
                holder.mTextScore.setText(String.valueOf(levelScore));
                if (levelTrophy.equals("trophy_bronze"))
                    holder.mTextScore.setTextColor(ContextCompat.getColor(mContext, R.color.bronze));
                if (levelTrophy.equals("trophy_silver"))
                    holder.mTextScore.setTextColor(ContextCompat.getColor(mContext, R.color.silver));
                if (levelTrophy.equals("trophy_gold"))
                    holder.mTextScore.setTextColor(ContextCompat.getColor(mContext, R.color.gold));
                holder.mTextScore.setVisibility(View.VISIBLE);
            } else holder.mTextScore.setVisibility(View.GONE);
            int id = mContext.getResources().getIdentifier(levelTrophy, "drawable", mContext.getPackageName());
            Drawable drawable = ContextCompat.getDrawable(mContext, id);
            holder.mImageIcon.setImageDrawable(drawable);
            if (current_level.getLevel_puzzles_count() != 0) {
                double percentage = 100.0 * current_level.getLevel_puzzles_completed()
                        / current_level.getLevel_puzzles_count();
                holder.mProgressBar.setProgress((int) percentage);
            } else {
                holder.mProgressBar.setProgress(0);
            }
            if (position == 0 || (mLevelList.get(position - 1).getLevel_completed() == 1)) {
                holder.mLinearContainer.setFocusable(true);
                holder.mLinearContainer.setClickable(true);
                holder.mTextNumber.setAlpha(1.0f);
                holder.mTextTitle.setAlpha(1.0f);
                holder.mTextProgressNumeric.setAlpha(1.0f);
                holder.mTextScore.setAlpha(1.0f);
                holder.mProgressBar.setAlpha(0.85f);
                holder.mViewDivider.setAlpha(0.12f);
                holder.mImageIcon.setAlpha(1.0f);
                holder.mTextNumber.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "Roboto-Medium.ttf"));
            } else {
                holder.mLinearContainer.setFocusable(false);
                holder.mLinearContainer.setClickable(false);
                holder.mImageIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.lock_104));
                holder.mTextNumber.setAlpha(0.4f);
                holder.mTextTitle.setAlpha(0.4f);
                holder.mTextProgressNumeric.setAlpha(0.4f);
                holder.mTextScore.setAlpha(0.4f);
                holder.mProgressBar.setAlpha(0.4f);
                holder.mViewDivider.setAlpha(0.08f);
                holder.mImageIcon.setAlpha(0.2f);
                holder.mTextNumber.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "Roboto-Regular.ttf"));
            }
        }
    }

    public void setListener(ClickListener listener) {
        this.mClickListener = listener;
    }

    public void setLevelList(ArrayList<Level> levelList) {
        this.mLevelList = levelList;
        notifyDataSetChanged();
    }

    public void clearLevelList() {
        this.mLevelList.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTextNumber, mTextTitle, mTextProgressNumeric, mTextScore, mTextId;
        private ImageView mImageIcon;
        private ProgressBar mProgressBar;
        private LinearLayout mLinearContainer;
        private View mViewDivider;

        public ViewHolder(View itemView) {
            super(itemView);
            mLinearContainer = (LinearLayout) itemView.findViewById(R.id.linear_container);
            mTextNumber = (TextView) itemView.findViewById(R.id.text_number);
            mTextTitle = (TextView) itemView.findViewById(R.id.text_title);
            mTextProgressNumeric = (TextView) itemView.findViewById(R.id.text_progress_numeric);
            mTextScore = (TextView) itemView.findViewById(R.id.text_score);
            mTextId = (TextView) itemView.findViewById(R.id.text_id);
            mImageIcon = (ImageView) itemView.findViewById(R.id.image_icon);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
            mViewDivider = itemView.findViewById(R.id.view_divider);
            Typeface Roboto_Regular = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Regular.ttf");
            mTextNumber.setTypeface(Roboto_Regular);
            mTextTitle.setTypeface(Roboto_Regular);
            mTextProgressNumeric.setTypeface(Roboto_Regular);
            mTextId.setTypeface(Roboto_Regular);
            mTextScore.setTypeface(Roboto_Regular, Typeface.BOLD);
            mLinearContainer.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null) {
                mClickListener.itemClicked(getAdapterPosition(), mLevelList.get(getAdapterPosition()).getLevel_id());
            }
        }
    }

    public interface ClickListener {
        void itemClicked(int position, int level_id);
    }
}
