package adapters;

import android.content.Context;
import android.graphics.Typeface;
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
import models.Puzzle;

/**
 * Created by Lucien on 9/6/2016.
 */
public class PuzzleAdapter extends RecyclerView.Adapter<PuzzleAdapter.ViewHolder> {

    private Context mContext;
    private ClickListener mClickListener;
    private LayoutInflater mLayoutInflater;
    private List<Puzzle> mPuzzleList = Collections.emptyList();
    private String mClassName = getClass().toString();


    public PuzzleAdapter(Context context) {
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public PuzzleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_puzzle, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PuzzleAdapter.ViewHolder holder, int position) {
        Puzzle puzzle = mPuzzleList.get(position);
        holder.mTextId.setText(mContext.getString(R.string.string_puzzle_id, puzzle.getPuzzle_database_id()));
        holder.mTextInstructions.setText(puzzle.getPuzzle_instructions());
        holder.mTextData.setText(puzzle.getPuzzle_data());
    }

    @Override
    public int getItemCount() {
        return mPuzzleList.size();
    }

    public void setListener(ClickListener listener) {
        this.mClickListener = listener;
    }

    public void setPuzzleList(ArrayList<Puzzle> puzzleList) {
        this.mPuzzleList = puzzleList;
        notifyDataSetChanged();
    }

    public void clearPuzzleList() {
        this.mPuzzleList.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTextId, mTextInstructions, mTextData;
        private LinearLayout mLinearContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            mLinearContainer = (LinearLayout) itemView.findViewById(R.id.linear_container);
            mTextId = (TextView) itemView.findViewById(R.id.text_id);
            mTextInstructions = (TextView) itemView.findViewById(R.id.text_instructions);
            mTextData = (TextView) itemView.findViewById(R.id.text_data);
            mLinearContainer = (LinearLayout) itemView.findViewById(R.id.linear_container);
            Typeface Roboto_Regular = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Regular.ttf");
            mTextId.setTypeface(Roboto_Regular);
            mTextInstructions.setTypeface(Roboto_Regular);
            mTextData.setTypeface(Roboto_Regular);
            mLinearContainer.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null) {
                mClickListener.itemClicked(getAdapterPosition(), mPuzzleList.get(getAdapterPosition()).getPuzzle_database_id());
            }
        }
    }

    public interface ClickListener {
        void itemClicked(int position, int puzzle_id);
    }
}
