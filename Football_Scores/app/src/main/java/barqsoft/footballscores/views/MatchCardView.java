package barqsoft.footballscores.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import barqsoft.footballscores.R;
import barqsoft.footballscores.utils.Utilies;
import barqsoft.footballscores.models.Match;
import butterknife.Bind;
import butterknife.ButterKnife;

public class MatchCardView extends CardView {

    @Bind(R.id.textview_home_name)
    TextView textViewHomeName;

    @Bind(R.id.textview_away_name)
    TextView textViewAwayName;

    @Bind(R.id.textview_score)
    TextView textViewScore;

    @Bind(R.id.textview_match_time)
    TextView textViewMatchTime;

    @Bind(R.id.imageview_home_logo)
    ImageView imageViewHomeLogo;

    @Bind(R.id.imageview_away_logo)
    ImageView imageViewAwayLogo;

    @Bind(R.id.layout_detail_container)
    ViewGroup layoutDetailContainer;

//    DetailView

    @Bind(R.id.textview_match_day)
    TextView textViewMatchDay;

    @Bind(R.id.textview_league)
    TextView textViewLeague;

    @Bind(R.id.button_share)
    Button buttonShare;


    private Match match;

    public MatchCardView(Context context) {
        super(context);
        init();
    }

    public MatchCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        View.inflate(getContext(), R.layout.match_cardview, this);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(layoutParams);

        ButterKnife.bind(this);

        initListeners();
    }

    private void initListeners() {
        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shareMessage = match.getHomeName() + " " + match.getScore() + " " + match.getAwayName();
                Utilies.shareMatchInfo(getContext(), shareMessage);
            }
        });
    }

    public void setMatch(@NonNull Match match) {
        this.match = match;
        imageViewHomeLogo.setImageResource(Utilies.getTeamCrestByTeamName(match.getHomeName()));
        imageViewAwayLogo.setImageResource(Utilies.getTeamCrestByTeamName(match.getAwayName()));

        textViewMatchTime.setText(match.getMatchTime());
        textViewHomeName.setText(match.getHomeName());
        textViewAwayName.setText(match.getAwayName());
        textViewScore.setText(match.getScore());

        loadDetailInfo();
        showDetailView(true);
    }

    public void showDetailView(boolean show) {
        layoutDetailContainer.setVisibility(show ? VISIBLE : GONE);
    }

    public boolean isDetailViewVisible() {
        return layoutDetailContainer.getVisibility() == VISIBLE;
    }


    private void loadDetailInfo() {
        String matchDay = Utilies.getMatchDay(match.getMatchDay(), match.getLeague());
        String league = Utilies.getLeague(match.getLeague());
        textViewMatchDay.setText(matchDay);
        textViewLeague.setText(league);
        textViewMatchDay.setContentDescription(matchDay);
        textViewLeague.setContentDescription(league);
    }
}
