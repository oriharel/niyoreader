package com.niyo.reader.app;

import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.niyo.reader.app.data.FeedsTableColumn;
import com.niyo.reader.app.data.InsertXmlToProvider;
import com.niyo.reader.app.data.NiyoReader;

/**
 * Created by oriharel on 6/7/14.
 */
public class ChannelsListFragment extends ListFragment{

    public static final String LOG_TAG = ChannelsListFragment.class.getSimpleName();
    SimpleCursorAdapter _groupsAdapter;
    LoaderManager.LoaderCallbacks<Cursor> mLoader;
    int mLoaderId;
    boolean mDualPane;
    int mCurCheckPosition = 0;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String[] columns = new String[]{FeedsTableColumn.FEED_GROUP};
        int[] to = new int[] { R.id.groupTitle };

        _groupsAdapter = new SimpleCursorAdapter(getActivity(), R.layout.feed_group_item, null,
                columns, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        setListAdapter(_groupsAdapter);

        View feedsFrame = getActivity().findViewById(R.id.feeds_list_layout);

        mDualPane = feedsFrame != null && feedsFrame.getVisibility() == View.VISIBLE;

        if (savedInstanceState != null) {
            // Restore last state for checked position.
            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
        }

        if (mDualPane) {
            // In dual-pane mode, the list view highlights the selected item.
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            // Make sure our UI is in the correct state.
            showFeedsList(null, mCurCheckPosition);
        }

        initLoader();
        requestFeedsUpadte();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(LOG_TAG, "Clicking a feed channel id "+id);
        showFeedsList(v, position);
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

    private void showFeedsList(View v, int curPosition) {

        // Otherwise we need to launch a new activity to display
        // the dialog fragment with selected text.
        Intent intent = new Intent();
        intent.setClass(getActivity(), FeedsListActivity.class);
        TextView groupTitleView = (TextView)v.findViewById(R.id.groupTitle);
        intent.putExtra("index", curPosition);
        intent.putExtra("groupTitle", groupTitleView.getText());
        startActivity(intent);

    }


    private void populateFeedGroupsList(Cursor cursor) {

        _groupsAdapter.swapCursor(cursor);

    }

    private void tryToLoadLocalXml() {

        InsertXmlToProvider task = new InsertXmlToProvider(getActivity(), new ServiceCaller() {
            @Override
            public void success(Object data) {


            }

            @Override
            public void failure(Object data, String description) {

            }
        });

        Log.d(LOG_TAG, "going to parse local xml file and insert into provider");
        task.execute("aol_reader_export.xml");

    }

    public static class FeedsActivity extends Activity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            if (getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_LANDSCAPE) {
                // If the screen is now in landscape mode, we can show the
                // dialog in-line with the list so we don't need this activity.
                finish();
                return;
            }

            if (savedInstanceState == null) {
                // During initial setup, plug in the details fragment.
                FeedsListFragment details = new FeedsListFragment();
                details.setArguments(getIntent().getExtras());
                getFragmentManager().beginTransaction().add(android.R.id.content, details).commit();
            }
        }
    }

    public static class FeedsListFragment extends Fragment {
        /**
         * Create a new instance of DetailsFragment, initialized to
         * show the text at 'index'.
         */
        public static FeedsListFragment newInstance(int index) {
            FeedsListFragment f = new FeedsListFragment();

            // Supply index input as an argument.
            Bundle args = new Bundle();
            args.putInt("index", index);
            f.setArguments(args);

            return f;
        }

        public int getShownIndex() {
            return getArguments().getInt("index", 0);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            if (container == null) {
                // We have different layouts, and in one of them this
                // fragment's containing frame doesn't exist.  The fragment
                // may still be created from its saved state, but there is
                // no reason to try to create its view hierarchy because it
                // won't be displayed.  Note this is not needed -- we could
                // just run the code below, where we would create and return
                // the view hierarchy; it would just never be used.
                return null;
            }

            ScrollView scroller = new ScrollView(getActivity());
            TextView text = new TextView(getActivity());
            int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    4, getActivity().getResources().getDisplayMetrics());
            text.setPadding(padding, padding, padding, padding);
            scroller.addView(text);
            text.setText("THIS IS WHERE THE FEEDS LIST SHOULD BE!!");
            return scroller;
        }
    }


}
