package it.jaschke.alexandria.api;


import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.data.AlexandriaContract;

public class BookListAdapter extends CursorAdapter {

    private Picasso picasso;

    public static class ViewHolder {
        public final ImageView imageViewBookCover;
        public final TextView textViewbookTitle;
        public final TextView textViewbookSubTitle;

        public ViewHolder(View view) {
            imageViewBookCover = (ImageView) view.findViewById(R.id.fullBookCover);
            textViewbookTitle = (TextView) view.findViewById(R.id.listBookTitle);
            textViewbookSubTitle = (TextView) view.findViewById(R.id.listBookSubTitle);
        }
    }

    public BookListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        picasso = Picasso.with(context);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String imgUrl = cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        if (imgUrl != null && imgUrl.length() > 0) {
            picasso.load(imgUrl).into(viewHolder.imageViewBookCover);
        }else{
            picasso.load(R.drawable.ic_launcher).into(viewHolder.imageViewBookCover);
        }

        String bookTitle = cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        viewHolder.textViewbookTitle.setText(bookTitle);

        String bookSubTitle = cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));

        if(bookSubTitle.length()>0) {
            viewHolder.textViewbookSubTitle.setText(bookSubTitle);
            viewHolder.textViewbookSubTitle.setVisibility(View.VISIBLE);
        }else{
            viewHolder.textViewbookSubTitle.setVisibility(View.GONE);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.book_list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }
}
