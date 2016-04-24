package perklun.divebox.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import perklun.divebox.fragments.DiveFrag;
import perklun.divebox.fragments.ProfileFrag;
import perklun.divebox.fragments.TweetFrag;

/**
 * Created by perklun on 4/23/2016.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {

    private static int NUM_ITEMS = 3;
    private String tabTitles[] = new String[] { "Feed", "Dives", "Profile" };


    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // Fragment # 0 - This will show FirstFragment
                return TweetFrag.newInstance();
            case 1: // Fragment # 0 - This will show FirstFragment different title
                return DiveFrag.newInstance();
            case 2: // Fragment # 0 - This will show FirstFragment different title
                return ProfileFrag.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
