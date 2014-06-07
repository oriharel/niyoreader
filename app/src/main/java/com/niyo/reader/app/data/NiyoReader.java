package com.niyo.reader.app.data;

import android.net.Uri;

/**
 * Created by oriharel on 6/6/14.
 */
public class NiyoReader {

    public static String AUTHORITY = "com.niyo.reader.provider";
    public static String SCHEME = "content://";
    public static final String FEEDS = "/feeds";
    public static final Uri FEEDS_URI =  Uri.parse(SCHEME + AUTHORITY + FEEDS);
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.reader.feed";
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.reader.feed";

    public static final String[] FEEDS_SUMMARY_PROJECTION = new String[] {
            FeedsTableColumn._ID,
            FeedsTableColumn.TITLE,
            FeedsTableColumn.XML_URL,
            FeedsTableColumn.FEED_GROUP
    };

    public static final String[] FEEDS_GROUPS_PROJECTION = new String[] {
            FeedsTableColumn._ID,
            FeedsTableColumn.FEED_GROUP
    };
}
