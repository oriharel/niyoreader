package com.niyo.reader.app;

import android.app.Fragment;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;

import com.niyo.reader.app.data.FeedsTableColumn;
import com.niyo.reader.app.data.NiyoReader;

/**
 * Created by oriharel on 6/13/14.
 */
public class GroupFeedsFragment extends ListFragment {

    public static final String LOG_TAG = GroupFeedsFragment.class.getSimpleName();
    LoaderManager.LoaderCallbacks<Cursor> mLoader;
    SimpleCursorAdapter _groupsAdapter;
    int mLoaderId;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String[] columns = new String[]{FeedsTableColumn.TITLE};
        int[] to = new int[] { R.id.groupTitle };

        _groupsAdapter = new SimpleCursorAdapter(getActivity(), R.layout.feed_group_item, null,
                columns, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        setListAdapter(_groupsAdapter);


        Intent intent = getActivity().getIntent();
        String groupTitle = intent.getStringExtra("groupTitle");

        initLoader(groupTitle);
        requestFeedsUpadte();

    }

    private void initLoader(final String groupTitle) {

        mLoader = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
                Uri baseUri = NiyoReader.FEEDS_URI;

                // Now create and return a CursorLoader that will take care of
                // creating a Cursor for the data being displayed.
                String select = "((" + FeedsTableColumn.FEED_GROUP + " NOTNULL) AND ("
                        + FeedsTableColumn.FEED_GROUP + " = '"+groupTitle+"' ))";
                Loader<Cursor> result = new CursorLoader(getActivity(), baseUri,
                        NiyoReader.FEEDS_SUMMARY_PROJECTION, select, null,
                        FeedsTableColumn.FEED_GROUP + " COLLATE LOCALIZED ASC");

                mLoaderId = result.getId();
                return result;
            }

            @Override
            public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

                Log.d(LOG_TAG, "finished loading feeds from local storage");

                if (cursor.getCount() <= 0) {
                    Log.d(LOG_TAG, "got 0 feeds from local storage");
                }
                else {
                    populateFeedsList(cursor);
                }

            }

            @Override
            public void onLoaderReset(Loader<Cursor> cursorLoader) {

            }
        };

    }

    private void populateFeedsList(Cursor cursor) {
        _groupsAdapter.swapCursor(cursor);
    }

    private void requestFeedsUpadte() {
        Log.d(LOG_TAG, "requesting feeds data from provider");
        getLoaderManager().initLoader(0, null, mLoader);
    }
}
