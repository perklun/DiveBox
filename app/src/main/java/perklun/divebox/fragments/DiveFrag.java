package perklun.divebox.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
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
    private SharedPreferences mSettings;

    public static DiveFrag newInstance() {
        DiveFrag fragmentFirst = new DiveFrag();
        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSettings = getActivity().getSharedPreferences(getString(R.string.SHARED_PREFERENCE_FILE_KEY),0);
        String googleID = mSettings.getString(getString(R.string.SHARED_PREF_GOOGLE_ID_KEY),"");
        dbHelper = DiveBoxDatabaseHelper.getDbInstance(this.getContext());
        divesList = dbHelper.getAllDives(googleID);
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
                        getActivity().startActivityForResult(i, Constants.REQUEST_CODE_VIEW_DIVE);
                    }
                }
        );
        return view;
    }

    public void addDiveUpdateRecyclerViewAdapter(Dive dive){
        divesList.add(dive);
        diveRecyclerViewAdapter.notifyItemInserted(divesList.size()-1);
    }

    public void deleteDiveUpdateRecyclerViewAdapter(Dive dive){
        int divePos = 0;
        //TODO: Hackyway to keep find index as id != to index in list
        for(divePos = 0; divePos < divesList.size(); divePos++){
            if(divesList.get(divePos).getId() == dive.getId()){
                break;
            }
        }
        if(divePos >= 0){
            divesList.remove(divePos);
            diveRecyclerViewAdapter.notifyItemRemoved(divePos);
        }
    }
}
