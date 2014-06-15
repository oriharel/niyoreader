package com.niyo.reader.app.data;

import android.net.Uri;

/**
 * Created by oriharel on 6/6/14.
 */
public class NiyoReader {

    public static String AUTHORITY = "com.niyo.reader.provider";
    public static String SCHEME = "content://";
    public static final String FEEDS = "/feeds";
    public static final String FEED_ITEMS = "/feedItems";
    public static final Uri FEEDS_URI =  Uri.parse(SCHEME + AUTHORITY + FEEDS);
    public static final Uri FEED_ITEMS_URI = Uri.parse(SCHEME + AUTHORITY + FEED_ITEMS);
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

    public static final String[] FEEDS_ITEMS_PROJECTION = new String[] {
            FeedItemsTableColumns._ID,
            FeedItemsTableColumns.TITLE,
            FeedItemsTableColumns.LINK,
            FeedItemsTableColumns.GUID,
            FeedItemsTableColumns.PUB_DATE,
            FeedItemsTableColumns.AUTHOR,
            FeedItemsTableColumns.DESCRIPTION,
            FeedItemsTableColumns.ENCLUSURE_URL,
            FeedItemsTableColumns.CONTENT,
            FeedItemsTableColumns.FEED_URL

    };
}
