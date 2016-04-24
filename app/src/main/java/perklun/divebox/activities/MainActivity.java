package perklun.divebox.activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import perklun.divebox.R;
import perklun.divebox.adapters.MainPagerAdapter;
import perklun.divebox.fragments.DiveFrag;
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
        if(requestCode == Constants.REQUEST_CODE_CREATE_DIVE && resultCode == Constants.DB_OPS_SUCCESS) {
            Toast.makeText(this, getString(R.string.MESSAGE_DIVE_CREATE), Toast.LENGTH_SHORT).show();
            DiveFrag diveFrag = (DiveFrag) adapterMainPager.getItem(1);
            //TODO: Doesn't update when a new diveFrag is created
            //diveFrag.updateRecyclerViewAdapter();

        }
    }
}
