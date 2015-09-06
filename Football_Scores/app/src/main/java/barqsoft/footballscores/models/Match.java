package barqsoft.footballscores.models;

import android.database.Cursor;

import java.io.Serializable;

import barqsoft.footballscores.utils.Utilies;

public class Match implements Serializable {

    public static final int COL_HOME = 3;
    public static final int COL_AWAY = 4;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_DATE = 1;
    public static final int COL_LEAGUE = 5;
    public static final int COL_MATCHDAY = 9;
    public static final int COL_ID = 8;
    public static final int COL_MATCHTIME = 2;

    private int id;
    private String homeName;
    private String awayName;
    private String score;
    private String matchTime;
    private String date;
    private int league;
    private int matchDay;

    public Match(Cursor cursor) {
        id = cursor.getInt(COL_ID);
        date = cursor.getString(COL_DATE);
        homeName = cursor.getString(COL_HOME);
        awayName = cursor.getString(COL_AWAY);
        matchTime = cursor.getString(COL_MATCHTIME);
        league = cursor.getInt(COL_LEAGUE);
        matchDay = cursor.getInt(COL_MATCHDAY);
        score = Utilies.getScores(cursor.getInt(COL_HOME_GOALS), cursor.getInt(COL_AWAY_GOALS));
    }

    public int getId() {
        return id;
    }

    public String getHomeName() {
        return homeName;
    }

    public String getAwayName() {
        return awayName;
    }

    public String getScore() {
        return score;
    }

    public String getMatchTime() {
        return matchTime;
    }

    public String getDate() {
        return date;
    }

    public int getMatchDay() {
        return matchDay;
    }

    public int getLeague() {
        return league;
    }

}
