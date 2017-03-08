package sg.edu.ntu.mdp.common;

import android.graphics.Color;

public class Config {
	public static String log_id = "NTU_MDP";

	public static final int DEFAULT_NO_OF_ROW = 15;
	public static final int DEFAULT_NO_OF_COL = 20;
	public static final int DEFAULT_START_X = 0;
	public static final int DEFAULT_START_Y = 17;
	public static final int DEFAULT_GOAL_X = 12;
	public static final int DEFAULT_GOAL_Y = 17;
	public static final int DEFAULT_ROBOT_HEAD = 1;

    public static final int GRID_AUTO_UPDATE_TIME = 5000;
    public static final int INPUT_POS_ACTIVITY = 1150;
    //public static final int ACCELEROMETER_UPDATE_TIME = 100;
    public static final long BLUETOOTH_RECONNECTION_TIMEOUT = 10000;

	public static final int BORDER = Color.rgb(56, 56, 56);
	public static final int UNEXPLORED = Color.GRAY;
	public static final int EXPLORED = Color.WHITE;
	public static final int OBSTACLE = Color.BLACK;
    public static final int START = Color.RED;
    public static final int GOAL = Color.GREEN;

}




