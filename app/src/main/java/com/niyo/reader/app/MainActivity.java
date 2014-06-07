package com.niyo.reader.app;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.niyo.reader.app.data.FeedsTableColumn;
import com.niyo.reader.app.data.InsertXmlToProvider;
import com.niyo.reader.app.data.NiyoReader;

import java.util.List;


public class MainActivity extends Activity {
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    LoaderManager.LoaderCallbacks<Cursor> mLoader;
    SimpleCursorAdapter _groupsAdapter;
    int mLoaderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.noFeeds).setVisibility(View.GONE);
        findViewById(R.id.feedGroupsListView).setVisibility(View.GONE);

        String[] columns = new String[]{FeedsTableColumn.FEED_GROUP};
        int[] to = new int[] { R.id.groupTitle };

        _groupsAdapter = new SimpleCursorAdapter(this, R.layout.feed_group_item, null,
                columns, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        ListView groups = (ListView)findViewById(R.id.feedGroupsListView);
        groups.setAdapter(_groupsAdapter);

        initLoader();
        requestFeedsUpadte();
    }

    private void requestFeedsUpadte() {
        Log.d(LOG_TAG, "requesting feeds data from provider");
        getLoaderManager().initLoader(0, null, mLoader);
    }

    private void initLoader() {

        final MainActivity context = this;

        mLoader = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
                Uri baseUri = NiyoReader.FEEDS_URI;

                // Now create and return a CursorLoader that will take care of
                // creating a Cursor for the data being displayed.
                String select = "((" + FeedsTableColumn.FEED_GROUP + " NOTNULL) AND ("
                        + FeedsTableColumn.FEED_GROUP + " != '' ))";
                Loader<Cursor> result = new CursorLoader(context, baseUri,
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

    private void populateFeedGroupsList(Cursor cursor) {

        findViewById(R.id.noFeeds).setVisibility(View.GONE);

        ListView groups = (ListView)findViewById(R.id.feedGroupsListView);
        groups.setVisibility(View.VISIBLE);




        _groupsAdapter.swapCursor(cursor);


//        cursor.moveToFirst();
//        while(!cursor.isLast()) {
//            int groupIndex = cursor.getColumnIndex(FeedsTableColumn.FEED_GROUP);
//            Log.d(LOG_TAG, "populateFeedGroupsList with "+cursor.getString(groupIndex));
//            cursor.moveToNext();
//        }


    }

    private void tryToLoadLocalXml() {

        InsertXmlToProvider task = new InsertXmlToProvider(this, new ServiceCaller() {
            @Override
            public void success(Object data) {

                TextView noFeedsText = (TextView)findViewById(R.id.noFeeds);
                noFeedsText.setText("Oh now there are feeds!!");

            }

            @Override
            public void failure(Object data, String description) {

            }
        });

        Log.d(LOG_TAG, "going to parse local xml file and insert into provider");
        task.execute("aol_reader_export.xml");

    }

    private void showNoFriends() {
        findViewById(R.id.noFeeds).setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
