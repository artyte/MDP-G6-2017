package sg.edu.ntu.mdp.model.arena;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import sg.edu.ntu.mdp.activity.MainActivity;
import sg.edu.ntu.mdp.common.CommonOperation;
import sg.edu.ntu.mdp.common.Config;

public class ArenaView extends View {
    public static int MAZE_VIEW = 1;
    public static int SELECT_POS_VIEW = 2;
    private int gridSize;
    private int viewMode = MAZE_VIEW;
    private int numCol = 20;
    private int numRow = 15;
    protected MazeGridMap mazeGridMap;
    private boolean forSelection = false;
    protected Arena arena;

    public ArenaView(Context context) {
        this(context, null);
    }

    public ArenaView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void setupArena(Arena arena) {
        this.arena = arena;
        mazeGridMap = new MazeGridMap(arena);
        arena.getRobot().exploreCurrentPosition();

    }

    public void setupArena(Arena arena, int viewMode) {
        this.viewMode = viewMode;
        this.arena = arena;
        mazeGridMap = new MazeGridMap(arena);
    }

    @Override
    public void onDraw(Canvas canvas) {
        Log.e(Config.log_id, "draw arena map");
        // gridSize = ((getWidth() - (getWidth()/ numCol)  - (getWidth()/ numCol )   )  / numCol );
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridSize = ((getWidth() - (getWidth() / numCol)) / numCol);
        } else {
            boolean loop = true;
            int i = 0;
            while (loop) {
                gridSize = (getWidth() / (numCol + i + 2));
                i++;
                if (gridSize * numRow < getHeight()) {
                    loop = false;
                    gridSize = (getWidth() / (numCol + i + 2));
                }

            }
        }
        //draw maze grid map
        mazeGridMap.draw(canvas, gridSize);
        //draw robot
        arena.getRobot().draw(canvas, gridSize, getContext());

    }

    public void move(Robot.Move move) {
        if (move == Robot.Move.UP) {
            arena.getRobot().move(Robot.Move.UP);
        } else if (move == Robot.Move.LEFT) {
            arena.getRobot().move(Robot.Move.LEFT);
        } else if (move == Robot.Move.RIGHT) {
            arena.getRobot().move(Robot.Move.RIGHT);
        }
        invalidate();

    }



    public Arena getArena() {
        return arena;
    }
    //update explored

    /*
    public void gridUpdateMDF1( ) {
        String data=getArena().getMdf1BinaryData();
        Log.e(Config.log_id,"gridUpdateMDF1: "+data);
        if(data!=null)
        {
            arena.updateExplorationCellProperty(CommonOperation.convertGridFormat(data));
        }

    }

    public void gridUpdateMDF2( ) {
        try {

            String convertedData = CommonOperation.convertGridFormat(getArena().getMdf2BinaryData());
            arena.updateObstacleCellProperty(convertedData);
            Log.e(Config.log_id,"convertedData "+convertedData);
        }
        catch (Exception e)
        {
            Log.e(Config.log_id," gridupdate error "+ e.getMessage());
        }
    }*/

    public void gridUpdate(String gridData) {
        Log.e(Config.log_id, gridData);
        String gridDataInTernary = transpose(gridData);
        /*for (int i = 0; i < gridDataInHex.length(); i++) {
            gridDataInTernary += CommonOperation.HexToBinary(gridDataInHex.charAt(i) + "");
        }*/
        Log.e("fuck work", "grid data in Ternary: " + gridDataInTernary);
        arena.updateObstacleCellProperty(gridDataInTernary);

    }

    public String transpose(String gridData) {
        int gridlen = gridData.length();
        String[] gridRow = new String[(int)Math.ceil((double)gridlen/(double)20)];
        for (int i=0; i<gridRow.length; i++)
            gridRow[i] = gridData.substring(i*20, Math.min(gridlen, (i+1)*20));

        Log.e("String length", Integer.toString(gridRow.length));
        String transposed = "";
        for(int i = 19; i >= 0; i--) {
            for (int j = 0; j <= 14; j++) {
                transposed += gridRow[j].charAt(i);
            }
        }
        return transposed;
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        super.setOnTouchListener(l);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.viewMode == SELECT_POS_VIEW) {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                float startX = gridSize / 2;
                float startY = gridSize / 2;
                float endX = gridSize / 2 + gridSize * numCol;
                float endY = gridSize / 2 + gridSize * numRow;

                if (event.getX() > startX && event.getX() < endX && event.getY() > startY && event.getY() < endY) {
                    int y = (int) ((event.getX() - startX) / (gridSize));
                    int x = (int) ((event.getY() - startY) / (gridSize));
                    y = y + 1;
                    x = x - 1;
                    y = Math.abs(y - (numCol - 1));
                    Log.e(Config.log_id, "ROW X: " + x + "COL Y: " + y);
                    try {

                        if (y <= 17 && x <= 12) {
                            arena.getRobot().setX(x);
                            arena.getRobot().setY(y);
                            arena.getStartProperty().setX(x);
                            arena.getStartProperty().setY(y);
                        } else {

                            new CommonOperation().showToast(getContext(), "Wrong Postion");
                        }
                    } catch (Exception e) {
                        new CommonOperation().showToast(getContext(), "Wrong Location");
                    }
                    invalidate();
                }
            }

        }
        else
        {
                if(getContext() instanceof MainActivity)
                {
                    MainActivity mainActivity =  (MainActivity)getContext();
                    mainActivity.btnSendGridUpdate();
                }
        }
        return super.onTouchEvent(event);
    }



}
