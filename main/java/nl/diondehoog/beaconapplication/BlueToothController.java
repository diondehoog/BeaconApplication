package nl.diondehoog.beaconapplication;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DoDoH on 20-Sep-17.
 */

public class BlueToothController {
    private BluetoothLeScanner bles;
    private final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter mBluetoothAdapter;
    private MainActivity mActivity;
    private ScanSettings settings;
    private List<ScanFilter> filters = null;

    BlueToothController(MainActivity activity){
        this.settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                //.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .build();

        mActivity =activity;
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
            mActivity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    // Function to start the ble scan
    public void startScanning(){
        System.out.println("Started scanning");
        if(bles == null){
            bles =  mBluetoothAdapter.getBluetoothLeScanner();
        }
        bles.startScan(filters, settings, mScanCallback);
    }

    // Function to stop the ble scan
    public void stopScanning(){
        System.out.println("Stopped scanning");
        if(bles != null) {
            bles.stopScan(mScanCallback);
        }
    }

    // When the scan finds a ble signal
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result){
            System.out.println("ble Found: " + result.getDevice().getAddress());
            mActivity.updateMessage(result.getDevice().getAddress(), "test");
        }
    };

    // add a filter
    public void addFilter(String filter){
        if(filters == null){
            filters = new ArrayList<ScanFilter>();
        }
        ScanFilter sf = new ScanFilter.Builder().setDeviceAddress(filter).build();
        filters.add(sf);
        System.out.println("Filter added: " + filter);
    }
}
