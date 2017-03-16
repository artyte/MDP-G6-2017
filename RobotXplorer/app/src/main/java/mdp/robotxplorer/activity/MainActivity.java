package mdp.robotxplorer.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;

import mdp.robotxplorer.R;
import mdp.robotxplorer.arena.Arena;
import mdp.robotxplorer.arena.Robot;
import mdp.robotxplorer.common.Config;
import mdp.robotxplorer.common.Operation;
import mdp.robotxplorer.common.Protocol;
import mdp.robotxplorer.fragment.BasicDialogFragment;
import mdp.robotxplorer.fragment.CustomAlertDialogFragment;
import mdp.robotxplorer.fragment.DeviceListDialogFragment;
import mdp.robotxplorer.fragment.LogFragment;
import mdp.robotxplorer.fragment.MazeFragment;
//import mdp.robotxplorer.sensor.AccelerometerSensor;
import mdp.robotxplorer.service.BluetoothCommService;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        CompoundButton.OnCheckedChangeListener,
        MazeFragment.OnFragmentInteractionListener,
        LogFragment.OnListFragmentInteractionListener,
        DeviceListDialogFragment.DialogListener,
        CustomAlertDialogFragment.AlertDialogListener,
        BasicDialogFragment.AlertDialogListener {

    public static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    public static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    public static final int REQUEST_ENABLE_BT = 24;

    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    private BluetoothCommService bluetoothCommService = null;
    BluetoothAdapter btAdapter = null;
    ArrayList<String> logList = new ArrayList<String>();
    private String mConnectedDeviceName = null;
    private String mdf1 = "", mdf2 = "";
    boolean isShowingLog = false;
    //boolean isAccelerometerEnabled = false;

    TextView textViewX, textViewY, textViewDirection, textViewStatus;
    //Switch swArenaStart, swAutoGridUpdate, swUseAccelerometer;

    MazeFragment mazeFragment = null;
    LogFragment logFragment = null;
    Handler handlerAutoUpdate = new Handler();
    //AccelerometerSensor accelerometerSensorProvider;

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            Log.d(Config.log_id, "Broadcast Message received, Message type : " + intent.getExtras().getInt(Protocol.MESSAGE_TYPE));
            Message msg = Message.obtain();
            msg.what = intent.getExtras().getInt(Protocol.MESSAGE_TYPE);
            msg.setData(intent.getExtras());

            if (intent.getExtras().getInt(Protocol.MESSAGE_ARG1, -99) != -99) {
                msg.arg1 = intent.getExtras().getInt(Protocol.MESSAGE_ARG1, -99);
            }

            handleMessage(msg);
        }
    };

    protected void addMDF(View a) {
        logList.add(mdf1);
        logList.add(mdf2);
        LogFragment fragment = (LogFragment) getSupportFragmentManager().findFragmentByTag("logFragment");
        fragment.addLog(logList);
        Operation.showToast(this, "mdf strings added to log!");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //navigationView.getMenu().findItem(R.id.sw_auto_grid_update).setActionView(new Switch(this));
        //navigationView.getMenu().findItem(R.id.sw_use_accelerometer).setActionView(new Switch(this));

        textViewX = (TextView) findViewById(R.id.textViewX);
        textViewY = (TextView) findViewById(R.id.textViewY);

        textViewDirection = (TextView) findViewById(R.id.textViewDirection);
        textViewStatus = (TextView) findViewById(R.id.textViewStatus);
        //textViewBattery = (TextView) findViewById(R.id.textViewBattery);

        /*MenuItem miArenaStart = navigationView.getMenu().findItem(R.id.sw_arena_start);
        swArenaStart = (Switch) findViewById(R.id.sw_arena_start);
        swArenaStart.setOnCheckedChangeListener(this);
        miArenaStart.setActionView(swArenaStart);*/

        /*MenuItem miAutoGridUpdate = navigationView.getMenu().findItem(R.id.sw_auto_grid_update);
        swAutoGridUpdate = new Switch(this);
        swAutoGridUpdate.setOnCheckedChangeListener(this);
        miAutoGridUpdate.setActionView(swAutoGridUpdate);*/

        /*MenuItem miUseAccelerometer = navigationView.getMenu().findItem(R.id.sw_use_accelerometer);
        swUseAccelerometer = new Switch(this);
        swUseAccelerometer.setOnCheckedChangeListener(this);
        miUseAccelerometer.setActionView(swUseAccelerometer);*/

        BluetoothCommService btc = (BluetoothCommService) getLastCustomNonConfigurationInstance();

        if (btc != null)
            bluetoothCommService = btc;

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            mazeFragment = (MazeFragment) getSupportFragmentManager().findFragmentByTag("mazeFragment");
            logFragment  = (LogFragment)  getSupportFragmentManager().findFragmentByTag("logFragment");

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

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        // Phone does not support Bluetooth so let the user know and exit.
        if (btAdapter == null) {
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

        //accelerometerSensorProvider = new AccelerometerSensor(MainActivity.this, getApplicationContext());
    }

    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.

        if (btAdapter != null) {
            if (!btAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

            } else if (bluetoothCommService == null) {
                Log.e(Config.log_id, "on start bluetooth service restart");
                setupBlueToothCommService();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter("BlueToothLocalBroadcast");
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, intentFilter);
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.

        if (bluetoothCommService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            // Start the Bluetooth chat services

            if (bluetoothCommService.getConnectionState() == BluetoothCommService.STATE_NONE) {
                // Start the Bluetooth chat services
                bluetoothCommService.initialiseService();
            }

            if (bluetoothCommService.getConnectionState() == BluetoothCommService.STATE_LISTEN) {
                setStatus(R.string.title_not_connected);

            } else if (bluetoothCommService.getConnectionState() == BluetoothCommService.STATE_CONNECTED) {
                setStatus(R.string.title_not_connected);
                setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
            }
        } else {
            LogFragment fragment = (LogFragment) getSupportFragmentManager().findFragmentByTag("logFragment");
            if (fragment != null)
                fragment.addLog(logList);
        }

        setupScheduler();

        /*if (isAccelerometerEnabled) {
            if (accelerometerSensorProvider != null)
                accelerometerSensorProvider.startSensorUpdate();
        }*/
    }

    @Override
    protected void onPause() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        super.onPause();
        removeSchedulerCallBack();

        /*if (accelerometerSensorProvider !=null)
            accelerometerSensorProvider.stopSensorUpdate()*/
    }

    @Override
    public void onUiUpdate(Arena arena) {
        /*if (arena != null && arena.getRobot() != null) {
            if (textViewDirection != null) {
                if(arena.getRobot().getDirection() == 0)
                    textViewDirection.setText("270");

                else if(arena.getRobot().getDirection() == 1)
                    textViewDirection.setText("0");

                else if(arena.getRobot().getDirection() == 2)
                    textViewDirection.setText("90");

                else if(arena.getRobot().getDirection() == 3)
                    textViewDirection.setText("180");
            }

            if (textViewStatus != null)
                textViewStatus.setText(arena.getRobot().getStatus());
        }*/
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putStringArrayList("logList", logList);
        savedInstanceState.putString("mConnectedDeviceName", mConnectedDeviceName);
        savedInstanceState.putBoolean("isShowingMaze", isShowingLog);
        //savedInstanceState.putBoolean("isAccelerometerEnabled", isAccelerometerEnabled);

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
        //isAccelerometerEnabled = savedInstanceState.getBoolean("isAccelerometerEnabled");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else {
            moveTaskToBack(true);
            //super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        switch (item.getItemId()) {
            case R.id.nav_connect_device:
                FragmentManager fm = getSupportFragmentManager();
                DeviceListDialogFragment deviceListDialogFragment = new DeviceListDialogFragment();
                deviceListDialogFragment.setCancelable(true);
                //  deviceListDialogFragment.setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Dialog);
                deviceListDialogFragment.setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_DeviceDefault_Dialog_Alert);
                deviceListDialogFragment.show(fm, "deviceListDialogFragment");
                break;

            case R.id.nav_input_position:
                Intent intent = new Intent(getApplicationContext(), InputPositionActivity.class);
                startActivityForResult(intent, Config.INPUT_POS_ACTIVITY);// Activity is started with requestCode 2

                break;

            case R.id.nav_message_log:
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
                break;

            case R.id.nav_settings:
                intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);// Activity is started with requestCode 2
                break;

            case R.id.nav_view_mdf1:
                sendMessage(mdf1);
                break;

            case R.id.nav_view_mdf2:
                sendMessage(mdf2);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        /*Arena arena = getArena();

        switch (compoundButton.getId()) {
            case R.id.sw_arena_start_stop:
                if (arena != null) {
                    if (b) {
                        if (!arena.isStarted()) {
                            DialogFragment startRobotDialogFragment = new CustomAlertDialogFragment();
                            Bundle args = new Bundle();
                            args.putString("title", "Start");
                            args.putString("message", "Do you wish to start the robot?");
                            startRobotDialogFragment.setArguments(args);
                            startRobotDialogFragment.show(getSupportFragmentManager(), "startRobotDialogFragment");
                        }
                    } else {
                        if (arena.isStarted()) {
                            DialogFragment startRobotDialogFragment = new CustomAlertDialogFragment();
                            Bundle args = new Bundle();
                            args.putString("title", "Stop");
                            args.putString("message", "Do you wish to stop the robot?");
                            startRobotDialogFragment.setArguments(args);
                            startRobotDialogFragment.show(getSupportFragmentManager(), "startRobotDialogFragment");
                        }
                    }
                }

                break;

            case R.id.sw_auto_grid_update:
                System.out.println("Auto Grid Update is " + (b ? "on" : "off"));
                arena = getArena();

                if (arena != null) {
                    if (b) {
                        if(!arena.isStarted()) {
                            DialogFragment basicDialogFragment = new BasicDialogFragment();
                            Bundle args = new Bundle();
                            args.putString("title", "Error");
                            args.putString("message", "You are not allowed to switch to auto mode until you have started the robot");
                            basicDialogFragment.setArguments(args);
                            basicDialogFragment.show(getSupportFragmentManager(), "basicDialogFragment");

                        } else if (!arena.isAuto()) {
                            DialogFragment autoManualDialogFragment = new CustomAlertDialogFragment();
                            Bundle args = new Bundle();
                            args.putString("title", "Auto");
                            args.putString("message", "Do you wish to switch to auto update mode?");
                            autoManualDialogFragment.setArguments(args);
                            autoManualDialogFragment.show(getSupportFragmentManager(), "autoManualDialogFragment");
                        }

                    } else {
                        if (arena.isAuto()) {
                            DialogFragment autoManualDialogFragment = new CustomAlertDialogFragment();
                            Bundle args = new Bundle();
                            args.putString("title", "Manual");
                            args.putString("message", "Do you wish to switch to manual update mode?");
                            autoManualDialogFragment.setArguments(args);
                            autoManualDialogFragment.show(getSupportFragmentManager(), "autoManualDialogFragment");
                        }
                    }
                }

                break;

            case R.id.sw_use_accelerometer:
                System.out.println("Accelerometer is " + (b ? "on" : "off"));

                if (b) {
                    isAccelerometerEnabled = true;
                    if (accelerometerSensorProvider != null)
                        accelerometerSensorProvider.startSensorUpdate();

                } else {
                    isAccelerometerEnabled = false;
                    if (accelerometerSensorProvider != null)
                        accelerometerSensorProvider.stopSensorUpdate();
                }

                break;

            default:
                break;
        }*/
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return bluetoothCommService;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        /*if(dialog.getTag().equalsIgnoreCase("startRobotDialogFragment")) {
            Arena arena=getArena();

            if (arena != null && swArenaStart != null) {
                if (!arena.isStarted()) {
                    arena.setStarted(true);
                    swArenaStart.setChecked(true);

                } else if (arena.isStarted()&& swArenaStart != null) {
                    arena.setStarted(false);
                    swArenaStart.setChecked(false);

                    //reset stuff
                    //isAccelerometerEnabled = false;
                    arena.reset();
                    removeSchedulerCallBack();

                    if(accelerometerSensorProvider != null)
                        accelerometerSensorProvider.stopSensorUpdate();

                    mazeFragment = (MazeFragment) getSupportFragmentManager().findFragmentByTag("mazeFragment");

                    if (mazeFragment != null) {
                        mazeFragment.resetArenaView();
                    }

                    invalidateOptionsMenu();
                }
            }
        } else if(dialog.getTag().equalsIgnoreCase("autoManualDialogFragment")) {
            Arena arena=getArena();

            if (arena != null && swAutoGridUpdate != null) {
                if (!arena.isAuto()) {
                    arena.setAuto(true);
                    swAutoGridUpdate.setChecked(true);
                    setupScheduler();

                } else if (arena.isAuto()&& swAutoGridUpdate != null) {
                    arena.setAuto(false);
                    swAutoGridUpdate.setChecked(false);
                    removeSchedulerCallBack();
                }
            }
        }

        dialog.dismiss();*/
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        /*if(dialog.getTag().equalsIgnoreCase("startRobotDialogFragment")) {
            Arena arena = getArena();

            if (arena != null && swArenaStart != null) {
                if (!arena.isStarted()) {
                    swArenaStart.setChecked(false);

                }else  if (arena.isStarted() && swArenaStart != null) {
                    swArenaStart.setChecked(true);
                }
            }
        } else if (dialog.getTag().equalsIgnoreCase("autoManualDialogFragment")) {
            Arena arena=getArena();

            if (arena != null && swAutoGridUpdate != null) {
                swAutoGridUpdate.setChecked(arena.isAuto());
            }
        }

        dialog.dismiss();*/
    }

    /*@Override
    public void onAccelerometerChanged(String move) {
        Arena arena= getArena();

        if (arena!=null) {
            Robot.Move robotMove = null;

            if(move.equalsIgnoreCase(Protocol.MOVE_FORWARD)) {
                robotMove=Robot.Move.UP;

            }else if(move.equalsIgnoreCase(Protocol.TURN_LEFT)) {
                robotMove=Robot.Move.LEFT;

            } else if(move.equalsIgnoreCase(Protocol.TURN_RIGHT)) {
                robotMove=Robot.Move.RIGHT;
            }

            Boolean isSafe = arena.checkObstacles(robotMove);
            if(isSafe)
                sendMessage(move);
        }
    }*/

    @Override
    public void onActivityResult(String a) {
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

                        sendRobotPosition(robotInput);
                        //     sendMessage("{\"robotPosition\" : [10, 2, 90]}");
                    }
                } catch (Exception e) {
                    Log.e(Config.log_id, e.getMessage());
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
                    setupBlueToothCommService();

                } else {
                    // User did not enable Bluetooth or an error occurred
                    Toast.makeText(getApplicationContext(), R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    private void setupBlueToothCommService() {
        // Initialize the BluetoothChatService to perform bluetooth connections
        bluetoothCommService = BluetoothCommService.getInstance();
        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");

        // Intent serviceIntent = new Intent(getApplicationContext(),BluetoothCommService.class);
        //startService(serviceIntent);
    }

    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras().getString(DeviceListDialogFragment.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = btAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        bluetoothCommService.connect(device);
    }

    public Arena getArena() {
        MazeFragment fragment = (MazeFragment) getSupportFragmentManager().findFragmentByTag("mazeFragment");

        if (fragment != null) {
            return fragment.getArena();

        } else return null;
    }

    private void sendRobotPosition(Robot robotInput) {
        int robotX, robotY;
        robotX=robotInput.getX()+1;
        robotY=robotInput.getY()-18;
        String text=robotX+","+Math.abs(robotY);
        sendMessage(text);
    }

    private void moveRobot(Robot.Move move, int noOfMove) {
        for (int i = 0; i< noOfMove; i++) {
            moveRobot(move);
        }
    }

    public void moveRobot(Robot.Move move) {
        try {
            MazeFragment fragment = (MazeFragment) getSupportFragmentManager().findFragmentByTag("mazeFragment");
            if (fragment != null) {
                fragment.btnMove(move);
            }
        } catch (Exception e) {
            Log.e(Config.log_id, e.getMessage());
        }
    }

    public void startExploration(View a) {
        sendMessage(Protocol.START_EXPLORATION);
        textViewStatus.setText("Exploring");
    }

    public void startFastest(View a) {
        sendMessage(Protocol.START_FASTEST);
        textViewStatus.setText("Fastest");
    }

    public void btnSend(View a) {
        TextView textToSend = (TextView) findViewById(R.id.txtString);
        String data = textToSend.getText().toString();
        sendMessage(data);
    }

    public void btnCalibrate(View a) {
        sendMessage(Protocol.CALIBRATE);
    }

    public void btnResetGrid(View a) {
        String s = "{\"grid\":\"00000000000000000000";
        for(int i=0; i<=13; i++) s += "00000000000000000000";
        s += "\",\"robotPosition\":[1,1,0],\"status\":\"na\"}";
        handleJson(s);
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

    public void btnSendGridUpdate() {
        sendMessage(Protocol.SEND_ARENA);
        Operation.showToast(getApplicationContext(), "Grid data requested");
    }

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
                byte[] writeBuf = msg.getData().getByteArray(Protocol.MESSAGE_BUFFER);
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

                if (readMessage.startsWith("W") && readMessage.length() == 2 ||
                        readMessage.startsWith("W") && readMessage.length() == 3) {

                    try {
                        int noOfMoveForward = 0;
                        if (readMessage.length() == 2) {
                            noOfMoveForward = Integer.parseInt(readMessage.substring(1, 2));

                        } else {
                            noOfMoveForward = Integer.parseInt(readMessage.substring(1, 3));
                        }

                        moveRobot(Robot.Move.UP, noOfMoveForward);

                    } catch (Exception e) {
                        Log.e(Config.log_id,"Move up multiple error");
                    }

                } else if (readMessage.equalsIgnoreCase(Protocol.MOVE_FORWARD)) {
                    moveRobot(Robot.Move.UP);
                    //sendMessage(Protocol.MOVE_FORWARD);

                } else if (readMessage.equalsIgnoreCase(Protocol.TURN_LEFT)) {
                    moveRobot(Robot.Move.LEFT);
                    //sendMessage(Protocol.TURN_LEFT);

                } else if (readMessage.equalsIgnoreCase(Protocol.TURN_RIGHT)) {
                    moveRobot(Robot.Move.RIGHT);
                    //sendMessage(Protocol.TURN_RIGHT);

                } else if(readMessage.startsWith("grid")) {
                    handleMDFString(readMessage);
                }

                if (isJSONValid(readMessage)) {
                    handleJson(readMessage);
                }

                break;

            case Protocol.MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(Protocol.DEVICE_NAME);
                Operation.showToast(getApplicationContext(), "Connected to " + mConnectedDeviceName);
                break;

            case Protocol.MESSAGE_TOAST:
                Operation.showToast(getApplicationContext(), msg.getData().getString(Protocol.TOAST));
                break;
        }
    }

    /*private void handleMDFString(String readMessage) {
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

        try {
            if (getArena().getMdf1() != null && getArena().getMdf2() != null &&
                    !getArena().getMdf1().equalsIgnoreCase("") && !getArena().getMdf2().equalsIgnoreCase("")) {

                /*textViewMDFString.setText("MDF1: " + getArena().getMdf1() +
                        "\n" +"MDF2: " + getArena().getMdf2());
            }
        } catch(Exception e) {
            Log.e(Config.log_id, e.getMessage());
        }
    }*/

    private void handleJson(String readMessage) {
        handleRobotPositionUpdate(readMessage);
        handleGridUpdate(readMessage);
        handleMDFString(readMessage);
        handleStatusUpdate(readMessage);
    }

    /*private void handleRobotBatteryUpdate(String readMessage) {
        String battery = "";

        try {
            JSONObject obj = new JSONObject(readMessage);
            battery = obj.getString("battery");
            textViewBattery.setText(battery);

        } catch (Exception e) {
            Log.e(Config.log_id, e.getMessage());
        }
    }*/

    private void handleMDFString(String readMessage) {
        String grid = "";
        try {
            JSONObject obj = new JSONObject(readMessage);
            grid = transpose(obj.getString("grid"));
            String[] mdf = flip(grid).split("");

            mdf1 = "11";
            mdf2 = "";

            for (int i = 0; i < mdf.length; i++) {
                if (mdf[i].equals("0"))
                    mdf1 += "0";

                else if (mdf[i].equals("1")) {
                    mdf1 += "1";
                    mdf2 += "0";

                } else if (mdf[i].equals("2")){
                    mdf1 += "1";
                    mdf2 += "1";

                }
            }

            mdf1 += "11";
            mdf1 = new BigInteger(mdf1, 2).toString(16);
            mdf2 = countZerosToHex(mdf2) + new BigInteger(mdf2, 2).toString(16);
            Log.e("MDF2", mdf2);


        } catch (Exception e) {
            Log.e(Config.log_id, e.getMessage());
        }
    }

    private String countZerosToHex(String gridData) {
        String[] s = gridData.split("");
        int numOfZero = 0;
        String zeroStr = "";

        for (int i = 0; i < s.length; i++) {
            if (numOfZero == 4) {
                numOfZero = 0;
                zeroStr += "0";
                Log.e("MDF2", zeroStr);
            }

            if (s[i].equals("0")) numOfZero++;
            else break;
        }

        return zeroStr;
    }

    private String flip(String gridData) {
        int gridlen = gridData.length();
        String[] gridRow = new String[(int) Math.ceil((double) gridlen /(double) 15)]
                ;
        for (int i = 0; i < gridRow.length; i++)
            gridRow[i] = gridData.substring(i * 15, Math.min(gridlen, (i + 1) * 15));

        String finalString = "";

        for (int i = gridRow.length - 1; i > -1; i--)
            finalString += gridRow[i];

        return finalString;
    }

    private String transpose(String gridData) {
        int gridlen = gridData.length();
        String[] gridRow = new String[(int) Math.ceil((double) gridlen /(double) 20)];

        for (int i=0; i<gridRow.length; i++)
            gridRow[i] = gridData.substring(i * 20, Math.min(gridlen, (i + 1) * 20));

        String transposed = "";

        for(int i = 19; i >= 0; i--) {
            for (int j = 0; j <= 14; j++) {
                transposed += gridRow[j].charAt(i);
            }
        }

        return transposed;
    }

    /*private void handleMdf2Update(String readMessage) {
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
    }*/

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

            textViewX.setText(Integer.toString(x));
            textViewY.setText(Integer.toString(y));
            textViewDirection.setText(Integer.toString(direction));


            MazeFragment fragment = (MazeFragment) getSupportFragmentManager().findFragmentByTag("mazeFragment");


            if (fragment != null) {
                x -= 1;
                y = Math.abs(y - 18);

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
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }

        return true;
    }

    private void setStatus(CharSequence subTitle) {
        Log.d(Config.log_id, "state change " + subTitle);
        getSupportActionBar().setSubtitle(subTitle);
    }

    private void setStatus(int resId) {
        Log.d(Config.log_id, "state change " + resId);
        getSupportActionBar().setSubtitle(resId);
    }

    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (bluetoothCommService.getConnectionState() != BluetoothCommService.STATE_CONNECTED) {
            Operation.showToast(getApplicationContext(), getResources().getString(R.string.not_connected));
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

    private void setupScheduler() {
        /*if (swAutoGridUpdate != null && swAutoGridUpdate.isChecked()) {
            Runnable myRunnable = new Runnable() {
                public void run() {
                    Log.d(Config.log_id,"Auto Update grid runnable execution");
                    sendMessage(Protocol.SEND_ARENA);
                    //prepare and send the data here..
                    handlerAutoUpdate.removeCallbacks(null);
                    handlerAutoUpdate.postDelayed(this, Config.GRID_AUTO_UPDATE_INTERVAL);
                }
            };

            handlerAutoUpdate.postDelayed(myRunnable, Config.GRID_AUTO_UPDATE_INTERVAL);
        }*/
    }

    public void removeSchedulerCallBack() {
        try {
            handlerAutoUpdate.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            Log.e(Config.log_id, e.getMessage());
        }
    }
}