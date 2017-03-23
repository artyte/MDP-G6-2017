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
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
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
import android.widget.ToggleButton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import mdp.robotxplorer.R;
import mdp.robotxplorer.arena.Robot;
import mdp.robotxplorer.common.Config;
import mdp.robotxplorer.common.Operation;
import mdp.robotxplorer.common.Protocol;
import mdp.robotxplorer.fragment.ArenaFragment;
import mdp.robotxplorer.fragment.BasicDialogFragment;
import mdp.robotxplorer.fragment.CustomAlertDialogFragment;
import mdp.robotxplorer.fragment.DeviceListDialogFragment;
import mdp.robotxplorer.fragment.LogFragment;
//import mdp.robotxplorer.sensor.AccelerometerSensor;
import mdp.robotxplorer.service.BluetoothCommService;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        CompoundButton.OnCheckedChangeListener,
        DeviceListDialogFragment.DialogListener,
        CustomAlertDialogFragment.AlertDialogListener,
        BasicDialogFragment.AlertDialogListener {

    public static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    public static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    public static final int REQUEST_ENABLE_BT = 24;

    Timer explorationTimer, fastestTimer;
    long explorationStartTime, fastestStartTime;

    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    private BluetoothCommService bluetoothCommService = null;
    BluetoothAdapter btAdapter = null;
    ArrayList<String> logList = new ArrayList<String>();
    private String mConnectedDeviceName = null;
    boolean isShowingLog = false;
    //boolean isAccelerometerEnabled = false;

    Menu menu;
    ToggleButton tgbExploration, tgbFastest;
    TextView textViewExplorationTimer, textViewFastestTimer;
    TextView textViewX, textViewY, textViewDirection, textViewStatus, textViewMDF1, textViewMDF2;
    //Switch swArenaStart, swAutoGridUpdate, swUseAccelerometer;

    ArenaFragment arenaFragment;
    LogFragment logFragment;
    Handler explorationTimerHandler, fastestTimerHandler, handlerAutoUpdate = new Handler();

    //AccelerometerSensor accelerometerSensorProvider;

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            Log.d(Config.log_id, "Broadcast Message received");
            Log.d(Config.log_id, "Message type: " + intent.getExtras().getInt(Protocol.MESSAGE_TYPE));
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
        String fileDir = "storage/emulated/0/Download/mdf_strings.txt";
        String data = "MDF1: " + arenaFragment.getMDF1() + "\n\r" + "MDF2: " + arenaFragment.getMDF2();
        Operation.writeToFile(data, fileDir);
        Operation.showToast(this, "MDF file created!");
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
        tgbExploration = (ToggleButton) findViewById(R.id.tgbExploration);
        tgbExploration.setOnCheckedChangeListener(this);

        tgbFastest = (ToggleButton) findViewById(R.id.tgbFastest);
        tgbFastest.setOnCheckedChangeListener(this);

        textViewX = (TextView) findViewById(R.id.textViewX);
        textViewY = (TextView) findViewById(R.id.textViewY);

        textViewDirection = (TextView) findViewById(R.id.textViewDirection);
        textViewStatus = (TextView) findViewById(R.id.textViewStatus);

        textViewMDF1 = (TextView) findViewById(R.id.mdf1_textview);
        textViewMDF2 = (TextView) findViewById(R.id.mdf2_textview);

        textViewExplorationTimer = (TextView) findViewById(R.id.textViewExplorationTimer);
        textViewFastestTimer = (TextView) findViewById(R.id.textViewFastestTimer);

        explorationTimerHandler = new Handler() {
            public void handleMessage(Message msg) {
                long elapsedTime = System.currentTimeMillis() - explorationStartTime;

                int millis  = (int) (elapsedTime % 1000) / 10 ;
                int seconds = (int) (elapsedTime / 1000) % 60 ;
                int minutes = (int) ((elapsedTime / (1000 * 60)) % 60);

                String explorationTimeDisplay = String.format("%02d:%02d:%02d", minutes, seconds, millis);
                textViewExplorationTimer.setText(explorationTimeDisplay);
        }};

        fastestTimerHandler = new Handler() {
            public void handleMessage(Message msg) {
                long elapsedTime = System.currentTimeMillis() - fastestStartTime;

                int millis  = (int) (elapsedTime % 1000) / 10 ;
                int seconds = (int) (elapsedTime / 1000) % 60 ;
                int minutes = (int) ((elapsedTime / (1000 * 60)) % 60);

                String fastestTimeDisplay = String.format("%02d:%02d:%02d", minutes, seconds, millis);
                textViewFastestTimer.setText(fastestTimeDisplay);
            }};

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

            arenaFragment = (ArenaFragment) getSupportFragmentManager().findFragmentByTag("arenaFragment");
            logFragment  = (LogFragment)  getSupportFragmentManager().findFragmentByTag("logFragment");

            if (arenaFragment == null)
                arenaFragment = new ArenaFragment();

            if (logFragment == null)
                logFragment = new LogFragment();

            transaction.add(R.id.main_fragment, arenaFragment, "arenaFragment");
            transaction.add(R.id.main_fragment, logFragment, "logFragment");

            if (isShowingLog && arenaFragment != null && logFragment != null) {
                transaction.show(logFragment);
                transaction.hide(arenaFragment);

            } else if (!isShowingLog && arenaFragment != null && logFragment != null) {
                transaction.show(arenaFragment);
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
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putStringArrayList("logList", logList);
        savedInstanceState.putString("mConnectedDeviceName", mConnectedDeviceName);
        savedInstanceState.putBoolean("isShowingMaze", isShowingLog);
        //savedInstanceState.putBoolean("isAccelerometerEnabled", isAccelerometerEnabled);

        //Save the fragment's instance
        if (arenaFragment != null)
            getSupportFragmentManager().putFragment(savedInstanceState, "mContent", arenaFragment);
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
        this.menu = menu;
        getMenuInflater().inflate(R.menu.input_pos, menu);

        for (int i = 0; i < menu.size(); i++){
            menu.getItem(i).setVisible(false);
            Drawable drawable = menu.getItem(i).getIcon();

            if (drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            }
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.menu_done:
                enableInputPosition(false);
                displayRobotInfo(arenaFragment.getRobot());
                sendRobotPosition(arenaFragment.getArena().getRobot());
                break;

            case R.id.menu_rotate:
                arenaFragment.turnRight();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_connect_device:
                selectDevice();
                break;

            case R.id.nav_input_position:
                inputPosition();
                break;

            case R.id.nav_reset_grid:
                resetGrid();
                break;

            case R.id.nav_message_log:
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                LogFragment logFragment = (LogFragment) getSupportFragmentManager().findFragmentByTag("logFragment");
                arenaFragment = (ArenaFragment) getSupportFragmentManager().findFragmentByTag("arenaFragment");

                if (isShowingLog && logFragment != null & arenaFragment != null) {
                    transaction.show(arenaFragment);
                    transaction.hide(logFragment);
                    isShowingLog = false;

                } else if (!isShowingLog && arenaFragment != null && logFragment != null) {
                    transaction.hide(arenaFragment);
                    transaction.show(logFragment);
                    isShowingLog = true;
                }

                transaction.commit();
                break;

            case R.id.nav_settings:
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);// Activity is started with requestCode 2
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
        //Arena arena = getArena();
        switch (compoundButton.getId()) {
            case R.id.tgbExploration:
                if (b) startExploration();
                else   finishExploration();
                break;

            case R.id.tgbFastest:
                if (b) startFastest();
                else   finishFastest();
                break;
        }
        /*switch (compoundButton.getId()) {
            case R.id.sw_auto_grid_update:
                arena = getArena();

                if (arena != null) {
                    if (b) {
                        if (!arena.isAuto()) {
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
        /*if(dialog.getTag().equalsIgnoreCase("autoManualDialogFragment")) {
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
        /*if (dialog.getTag().equalsIgnoreCase("autoManualDialogFragment")) {
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
    }

    private void selectDevice() {
        FragmentManager fm = getSupportFragmentManager();
        DeviceListDialogFragment deviceListDialogFragment = new DeviceListDialogFragment();
        deviceListDialogFragment.setCancelable(true);
        deviceListDialogFragment.setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_DeviceDefault_Dialog_Alert);
        deviceListDialogFragment.show(fm, "deviceListDialogFragment");
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

    public void displayRobotInfo(Robot robot) {
        textViewX.setText(String.valueOf(robot.getXPos()));
        textViewY.setText(String.valueOf(robot.getYPos()));
        textViewStatus.setText(robot.getStatus());
    }

    public void inputPosition() {
        if (arenaFragment.getArenaView().getArena().isReset())
            enableInputPosition(true);
        else
            Operation.showToast(this, "Please reset your arena first");
    }

    private void enableInputPosition(boolean b) {
        arenaFragment.getArenaView().selectingPosition(b);

        findViewById(R.id.btnSend).setEnabled(!b);
        findViewById(R.id.tgbExploration).setEnabled(!b);
        findViewById(R.id.tgbFastest).setEnabled(!b);
        findViewById(R.id.btnResetExplorationTimer).setEnabled(!b);
        findViewById(R.id.btnResetFastestTimer).setEnabled(!b);
        findViewById(R.id.btnF1).setEnabled(!b);
        findViewById(R.id.btnF2).setEnabled(!b);

        for (int i = 0; i < menu.size(); i ++)
            menu.getItem(i).setVisible(b);
    }

    private void resetGrid() {
        arenaFragment.resetGrid();
        textViewMDF1.setText(arenaFragment.getMDF1());
        textViewMDF2.setText(arenaFragment.getMDF2());
    }

    private void sendRobotPosition(Robot robotInput) {
        sendMessage(robotInput.getXPos() + ", " + robotInput.getYPos());
    }

    private void startExploration() {
        sendMessage(Protocol.START_EXPLORATION);
        textViewStatus.setText("Exploring");
        explorationStartTime = System.currentTimeMillis();

        explorationTimer = new Timer();
        explorationTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                explorationTimerHandler.obtainMessage(1).sendToTarget();
            }
        }, 0, 10);

        Operation.showToast(this, "Exploration Started");
    }

    private void finishExploration() {
        explorationTimer.cancel();
        textViewStatus.setText("N/A");
        Operation.showToast(this, "Exploration Finished");
    }

    public void resetExplorationTimer(View a) {
        textViewExplorationTimer.setText("00:00:00");
    }

    private void startFastest() {
        sendMessage(Protocol.START_FASTEST);
        textViewStatus.setText("Fastest");
        fastestStartTime = System.currentTimeMillis();

        fastestTimer = new Timer();
        fastestTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                fastestTimerHandler.obtainMessage(1).sendToTarget();
            }
        }, 0, 10);

        Operation.showToast(this, "Fastest Started");
    }

    private void finishFastest() {
        fastestTimer.cancel();
        textViewStatus.setText("N/A");
        Operation.showToast(this, "Fastest Finished");
    }

    public void resetFastestTimer(View a) {
        textViewFastestTimer.setText("00:00:00");
    }

    public void btnSend(View a) {
        TextView textToSend = (TextView) findViewById(R.id.txtString);
        String data = textToSend.getText().toString();
        sendMessage(data);
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
                String readMessage = new String(buffer, 0, bytes).trim();

                Log.e(Config.log_id, "Protocol.MESSAGE_READ: " + readMessage);
                logList.add(0, mConnectedDeviceName + ":  " + readMessage );

                if (arenaFragment != null && readMessage.startsWith("W")
                        && (readMessage.length() == 2 || readMessage.length() == 3)) {

                    try {
                        int noOfMoveForward = 0;

                        if (readMessage.length() == 2) {
                            noOfMoveForward = Integer.parseInt(readMessage.substring(1, 2));

                        } else {
                            noOfMoveForward = Integer.parseInt(readMessage.substring(1, 3));
                        }

                        for (int i = 0; i < noOfMoveForward; i ++)
                            arenaFragment.moveForward();

                    } catch (Exception e) {
                        Log.e(Config.log_id,"Move up multiple error");
                    }

                } else if (readMessage.equalsIgnoreCase(Protocol.MOVE_FORWARD)) {
                    arenaFragment.moveForward();
                    //sendMessage(Protocol.MOVE_FORWARD);

                } else if (readMessage.equalsIgnoreCase(Protocol.TURN_LEFT)) {
                    arenaFragment.turnLeft();
                    //sendMessage(Protocol.TURN_LEFT);

                } else if (readMessage.equalsIgnoreCase(Protocol.TURN_RIGHT)) {
                    arenaFragment.turnRight();
                    //sendMessage(Protocol.TURN_RIGHT);

                } else if (readMessage.startsWith("grid")) {
                    handleGridUpdate(readMessage);
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

    private void handleJson(String readMessage) {
        handleRobotPositionUpdate(readMessage);
        handleGridUpdate(readMessage);
        handleStatusUpdate(readMessage);
    }

    private void handleGridUpdate(String readMessage) {
        String gridData = "";

        try {
            arenaFragment = (ArenaFragment) getSupportFragmentManager().findFragmentByTag("arenaFragment");
            JSONObject obj = new JSONObject(readMessage);
            gridData = obj.getString("grid");

            if (arenaFragment != null) {
                arenaFragment.gridUpdate(gridData);
                textViewMDF1.setText(arenaFragment.getMDF1());
                textViewMDF2.setText(arenaFragment.getMDF2());
            }
        } catch (Exception e) {
            Log.e(Config.log_id, e.getMessage());
        }
    }

    private void handleStatusUpdate(String readMessage) {
        String status = "";

        try {
            arenaFragment = (ArenaFragment) getSupportFragmentManager().findFragmentByTag("arenaFragment");
            JSONObject obj = new JSONObject(readMessage);

            status = obj.getString("status");
            textViewStatus.setText(status);

            if (arenaFragment != null)
                arenaFragment.statusUpdate(status);

        } catch (Exception e) {
            Log.e(Config.log_id, e.getMessage());
        }
    }

    private void handleRobotPositionUpdate(String readMessage) {
        try {
            arenaFragment = (ArenaFragment) getSupportFragmentManager().findFragmentByTag("arenaFragment");
            JSONObject obj = new JSONObject(readMessage);

            int x = (int) obj.getJSONArray("robotPosition").get(0);
            int y = (int) obj.getJSONArray("robotPosition").get(1);
            int direction = (int) obj.getJSONArray("robotPosition").get(2);

            textViewX.setText(Integer.toString(x));
            textViewY.setText(Integer.toString(y));
            textViewDirection.setText(Integer.toString(direction));

            if (arenaFragment != null)
                arenaFragment.moveRobot(x, y, direction);

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

    public void removeSchedulerCallBack() {
        try {
            handlerAutoUpdate.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            Log.e(Config.log_id, e.getMessage());
        }
    }
}