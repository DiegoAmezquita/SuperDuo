package it.jaschke.alexandria;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;


public class AddBook extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String EAN_CONTENT = "eanContent";

    private String prefixEan = "978";

    @Bind(R.id.ean)
    EditText editTextEan;

    @Bind(R.id.bookTitle)
    TextView textViewBookTitle;

    @Bind(R.id.bookSubTitle)
    TextView textViewBookSubTitle;

    @Bind(R.id.authors)
    TextView textViewAuthors;

    @Bind(R.id.categories)
    TextView textViewCategories;

    @Bind(R.id.bookCover)
    ImageView imageViewBookCover;

    @Bind(R.id.save_button)
    View buttonSave;

    @Bind(R.id.delete_button)
    View buttonDelete;

    @Bind(R.id.scan_button)
    View buttonScan;

    public AddBook() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (editTextEan != null) {
            outState.putString(EAN_CONTENT, editTextEan.getText().toString());
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_add_book, container, false);
        ButterKnife.bind(this, rootView);

        editTextEan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //no need
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //no need
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkAndSearchBook(s.toString());


            }
        });

        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String prompt = getString(R.string.prompt_scan);

                IntentIntegrator.forSupportFragment(AddBook.this)
                        .setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES)
                        .setCaptureActivity(CaptureActivityOrientation.class)
                        .setOrientationLocked(false)
                        .setPrompt(prompt)
                        .initiateScan();

            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextEan.setText("");
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, editTextEan.getText().toString());
                bookIntent.setAction(BookService.DELETE_BOOK);
                getActivity().startService(bookIntent);
                editTextEan.setText("");
            }
        });

        if (savedInstanceState != null) {
            editTextEan.setText(savedInstanceState.getString(EAN_CONTENT));
            editTextEan.setHint("");
        }

        return rootView;
    }

    private void checkAndSearchBook(String ean) {
        //catch isbn10 numbers
        if (ean.length() == 10 && !ean.startsWith(prefixEan)) {
            ean = prefixEan + ean;
        }
        if (ean.length() < 13) {
            clearFields();
            return;
        }
        //Once we have an ISBN, start a book intent
        Intent bookIntent = new Intent(getActivity(), BookService.class);
        bookIntent.putExtra(BookService.EAN, ean);
        bookIntent.setAction(BookService.FETCH_BOOK);
        getActivity().startService(bookIntent);
        AddBook.this.restartLoader();
    }

    private void restartLoader() {
        int LOADER_ID = 1;
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (editTextEan.getText().length() == 0) {
            return null;
        }
        String eanStr = editTextEan.getText().toString();
        if (eanStr.length() == 10 && !eanStr.startsWith(prefixEan)) {
            eanStr = prefixEan + eanStr;
        }

        eanStr = eanStr.replaceAll("[^\\d.]", "");

        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(eanStr)),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        String bookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        textViewBookTitle.setText(bookTitle);

        String bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        textViewBookSubTitle.setText(bookSubTitle);

        String authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
        if (authors != null) {
            String[] authorsArr = authors.split(",");
            textViewAuthors.setLines(authorsArr.length);
            textViewAuthors.setText(authors.replace(",", "\n"));
        }
        String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        if (imgUrl != null && imgUrl.length() > 0 && Patterns.WEB_URL.matcher(imgUrl).matches()) {
            Picasso.with(getActivity()).load(imgUrl).into(imageViewBookCover);
            imageViewBookCover.setVisibility(View.VISIBLE);
        }

        String categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
        textViewCategories.setText(categories);

        buttonSave.setVisibility(View.VISIBLE);
        buttonDelete.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    private void clearFields() {
        textViewBookTitle.setText("");
        textViewBookSubTitle.setText("");
        textViewAuthors.setText("");
        textViewCategories.setText("");
        imageViewBookCover.setVisibility(View.INVISIBLE);
        buttonSave.setVisibility(View.INVISIBLE);
        buttonDelete.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle(R.string.scan_add_book);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && data.getExtras() != null && data.getExtras().containsKey(Intents.Scan.RESULT)) {
            String eanCode = data.getExtras().getString(Intents.Scan.RESULT);
            eanCode = eanCode.replaceAll("[^\\d.]", "");
            editTextEan.setText(eanCode);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}

