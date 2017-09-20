package nl.diondehoog.beaconapplication;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.util.Log;

/**
 * Created by DoDoH on 20-Sep-17.
 */

public class BlueToothController {
    private BluetoothLeScanner bles;
    private final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter mBluetoothAdapter;
    Activity activity;

    BlueToothController(Activity activity){
        this.activity=activity;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null){
            Log.i("error", "Bluetooth not supported");
        }
        bles =  mBluetoothAdapter.getBluetoothLeScanner();
    }

    public BluetoothAdapter getBTAdapter(){
        return mBluetoothAdapter;
    }

    // Request the user to turn on Bluetooth
    public void requestBT(){
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    // Function to start the ble scan
    public void startScanning(){
        if(bles == null){
            bles =  mBluetoothAdapter.getBluetoothLeScanner();
        }
        bles.startScan(mScanCallback);
    }

    // Function to stop the ble scan
    public void stopScanning(){
        if(bles != null) {
            bles.stopScan(mScanCallback);
        }
    }

    // When the scan finds a ble signal
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result){
            Log.i("callbackType", String.valueOf(callbackType));
            Log.i("result", result.getDevice().getAddress());
        }
    };

    // Callback when an activity is finished (asking for bluetooth permission)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_ENABLE_BT:
                if(resultCode == activity.RESULT_OK) {
                    Log.i("Bluetooh", "Enabled");
                } else {
                    Log.i("Bluetooh", "Denied");
                }
        }
    }
}
