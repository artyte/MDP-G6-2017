package com.example.artyte.robotremote;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;

public class MainActivity extends Activity {

    private ListView btListView;
    private ArrayList<String> mDeviceList = new ArrayList<String>();
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();;
    private IntentFilter filter = new IntentFilter();
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mDeviceList.add(device.getName() + "\n" + device.getAddress());
                btListView.setAdapter(new ArrayAdapter<String>(context,
                        android.R.layout.simple_list_item_1, mDeviceList));
            }
            else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR) == BluetoothAdapter.STATE_ON) {
                    mBluetoothAdapter.startDiscovery();
                }
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btListView = (ListView) findViewById(R.id.btListView);

        mBluetoothAdapter.startDiscovery();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

        btListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, RemoteMain.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    public void refreshPage(MenuItem item) {
        if(btListView.getAdapter() != null) {
            ((ArrayAdapter) btListView.getAdapter()).clear();
            ((ArrayAdapter) btListView.getAdapter()).notifyDataSetChanged();
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothAdapter.startDiscovery();
        }
    }

    public void onOffBT(MenuItem item) {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
        else {
            mBluetoothAdapter.disable();
        }
    }
}
