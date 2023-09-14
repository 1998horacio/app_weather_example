package com.horacioapp.animefuluvu;

import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetWeatherDataTask extends AsyncTask<String[], Void, String[]> {

    private MainActivity activity;
    private String url;
    private XmlPullParserFactory xmlFactoryObject;

    public GetWeatherDataTask(MainActivity activity, String url) {
        this.activity = activity;
        this.url = url;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String[] doInBackground(String[]... params) {
        try {
            URL url = new URL(this.url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000 /* milliseconds */);
            connection.setConnectTimeout(15000 /* milliseconds */);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            InputStream stream = connection.getInputStream();

            xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser myParser = xmlFactoryObject.newPullParser();

            myParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            myParser.setInput(stream, null);
            String[] result = parseXML(myParser);
            stream.close();

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("AsyncTask", "exception");
            return null;
        }
    }

    public String[] parseXML(XmlPullParser myParser) {

        int event;
        String text = null;
        String[] result = new String[5];

        try {
            event = myParser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                String name = myParser.getName();

                switch (event) {
                    case XmlPullParser.START_TAG:
                        break;
                    case XmlPullParser.TEXT:
                        text = myParser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        //get country name
                        if (name.equals("country")) {
                            result[4] = text;
                        } else if (name.equals("humidity")) { //get humidity
                            result[0] = myParser.getAttributeValue(null, "value");

                        } else if (name.equals("pressure")) { //get pressure
                            result[1] = myParser.getAttributeValue(null, "value");

                        } else if (name.equals("temperature")) { //get temperature
                            result[2] = myParser.getAttributeValue(null, "value");

                        } else if (name.equals("coord")) { //get location
                            result[3] = "(" + myParser.getAttributeValue(null, "lat") + " , "
                                    + myParser.getAttributeValue(null, "lon") + ")";
                        }
                        break;
                }
                event = myParser.next();
            }
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String[] result) {
        //call back data to main thread
        activity.callBackData(result);

    }

}