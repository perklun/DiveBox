package perklun.divebox.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import perklun.divebox.R;
import perklun.divebox.contentprovider.DiveBoxDatabaseContract;

/**
 * Created by perklun on 4/23/2016.
 */
public class DiveRecyclerViewAdapter extends CursorRecyclerViewAdapter<DiveRecyclerViewAdapter.ViewHolder> {


    public DiveRecyclerViewAdapter(Context context, Cursor cursor) {
        super(context, cursor);
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
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        viewHolder.diveTitle.setText(cursor.getString(cursor.getColumnIndex(DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_TITLE)));
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
