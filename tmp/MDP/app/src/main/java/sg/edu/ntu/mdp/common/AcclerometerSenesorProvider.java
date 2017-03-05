package sg.edu.ntu.mdp.common;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;
import sg.edu.ntu.mdp.model.arena.Robot;

/*
public class AcclerometerSenesorProvider implements SensorEventListener {


    Handler handlerSensorMovement = new Handler();
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    SensorProvider activity;
    Context context;

    public AcclerometerSenesorProvider(SensorProvider activity, Context context) {
        this.activity = activity;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];
        removeHandlerCallback();
        if (x > (-2) && x < (2) && y > (-2) && y < (2)) {
            Log.e(Config.log_id,"Center");
        } else if (Math.abs(x) > Math.abs(y)) {
            if (x < 0) {
                SensorMovementRunnable sensorMovementRunnable = new SensorMovementRunnable(Robot.Move.UP, (Activity) activity);
                handlerSensorMovement.postDelayed(sensorMovementRunnable, Config.ACCELEROMETER_UPDATE_TIME);
                Log.e(Config.log_id, "UP");
                mSensorManager.unregisterListener(this);
            }else
            if (x > 0) {
                SensorMovementRunnable sensorMovementRunnable = new SensorMovementRunnable(null, (Activity) activity);
                handlerSensorMovement.postDelayed(sensorMovementRunnable, Config.ACCELEROMETER_UPDATE_TIME);
                Log.e(Config.log_id, "Dowm");
                mSensorManager.unregisterListener(this);
            }
        } else {
            if (y < 0) {
                SensorMovementRunnable sensorMovementRunnable = new SensorMovementRunnable(Robot.Move.LEFT, (Activity) activity);
                handlerSensorMovement.postDelayed(sensorMovementRunnable, Config.ACCELEROMETER_UPDATE_TIME);
                Log.e(Config.log_id, "LEFT");
                mSensorManager.unregisterListener(this);
            }else
            if (y > 0) {
                SensorMovementRunnable sensorMovementRunnable = new SensorMovementRunnable(Robot.Move.RIGHT, (Activity) activity);
                handlerSensorMovement.postDelayed(sensorMovementRunnable, Config.ACCELEROMETER_UPDATE_TIME);
                Log.e(Config.log_id, "RIGHT");
                mSensorManager.unregisterListener(this);
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {


    }

    public void startSensorUpdate() {
        Log.d(Config.log_id,"Start sensor update");
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

    }

    public void stopSensorUpdate() {
        Log.d(Config.log_id,"stop sensor update");
        removeHandlerCallback();
        try {
            if (mSensorManager != null)
                mSensorManager.unregisterListener(this);
        } catch (Exception e) {

        }

    }

    public interface SensorProvider {
        public void onAcclerometerChanged(String moveForward);
    }


    public class SensorMovementRunnable implements Runnable {
        private Robot.Move move;
        private Activity context;


        public SensorMovementRunnable(Robot.Move move, Activity context) {
            this.move = move;
            this.context = context;
        }

        public void run() {
            removeHandlerCallback();
            try {
                mSensorManager.registerListener(AcclerometerSenesorProvider.this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            } catch (Exception e) {
            }
            if(move==null)
            {

            }else
            if (move == Robot.Move.UP) {
                activity.onAcclerometerChanged(Protocol.MOVE_FORWARD);
            } else if (move == Robot.Move.LEFT) {
                activity.onAcclerometerChanged(Protocol.TURN_LEFT);
            } else if (move == Robot.Move.RIGHT) {
                activity.onAcclerometerChanged(Protocol.TURN_RIGHT);

            }
        }
    }

    private void removeHandlerCallback() {
        try {
            handlerSensorMovement.removeCallbacks(null);
        } catch (Exception e) {

        }

    }
}
*/