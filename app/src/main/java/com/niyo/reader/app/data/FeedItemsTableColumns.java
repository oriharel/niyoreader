package com.niyo.reader.app.data;

import android.provider.BaseColumns;

/**
 * Created by oriharel on 6/15/14.
 */
public class FeedItemsTableColumns implements BaseColumns {
    public static final String TITLE = "title";
    public static final String LINK = "link";
    public static final String GUID = "guid";
    public static final String PUB_DATE = "pubDate";
    public static final String AUTHOR = "author";
    public static final String DESCRIPTION = "description";
    public static final String ENCLUSURE_URL = "enclosureUrl";
    public static final String CONTENT = "content";
    public static final String FEED_URL = "feedUrl";

    public static final int COLUMN_ID_INDEX = 1;
    public static final int COLUMN_TITLE_INDEX = 2;
    public static final int COLUMN_LINK_INDEX = 3;
    public static final int COLUMN_GUID_INDEX = 4;
    public static final int COLUMN_PUB_DATE_INDEX = 5;
    public static final int COLUMN_AUTHOR_INDEX = 6;
    public static final int COLUMN_DESCRIPTION_INDEX = 7;
    public static final int COLUMN_ENCLOSURE_URL = 8;
    public static final int COLUMN_CONTENT_INDEX = 9;
    public static final int COLUMN_FEED_URL_INDEX = 10;
}
