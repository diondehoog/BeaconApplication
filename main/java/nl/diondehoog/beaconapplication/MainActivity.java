package nl.diondehoog.beaconapplication;

import android.bluetooth.BluetoothAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private ToggleButton scanToggle;
    private BlueToothController btController;
    private InternetController intController;
    private Map<String, String> bleMessages = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btController = new BlueToothController(this);
        btController.requestBT();
        intController = new InternetController(this);

        intController.readMacAddress();

        initiateScanButton();
    }

    public void updateMessage(String sender, String msg){
        bleMessages.put(sender, msg);
    }

    public BlueToothController getBtController(){
        return btController;
    }

    public Map<String, String> getBleMessages(){
        return bleMessages;
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
                    btController.stopScanning();
                    intController.sendPost();
                }
            }
        });
    }




}