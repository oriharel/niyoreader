package com.niyo.reader.app.data;

import android.provider.BaseColumns;

/**
 * Created by oriharel on 6/6/14.
 */
public class FeedsTableColumn implements BaseColumns {
    public static final String TITLE = "title";
    public static final String TEXT = "text";
    public static final String TYPE = "type";
    public static final String XML_URL = "xml_url";
    public static final String HTML_URL = "html_url";
    public static final String FEED_GROUP = "feed_group";

    public static final int COLUMN_ID_PATH_INDEX = 1;
    public static final int COLUMN_TITLE_INDEX = 2;
    public static final int COLUMN_TEXT_INDEX = 3;
    public static final int COLUMN_TYPE_INDEX = 4;
    public static final int COLUMN_XML_URL_INDEX = 5;
    public static final int COLUMN_HTML_URL_INDEX = 6;
    public static final int COLUMN_FEED_GROUP_INDEX = 7;
}
