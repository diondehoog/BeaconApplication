package nl.diondehoog.beaconapplication;

import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by DoDoH on 25-Sep-17.
 */

public class InternetController {
    MainActivity mActivity;
    private int sendPostDelay = 10000; //milliseconds
    private int readMACDelay = 300000; // milliseconds
    String postAddress = "http://www.bassaidaidojo.nl/test.php";
    String macAddress = "";//"https://diondehoog.github.io/test.txt";
    Timer PostTimer;
    Timer MACTimer;
    boolean firstPOSTscan = true;

    InternetController(MainActivity activity) {
        mActivity = activity;
    }

    // execute the readMacAddress
    public void readMacAddress(){new readMacAdress().execute();}

    // execute the postrequest
    public void sendPost(){
        if(firstPOSTscan){
            firstPOSTscan = false;
        } else {
            new SendPostRequest().execute();
        }
    }

    public void setPostAddress(String address){
        postAddress = address;
    }

    public void setMacAddress(String address){
        macAddress = address;
    }


    // readMacAdress class (asynctask)
    public class readMacAdress extends AsyncTask<String, Void, String> {

        // the stuff to do in the background
        protected String doInBackground(String... arg0) {

            // instantiate variables
            URL url = null;
            HttpURLConnection conn = null;
            String response = "";

            try {
                // connect to website
                url = new URL(macAddress);
                conn=(HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(15000);
                conn.connect();

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
            // catch exceptions
            catch(Exception e){
                System.out.println("Exception found: " + e.getMessage());
                Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_LONG).show();
            }
            // disconnect
            finally{
                if(conn != null) {
                    conn.disconnect();
                }
                return response;
            }
        }

        protected void onPostExecute(String result){
            System.out.println("MAC addresses read");
        }
    }

    public void startTimers(){
        firstPOSTscan = true;
        startPostTimer();
        startMACTimer();
    }

    public void stopTimers(){
        stopPostTimer();
        stopMACTimer();
    }

    private void stopPostTimer(){
        PostTimer.cancel();
        PostTimer = null;
    }

    private void stopMACTimer(){
        MACTimer.cancel();
        MACTimer = null;
    }

    private void startPostTimer(){
        PostTimer = new Timer();
        PostTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    sendPost();

                }
            }, 0, sendPostDelay);
    }

    private void startMACTimer(){
        MACTimer = new Timer();
        MACTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                readMacAddress();

            }
        }, 0, readMACDelay);
    }

    // sendPostrequest class (asynctask)
    public class SendPostRequest extends AsyncTask<String, Void, String> {

        // the stuff to do in the background
        protected String doInBackground(String... arg0) {

            // instantiate variables
            URL url = null;
            HttpURLConnection conn = null;
            String response = "";

            try {

                // set url path
                url = new URL(postAddress);

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
                } else {
                    response ="false : "+responseCode;
                }
            }
            // catch exception
            catch(Exception e){
                Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_LONG).show();
                response = "Exception: " + e.getMessage();
            }
            // disconnect
            finally{
                conn.disconnect();
                return response;
            }
        }

        // when the post is finished, show the result as a toast
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(mActivity, result,
                    Toast.LENGTH_SHORT).show();
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