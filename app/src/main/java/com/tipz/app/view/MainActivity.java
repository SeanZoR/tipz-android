package com.tipz.app.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.widget.Toast;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.tipz.app.BuildConfig;
import com.tipz.app.R;
import com.tipz.app.TipzApplication;
import com.tipz.app.control.services.TipzService;
import com.tipz.app.view.fragments.tips.TipsFavoriteFragment;
import com.tipz.app.view.fragments.tips.TipsFragment;

public class MainActivity extends BaseActivity<TipzApplication>
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    // Represents the tag of the added fragments
    private final String TAG_FRAGMENT_TIPS_FEATURED = TAG + "TAG_FRAGMENT_TIPS_FEATURED";
    private final String TAG_FRAGMENT_TIPS_FAVORITE = TAG + "TAG_FRAGMENT_TIPS_FAVORITE";

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private TipsFragment mTipsFeaturedFragment;
    private TipsFragment mTipsFavoriteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Handling dynamic fragments section.
        // If this is the first time the Activity is created (and it's not a restart of it)
        if (savedInstanceState == null) {
            mTipsFeaturedFragment = new TipsFragment();
            mTipsFavoriteFragment = new TipsFavoriteFragment();
        }
        // Else, it's a restart, just fetch the already existing fragments
        else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            mTipsFeaturedFragment = (TipsFragment) fragmentManager.findFragmentByTag(
                    TAG_FRAGMENT_TIPS_FEATURED);
            mTipsFavoriteFragment = (TipsFragment) fragmentManager.findFragmentByTag(
                    TAG_FRAGMENT_TIPS_FAVORITE);
        }

        // This is the title of the Activity, but it is expected that the inflated
        // fragments will come with their own title and than it will be overridden
        mTitle = getTitle();

        // This fragment is instantiated in a static way, so just find it by ID and reference it
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout),
                mToolbar);

        WakefulIntentService.sendWakefulWork(this, TipzService.actionGetTipsIntent(this));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        BaseFragment fragmentToSetInContainer = null;
        String tagToSetInContainer = null;

        // TODO: Create a real array/enum of possible drawer items
        switch (position) {
            case 0:
                fragmentToSetInContainer = mTipsFeaturedFragment;
                tagToSetInContainer = TAG_FRAGMENT_TIPS_FEATURED;
                break;
            case 1:
                fragmentToSetInContainer = mTipsFavoriteFragment;
                tagToSetInContainer = TAG_FRAGMENT_TIPS_FAVORITE;
                break;
        }

        // Now do the actual swap of views
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragmentToSetInContainer, tagToSetInContainer)
                .commit();

        // Set the title of the activity according to the fragment
        int titleRes = fragmentToSetInContainer.getTitle();
        if (titleRes > 0) {
            mTitle = getString(titleRes);
        }
    }

    public void restoreActionBar() {
        mToolbar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_main;
    }
}