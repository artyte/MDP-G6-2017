package sg.edu.ntu.mdp.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import sg.edu.ntu.mdp.R;
import sg.edu.ntu.mdp.common.AcclerometerSenesorProvider;
import sg.edu.ntu.mdp.common.CommonOperation;
import sg.edu.ntu.mdp.common.Config;
import sg.edu.ntu.mdp.common.Protocol;
import sg.edu.ntu.mdp.fragment.BasicDialogFragment;
import sg.edu.ntu.mdp.fragment.CustomAlertDialogFragment;
import sg.edu.ntu.mdp.fragment.DeviceListDialogFragment;
import sg.edu.ntu.mdp.fragment.LogFragment;
import sg.edu.ntu.mdp.fragment.MazeFragment;
import sg.edu.ntu.mdp.model.arena.Arena;
import sg.edu.ntu.mdp.model.arena.Robot;
import sg.edu.ntu.mdp.service.BluetoothCommService;

public class MainActivity extends AppCompatActivity implements DeviceListDialogFragment.DialogListener, MazeFragment.OnFragmentInteractionListener, LogFragment.OnListFragmentInteractionListener, CompoundButton.OnCheckedChangeListener, AcclerometerSenesorProvider.SensorProvider, CustomAlertDialogFragment.AlertDialogListener, BasicDialogFragment.AlertDialogListener {
    public static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    public static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    public static final int REQUEST_ENABLE_BT = 24;

    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    private BluetoothCommService bluetoothCommService = null;
    BluetoothAdapter btAdapater = null;
    ArrayList<String> logList = new ArrayList<String>();
    private String mConnectedDeviceName = null;
    boolean isShowingLog = false;
    boolean isAccelerometerEnabled = false;
    TextView textViewX;
    TextView textViewY;
    TextView textViewDirection;
    TextView textViewStatus;
    TextView textViewMDFString;
    TextView textViewBattery;
    MazeFragment mazeFragment = null;
    LogFragment logFragment = null;
    ToggleButton tgbStartStop, tgbAutoManual;
    Handler handlerAutoUpdate = new Handler();
    AcclerometerSenesorProvider acclerometerSenesorProvider;

    private void setUpBlueToothCommService() {
        // Initialize the BluetoothChatService to perform bluetooth connections
        bluetoothCommService = new BluetoothCommService(this);
        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");

        // Intent serviceIntent = new Intent(getApplicationContext(),BluetoothCommService.class);
        //startService(serviceIntent);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (isShowingLog) {
            menu.findItem(R.id.menu_hideOrShowLog).setTitle("Hide Log");
        } else {
            menu.findItem(R.id.menu_hideOrShowLog).setTitle("Show Log");
        }

        if (isAccelerometerEnabled) {
            menu.findItem(R.id.menu_use_accelerometer).setTitle("Stop Accelerometer");
        } else {
            menu.findItem(R.id.menu_use_accelerometer).setTitle("Use Accelerometer");

        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        invalidateOptionsMenu();
        setTitle("G6 Robot Remote");
        BluetoothCommService btc = (BluetoothCommService) getLastCustomNonConfigurationInstance();
        if (btc != null)
            bluetoothCommService = btc;
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            mazeFragment = (MazeFragment) getSupportFragmentManager().findFragmentByTag("mazeFragment");
            logFragment = (LogFragment) getSupportFragmentManager().findFragmentByTag("logFragment");
            if (mazeFragment == null)
                mazeFragment = new MazeFragment();
            if (logFragment == null)
                logFragment = new LogFragment();
            transaction.add(R.id.main_fragment, mazeFragment, "mazeFragment");
            transaction.add(R.id.main_fragment, logFragment, "logFragment");
            if (isShowingLog && mazeFragment != null && logFragment != null) {
                transaction.show(logFragment);
                transaction.hide(mazeFragment);

            } else if (!isShowingLog && mazeFragment != null && logFragment != null) {

                transaction.show(mazeFragment);
                transaction.hide(logFragment);
            }
            transaction.commit();
        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            textViewBattery = (TextView) findViewById(R.id.textViewBattery);
            textViewX = (TextView) findViewById(R.id.textViewX);
            textViewY = (TextView) findViewById(R.id.textViewY);
            textViewDirection = (TextView) findViewById(R.id.textViewDirection);
            textViewStatus = (TextView) findViewById(R.id.textViewStatus);
            textViewMDFString = (TextView) findViewById(R.id.textViewMDFString);
            tgbStartStop = (ToggleButton) findViewById(R.id.tgbStartStop);
            tgbStartStop.setOnCheckedChangeListener(this);
            tgbAutoManual = (ToggleButton) findViewById(R.id.tgbAutoManual);
            tgbAutoManual.setOnCheckedChangeListener(this);
        }
        btAdapater = BluetoothAdapter.getDefaultAdapter();

