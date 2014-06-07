package com.niyo.reader.app.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.niyo.reader.app.AndroidUtil;

import java.util.HashMap;

/**
 * Created by oriharel on 6/6/14.
 */
public class ReaderContentProvider extends ContentProvider {
    public static final String LOG_TAG = ReaderContentProvider.class.getSimpleName();
    private NiyoDbHelper _dbHelper;
    private static final String DATABASE_NAME = "niyoreader.db";
    private static final int DATABASE_VERSION = 2;

    public static final int FEEDS = 1;
    public static final int FEED_ID = 2;

    private static final UriMatcher sUriMatcher;

    private static HashMap<String, String> sFeedsProjectionMap;

    static {

        /*
         * Creates and initializes the URI matcher
         */
        // Create a new instance
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        sUriMatcher.addURI(NiyoReader.AUTHORITY, "feeds", FEEDS);

        // Add a pattern that routes URIs terminated with "feeds" plus an integer
        // to a feed ID operation
        sUriMatcher.addURI(NiyoReader.AUTHORITY, "feeds/#", FEED_ID);

        sFeedsProjectionMap = new HashMap<String, String>();

        // Maps the string "_ID" to the column name "_ID"
        sFeedsProjectionMap.put(FeedsTableColumn._ID, FeedsTableColumn._ID);

        // Maps "title" to "title"
        sFeedsProjectionMap.put(FeedsTableColumn.TITLE, FeedsTableColumn.TITLE);

        // Maps "note" to "note"
        sFeedsProjectionMap.put(FeedsTableColumn.XML_URL, FeedsTableColumn.XML_URL);

        sFeedsProjectionMap.put(FeedsTableColumn.FEED_GROUP, FeedsTableColumn.FEED_GROUP);

    }

    @Override
    public boolean onCreate() {
        Log.d(LOG_TAG, "onCreate started");
        Context context = getContext();

        setDbHelper(new NiyoDbHelper(context, DATABASE_NAME, null, DATABASE_VERSION));
        return getWritableDb() == null ? false : true;
    }

    private SQLiteDatabase getWritableDb()
    {
        return getDbHelper().getWritableDatabase();
    }

    private SQLiteDatabase getReadableDb() {
        return getDbHelper().getReadableDatabase();
    }

    private NiyoDbHelper getDbHelper() {
        return _dbHelper;
    }

