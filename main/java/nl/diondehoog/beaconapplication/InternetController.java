package nl.diondehoog.beaconapplication;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
                    URL url = new URL("https://diondehoog.github.io/test.txt");
                    HttpURLConnection conn=(HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(60000);

                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    String str;

                    while ((str = in.readLine()) != null) {
                        mActivity.getBtController().addFilter(str);
                    }
                    in.close();
                    conn.disconnect();

                } catch (Exception e) {
                    Log.i("Exception found:", e.toString());
                }
            }
        }).start();
    }


    public void sendPost(){
        new SendPostRequest().execute();
    }



    /* PHP should be of form
    * <?php
    * $key = $_POST['key'];
    * $key = $_POST['key'];
    * print_r(json_encode($_POST));
    * ?>*/

    public class SendPostRequest extends AsyncTask<String, Void, String> {


        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try{
                URL url = new URL("https://studytutorial.in/post.php");

                JSONObject postDataParams = new JSONObject();
                Map<String, String> messages = mActivity.getBleMessages();
                for(String key: messages.keySet()){
                    postDataParams.put(key, messages.get(key));
                }

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    conn.disconnect();
                    return sb.toString();

                } else {
                    conn.disconnect();
                    return new String("false : "+responseCode);
                }



            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(mActivity, result, Toast.LENGTH_LONG).show();
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
