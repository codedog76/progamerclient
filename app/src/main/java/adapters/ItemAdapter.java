package adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.example.progamer.R;
import com.woxthebox.draglistview.DragItemAdapter;
import java.util.ArrayList;

public class ItemAdapter extends DragItemAdapter<Pair<Long, String>, ItemAdapter.ViewHolder> {

    private int mLayoutId;
    private int mGrabHandleId;
    private Context mContext;

    public ItemAdapter(ArrayList<Pair<Long, String>> list, int layoutId, int grabHandleId, boolean dragOnLongPress, Context context) {
        super(dragOnLongPress);
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        mContext = context;
        setHasStableIds(true);
        setItemList(list);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        String text = mItemList.get(position).second;
        holder.mText.setText(text);
        holder.itemView.setTag(text);
    }

    @Override
    public long getItemId(int position) {
        return mItemList.get(position).first;
    }

    public class ViewHolder extends DragItemAdapter<Pair<Long, String>, ItemAdapter.ViewHolder>.ViewHolder {
        private TextView mText;

        public ViewHolder(final View itemView) {
            super(itemView, mGrabHandleId);
            mText = (TextView) itemView.findViewById(R.id.text);
            Typeface Roboto_Regular = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Regular.ttf");
            mText.setTypeface(Roboto_Regular);
        }

        @Override
        public void onItemClicked(View view) {
        }

        @Override
        public boolean onItemLongClicked(View view) {
            return true;
        }
    }
}