package nl.diondehoog.beaconapplication;

import android.app.Activity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DoDoH on 25-Sep-17.
 */

public class InternetController {
    MainActivity mActivity;

    InternetController(MainActivity activity) {
        mActivity = activity;

    }


    // read the MAC addresses from a text file on a website
    public List<String> readMacAddress(){
        final List<String> result = new ArrayList<String>();
        // new thread, so the internet stuff runs in the background
        new Thread(new Runnable(){
            public void run(){
                try {
                    URL url = new URL("https://diondehoog.github.io/test.txt");
                    HttpURLConnection conn=(HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(60000);

                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    String str;

                    while ((str = in.readLine()) != null) {
                        result.add(str);
                        Log.i("address:", str);
                    }
                    in.close();

                } catch (Exception e) {
                    Log.i("Exception found:", e.toString());
                }
            }
        }).start();

        return result;
    }
}
