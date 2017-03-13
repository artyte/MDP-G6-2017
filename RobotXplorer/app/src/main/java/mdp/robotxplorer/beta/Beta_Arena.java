package mdp.robotxplorer.beta;

import android.util.Log;

import mdp.robotxplorer.common.Config;

public class Beta_Arena {
    private Beta_GridCell[][] gridMap;
    private int length, width;
    private Beta_Robot betaRobot;

    public Beta_Arena() {
        length = Config.ARENA_LENGTH;
        width  = Config.ARENA_WIDTH;

        gridMap = new Beta_GridCell[width][length];
        betaRobot = new Beta_Robot(1, 18, Beta_Robot.Direction.EAST);

        for (int i = 0; i < width; i ++) {
            for (int j = 0; j <length; j ++) {
                gridMap[i][j] = new Beta_GridCell(i, j);
            }
        }
    }

    public Beta_GridCell[][] getGridMap() {
        return gridMap;
    }

    public int getLength() {
        return length;
    }

    public int getWidth() {
        return width;
    }

    public Beta_Robot getBetaRobot() {
        return betaRobot;
    }

    public void updateGridMap(String ternaryGridData) {
        int count = 0;

        try {
            for (int x = 0; x < width; x ++)
                for (int y = 0; y < length; y ++) {
                    if (ternaryGridData.charAt(count) == '2') {
                        gridMap[x][y].setExplored(true);
                        gridMap[x][y].setHaveObstacle(true);

                    } else if (ternaryGridData.charAt(count) == '1') {
                        gridMap[x][y].setExplored(true);
                        gridMap[x][y].setHaveObstacle(false);

                    } else {
                        gridMap[x][y].setExplored(false);
                        gridMap[x][y].setHaveObstacle(false);
                    }

                    count++;
                }
        } catch (Exception e) {
            Log.e(Config.log_id,e.getMessage());
        }
    }
}
