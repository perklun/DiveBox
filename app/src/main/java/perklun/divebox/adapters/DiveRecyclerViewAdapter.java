package perklun.divebox.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import perklun.divebox.R;
import perklun.divebox.models.Dive;

/**
 * Created by perklun on 4/23/2016.
 */
public class DiveRecyclerViewAdapter extends RecyclerView.Adapter<DiveRecyclerViewAdapter.ViewHolder> {

    private List<Dive> mDives;

    public DiveRecyclerViewAdapter(List<Dive> dives){
        mDives = dives;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View diveView = inflater.inflate(R.layout.layout_dive_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(diveView);
        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Dive dive = mDives.get(position);
        TextView tvDiveTitle = holder.diveTitle;
        tvDiveTitle.setText(dive.getTitle());
    }

    @Override
    public int getItemCount() {
        return mDives.size();
    }

    // Viewholder for dive item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView diveTitle;
        public ViewHolder(View itemView) {
            super(itemView);
            diveTitle = (TextView) itemView.findViewById(R.id.tv_dive_item_title);
        }
    }
}
