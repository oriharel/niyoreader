package com.niyo.reader.app.data;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.niyo.reader.app.ServiceCaller;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by oriharel on 6/7/14.
 */
public class InsertXmlToProvider extends AsyncTask<String, Void, Integer> {

    public static final String LOG_TAG = InsertXmlToProvider.class.getSimpleName();
    private Context _context;
    private XmlPullParserFactory _xmlFactory;
    private ServiceCaller _caller;

    public InsertXmlToProvider(Context context, ServiceCaller caller) {
        _context = context;
        _caller = caller;
        try {
            _xmlFactory = XmlPullParserFactory.newInstance();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Integer doInBackground(String... params) {

        String fileName = params[0];
        Integer result = 1;
        Log.d(LOG_TAG, "starting processing file " + fileName);

        try {
            InputStream istr = _context.getAssets().open(fileName);
            _xmlFactory.setNamespaceAware(true);
            XmlPullParser parser = _xmlFactory.newPullParser();
            parser.setInput(istr, "UTF-8");
            int eventType = parser.getEventType();
            String groupTitle = "";

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {
                    Log.d(LOG_TAG, "starting "+fileName+" parsing");
                }
                else if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("outline")) {
                        String xmlUrl = parser.getAttributeValue(null, "xmlUrl");
                        String feedTitle = parser.getAttributeValue(null, "title");
                        if (!TextUtils.isEmpty(xmlUrl)) {
                            ContentValues values = new ContentValues();
                            values.put(FeedsTableColumn.TITLE, feedTitle);
                            values.put(FeedsTableColumn.XML_URL, xmlUrl);
                            values.put(FeedsTableColumn.FEED_GROUP, groupTitle);

                            Log.d(LOG_TAG, "inserting "+feedTitle+" into provider");
                            Uri insertResult = _context.getContentResolver().insert(NiyoReader.FEEDS_URI, values);
                            Log.d(LOG_TAG, "added a friend name "+feedTitle+" result was "+insertResult.toString());
                            result = 0;
                        }
                        else {
                            groupTitle = parser.getAttributeValue(null, "title");
                        }
                    }
                }
                eventType = parser.next();
            }

        } catch (IOException e) {
            e.printStackTrace();
            result = 1;
        }
         catch (XmlPullParserException e) {
            e.printStackTrace();
             result = 1;
        }

        return result;

    }

    @Override
    protected void onPostExecute(Integer result) {
        Log.d(LOG_TAG, "feeds db insertion succeeded");

        if (result == 0) {
            _caller.success(result);
        }
        else {
            _caller.failure(result, "Something went wrong during feed insertion");
        }


    }
}
