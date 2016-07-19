package adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.progamer.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;
import models.User;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    private Context context;
    private clickListener clickListener;
    private LayoutInflater layoutInflater;
    private List<User> userList = Collections.emptyList();
    private String leaderboardType;

    public LeaderboardAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setListener(clickListener listener) {
        this.clickListener = listener;
    }

    public void setUserList(ArrayList<User> userList, String leaderboardType) {
        this.userList = userList;
        this.leaderboardType = leaderboardType;
        notifyDataSetChanged();
    }

    public void clearUserList() {
        this.userList.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.leaderboard_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = userList.get(position);
        if (position <= 2) {
            holder.leaderboardRowRankTextView.setVisibility(View.GONE);
            holder.leaderboardRowRankCircleImageView.setVisibility(View.VISIBLE);
            if (position == 0)
                holder.leaderboardRowRankCircleImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.trophy_gold));
            if (position == 1)
                holder.leaderboardRowRankCircleImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.trophy_silver));
            if (position == 2)
                holder.leaderboardRowRankCircleImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.trophy_bronze));
        } else {
            holder.leaderboardRowRankTextView.setText(String.valueOf(position + 1));
            holder.leaderboardRowRankTextView.setVisibility(View.VISIBLE);
            holder.leaderboardRowRankCircleImageView.setVisibility(View.GONE);
        }
        holder.leaderboardRowNameTextView.setText(user.getUser_nickname());
        if(leaderboardType.equals("overall")){
            holder.leaderboardRowScoreTextView.setText(String.valueOf(user.getUser_overall_score()));
        }
        if(leaderboardType.equals("attempts")){
            holder.leaderboardRowScoreTextView.setText(String.valueOf(user.getUser_overall_attempts()));
        }
        if(leaderboardType.equals("time")){
            holder.leaderboardRowScoreTextView.setText(String.valueOf(user.getUser_overall_time()));
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView leaderboardRowRankTextView, leaderboardRowNameTextView, leaderboardRowScoreTextView;
        private CircleImageView leaderboardRowRankCircleImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            leaderboardRowRankTextView = (TextView) itemView.findViewById(R.id.leaderboardRowRankTextView);
            leaderboardRowNameTextView = (TextView) itemView.findViewById(R.id.leaderboardRowNameTextView);
            leaderboardRowScoreTextView = (TextView) itemView.findViewById(R.id.leaderboardRowScoreTextView);
            leaderboardRowRankCircleImageView = (CircleImageView) itemView.findViewById(R.id.leaderboardRowRankCircleImageView);

            Typeface Roboto_Regular = Typeface.createFromAsset(context.getAssets(), "Roboto-Regular.ttf");
            leaderboardRowRankTextView.setTypeface(Roboto_Regular);
            leaderboardRowNameTextView.setTypeface(Roboto_Regular);
            leaderboardRowScoreTextView.setTypeface(Roboto_Regular);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.itemClicked(getAdapterPosition(), userList.get(getAdapterPosition()));
            }
        }
    }

    public interface clickListener {
        void itemClicked(int position, User selected_user);
    }
}