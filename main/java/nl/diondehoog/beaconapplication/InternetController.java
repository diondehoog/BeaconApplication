package nl.diondehoog.beaconapplication;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by DoDoH on 25-Sep-17.
 */

public class InternetController {
    MainActivity mActivity;

    InternetController(MainActivity activity) {
        mActivity = activity;

    }


    // read the MAC addresses from a text file on a website
    public void readMacAddress(){
        final List<String> result = new ArrayList<String>();
        // new thread, so the internet stuff runs in the background
        new Thread(new Runnable(){
            public void run(){
                try {
                    // connect to website
                    URL url = new URL("https://diondehoog.github.io/test.txt");
                    HttpURLConnection conn=(HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(60000);

                    // read textfile
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String str;
                    while ((str = in.readLine()) != null) {
                        System.out.println("Adress found: " + str);
                        mActivity.getBtController().addFilter(str);
                    }

                    // close reader and connection
                    in.close();
                    conn.disconnect();

                } catch (Exception e) {
                    Log.i("Exception found:", e.toString());
                }
            }
        }).start();
    }

    // execute the postrequest
    public void sendPost(){
        new SendPostRequest().execute();
    }


    // sendPostrequest class (asynctask)
    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        // the stuff to do in the background
        protected String doInBackground(String... arg0) {

            try {

                URL url = new URL("http://www.bassaidaidojo.nl/test.php"); // here is your URL path

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("name", "abc");
                postDataParams.put("email", "abc@gmail.com");
                Log.e("params",postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(getPostDataString(postDataParams));
                writer.flush();
                writer.close();

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while((line = reader.readLine()) != null){
                        result.append(line);
                    }

                    return result.toString();

                }
                else {
                    return new String("false : "+responseCode);
                }
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(mActivity, result,
                    Toast.LENGTH_LONG).show();
        }
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }
}