        // Phone does not support Bluetooth so let the user know and exit.
        if (btAdapater == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Not compatible")
                    .setMessage("Your phone does not support Bluetooth")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        acclerometerSenesorProvider= new AcclerometerSenesorProvider(MainActivity.this,getApplicationContext());





    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return bluetoothCommService;
    }

    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.


        if (btAdapater != null) {
            if (!btAdapater.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else if (bluetoothCommService == null) {
                Log.e(Config.log_id, "on start bluetooth service restart");
                setUpBlueToothCommService();


            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_use_accelerometer:
                //start sensor
                if (isAccelerometerEnabled == false) {
                    isAccelerometerEnabled = true;
                    if(acclerometerSenesorProvider!=null)
                        acclerometerSenesorProvider.startSensorUpdate();
                } else {
                    isAccelerometerEnabled = false;
                    if(acclerometerSenesorProvider!=null)
                        acclerometerSenesorProvider.stopSensorUpdate();
                }

                invalidateOptionsMenu();
                break;
            case R.id.menu_connect:
                FragmentManager fm = getSupportFragmentManager();
                DeviceListDialogFragment deviceListDialogFragment = new DeviceListDialogFragment();
                deviceListDialogFragment.setCancelable(true);
                //  deviceListDialogFragment.setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Dialog);
                deviceListDialogFragment.setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_DeviceDefault_Dialog_Alert);
                deviceListDialogFragment.show(fm, "deviceListDialogFragment");

                break;
            case R.id.menu_visability:
                if (btAdapater != null && btAdapater.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                    Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 200);
                    startActivity(discoverableIntent);
                }
                break;
            case R.id.menu_setting:
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);// Activity is started with requestCode 2
                break;
            case R.id.menu_hideOrShowLog:
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                LogFragment logFragment = (LogFragment) getSupportFragmentManager().findFragmentByTag("logFragment");
                MazeFragment mazeFragment = (MazeFragment) getSupportFragmentManager().findFragmentByTag("mazeFragment");
                if (isShowingLog && logFragment != null & mazeFragment != null) {
                    transaction.show(mazeFragment);
                    transaction.hide(logFragment);
                    isShowingLog = false;
                } else if (!isShowingLog && mazeFragment != null && logFragment != null) {

                    transaction.hide(mazeFragment);
                    transaction.show(logFragment);
                    isShowingLog = true;
                }
                transaction.commit();
                invalidateOptionsMenu();

                break;
            case R.id.menu_input_pos:
                intent = new Intent(getApplicationContext(), InputPositionActivity.class);
                startActivityForResult(intent, Config.INPUT_POS_ACTIVITY);// Activity is started with requestCode 2
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras().getString(DeviceListDialogFragment.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        btAdapater = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = btAdapater.getRemoteDevice(address);
        // Attempt to connect to the device
        bluetoothCommService.connect(device);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Config.INPUT_POS_ACTIVITY:
                try {
                    Robot robotInput = (Robot) data.getSerializableExtra("robotInput");
                    if (robotInput != null) {
                        MazeFragment fragment = (MazeFragment) getSupportFragmentManager().findFragmentByTag("mazeFragment");
                        if (fragment != null) {
                            fragment.moveRobot(robotInput.getX(), robotInput.getY(), robotInput.getDirection());
                        }
                        sendRobotPositon(robotInput);
                        //     sendMessage("{\"robotPosition\" : [10, 2, 90]}");
                    }
                } catch (Exception e) {
                }
                break;
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setUpBlueToothCommService();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Toast.makeText(getApplicationContext(), R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    private void sendRobotPositon(Robot robotInput) {
        int robotX, robotY;
        robotX=robotInput.getX()+1;
        robotY=robotInput.getY()+1-17;
        String text=robotX+","+Math.abs(robotY);
        sendMessage(text);
        Log.d(Config.log_id,"robot post json "+text);
    }

    public void btnExplore(View a) {

        sendMessage(Protocol.START_EXPLORATION);
    }

    public void btnFastest(View a) {

        sendMessage(Protocol.START_FASTEST);
    }

    public void btnF1(View a) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String data = sharedPref.getString("pref_f1", Protocol.MOVE_FORWARD);
        sendMessage(data);
        Log.d(Config.log_id, data);
    }

    public void btnF2(View a) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String data = sharedPref.getString("pref_f2", Protocol.TURN_RIGHT);
        sendMessage(data);
        Log.d(Config.log_id, data);

    }

