package mdp.robotxplorer.common;

import android.graphics.Color;

public interface Config {
    String log_id = "NTU_MDP";

    int ARENA_LENGTH = 20;
    int ARENA_WIDTH  = 15;
    int ARENA_AREA   = ARENA_LENGTH * ARENA_WIDTH;

    int BORDER = Color.rgb(56, 56, 56);
    int UNEXPLORED = Color.GRAY;
    int FREE_SPACE = Color.WHITE;
    int OBSTACLE   = Color.BLACK;

    int START = Color.RED;
    int GOAL = Color.GREEN;

    int INPUT_POS_ACTIVITY = 1150;
    int GRID_AUTO_UPDATE_INTERVAL = 10000;
    int ACCELEROMETER_UPDATE_INTERVAL = 100;
}

