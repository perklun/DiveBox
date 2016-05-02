package perklun.divebox.activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import perklun.divebox.R;
import perklun.divebox.adapters.MainPagerAdapter;
import perklun.divebox.fragments.DiveFrag;
import perklun.divebox.fragments.MapFrag;
import perklun.divebox.models.Dive;
import perklun.divebox.utils.Constants;

public class MainActivity extends AppCompatActivity {

    MainPagerAdapter adapterMainPager;
    FloatingActionButton createDive;
    ViewPager vpPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterMainPager = new MainPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterMainPager);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(vpPager);

        //FAB button onclick listener
        createDive = (FloatingActionButton) findViewById(R.id.create_dive_fab);
        createDive.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, CreateDiveActivity.class);
                startActivityForResult(i, Constants.REQUEST_CODE_CREATE_DIVE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        DiveFrag diveFrag = adapterMainPager.getDiveFrag();
        MapFrag mapFrag = adapterMainPager.getMapFrag();
        Dive newDive = data.getParcelableExtra(Constants.DIVE);
        if(requestCode == Constants.REQUEST_CODE_CREATE_DIVE && resultCode == Constants.DB_OPS_SUCCESS) {
            Toast.makeText(this, getString(R.string.MESSAGE_DIVE_CREATE), Toast.LENGTH_SHORT).show();
            //update diveFrag
            diveFrag.addDiveUpdateRecyclerViewAdapter(newDive);
            //update mapFrag
            mapFrag.addDiveUpdateRecyclerViewAdapter(newDive);
        }
        else if(requestCode == Constants.REQUEST_CODE_VIEW_DIVE && resultCode == Constants.DB_OPS_SUCCESS){
            Toast.makeText(this, getString(R.string.MESSAGE_DIVE_DELETED), Toast.LENGTH_SHORT).show();
            //update diveFrag
            diveFrag.deleteDiveUpdateRecyclerViewAdapter(newDive);
            //update mapFrag
            mapFrag.deleteDiveUpdateRecyclerViewAdapter(newDive);
        }
    }
}
