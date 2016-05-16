package perklun.divebox.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import perklun.divebox.R;
import perklun.divebox.activities.ViewDiveActivity;
import perklun.divebox.adapters.DiveRecyclerViewAdapter;
import perklun.divebox.contentprovider.DiveBoxDatabaseContract;
import perklun.divebox.db.DiveBoxDatabaseHelper;
import perklun.divebox.models.Dive;
import perklun.divebox.utils.Constants;
import perklun.divebox.utils.ItemClickSupport;

public class DiveFrag extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    // newInstance constructor for creating fragment with arguments
    RecyclerView diveRecyclerView;
    DiveRecyclerViewAdapter diveRecyclerViewAdapter;
    DiveBoxDatabaseHelper dbHelper;
    List<Dive> divesList;
    private SharedPreferences mSettings;
    // Identifies a particular Loader being used in this component
    private static final int DIVE_LOADER = 0;
    private String googleId;

    public static DiveFrag newInstance() {
        DiveFrag fragmentFirst = new DiveFrag();
        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSettings = getActivity().getSharedPreferences(getString(R.string.SHARED_PREFERENCE_FILE_KEY),0);
        googleId = mSettings.getString(getString(R.string.SHARED_PREF_GOOGLE_ID_KEY),"");
        dbHelper = DiveBoxDatabaseHelper.getDbInstance(this.getContext());
        divesList = dbHelper.getAllDives(googleId);
        diveRecyclerViewAdapter = new DiveRecyclerViewAdapter(getContext(), null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Loader
        getLoaderManager().initLoader(DIVE_LOADER, null, this);
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
        getLoaderManager().restartLoader(DIVE_LOADER, null, this);
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
        }
        getLoaderManager().restartLoader(DIVE_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define the columns to retrieve
        switch(id){
            case DIVE_LOADER:
                String[] projectionFields = new String[] {
                        DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_ID,
                        DiveBoxDatabaseContract.DiveEntry.KEY_DIVE_TITLE};
                String[] selectionArgs = new String[]{
                        googleId
                };
                // Construct the loader
                //TODO: Set selection criteria to user and args, also set sort order
                return new CursorLoader(
                        getActivity(),
                        DiveBoxDatabaseContract.DiveEntry.CONTENT_URI, // URI
                        projectionFields, // projection fields
                        null, // the selection criteria (always need to filter by user id)
                        null, // the selection args
                        null // the sort order
                );
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        diveRecyclerViewAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        diveRecyclerViewAdapter.changeCursor(null);
    }
}
