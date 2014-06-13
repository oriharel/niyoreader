package com.niyo.reader.app;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by oriharel on 6/13/14.
 */
public class FeedsListActivity extends Activity {
    public static final String LOG_TAG = FeedsListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.feed_group_list);

    }
}
