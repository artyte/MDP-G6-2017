package mdp.robotxplorer.arena;

import java.io.Serializable;

import mdp.robotxplorer.common.Config;
import mdp.robotxplorer.common.Operation;

public class Arena implements Serializable {
    private GridCell[][] gridMap;
    private Robot robot;
    private char[] mdf1, mdf2;

    protected enum GridCell {
        UNEXPLORED, FREE_SPACE, OBSTACLE
    }

    public Arena() {
        gridMap = new GridCell[Config.ARENA_WIDTH][Config.ARENA_LENGTH];
        robot = new Robot(1, 1, Robot.Direction.EAST);

        mdf1 = new char[Config.ARENA_AREA];
        mdf2 = new char[Config.ARENA_AREA];

        for (int x = 0; x < Config.ARENA_WIDTH; x ++) {
            for (int y = 0; y < Config.ARENA_LENGTH; y ++) {
                gridMap[x][y] = GridCell.UNEXPLORED;

                mdf1[y * Config.ARENA_WIDTH + x] = '0';
                mdf2[y * Config.ARENA_WIDTH + x] = ' ';
            }
        }
    }

    public GridCell[][] getGridMap() {
        return gridMap;
    }

    public Robot getRobot() {
        return robot;
    }

    public void updateGridMap(String ternaryGridData) {
        int count = 0;

        for (int x = 0; x < Config.ARENA_WIDTH; x ++) {
            for (int y = 0; y < Config.ARENA_LENGTH; y++) {
                if (count < ternaryGridData.length()) {
                    switch (ternaryGridData.charAt(count)) {
                        default :
                        case '0':
                            gridMap[x][y] = GridCell.UNEXPLORED;
                            mdf1[y * Config.ARENA_WIDTH + x] = '0';
                            mdf2[y * Config.ARENA_WIDTH + x] = ' ';
                            break;

                        case '1':
                            gridMap[x][y] = GridCell.FREE_SPACE;
                            mdf1[y * Config.ARENA_WIDTH + x] = '1';
                            mdf2[y * Config.ARENA_WIDTH + x] = '0';
                            break;

                        case '2':
                            gridMap[x][y] = GridCell.OBSTACLE;
                            mdf1[y * Config.ARENA_WIDTH + x] = '1';
                            mdf2[y * Config.ARENA_WIDTH + x] = '1';
                            break;
                    }
                }

                count++;
            }
        }
    }

    public String getMDF1() {
        return Operation.binaryToHex("11" + String.valueOf(mdf1) + "11");
    }

    public String getMDF2() {
        return Operation.binaryToHex(String.valueOf(mdf2).replaceAll(" ", ""));
    }

    public boolean isReset() {
        //Arena is considered reset if and only if all the grid cells are unexplored
        return String.valueOf(mdf2).replaceAll(" ", "").equalsIgnoreCase("");
    }

    public void resetArena() {
        robot.setPosition(1, 1);
        robot.setDirection(Robot.Direction.EAST);
        robot.setStatus("N/A");

        for (int x = 0; x < Config.ARENA_WIDTH; x ++) {
            for (int y = 0; y < Config.ARENA_LENGTH; y ++) {
                gridMap[x][y] = GridCell.UNEXPLORED;

                mdf1[y * Config.ARENA_WIDTH + x] = '0';
                mdf2[y * Config.ARENA_WIDTH + x] = ' ';
            }
        }
    }
}