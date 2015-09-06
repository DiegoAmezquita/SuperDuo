package it.jaschke.alexandria;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.Menu;

public class NavigationView extends android.support.design.widget.NavigationView {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    private OnNavigationItemSelectedListener mListener;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    public NavigationView(Context context) {
        super(context);
        init(null);
    }

    public NavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(null);
    }

    public NavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(null);
    }

    private void init(Bundle savedInstanceState) {
        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        } else {
            String key = getContext().getString(R.string.initial_fragment);
            mCurrentSelectedPosition = Integer.parseInt(sp.getString(key, "0"));
        }

        // Select either the default item (0) or the last selected item.
        restoreSelectedPosition(mCurrentSelectedPosition);
    }

    public boolean isUserLearnedDrawer() {
        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        return !mUserLearnedDrawer && !mFromSavedInstanceState;
    }

    public void onDrawerOpened() {
        if (!mUserLearnedDrawer) {
            // The user manually opened the drawer; store this flag to prevent auto-showing
            // the navigation drawer automatically in the future.
            mUserLearnedDrawer = true;
            SharedPreferences sp = PreferenceManager
                    .getDefaultSharedPreferences(getContext());
            sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
        }
    }

    public void onSaveInstance(Bundle outState) {
        int position = findSelectedPosition();
        outState.putInt(STATE_SELECTED_POSITION, position);
    }

    private int findSelectedPosition() {
        Menu menu = getMenu();
        int count = menu.size();

        for (int i = 0; i < count; i++) {
            if (menu.getItem(i).isChecked()) {
                return i;
            }
        }
        return 0;
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        init(savedInstanceState);
    }

    private void restoreSelectedPosition(int position) {
        Menu menu = getMenu();
        menu.getItem(position).setChecked(true);
        if(mListener!=null) {
            mListener.onNavigationItemSelected(menu.getItem(position));
        }
    }


    @Override
    public void setNavigationItemSelectedListener(OnNavigationItemSelectedListener listener) {
        super.setNavigationItemSelectedListener(listener);
        mListener = listener;
        restoreSelectedPosition(mCurrentSelectedPosition);
    }
}
