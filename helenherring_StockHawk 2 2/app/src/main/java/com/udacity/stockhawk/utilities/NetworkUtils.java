package com.udacity.stockhawk.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by helenherring on 2/17/17.
 */

public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String STOCK_GRAPH_BASE_URL = "https://chart.finance.yahoo.com/z?";



    /* The format we want our API to return */
    private static final int page = 1;
    private static final String time = "6m";
    private static final String end = "l";
    private static final String endDate = "on";
    private static final String size = "m50,m200";
    private static final String include_video = "false";

    final static String STOCK_PARAM = "s";
    final static String TIME_PARAM = "t";
    final static String END_PARAM = "q";
    final static String END_PARAM_2 = "l";
    final static String SIZE_PARAM = "p";

    public static String getUrlForGraph(String stock) {

        Uri builtUri = Uri.parse(STOCK_GRAPH_BASE_URL).buildUpon()
                .appendQueryParameter(STOCK_PARAM, stock)
                .appendQueryParameter(TIME_PARAM, time)
                .appendQueryParameter(END_PARAM, end)
                .appendQueryParameter(END_PARAM_2, endDate)
                .appendQueryParameter(SIZE_PARAM, size)
                .build();

        return builtUri.toString();
    }




    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        //System.out.println("Attempting to get HTTP response");

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        //System.out.println("URL connection made");

        try {
            //System.out.println("In try segment");
            InputStream in = urlConnection.getInputStream();

            //System.out.println("Got input stream");
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            //System.out.println("Used scanner delimiter");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                //System.out.println("Returning http response.");
                return scanner.next();

            } else {
                //System.out.println("Returning null");
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
