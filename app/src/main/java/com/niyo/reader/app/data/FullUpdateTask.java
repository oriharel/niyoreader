package com.niyo.reader.app.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by oriharel on 6/15/14.
 */
public class FullUpdateTask extends AsyncTask<Void, Void, Void > {

    public static final String LOG_TAG = FullUpdateTask.class.getSimpleName();

    private Context _context;
    private XmlPullParserFactory _xmlFactory;

    public FullUpdateTask(Context context) {

        _context = context;

        try {
            _xmlFactory = XmlPullParserFactory.newInstance();
            _xmlFactory.setNamespaceAware(true);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected Void doInBackground(Void... voids) {

        String select = "((" + FeedsTableColumn.XML_URL + " NOTNULL) AND ("
                + FeedsTableColumn.XML_URL + " != '' ))";

        Cursor cursor = _context.getContentResolver().query(NiyoReader.FEEDS_URI, NiyoReader.FEEDS_SUMMARY_PROJECTION,
                select, null, FeedsTableColumn.XML_URL + " COLLATE LOCALIZED ASC");

        cursor.moveToFirst();

        while(!cursor.isLast()) {

            String xmlUrl = cursor.getString(2);
            Log.d(LOG_TAG, "going to call for " + xmlUrl);
            URL url = null;
            try {
                url = new URL(xmlUrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                int sc = con.getResponseCode();
                if (sc == 200) {
                    InputStream is = con.getInputStream();
                    processXmlAndInsert(is, xmlUrl);
                    is.close();
                } else if (sc == 401) {
                    Log.e(LOG_TAG, "Server auth error, please try again.");
                } else {
                    String msg = "Server returned the following error code: " + sc;
                    Log.e(LOG_TAG, msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            cursor.moveToNext();

        }

        return null;
    }

    private void processXmlAndInsert(InputStream feedXmlAsIs, String xmlUrl) {

        XmlPullParser parser;
        try {
            parser = _xmlFactory.newPullParser();
            FeedItem newItem = null;

            parser.setInput(feedXmlAsIs, "UTF-8");
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {
                    Log.d(LOG_TAG, "starting "+xmlUrl+" parsing");
                }
                else if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("item")) {
                        newItem = new FeedItem();
                    }
                    else if (newItem != null) {
                        if (parser.getName().equals("title")) {
                            newItem.setFeedTitle(parser.getText());
                            Log.d(LOG_TAG, "got feed with title "+newItem.getFeedTitle());
                        }
                    }

                }
                else if (eventType == XmlPullParser.TEXT) {
                    if (parser.getName().equals("item")) {
                        newItem = new FeedItem();
                    }
                    else if (newItem != null) {
                        if (parser.getName().equals("title")) {
                            newItem.setFeedTitle(parser.getText());
                            Log.d(LOG_TAG, "got feed with title "+newItem.getFeedTitle());
                        }
                    }

                }
                eventType = parser.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
