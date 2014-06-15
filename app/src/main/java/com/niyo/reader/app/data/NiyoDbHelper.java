package com.niyo.reader.app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by oriharel on 6/6/14.
 */
public class NiyoDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = NiyoDbHelper.class.getSimpleName();
    public static final String FEEDS_TABLE = "feeds";
    public static final String FEED_ITEMS_TABLE = "feedItems";

    public static final String TABLE_FEEDS_CREATE = "create table " + FEEDS_TABLE + "("
            + FeedsTableColumn._ID + " integer, "
            + FeedsTableColumn.TITLE + " TEXT, "
            + FeedsTableColumn.TEXT + " TEXT, "
            + FeedsTableColumn.TYPE + " TEXT, "
            + FeedsTableColumn.XML_URL + " TEXT, "
            + FeedsTableColumn.HTML_URL + " TEXT, "
            + FeedsTableColumn.FEED_GROUP + " TEXT);";

    public static final String TABLE_FEED_ITEMS_CREATE = "create table "+ FEED_ITEMS_TABLE + "("
            + FeedItemsTableColumns._ID + " integer, "
            + FeedItemsTableColumns.TITLE + " TEXT, "
            + FeedItemsTableColumns.LINK + " TEXT, "
            + FeedItemsTableColumns.GUID + " TEXT, "
            + FeedItemsTableColumns.PUB_DATE + " DATE, "
            + FeedItemsTableColumns.AUTHOR + " TEXT, "
            + FeedItemsTableColumns.DESCRIPTION + " TEXT, "
            + FeedItemsTableColumns.ENCLUSURE_URL + " TEXT, "
            + FeedItemsTableColumns.CONTENT + " TEXT, "
            + FeedItemsTableColumns.FEED_URL + " TEXT);";


    public NiyoDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                        int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG, "onCreate started "+TABLE_FEEDS_CREATE);
        db.execSQL(TABLE_FEEDS_CREATE);
        db.execSQL(TABLE_FEED_ITEMS_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.d(LOG_TAG, "onUpgrade started "+TABLE_FEEDS_CREATE);

    }
}
