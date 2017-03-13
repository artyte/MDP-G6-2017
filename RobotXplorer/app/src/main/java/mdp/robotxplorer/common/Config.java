package mdp.robotxplorer.common;

import android.graphics.Color;

public interface Config {
    String log_id = "NTU_MDP";

    int ARENA_LENGTH = 20;
    int ARENA_WIDTH  = 15;

    int DEFAULT_START_X = 0;
    int DEFAULT_START_Y = 17;
    int DEFAULT_GOAL_X = 12;
    int DEFAULT_GOAL_Y = 17;
    int DEFAULT_ROBOT_HEAD = 1;

    int BORDER = Color.rgb(56, 56, 56);
    int OBSTACLE = Color.BLACK;
    int UNEXPLORED = Color.GRAY;
    int EXPLORED = Color.WHITE;
    int START = Color.RED;
    int GOAL = Color.GREEN;

    int INPUT_POS_ACTIVITY = 1150;
    int GRID_AUTO_UPDATE_INTERVAL = 10000;
    int ACCELEROMETER_UPDATE_INTERVAL = 100;
}