    private void setDbHelper(NiyoDbHelper dbHelper) {
        _dbHelper = dbHelper;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(LOG_TAG, "query started with "+uri);
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        qb.setTables(NiyoDbHelper.FEEDS_TABLE);

        switch (sUriMatcher.match(uri)) {

            case FEEDS:
                qb.setProjectionMap(sFeedsProjectionMap);
                break;

            case FEED_ID:
                qb.setProjectionMap(sFeedsProjectionMap);
                qb.appendWhere(
                        FeedsTableColumn._ID +    // the name of the ID column
                                "=" +
                                // the position of the note ID itself in the incoming URI
                                uri.getPathSegments().get(FeedsTableColumn.COLUMN_ID_PATH_INDEX));
                break;

            default:
                // If the URI doesn't match any of the known patterns, throw an exception.
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Log.d(LOG_TAG, "going to query with selection "+selection);
        Log.d(LOG_TAG, "projection is "+ AndroidUtil.getArrayAsString(projection));
        Log.d(LOG_TAG, "selectionArgs is "+AndroidUtil.getArrayAsString(selectionArgs));
        Log.d(LOG_TAG, "sort order is "+sortOrder);
        String orderBy = FeedsTableColumn.TITLE;
        qb.setDistinct(true);
        Cursor cursor = qb.query(getReadableDb(), projection, selection, selectionArgs, null, null, orderBy);

        Log.d(LOG_TAG, "got " + cursor.getCount()
                + " results from uri " + uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        /**
         * Chooses the MIME type based on the incoming URI pattern
         */
        switch (sUriMatcher.match(uri)) {

            // If the pattern is for feeds, returns the general content type.
            case FEEDS:
                return NiyoReader.CONTENT_TYPE;

            // If the pattern is for friend IDs, returns the feed ID content type.
            case FEED_ID:
                return NiyoReader.CONTENT_ITEM_TYPE;

            // If the URI pattern doesn't match any permitted patterns, throws an exception.
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        Log.d(LOG_TAG, "insert started "+uri);
        Log.d(LOG_TAG, "the feed title is "+contentValues.getAsString(FeedsTableColumn.TITLE));
        String table = NiyoDbHelper.FEEDS_TABLE;
        getWritableDb().insert(table, FeedsTableColumn.TITLE, contentValues);
        getContext().getContentResolver().notifyChange(uri, null);
        return uri;
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        // Opens the database object in "write" mode.
        SQLiteDatabase db = getWritableDb();
        String finalWhere;

        int count;

        // Does the delete based on the incoming URI pattern.
        switch (sUriMatcher.match(uri)) {

            // If the incoming pattern matches the general pattern for friends, does a delete
            // based on the incoming "where" columns and arguments.
            case FEEDS:
                count = db.delete(
                        NiyoDbHelper.FEEDS_TABLE,  // The database table name
                        where,                     // The incoming where clause column names
                        whereArgs                  // The incoming where clause values
                );
                break;

            // If the incoming URI matches a single friend ID, does the delete based on the
            // incoming data, but modifies the where clause to restrict it to the
            // particular friend ID.
            case FEED_ID:
                /*
                 * Starts a final WHERE clause by restricting it to the
                 * desired friend ID.
                 */
                finalWhere =
                        FeedsTableColumn._ID +                              // The ID column name
                                " = " +                                          // test for equality
                                uri.getPathSegments().                           // the incoming note ID
                                        get(FeedsTableColumn.COLUMN_ID_PATH_INDEX)
                ;

                // If there were additional selection criteria, append them to the final
                // WHERE clause
                if (where != null) {
                    finalWhere = finalWhere + " AND " + where;
                }

                // Performs the delete.
                count = db.delete(
                        NiyoDbHelper.FEEDS_TABLE,  // The database table name.
                        finalWhere,                // The final WHERE clause
                        whereArgs                  // The incoming where clause values.
                );
                break;

            // If the incoming pattern is invalid, throws an exception.
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        /*Gets a handle to the content resolver object for the current context, and notifies it
         * that the incoming URI changed. The object passes this along to the resolver framework,
         * and observers that have registered themselves for the provider are notified.
         */
        getContext().getContentResolver().notifyChange(uri, null);

        // Returns the number of rows deleted.
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        // Opens the database object in "write" mode.
        SQLiteDatabase db = getWritableDb();
        int count;
        String finalWhere;

        // Does the update based on the incoming URI pattern
        switch (sUriMatcher.match(uri)) {

            // If the incoming URI matches the general notes pattern, does the update based on
            // the incoming data.
            case FEEDS:

                // Does the update and returns the number of rows updated.
                count = db.update(
                        NiyoDbHelper.FEEDS_TABLE, // The database table name.
                        values,                   // A map of column names and new values to use.
                        where,                    // The where clause column names.
                        whereArgs                 // The where clause column values to select on.
                );
                break;

            // If the incoming URI matches a single note ID, does the update based on the incoming
            // data, but modifies the where clause to restrict it to the particular note ID.
            case FEED_ID:
                // From the incoming URI, get the note ID
                String noteId = uri.getPathSegments().get(FeedsTableColumn.COLUMN_ID_PATH_INDEX);

                /*
                 * Starts creating the final WHERE clause by restricting it to the incoming
                 * note ID.
                 */
                finalWhere =
                        FeedsTableColumn._ID +                              // The ID column name
                                " = " +                                          // test for equality
                                uri.getPathSegments().                           // the incoming note ID
                                        get(FeedsTableColumn.COLUMN_ID_PATH_INDEX)
                ;

                // If there were additional selection criteria, append them to the final WHERE
                // clause
                if (where !=null) {
                    finalWhere = finalWhere + " AND " + where;
                }


                // Does the update and returns the number of rows updated.
                count = db.update(
                        NiyoDbHelper.FEEDS_TABLE, // The database table name.
                        values,                   // A map of column names and new values to use.
                        finalWhere,               // The final WHERE clause to use
                        // placeholders for whereArgs
                        whereArgs                 // The where clause column values to select on, or
                        // null if the values are in the where argument.
                );
                break;
            // If the incoming pattern is invalid, throws an exception.
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        /*Gets a handle to the content resolver object for the current context, and notifies it
         * that the incoming URI changed. The object passes this along to the resolver framework,
         * and observers that have registered themselves for the provider are notified.
         */
        getContext().getContentResolver().notifyChange(uri, null);

        // Returns the number of rows updated.
        return count;
    }
}
