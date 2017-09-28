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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
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


    // readMacAdress class (asynctask)
    public class readMacAdress extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        // the stuff to do in the background
        protected String doInBackground(String... arg0) {

            // instantiate variables
            URL url = null;
            HttpURLConnection conn = null;
            String response = "";

            try {
                // connect to website
                url = new URL("https://diondehoog.github.io/test.txt");
                conn=(HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(15000);

                // read textfile
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String str;
                while ((str = in.readLine()) != null) {
                    System.out.println("Adress found: " + str);
                    mActivity.getBtController().addFilter(str);
                }

                // close reader and connection
                in.close();
            }
            // catch exception
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }
            // disconnect
            finally{
                conn.disconnect();
                return "";
            }
        }
    }


    public void readMacAddress(){new readMacAdress().execute();}

    // execute the postrequest
    public void sendPost(){
        new SendPostRequest().execute();
    }


    // sendPostrequest class (asynctask)
    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        // the stuff to do in the background
        protected String doInBackground(String... arg0) {

            // instantiate variables
            URL url = null;
            HttpURLConnection conn = null;
            String response = "";

            try {
                // set url path
                url = new URL("http://www.bassaidaidojo.nl/test.php"); // here is your URL path

                // put all the values in
                HashMap<String, String> messages = mActivity.getBleMessages();

                // set connection attributes
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // send the post and close the writer
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(HashMapToString(messages));
                System.out.println("Send message: " + HashMapToString(messages));
                writer.flush();
                writer.close();

                // check the responsecode
                int responseCode=conn.getResponseCode();

                // get response or show error
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line=br.readLine()) != null) {
                        response+=line;
                    }
                    return response;
                }
                else {
                    return new String("false : "+responseCode);
                }
            }
            // catch exception
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }
            // disconnect
            finally{
                conn.disconnect();
            }
        }

        // when the post is finished, show the result as a toast
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(mActivity, result,
                    Toast.LENGTH_LONG).show();
        }
    }

    // convert hashmap to a string
    private String HashMapToString(HashMap<String, String> params) {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(entry.getKey());
            result.append("=");
            result.append(entry.getValue());
        }

        return result.toString();
    }
}
