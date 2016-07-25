package adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.progamer.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;
import models.User;
import singletons.DatabaseHandlerSingleton;

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
        User current_user = userList.get(position);
        holder.leaderboardRowRankTextView.setText("#"+String.valueOf(position + 1));
        int id = context.getResources().getIdentifier("avatar_"+String.valueOf(current_user.getUser_avatar()), "drawable", context.getPackageName());
        Drawable drawable = ContextCompat.getDrawable(context, id);
        holder.leaderboardRowRankCircleImageView.setImageDrawable(drawable);
        if (position <= 2) {
            holder.leaderboardRowRankCircleImageView.setBorderWidth(5);
            if (position == 0)
                holder.leaderboardRowRankCircleImageView.setBorderColor(ContextCompat.getColor(context, R.color.gold));
            if (position == 1)
                holder.leaderboardRowRankCircleImageView.setBorderColor(ContextCompat.getColor(context, R.color.silver));
            if (position == 2)
                holder.leaderboardRowRankCircleImageView.setBorderColor(ContextCompat.getColor(context, R.color.bronze));
        } else {
            holder.leaderboardRowRankCircleImageView.setBorderWidth(0);
            holder.leaderboardRowRankCircleImageView.setBorderColor(ContextCompat.getColor(context, R.color.grey_50));
        }
        holder.leaderboardRowNameTextView.setText(current_user.getUser_nickname());
        if(leaderboardType.equals("overall")){
            holder.leaderboardRowScoreTextView.setText(String.valueOf(current_user.getUser_overall_score()));
        }
        if(leaderboardType.equals("attempts")){
            holder.leaderboardRowScoreTextView.setText(String.valueOf(current_user.getUser_overall_attempts()));
        }
        if(leaderboardType.equals("time")){
            holder.leaderboardRowScoreTextView.setText(String.valueOf(current_user.getUser_overall_time()));
        }
        if(current_user.getUser_student_number_id().equals(DatabaseHandlerSingleton.getInstance(context).getLoggedUserStudentNumber())) {
            holder.leaderboardLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.green_100));
        }
        else {
            holder.leaderboardLayout.setBackgroundColor(0);
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView leaderboardRowRankTextView, leaderboardRowNameTextView, leaderboardRowScoreTextView;
        private CircleImageView leaderboardRowRankCircleImageView;
        private LinearLayout leaderboardLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            leaderboardRowRankTextView = (TextView) itemView.findViewById(R.id.leaderboardRowRankTextView);
            leaderboardRowNameTextView = (TextView) itemView.findViewById(R.id.leaderboardRowNameTextView);
            leaderboardRowScoreTextView = (TextView) itemView.findViewById(R.id.leaderboardRowScoreTextView);
            leaderboardRowRankCircleImageView = (CircleImageView) itemView.findViewById(R.id.leaderboardRowRankCircleImageView);
            leaderboardLayout = (LinearLayout) itemView.findViewById(R.id.leaderboardLayout);
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