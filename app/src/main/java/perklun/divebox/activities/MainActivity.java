package perklun.divebox.activities;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import perklun.divebox.R;
import perklun.divebox.adapters.MainPagerAdapter;

public class MainActivity extends AppCompatActivity {

    MainPagerAdapter adapterMainPage;
    FloatingActionButton createDive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterMainPage = new MainPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterMainPage);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(vpPager);

        //FAB button onclick listener
        createDive = (FloatingActionButton) findViewById(R.id.create_dive_fab);
        createDive.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            }
        });
    }
}
