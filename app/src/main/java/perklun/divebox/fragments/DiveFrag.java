package perklun.divebox.fragments;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import perklun.divebox.R;
import perklun.divebox.activities.ViewDiveActivity;
import perklun.divebox.adapters.DiveRecyclerViewAdapter;
import perklun.divebox.db.DiveBoxDatabaseHelper;
import perklun.divebox.models.Dive;
import perklun.divebox.utils.Constants;
import perklun.divebox.utils.ItemClickSupport;

public class DiveFrag extends Fragment {
    // newInstance constructor for creating fragment with arguments

    RecyclerView diveRecyclerView;
    DiveRecyclerViewAdapter diveRecyclerViewAdapter;
    DiveBoxDatabaseHelper dbHelper;
    List<Dive> divesList;

    public static DiveFrag newInstance() {
        DiveFrag fragmentFirst = new DiveFrag();
        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = DiveBoxDatabaseHelper.getDbInstance(this.getContext());
        divesList = dbHelper.getAllDives();
        diveRecyclerViewAdapter = new DiveRecyclerViewAdapter(divesList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dive, container, false);
        diveRecyclerView = (RecyclerView) view.findViewById(R.id.recycle_view_dives);
        diveRecyclerView.setAdapter(diveRecyclerViewAdapter);
        diveRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        // Item click handler for recycler view
        ItemClickSupport.addTo(diveRecyclerView).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Intent i = new Intent(getActivity(), ViewDiveActivity.class);
                        i.putExtra(Constants.DIVE, divesList.get(position));
                        startActivity(i);
                    }
                }
        );
        return view;
    }

    public void addDiveUpdateRecyclerViewAdapter(Dive dive){
        divesList.add(dive);
        diveRecyclerViewAdapter.notifyItemInserted(divesList.size()-1);
    }
}
