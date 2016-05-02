package perklun.divebox.adapters;

import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import perklun.divebox.fragments.DiveFrag;
import perklun.divebox.fragments.MapFrag;
import perklun.divebox.fragments.ProfileFrag;
import perklun.divebox.fragments.TweetFrag;

/**
 * Created by perklun on 4/23/2016.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {

    private TweetFrag tweetFrag;
    private DiveFrag diveFrag;
    private ProfileFrag profileFrag;
    private MapFrag mapFrag;


    private String tabTitles[] = new String[] { "Feed", "Dives","Map", "Profile" };
    private static int NUM_ITEMS = 4;

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return TweetFrag.newInstance();
            case 1:
                return DiveFrag.newInstance();
            case 2:
                return MapFrag.newInstance();
            case 3:
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

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
        // save the appropriate reference depending on position
        switch (position) {
            case 0:
                tweetFrag = (TweetFrag) createdFragment;
                break;
            case 1:
                diveFrag = (DiveFrag) createdFragment;
                break;
            case 2:
                mapFrag = (MapFrag) createdFragment;
                break;
            case 3:
                profileFrag = (ProfileFrag) createdFragment;
                break;
        }
        return createdFragment;
    }

    public DiveFrag getDiveFrag(){
        return diveFrag;
    }

    public MapFrag getMapFrag(){
        return mapFrag;
    }

    public ProfileFrag getProfileFrag() {
        return profileFrag;
    }
}
