package perklun.divebox.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import perklun.divebox.R;
import perklun.divebox.adapters.DiveRecyclerViewAdapter;
import perklun.divebox.db.DiveBoxDatabaseHelper;

public class DiveFrag extends Fragment {
    // newInstance constructor for creating fragment with arguments

    RecyclerView diveRecyclerView;
    DiveRecyclerViewAdapter diveRecyclerViewAdapter;
    DiveBoxDatabaseHelper dbHelper;

    public static DiveFrag newInstance() {
        DiveFrag fragmentFirst = new DiveFrag();
        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = DiveBoxDatabaseHelper.getDbInstance(this.getContext());
        diveRecyclerViewAdapter = new DiveRecyclerViewAdapter(dbHelper.getAllDives());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dive, container, false);

        diveRecyclerView = (RecyclerView) view.findViewById(R.id.recycle_view_dives);
        diveRecyclerView.setAdapter(diveRecyclerViewAdapter);
        diveRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        return view;
    }

    public void updateRecyclerViewAdapter(){
        diveRecyclerViewAdapter.notifyDataSetChanged();
    }
}
