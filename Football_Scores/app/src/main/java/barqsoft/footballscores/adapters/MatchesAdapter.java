package barqsoft.footballscores.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.models.Match;
import barqsoft.footballscores.views.MatchCardView;

public class MatchesAdapter extends CursorRecyclerViewAdapter<MatchesAdapter.ViewHolder> {


    public MatchesAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, Cursor cursor) {
        final Match match = new Match(cursor);
        viewHolder.cardView.setMatch(match);
        viewHolder.cardView.showDetailView(MainActivity.selected_match_id == match.getId());
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewHolder.cardView.isDetailViewVisible()) {
                    viewHolder.cardView.showDetailView(false);
                    MainActivity.selected_match_id = -1;
                } else {
                    viewHolder.cardView.showDetailView(true);
                    MainActivity.selected_match_id = match.getId();
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MatchCardView itemView = new MatchCardView(parent.getContext());
        return new ViewHolder(itemView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MatchCardView cardView;

        public ViewHolder(MatchCardView cardView) {
            super(cardView);
            this.cardView = cardView;
        }
    }
}