    @Override
    public void onActivityResult(String a) {
    }

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Get extra data included in the Intent
            Log.d(Config.log_id, "Braodcast Messsage received, Mesage type : " + intent.getExtras().getInt(Protocol.MESSAGE_TYPE));
            Message msg = Message.obtain();
            msg.what = intent.getExtras().getInt(Protocol.MESSAGE_TYPE);
            msg.setData(intent.getExtras());
            if (intent.getExtras().getInt(Protocol.MESSAGE_ARG1, -99) != -99) {
                msg.arg1 = intent.getExtras().getInt(Protocol.MESSAGE_ARG1, -99);
            }

            handleMessage(msg);

        }
    };

    public void handleMessage(Message msg) {
        switch (msg.what) {
            case Protocol.MESSAGE_STATE_CHANGE:
                switch (msg.arg1) {
                    case BluetoothCommService.STATE_CONNECTED:
                        setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                        // logList.clear();
                        break;
                    case BluetoothCommService.STATE_CONNECTING:
                        setStatus(R.string.title_connecting);
                        break;
                    case BluetoothCommService.STATE_LISTEN:
                    case BluetoothCommService.STATE_NONE:
                        setStatus(R.string.title_not_connected);
                        break;
                }
                break;
            case Protocol.MESSAGE_WRITE:
                byte[] writeBuf = (byte[]) msg.getData().getByteArray(Protocol.MESSAGE_BUFFER);
                String writeMessage = new String(writeBuf);
                logList.add(0, "Me:  " + writeMessage);

                break;
            case Protocol.MESSAGE_READ:
                LogFragment fragment = (LogFragment) getSupportFragmentManager().findFragmentByTag("logFragment");
                if (fragment != null)
                    fragment.addLog(logList);
                int bytes = msg.getData().getInt(Protocol.MESSAGE_BYTES);
                byte[] buffer = msg.getData().getByteArray(Protocol.MESSAGE_BUFFER);
                String readMessage = new String(buffer, 0, bytes);
                readMessage=readMessage.trim();
                Log.e(Config.log_id, "Protocol.MESSAGE_READ: " + readMessage);
                logList.add(0, mConnectedDeviceName + ":  " + readMessage );

                if(readMessage.startsWith("W")  && readMessage.length()==2 || readMessage.startsWith("W")  && readMessage.length()==3 )
                {

                    try {
                        int noOfMoveFoward = 0;
                        if (readMessage.length() == 2) {
                            noOfMoveFoward = Integer.parseInt(readMessage.substring(1, 2));
                        } else

                        {
                            noOfMoveFoward = Integer.parseInt(readMessage.substring(1, 3));
                        }
                        moveRobot(Robot.Move.UP, noOfMoveFoward);

                    }
                    catch (Exception e)
                    {
                        Log.e(Config.log_id,"Move up multiple error");

                    }

                }else
                if (readMessage.equalsIgnoreCase(Protocol.MOVE_FORWARD)) {
                    moveRobot(Robot.Move.UP);
                    //sendMessage(Protocol.MOVE_FORWARD);
                } else if (readMessage.equalsIgnoreCase(Protocol.TURN_LEFT)) {
                    moveRobot(Robot.Move.LEFT);
                    //sendMessage(Protocol.TURN_LEFT);
                }else if (readMessage.equalsIgnoreCase(Protocol.TURN_RIGHT)) {
                    moveRobot(Robot.Move.RIGHT);
                    //sendMessage(Protocol.TURN_RIGHT);
                }else if(readMessage.startsWith("grid"))
                {
                    handleMDFString(readMessage);
                }

                if (isJSONValid(readMessage)) {
                    handleJson(readMessage);
                }


                break;
            case Protocol.MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(Protocol.DEVICE_NAME);
                new CommonOperation().showToast(getApplicationContext(), "Connected to " + mConnectedDeviceName);
                break;
            case Protocol.MESSAGE_TOAST:
                new CommonOperation().showToast(getApplicationContext(), msg.getData().getString(Protocol.TOAST));

                break;
        }
    }



    private void handleMDFString(String readMessage) {
        String [] text  = readMessage.split(":");
        String mdf1 = text[1];
        String mdf2= text[2];

        MazeFragment fragment = (MazeFragment) getSupportFragmentManager().findFragmentByTag("mazeFragment");
        if (fragment != null) {
            getArena().setMdf1(mdf1);
            getArena().setMdf2(mdf2);
            fragment.gridUpdateMDF1(mdf1);
            fragment.gridUpdateMDF2(mdf1);
        }
        try{
            if(getArena().getMdf1()!=null && getArena().getMdf2()!=null && !getArena().getMdf1().equalsIgnoreCase("") && !getArena().getMdf2().equalsIgnoreCase("") )
            {
                textViewMDFString.setText("MDF1:" +getArena().getMdf1()+"\n"+"MDF2:"+getArena().getMdf2());
            }
        }catch(Exception e)
        {

        }
    }

    private void handleJson(String readMessage) {
        if(readMessage.substring(0,2).equals("10")) { //10 for android
            readMessage = readMessage.substring(2);
            handleRobotBatteryUpdate(readMessage);
            handleRobotPositionUpdate(readMessage);
            handleGridUpdate(readMessage);
            handleMdf1Update(readMessage);
            handleMdf2Update(readMessage);
            handleStatusUpdate(readMessage);
        }
    }

    private void handleRobotBatteryUpdate(String readMessage) {
        String battery = "";
        try {
            JSONObject obj = new JSONObject(readMessage);
            battery = obj.getString("battery");
            textViewBattery.setText(battery);
        } catch (Exception e) {
            Log.e(Config.log_id, e.getMessage());
        }
    }

    private void handleMdf2Update(String readMessage) {
        String gridData = "";
        try {
            JSONObject obj = new JSONObject(readMessage);
            gridData = obj.getString("mdf2");
            getArena().setMdf2(gridData);
            MazeFragment fragment = (MazeFragment) getSupportFragmentManager().findFragmentByTag("mazeFragment");
            if (fragment != null) {
                fragment.gridUpdateMDF1(gridData);
            }

        } catch (Exception e) {
            Log.e(Config.log_id, "handleMdf2Update "+e.getMessage());
        }
    }
    private void handleMdf1Update(String readMessage) {
        String gridData = "";
        try {
            JSONObject obj = new JSONObject(readMessage);
            gridData = obj.getString("mdf1");
            getArena().setMdf1(gridData); //save it
            MazeFragment fragment = (MazeFragment) getSupportFragmentManager().findFragmentByTag("mazeFragment");
            if (fragment != null) {
                fragment.gridUpdateMDF2(gridData);
            }
        } catch (Exception e) {

            Log.e(Config.log_id, "handleMdf1Update "+e.getMessage());
        }
    }

    private void handleStatusUpdate(String readMessage) {

        String status = "";
        try {
            JSONObject obj = new JSONObject(readMessage);
            status = obj.getString("status");
            textViewStatus.setText(status);
            MazeFragment fragment = (MazeFragment) getSupportFragmentManager().findFragmentByTag("mazeFragment");
            if (fragment != null) {
                fragment.statusUpdate(status);
            }
        } catch (Exception e) {

            Log.e(Config.log_id, e.getMessage());
        }
    }

    private void handleGridUpdate(String readMessage) {
        String gridData = "";
        try {
            JSONObject obj = new JSONObject(readMessage);
            gridData = obj.getString("grid");
            MazeFragment fragment = (MazeFragment) getSupportFragmentManager().findFragmentByTag("mazeFragment");
            if (fragment != null) {
                fragment.gridUpdate(gridData);
            }
        } catch (Exception e) {
            Log.e(Config.log_id, e.getMessage());
        }


    }

    private void handleRobotPositionUpdate(String readMessage) {
        try {
            JSONObject obj = new JSONObject(readMessage);
            int x = (int) obj.getJSONArray("robotPosition").get(0);
            int y = (int) obj.getJSONArray("robotPosition").get(1);
            int direction = (int) obj.getJSONArray("robotPosition").get(2);
            MazeFragment fragment = (MazeFragment) getSupportFragmentManager().findFragmentByTag("mazeFragment");
            if (fragment != null) {
                if (direction == 0)
                    fragment.moveRobot(x, y, 1);
                else if (direction == 90)
                    fragment.moveRobot(x, y, 2);
                else if (direction == 180)
                    fragment.moveRobot(x, y, 3);
                else if (direction == 270)
                    fragment.moveRobot(x, y, 0);
            }
        } catch (Exception e) {
            Log.e(Config.log_id, e.getMessage());
        }
    }


    public boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(BluetoothCommService.ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, intentFilter);
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (bluetoothCommService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            // Start the Bluetooth chat services

            if (bluetoothCommService.getState() == BluetoothCommService.STATE_NONE) {
                // Start the Bluetooth chat services
                bluetoothCommService.start();
            }
            if (bluetoothCommService.getState() == BluetoothCommService.STATE_LISTEN) {

                setStatus(R.string.title_not_connected);
            } else if (bluetoothCommService.getState() == BluetoothCommService.STATE_CONNECTED) {

                setStatus(R.string.title_not_connected);
                setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
            }


        } else {
            LogFragment fragment = (LogFragment) getSupportFragmentManager().findFragmentByTag("logFragment");
            if (fragment != null)
                fragment.addLog(logList);
        }


        setupScheduler();
        if (isAccelerometerEnabled)
        {
            if(acclerometerSenesorProvider!=null)
                acclerometerSenesorProvider.startSensorUpdate();
        }
    }

    private void setupScheduler() {
        if (tgbAutoManual != null && tgbAutoManual.isChecked() == true) {
            Runnable myRunnable = new Runnable() {
                public void run() {
                    Log.d(Config.log_id,"Auto Update grid runnable execution");
                    sendMessage(Protocol.SEND_ARENA);
                    //prepare and send the data here..
                    handlerAutoUpdate.removeCallbacks(null);
                    handlerAutoUpdate.postDelayed(this, Config.GRID_AUTO_UPDATE_TIME);
                }
            };
            handlerAutoUpdate.postDelayed(myRunnable, Config.GRID_AUTO_UPDATE_TIME);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        moveTaskToBack(true);
        //super.onBackPressed();
    }


    private void setStatus(CharSequence subTitle) {

        Log.d(Config.log_id, "state change " + subTitle);
        getSupportActionBar().setSubtitle(subTitle);
    }

    private void setStatus(int resId) {

        Log.d(Config.log_id, "state change " + resId);
        getSupportActionBar().setSubtitle(resId);
    }

    @Override
    protected void onPause() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        super.onPause();
        removeSchedulerCallBack();

        if(acclerometerSenesorProvider!=null)
            acclerometerSenesorProvider.stopSensorUpdate();
    }

    public void removeSchedulerCallBack() {
        try {
            handlerAutoUpdate.removeCallbacksAndMessages(null);
        } catch (Exception e) {

        }

    }

    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (bluetoothCommService.getState() != BluetoothCommService.STATE_CONNECTED) {
            new CommonOperation().showToast(getApplicationContext(), getResources().getString(R.string.not_connected));
            return;
        }
        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            bluetoothCommService.write(send);
            mOutStringBuffer = new StringBuffer("");
            mOutStringBuffer.setLength(0);
            Log.d(Config.log_id, "send message: " + message);
        }
    }

    @Override
    public void onUiUpdate(Arena arena) {
        if (arena != null && arena.getRobot() != null) {
            if (textViewX != null)
                textViewX.setText(arena.getRobot().getX() + "");
            if (textViewY != null)
                textViewY.setText(arena.getRobot().getY() + "");
            if (textViewDirection != null) {
                if(arena.getRobot().getDirection()==0)
                    textViewDirection.setText("260");
                else
                if(arena.getRobot().getDirection()==1)
                    textViewDirection.setText("0");
                else
                if(arena.getRobot().getDirection()==2)
                    textViewDirection.setText("90");
                else
                if(arena.getRobot().getDirection()==3)
                    textViewDirection.setText("180");
            }
            if (textViewStatus != null)
                textViewStatus.setText(arena.getRobot().getStatus());
            if (tgbStartStop != null) {
                if (arena.isStarted()) {
                    tgbStartStop.setChecked(true);
                } else
                    tgbStartStop.setChecked(false);
            }
            if (tgbAutoManual != null) {
                if (arena.isAuto()) {
                    tgbAutoManual.setChecked(true);
                } else
                    tgbAutoManual.setChecked(false);
            }
        }
        invalidateOptionsMenu();


    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putStringArrayList("logList", logList);
        savedInstanceState.putString("mConnectedDeviceName", mConnectedDeviceName);
        savedInstanceState.putBoolean("isShowingMaze", isShowingLog);
        savedInstanceState.putBoolean("isAccelerometerEnabled", isAccelerometerEnabled);

        //Save the fragment's instance
        if (mazeFragment != null)
            getSupportFragmentManager().putFragment(savedInstanceState, "mContent", mazeFragment);
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        logList = savedInstanceState.getStringArrayList("logList");
        mConnectedDeviceName = savedInstanceState.getString("mConnectedDeviceName");
        isShowingLog = savedInstanceState.getBoolean("isShowingMaze");
        isAccelerometerEnabled = savedInstanceState.getBoolean("isAccelerometerEnabled");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bluetoothCommService != null) {
            //bluetoothCommService.stop();
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton.getId() == R.id.tgbStartStop) {
            Arena arena = getArena();
            if(arena!=null) {
                if (compoundButton.isChecked()) {
                    if ( arena.isStarted() == false) {
                        compoundButton.setChecked(false);
                        DialogFragment startRobotDialogFragment = new CustomAlertDialogFragment();
                        Bundle args = new Bundle();
                        args.putString("title", "Start");
                        args.putString("message", "Do you wish to start the robot?");
                        startRobotDialogFragment.setArguments(args);
                        startRobotDialogFragment.show(getSupportFragmentManager(), "startRobotDialogFragment");
                    }

                } else {
                    if ( arena.isStarted() == true) {
                        compoundButton.setChecked(true);
                        DialogFragment startRobotDialogFragment = new CustomAlertDialogFragment();
                        Bundle args = new Bundle();
                        args.putString("title", "Stop");
                        args.putString("message", "Do you wish to stop the robot?");
                        startRobotDialogFragment.setArguments(args);
                        startRobotDialogFragment.show(getSupportFragmentManager(), "startRobotDialogFragment");

                    }
                }
            }

        } else if (compoundButton.getId() == R.id.tgbAutoManual) {
            Arena arena = getArena();
            if (arena != null) {

                if (compoundButton.isChecked()) {
                    if(arena.isStarted()==false)
                    {   compoundButton.setChecked(false);
                        DialogFragment basicDialogFragment = new BasicDialogFragment();
                        Bundle args = new Bundle();
                        args.putString("title", "Error");
                        args.putString("message", "You are not allowed to switch to auto mode until you have started the robot");
                        basicDialogFragment.setArguments(args);
                        basicDialogFragment.show(getSupportFragmentManager(), "basicDialogFragment");
                    }else
                    if (arena != null && arena.isAuto() == false ) {
                        compoundButton.setChecked(false);
                        DialogFragment autoManualDialogFragment = new CustomAlertDialogFragment();
                        Bundle args = new Bundle();
                        args.putString("title", "Auto");
                        args.putString("message", "Do you wish to switch to auto update mode?");
                        autoManualDialogFragment.setArguments(args);
                        autoManualDialogFragment.show(getSupportFragmentManager(), "autoManualDialogFragment");
                    }

                } else {
                    if (arena != null && arena.isAuto() == true) {
                        compoundButton.setChecked(true);
                        DialogFragment autoManualDialogFragment = new CustomAlertDialogFragment();
                        Bundle args = new Bundle();
                        args.putString("title", "Manual");
                        args.putString("message", "Do you wish to switch to manual update mode?");
                        autoManualDialogFragment.setArguments(args);
                        autoManualDialogFragment.show(getSupportFragmentManager(), "autoManualDialogFragment");
                    }
                }
            }

        }
    }
    public void btnSendGridUpdate() {
        sendMessage(Protocol.SEND_ARENA);
        new CommonOperation().showToast(getApplicationContext(), "Grid data requested");
    }


    @Override
    public void onAcclerometerChanged(String move) {
        Arena arena= getArena();
        if(arena!=null)
        {
            Robot.Move robotMove=null;
            if(move.equalsIgnoreCase(Protocol.MOVE_FORWARD)) {
                robotMove=Robot.Move.UP;
            }else
            if(move.equalsIgnoreCase(Protocol.TURN_LEFT)) {
                robotMove=Robot.Move.LEFT;
            }else
            if(move.equalsIgnoreCase(Protocol.TURN_RIGHT)) {
                robotMove=Robot.Move.RIGHT;
            }
            Boolean isSafe = arena.checkObstacles(robotMove);
            if(isSafe)
                sendMessage(move);
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        if(dialog.getTag().equalsIgnoreCase("startRobotDialogFragment"))
        {   Arena arena=getArena();
            if (arena != null && tgbStartStop != null) {
                if (arena.isStarted() == false ) {
                    arena.setStarted(true);
                    tgbStartStop.setChecked(true);
                }else  if (arena.isStarted() == true && tgbStartStop != null) {
                    arena.setStarted(false);
                    tgbStartStop.setChecked(false);
                    //reset stuff
                    isAccelerometerEnabled=false;
                    arena.reset();
                    removeSchedulerCallBack();
                    if(acclerometerSenesorProvider!=null)
                        acclerometerSenesorProvider.stopSensorUpdate();
                    mazeFragment = (MazeFragment) getSupportFragmentManager().findFragmentByTag("mazeFragment");
                    if (mazeFragment != null) {
                        mazeFragment.resetArenaView();
                    }
                    invalidateOptionsMenu();

                }

            }
        }
        else if(dialog.getTag().equalsIgnoreCase("autoManualDialogFragment"))
        {  Arena arena=getArena();
            if (arena != null && tgbAutoManual != null) {
                if (arena.isAuto() == false ) {
                    arena.setAuto(true);
                    tgbAutoManual.setChecked(true);
                    setupScheduler();
                }else  if (arena.isAuto() == true && tgbAutoManual != null) {
                    arena.setAuto(false);
                    tgbAutoManual.setChecked(false);
                    removeSchedulerCallBack();
                }

            }
        }

        dialog.dismiss();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        if(dialog.getTag().equalsIgnoreCase("startRobotDialogFragment"))
        {   Arena arena=getArena();
            if (arena != null && tgbStartStop != null) {
                if (arena.isStarted() == false ) {
                    tgbStartStop.setChecked(false);
                }else  if (arena.isStarted() == true && tgbStartStop != null) {
                    tgbStartStop.setChecked(true);
                }
            }
        }else
        if(dialog.getTag().equalsIgnoreCase("autoManualDialogFragment"))
        { Arena arena=getArena();
            if (arena != null && tgbAutoManual != null) {
                if (arena.isAuto() == false ) {
                    tgbAutoManual.setChecked(false);
                }
                else
                if (arena.isAuto() == true ) {
                    tgbAutoManual.setChecked(true);

                }


            }

        }
        dialog.dismiss();

    }
    public Arena getArena()
    {
        MazeFragment fragment = (MazeFragment) getSupportFragmentManager().findFragmentByTag("mazeFragment");
        if (fragment != null) {
            return fragment.getArena();
        }else return null;
    }


    private void moveRobot(Robot.Move move, int noOfMove) {

        for(int i=0;i<noOfMove;i++)
        {
            Log.e(Config.log_id," i");
            moveRobot(move);

        }
    }
    public void moveRobot(Robot.Move move) {
        try {
            MazeFragment fragment = (MazeFragment) getSupportFragmentManager().findFragmentByTag("mazeFragment");
            if (fragment != null) {
                fragment.btnMove(move);
            }
        }
        catch ( Exception e)
        {
            Log.e(Config.log_id,e.getMessage());

        }
    }
}