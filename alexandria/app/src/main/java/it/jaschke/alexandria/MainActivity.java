package it.jaschke.alexandria;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.jaschke.alexandria.api.Callback;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FragmentManager.OnBackStackChangedListener, Callback {

    public static final String MESSAGE_EVENT = "MESSAGE_EVENT";
    public static final String MESSAGE_KEY = "MESSAGE_EXTRA";
    public static final String MESSAGE_ERROR_KEY = "MESSAGE_ERROR";
    public static final String TAG_BOOK_DETAIL = "Book Detail";
    public static final int TIME_SPLASH = 2000;
    public static boolean IS_TABLET = false;

    private ActionBarDrawerToggle mDrawerToggle;
    private BroadcastReceiver messageReciever;

    private Fragment nextFragment;
    boolean fromSavedInstance;

    @Bind(R.id.splash)
    View viewSplash;

    @Bind(R.id.navigation_view)
    NavigationView navigationView;

    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IS_TABLET = isTablet();
        if (IS_TABLET) {
            setContentView(R.layout.activity_main_tablet);
        } else {
            setContentView(R.layout.activity_main);
        }

        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            nextFragment = getSupportFragmentManager().getFragment(savedInstanceState, "current_fragment");
            navigationView.onRestoreInstanceState(savedInstanceState);
            fromSavedInstance = true;

            FragmentManager fm = getSupportFragmentManager();
            for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                fm.popBackStack();
            }

        }


        initReceiver();
        initToolbar();
        setupDrawerLayout();

        hideSplash(savedInstanceState != null);
        checkIfShouldDisplayHomeUp();

    }

    private void initReceiver() {
        messageReciever = new MessageReceiever();
        IntentFilter filter = new IntentFilter(MESSAGE_EVENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReciever, filter);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupDrawerLayout() {
        navigationView.setNavigationItemSelectedListener(this);

        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                navigationView.onDrawerOpened();
                hideKeyboard();
            }
        };


        // Set the drawer toggle as the DrawerListener
        drawerLayout.setDrawerListener(mDrawerToggle);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }


        getSupportFragmentManager().addOnBackStackChangedListener(this);

    }

    private void hideSplash(boolean immediately) {
        if (immediately) {
            viewSplash.setVisibility(View.GONE);
            checkNavigationViewLearned();
        } else {
            new CountDownTimer(TIME_SPLASH, TIME_SPLASH) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    viewSplash.setVisibility(View.GONE);
                    checkNavigationViewLearned();
                }
            }.start();
        }
    }

    private void checkNavigationViewLearned() {
        if (navigationView.isUserLearnedDrawer()) {
            drawerLayout.openDrawer(navigationView);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {

        if (nextFragment == null || !fromSavedInstance) {
            switch (menuItem.getItemId()) {
                default:
                case R.id.drawer_list_books:
                    nextFragment = new ListOfBooks();
                    break;
                case R.id.drawer_scan_add_book:
                    nextFragment = new AddBook();
                    break;
                case R.id.drawer_about_app:
                    nextFragment = new About();
                    break;
            }
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, nextFragment);

        fragmentTransaction.commit();

        menuItem.setChecked(true);
        drawerLayout.closeDrawers();
        fromSavedInstance = false;
        return true;
    }

    @Override
    public void onItemSelected(String ean) {
        Bundle args = new Bundle();
        args.putString(BookDetail.EAN_KEY, ean);

        BookDetail fragment = new BookDetail();
        fragment.setArguments(args);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        int id = R.id.container;
        if (findViewById(R.id.right_container) != null) {
            id = R.id.right_container;
        } else {
            fragmentTransaction.addToBackStack(TAG_BOOK_DETAIL);
        }

        fragmentTransaction.replace(id, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReciever);
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() < 1) {
            finish();
        }
        super.onBackPressed();
    }

    @Override
    public void onBackStackChanged() {
        checkIfShouldDisplayHomeUp();
    }

    public void checkIfShouldDisplayHomeUp() {
        if (getSupportActionBar() != null) {
            boolean showArrowBack = getSupportFragmentManager().getBackStackEntryCount() > 0;
            mDrawerToggle.setDrawerIndicatorEnabled(!showArrowBack);
//            mDrawerToggle.syncState();
        }
    }


    private boolean isTablet() {
        return (getApplicationContext().getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getSupportFragmentManager().putFragment(outState, "current_fragment", nextFragment);
        super.onSaveInstanceState(outState);
        navigationView.onSaveInstance(outState);
    }


    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(drawerLayout.getWindowToken(), 0);
    }

    private void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private class MessageReceiever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra(MESSAGE_KEY) != null) {
                showToastMessage(intent.getStringExtra(MESSAGE_KEY));
            } else if (intent.getStringExtra(MESSAGE_ERROR_KEY) != null) {
                showToastMessage(intent.getStringExtra(MESSAGE_ERROR_KEY));
            }
        }
    }

}