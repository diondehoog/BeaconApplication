package nl.diondehoog.beaconapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {
    private ToggleButton scanToggle;
    private BlueToothController btController;
    private InternetController intController;
    private HashMap<String, String> bleMessages = new HashMap<String, String>();

    // when the activity is started
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

    // add a found message to the messages
    public void updateMessage(String sender, String msg){
        System.out.println("Adding message: " + msg);
        bleMessages.put(sender, msg);
    }

    // return the bluetoothcontroller
    public BlueToothController getBtController(){
        return btController;
    }

    public InternetController getIntController() { return intController; }

    // return the found ble messages
    public HashMap<String, String> getBleMessages(){
        System.out.println("Returning Messages" + bleMessages);
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