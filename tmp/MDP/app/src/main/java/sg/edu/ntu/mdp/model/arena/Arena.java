package sg.edu.ntu.mdp.model.arena;

import android.util.Log;
import java.io.Serializable;
import sg.edu.ntu.mdp.common.CommonOperation;
import sg.edu.ntu.mdp.common.Config;

/**
 * Created by ericl on 08/09/2016.
 */
public class Arena implements Serializable {

    Robot robot;
    Cell[][] cellArray;
    int numRow,numCol;
    StartProperty startProperty;
    GoalProperty goalProperty;
    boolean isStarted=false;
    boolean isAuto=false;
    String mdf1="";
    String mdf2="";

    public String getMdf1() {
        return mdf1;
    }

    public void setMdf1(String mdf1) {
        this.mdf1 = mdf1;
    }


    public String getMdf2() {
        return mdf2;
    }
    public String getMDF2Binary()
    {
        try {

            String data = "";
            data = getMdf2();
            String gridDataInBinary = "";
            for (int i = 0; i < data.length(); i++) {
                gridDataInBinary += CommonOperation.HexToBinary(data.charAt(i) + "");
            }
            return gridDataInBinary;

        }
        catch (Exception e)
        {
            Log.e(Config.log_id," getMdf2BinaryData error"+ e.getMessage());
        }
        return null;
    }

    public String getMdf2BinaryData()
    {
        try {
            //remove the append binary
            char [] cellDataArray  = getMdf1BinaryData().toCharArray();
            String mdf2Binary = getMDF2Binary();
            //might have been resulted from padding 0, therefore need to go from the back
            Log.e(Config.log_id,"mdf1 binary data "+ getMdf1BinaryData());
            int c=0;
            for(int i=0;i<cellDataArray.length;i++)
            {
                if (cellDataArray[i] == '1') {
                    c++;
                }
            }
           Log.d(Config.log_id,"MDF1 explored size "+c);
            Log.d(Config.log_id,"mdf2Binary  size "+mdf2Binary.length());
            Log.d(Config.log_id,"mdf2 binary  data "+mdf2Binary);
           mdf2Binary=mdf2Binary.substring(0,mdf2Binary.length());

            c=0;
            for (int i = 0; i < cellDataArray.length; i++) {
                if (cellDataArray[i] == '1') {
                    cellDataArray[i] = mdf2Binary.charAt(c);
                    c++;
                }
            }
           return String.valueOf(cellDataArray);
        }
        catch (Exception e)
        {
            Log.e(Config.log_id," getMdf2BinaryData error"+ e.getMessage());
        }
        return null;
    }
    public String getMdf1BinaryData()
    {
        try {
            String data = "";
            if (!getMdf1().equalsIgnoreCase("") && getMdf1() != null) {
                data = getMdf1();
                while (data.length() != 76) {
                    data = "0" + data;
                }
                String gridDataInBinary = "";
                for (int i = 0; i < data.length(); i++) {
                    gridDataInBinary += CommonOperation.HexToBinary(data.charAt(i) + "");
                }

                gridDataInBinary = gridDataInBinary.substring(2, 302);
                return gridDataInBinary;
            }
        }catch (Exception e)
        {

        }
        return null;
    }

    public void setMdf2(String mdf2) {
        this.mdf2 = mdf2;
    }

    public Arena(int numRow, int numCol,GoalProperty goalProperty,    StartProperty startProperty, int robotStartX, int robotStartY, int robotDirection) {
        this.numRow = numRow;
        this.numCol = numCol;
        this.robot=new Robot(robotStartX,robotStartY,robotDirection,this);
        this.goalProperty=goalProperty;
        this.startProperty=startProperty;
        cellArray= new Cell[numRow][numCol];
        for (int x = 0; x < getNumRow(); x++) {
            for (int y = 0; y < getNumCol(); y++) {
                cellArray[x][y] = new Cell(x, y);
            }
        }

    }

    public boolean isAuto() {
        return isAuto;
    }

    public void setAuto(boolean auto) {
        isAuto = auto;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }

    public Cell[][] getCellArray() {
        return cellArray;
    }

    public GoalProperty getGoalProperty() {
        return goalProperty;
    }

    public StartProperty getStartProperty() {
        return startProperty;
    }

    public void setStartProperty(StartProperty startProperty) {
        this.startProperty = startProperty;
    }

    public int getNumRow() {
        return numRow;
    }

    public int getNumCol() {
        return numCol;
    }

    public Robot getRobot() {
        return robot;
    }

    public void updateExplorationCellProperty(String gridDataInBinary) {
        int count = 0;
        //cell bg
        try {
            for (int y = 0; y < getNumCol(); y++)
                for (int x = 0; x < getNumRow(); x++) {
                    {
                        if (gridDataInBinary.charAt(count) == '1')
                            cellArray[x][y].setExplored(true);
                        else
                            cellArray[x][y].setExplored(false);
                        count++;
                    }
                }
        } catch (Exception e) {
            Log.e(Config.log_id,e.getMessage());

        }
    }
    public void updateObstacleCellProperty(String gridDataInBinary) {
        int count = 0;
        try {

            for (int y = 0; y < getNumCol(); y++)
                for (int x = 0; x < getNumRow(); x++) {
                    {
                        if (gridDataInBinary.charAt(count) == '1')
                            cellArray[x][y].setHaveObstacle(true);
                        else
                            cellArray[x][y].setHaveObstacle(false);
                        count++;
                    }
                }
        } catch (Exception e) {
            Log.e(Config.log_id,e.getMessage());

        }
    }

    public void reset() {

        setStartProperty(new StartProperty(Config.DEAFULT_START_X, Config.DEAFULT_START_Y));
        setStarted(false);
        setAuto(false);
        for (int x = 0; x < getNumRow(); x++) {
            for (int y = 0; y < getNumCol(); y++) {
                cellArray[x][y] = new Cell(x, y);
            }
        }

    }


    public Boolean checkObstacles(Robot.Move move) {

        Robot tempRobot = new Robot(getRobot().getX(),getRobot().getY(),getRobot().getDirection(),null);
        tempRobot.move(move);
        try{
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {

                    if (getCellArray() != null) {
                        getCellArray()[tempRobot.getX() + j][tempRobot.getY() + i].getY();
                    }
                }
            }
        }catch (Exception e)
        {
            Log.e(Config.log_id,"you have an error");
            return  false;
        }
        return true;
    }


}
