package madelinecameron.dreamlife.Activities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import madelinecameron.dreamlife.Character.GameCharacter;
import madelinecameron.dreamlife.GameState.Action;
import madelinecameron.dreamlife.GameState.GameEvent;
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
    private Boolean isRunning;
    private static Handler uiHandler;

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

        final TextView energyText = (TextView) findViewById(R.id.energy_text);
        final TextView foodText = (TextView) findViewById(R.id.food_text);
        final TextView funText = (TextView) findViewById(R.id.fun_text);
        final ProgressBar energyBar = (ProgressBar) findViewById(R.id.energy_bar);
        final ProgressBar foodBar = (ProgressBar) findViewById(R.id.food_bar);
        final ProgressBar funBar = (ProgressBar) findViewById(R.id.fun_bar);
        final TextView moneyText = (TextView) findViewById(R.id.money_text);
        final TextView dateText = (TextView) findViewById(R.id.date_text);
        final TextView jobText = (TextView) findViewById(R.id.job_text);

        uiHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message input) {
                HashMap<String, Object> updateMap = (HashMap<String, Object>)input.obj;
                for(String s : updateMap.keySet()) {
                    Integer updateValue = 0;  //Done because of enums not casting to int
                    switch(s.toUpperCase()) {
                        case "ENERGY":
                            updateValue = Integer.valueOf(updateMap.get(s).toString());
                            if (energyBar.getProgress() != updateValue) {  //Update energy
                                int attrMax = Integer.valueOf(GameState.getGameCharacter().getAttrLimit(s).toString());
                                energyBar.setProgress(updateValue);
                                energyText.setText(updateValue.toString() + "/" + attrMax);
                            }
                            break;
                        case "FOOD":
                            updateValue = Integer.valueOf(updateMap.get(s).toString());
                            if (foodBar.getProgress() != updateValue) {  //Update food
                                int attrMax = Integer.valueOf(GameState.getGameCharacter().getAttrLimit(s).toString());
                                foodBar.setProgress(updateValue);
                                foodText.setText(updateValue.toString() + "/" + attrMax);
                            }
                            break;
                        case "FUN":
                            updateValue = Integer.valueOf(updateMap.get(s).toString());
                            if (funBar.getProgress() != updateValue) {  //Update fun
                                int attrMax = Integer.valueOf(GameState.getGameCharacter().getAttrLimit(s).toString());
                                funBar.setProgress(updateValue);
                                funText.setText(updateValue.toString() + "/" + attrMax);
                            }
                            break;
                        case "MONEY":
                            updateValue = Integer.valueOf(updateMap.get(s).toString());
                            if (Integer.valueOf(moneyText.getText().toString().substring(1)) != updateValue) {  //Update money
                                Log.d("DreamLife", s);
                                Log.d("DreamLife", moneyText.getText().toString().substring(1));
                                Log.d("DreamLife", updateValue.toString());
                                moneyText.setText("$" + updateValue.toString());
                            }
                        default:
                            continue;
                    }
                }
            }
        };

        Thread gameLoop = new Thread(new GameLoop(uiHandler, getBaseContext(), this.getWindow().getDecorView().getRootView()));
        gameLoop.start();
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
            // Show 5 total pages.
            return 5;
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
                case 4:
                    return "Info".toUpperCase(l);
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
        private static View rootView;
        private ArrayAdapter<String> actionList = null;
        private static PageType type;

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

        private static Comparator<String> ALPHABETICAL_ORDER = new Comparator<String>() {
            public int compare(String str1, String str2) {
                int res = String.CASE_INSENSITIVE_ORDER.compare(str1, str2);
                if (res == 0) {
                    res = str1.compareTo(str2);
                }
                return res;
            }
        };


        public void loadList() {
            Bundle args = getArguments();
            switch(args.getInt(ARG_SECTION_NUMBER)) {
                case 1:  //Personal page
                    this.type = PageType.PERSONAL;
                    break;
                case 2:
                    this.type = PageType.WORK;
                    break;
                case 3:
                    this.type = PageType.EDUCATION;
                    break;
                case 4:
                    this.type = PageType.SHOP;
                    break;
                case 5:
                    this.type = PageType.INFO;
                    break;
            }

            try {
                ArrayList<String> actionNameList = new ArrayList<>();
                Log.d("DreamLife", String.format("Get all for %s", this.type.toString()));
                final HashMap<String, Action> actionMap = GameState.getValidActions(this.type);
                actionNameList.addAll(actionMap.keySet());
                Log.d("DreamLife", String.format("Number of valid actions: %d", actionNameList.size()));

                ListView actionView = (ListView) rootView.findViewById(R.id.action_list);

                actionView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Action clickedAction = GameState.getAction(parent.getItemAtPosition(position).toString());

                        clickedAction.applyAction();
                        while (GameState.hasMoreGameEvents()) {
                            GameEvent event = GameState.getNextGameEvent();
                            event.popMessageToast(getContext());
                        }
                        loadList();
                    }
                });

                Log.d("DreamLife", "Reloading list");
                actionList.clear();
                actionList.addAll(actionNameList);
                actionList.sort(ALPHABETICAL_ORDER);
                actionList.notifyDataSetChanged();
            }
            catch(Exception e) {
                Log.e("DreamLife", e.toString());
            }
        }
        @Override
        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);

            // Make sure that we are currently visible
            if (this.isVisible()) {
                loadList();
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            int layout = R.layout.action_fragment;
            Bundle args = getArguments();
            switch(args.getInt(ARG_SECTION_NUMBER)) {
                case 1:  //Personal page
                    this.type = PageType.PERSONAL;
                    break;
                case 2:
                    this.type = PageType.WORK;
                    break;
                case 3:
                    this.type = PageType.EDUCATION;
                    break;
                case 4:
                    this.type = PageType.SHOP;
                    break;
                case 5:
                    this.type = PageType.INFO;
                    layout = R.layout.info_fragment;
                    break;
            }

            this.rootView = inflater.inflate(layout, container, false);
            Log.d("DreamLife", this.type.name());

            try {
                ArrayList<String> actionNameList = new ArrayList<>();
                Log.d("DreamLife", String.format("Get all for %s", this.type.toString()));
                final HashMap<String, Action> actionMap = GameState.getValidActions(this.type);
                actionNameList.addAll(actionMap.keySet());
                Log.d("DreamLife", String.format("Number of valid actions: %d", actionNameList.size()));

                actionList = new ArrayAdapter<String>(getContext(), R.layout.action_layout, actionNameList);
                actionList.sort(ALPHABETICAL_ORDER);

                ListView actionView = (ListView) rootView.findViewById(R.id.action_list);
                actionView.setAdapter(actionList);

                actionView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Action clickedAction = GameState.getAction(parent.getItemAtPosition(position).toString());

                        clickedAction.applyAction();
                        while(GameState.hasMoreGameEvents()) {
                            Log.d("DreamLife", "Toast");
                            GameEvent event = GameState.getNextGameEvent();
                            event.popMessageToast(getContext());
                        }
                        Log.d("DreamLife", "Loading");
                        loadList();
                    }
                });

                TextView sectionLabel = (TextView) rootView.findViewById(R.id.section_label);
                sectionLabel.setText(this.type.name());
            }
            catch(Exception e) {
                Log.e("DreamLife", e.toString());
            }

            return rootView;
        }
    }

}
