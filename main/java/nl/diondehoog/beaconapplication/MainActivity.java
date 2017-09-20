package nl.diondehoog.beaconapplication;

import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import android.app.Activity;
import java.util.ArrayList;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter mBluetoothAdapter;
    private final int REQUEST_ENABLE_BT = 1;
    private ToggleButton scanToggle;
    private BluetoothLeScanner bles;
    private final ArrayList<String> urls=new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null){
            Log.i("error", "Bluetooth not supported");
        }
        bles =  mBluetoothAdapter.getBluetoothLeScanner();

        requestBT();
        initiateScanButton();
        readMacAddress();






        /*ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();*/


    }

    // read the MAC addresses from a text file on a website
    private void readMacAddress(){
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
                        urls.add(str);
                    }
                    in.close();
                } catch (Exception e) {
                    Log.i("Exception found:", e.toString());
                }

                //since we are in background thread, to post results we have to go back to ui thread. do the following for that

                MainActivity.this.runOnUiThread(new Runnable(){
                    public void run(){
                        if(urls.size() > 0)
                        Log.i("address:", urls.get(0)); // My TextFile has 3 lines
                    }
                });

            }
        }).start();

    }

    // Request the user to turn on Bluetooth
    private void requestBT(){
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    // Function to start the ble scan
    private void startScanning(){
        bles.startScan(mScanCallback);
    }

    // Function to stop the ble scan
    private void stopScanning(){
        bles.stopScan(mScanCallback);
    }

    // Callback when an activity is finished (asking for bluetooth permission)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_ENABLE_BT:
                if(resultCode == RESULT_OK) {
                    Log.i("Bluetooh", "Enabled");
                } else {
                    Log.i("Bluetooh", "Denied");
                }
        }
    }

    // Set the scan listener to the button
    private void initiateScanButton(){
        scanToggle = (ToggleButton) findViewById(R.id.ScanButton);
        scanToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if(isChecked && mBluetoothAdapter.isEnabled()){
                    startScanning();
                } else if (isChecked && !mBluetoothAdapter.isEnabled()) {
                    scanToggle.setChecked(false);
                    requestBT();
                } else {
                    stopScanning();
                }
            }
        });
    }

    // When the scan finds a ble signal
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result){
            Log.i("callbackType", String.valueOf(callbackType));
            Log.i("result", result.getDevice().getAddress());
        }
    };


}