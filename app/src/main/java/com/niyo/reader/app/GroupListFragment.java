package com.niyo.reader.app;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.niyo.reader.app.data.FeedsTableColumn;
import com.niyo.reader.app.data.InsertXmlToProvider;
import com.niyo.reader.app.data.NiyoReader;

/**
 * Created by oriharel on 6/7/14.
 */
public class GroupListFragment extends ListFragment{

    public static final String LOG_TAG = GroupListFragment.class.getSimpleName();
    SimpleCursorAdapter _groupsAdapter;
    LoaderManager.LoaderCallbacks<Cursor> mLoader;
    int mLoaderId;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String[] columns = new String[]{FeedsTableColumn.FEED_GROUP};
        int[] to = new int[] { R.id.groupTitle };

        _groupsAdapter = new SimpleCursorAdapter(getActivity(), R.layout.feed_group_item, null,
                columns, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        setListAdapter(_groupsAdapter);

        initLoader();
        requestFeedsUpadte();
    }

    private void requestFeedsUpadte() {
        Log.d(LOG_TAG, "requesting feeds data from provider");
        getLoaderManager().initLoader(0, null, mLoader);
    }

    private void initLoader() {

        mLoader = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
                Uri baseUri = NiyoReader.FEEDS_URI;

                // Now create and return a CursorLoader that will take care of
                // creating a Cursor for the data being displayed.
                String select = "((" + FeedsTableColumn.FEED_GROUP + " NOTNULL) AND ("
                        + FeedsTableColumn.FEED_GROUP + " != '' ))";
                Loader<Cursor> result = new CursorLoader(getActivity(), baseUri,
                        NiyoReader.FEEDS_GROUPS_PROJECTION, select, null,
                        FeedsTableColumn.FEED_GROUP + " COLLATE LOCALIZED ASC");

                mLoaderId = result.getId();
                return result;
            }

            @Override
            public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

                Log.d(LOG_TAG, "finished loading feeds from local storage");

                if (cursor.getCount() <= 0) {
                    Log.d(LOG_TAG, "got 0 feeds from local storage");
                    showNoFriends();
                    tryToLoadLocalXml();
                }
                else {
                    populateFeedGroupsList(cursor);
                }

            }

            @Override
            public void onLoaderReset(Loader<Cursor> cursorLoader) {

            }
        };

    }

    private void showNoFriends() {
        getActivity().findViewById(R.id.noFeeds).setVisibility(View.VISIBLE);
    }

    private void populateFeedGroupsList(Cursor cursor) {

        _groupsAdapter.swapCursor(cursor);

    }

    private void tryToLoadLocalXml() {

        InsertXmlToProvider task = new InsertXmlToProvider(getActivity(), new ServiceCaller() {
            @Override
            public void success(Object data) {

                TextView noFeedsText = (TextView)getActivity().findViewById(R.id.noFeeds);
                noFeedsText.setText("Oh now there are feeds!!");

            }

            @Override
            public void failure(Object data, String description) {

            }
        });

        Log.d(LOG_TAG, "going to parse local xml file and insert into provider");
        task.execute("aol_reader_export.xml");

    }
}
