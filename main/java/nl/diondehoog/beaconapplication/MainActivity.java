package nl.diondehoog.beaconapplication;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import org.json.JSONObject;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private ToggleButton scanToggle;
    private BlueToothController btController;
    private InternetController intController;
    private Map<String, String> bleMessages = new HashMap<String, String>();
    private List<String> filters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btController = new BlueToothController(this);
        btController.requestBT();
        intController = new InternetController(this);

        initiateScanButton();

        filters = intController.readMacAddress();
        btController.setFilters(filters);
        btController.printFilters();
    }

    public void updateMessage(String sender, String msg){
        bleMessages.put(sender, msg);
    }



    // Set the scan listener to the button
    private void initiateScanButton(){
        scanToggle = (ToggleButton) findViewById(R.id.ScanButton);
        scanToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if(isChecked && btController.getBTAdapter().isEnabled()){
                    btController.startScanning();
                } else if (isChecked && !btController.getBTAdapter().isEnabled()) {
                    scanToggle.setChecked(false);
                    btController.requestBT();
                } else {
                    btController. stopScanning();
                }
            }
        });
    }




}