package barqsoft.footballscores.widget;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.utils.Utilies;
import barqsoft.footballscores.models.Match;

public class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    ArrayList<Match> arrayMatches;
    private Context mContext;

    public WidgetRemoteViewsFactory(Context context) {
        mContext = context;
    }

    public void onCreate() {
        updateData();
    }

    private void updateData() {
        Cursor cursor = mContext.getContentResolver().query(DatabaseContract.scores_table.buildScore(), null, null, null, DatabaseContract.scores_table.DATE_COL);
        arrayMatches = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Match match = new Match(cursor);
                arrayMatches.add(match);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    @Override
    public void onDataSetChanged() {
        updateData();
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public int getCount() {
        return arrayMatches.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.layout_widget_item);

        Match match = arrayMatches.get(position);
        remoteViews.setTextViewText(R.id.textview_home_name, match.getHomeName());
        remoteViews.setTextViewText(R.id.textview_away_name, match.getAwayName());
        remoteViews.setTextViewText(R.id.textview_score, match.getScore());
        remoteViews.setTextViewText(R.id.textview_match_time, match.getMatchTime());
        remoteViews.setTextViewText(R.id.textview_date, match.getDate());
        remoteViews.setImageViewResource(R.id.imageview_home_logo, Utilies.getTeamCrestByTeamName(match.getHomeName()));
        remoteViews.setImageViewResource(R.id.imageview_away_logo, Utilies.getTeamCrestByTeamName(match.getAwayName()));


        return remoteViews;

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}