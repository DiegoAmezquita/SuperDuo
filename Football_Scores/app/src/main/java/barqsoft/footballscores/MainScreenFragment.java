package barqsoft.footballscores;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import barqsoft.footballscores.adapters.MatchesAdapter;
import barqsoft.footballscores.decorators.SpacesItemDecoration;
import barqsoft.footballscores.service.FetchService;
import butterknife.Bind;
import butterknife.ButterKnife;

public class MainScreenFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    @Bind(R.id.recyclerview_matches)
    RecyclerView recyclerViewMatches;

    @Bind(R.id.textview_message)
    TextView textViewMessage;

    private MatchesAdapter matchesAdapter;

    //    public ScoresAdapterOld mAdapter;
    public static final int SCORES_LOADER = 0;
    private String[] fragmentdate = new String[1];


    private void update_scores() {
        Intent service_start = new Intent(getActivity(), FetchService.class);
        getActivity().startService(service_start);
    }

    public void setFragmentDate(String date) {
        fragmentdate[0] = date;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        update_scores();
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        matchesAdapter = new MatchesAdapter(getActivity(), null);

        recyclerViewMatches.addItemDecoration(new SpacesItemDecoration(20));
        recyclerViewMatches.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewMatches.setAdapter(matchesAdapter);

        if (savedInstanceState != null) {
            fragmentdate[0] = savedInstanceState.getString("date");
        }

        getLoaderManager().initLoader(SCORES_LOADER, null, this);

        return rootView;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(), DatabaseContract.scores_table.buildScoreWithDate(),
                null, null, fragmentdate, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                cursor.moveToNext();
            }
            matchesAdapter.swapCursor(cursor);
            if (cursor.getCount() > 0) {
                textViewMessage.setVisibility(View.GONE);
            } else {
                textViewMessage.setText(getResources().getString(R.string.copy_no_data_to_show));
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        matchesAdapter.swapCursor(null);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("date", fragmentdate[0]);
        super.onSaveInstanceState(outState);
    }
}
