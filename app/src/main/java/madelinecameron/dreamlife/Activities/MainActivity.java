package madelinecameron.dreamlife.Activities;

import java.util.HashMap;
import java.util.Locale;

import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import madelinecameron.dreamlife.GameState.Action;
import madelinecameron.dreamlife.GameState.GameState;
import madelinecameron.dreamlife.R;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    private GameState gameState;
    private HashMap<Integer, ArrayAdapter<String>> actionPageMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        Log.i("DreamLife", "Creating game state...");
        this.gameState = new GameState(getBaseContext());
        this.actionPageMap = new HashMap<>();

        if(getBaseContext().getDatabasePath("DreamLife.db").exists() && GameState.getItemCount() == 0) {
            Log.i("DreamLife", "DB init, load items and actions");
            GameState.loadAllItems();
            GameState.loadAllActions();
        }

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_page1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_page2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_page3).toUpperCase(l);
                case 3:
                    return getString(R.string.title_page4).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();

            Log.d("DreamLife", String.valueOf(sectionNumber));
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            Bundle args = getArguments();
            PageType pageType = PageType.HOME;
            switch(args.getInt(ARG_SECTION_NUMBER)) {
                case 1:  //Personal page
                    pageType = PageType.PERSONAL;
                    break;
                case 2:
                    pageType = PageType.WORK;
                    break;
                case 3:
                    pageType = PageType.EDUCATION;
                    break;
                case 4:
                    pageType = PageType.SHOP;
                    break;
            }

            Log.d("DreamLife", pageType.name());

            try {
                ArrayAdapter<String> actionList = null;
                Log.d("DreamLife", String.format("Get all for %s", pageType.toString()));
                HashMap<String, Action> aList = GameState.getValidActions(pageType);
                Log.d("DreamLife", String.format("ValidGot: %d", aList.size()));

                String[] actionArray = new String[aList.size()];
                aList.keySet().toArray(actionArray);
                actionList = new ArrayAdapter<String>(getContext(), R.layout.action_layout, actionArray);

                ListView actionView = (ListView) rootView.findViewById(R.id.action_list);
                actionView.setAdapter(actionList);
            }
            catch(Exception e) {
                Log.e("DreamLife", e.toString());
            }

            return rootView;
        }
    }

}