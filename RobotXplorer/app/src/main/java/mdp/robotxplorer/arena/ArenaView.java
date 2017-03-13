package mdp.robotxplorer.arena;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import mdp.robotxplorer.activity.MainActivity;
import mdp.robotxplorer.common.Config;
import mdp.robotxplorer.common.Operation;

public class ArenaView extends View {
    public static int MAZE_VIEW = 1;
    public static int SELECT_POS_VIEW = 2;
    private int gridSize;
    private int viewMode = MAZE_VIEW;
    private int numCol = 20;
    private int numRow = 15;
    protected MazeGridMap mazeGridMap;
    //private boolean forSelection = false;
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
        Log.e(Config.log_id, "Height: " + canvas.getHeight() + " Width: " + canvas.getWidth());

        gridSize = (getWidth() / (numCol + 1));
        canvas.drawColor(Color.WHITE);

        mazeGridMap.draw(canvas, gridSize); //draw maze grid map
        arena.getRobot().draw(canvas, gridSize, getContext());//draw robot
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
    /*public void gridUpdateMDF1( ) {
        String data=getArena().getMdf1BinaryData();
        Log.e(Config.log_id,"gridUpdateMDF1: "+data);

        if (data != null) {
            arena.updateExplorationCellProperty(Operation.convertGridFormat(data));
        }
    }

    public void gridUpdateMDF2( ) {
        try {
            String convertedData = Operation.convertGridFormat(getArena().getMdf2BinaryData());
            arena.updateObstacleCellProperty(convertedData);
            Log.e(Config.log_id,"convertedData "+convertedData);

        } catch (Exception e) {
            Log.e(Config.log_id," gridupdate error "+ e.getMessage());
        }
    }*/

    public void gridUpdate(String gridData) {
        Log.e(Config.log_id, gridData);
        String gridDataInTernary = transpose(gridData);

        /*for (int i = 0; i < gridDataInHex.length(); i++) {
            gridDataInTernary += CommonOperation.HexToBinary(gridDataInHex.charAt(i) + "");
        }*/

        arena.updateObstacleCellProperty(gridDataInTernary);
    }

    public String transpose(String gridData) {
        int gridlen = gridData.length();
        String[] gridRow = new String[(int) Math.ceil((double) gridlen / (double) 20)];

        for (int i = 0; i < gridRow.length; i++)
            gridRow[i] = gridData.substring(i * 20, Math.min(gridlen, (i + 1) * 20));

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
                float endX = (gridSize / 2) + (gridSize * numCol);
                float endY = (gridSize / 2) + (gridSize * numRow);

                if (event.getX() > startX && event.getX() < endX && event.getY() > startY && event.getY() < endY) {
                    int y = (int) ((event.getX() - startX) / (gridSize));
                    int x = (int) ((event.getY() - startY) / (gridSize));

                    //y = y + 1;
                    //x = x - 1;

                    y = Math.abs(y - (numCol - 1));
                    Log.e(Config.log_id, "ROW X: " + x + "COL Y: " + y);

                    try {
                        if (y >= 1 && y <= 18 && x >= 1 && x <= 13) {
                            Operation.showToast(getContext(), "Coordinates: " + x + ", " + y);

                            arena.getRobot().setX(x - 1);
                            arena.getRobot().setY(y + 1);
                            arena.getStartProperty().setX(x - 1);
                            arena.getStartProperty().setY(y + 1);
                        } else {
                            Operation.showToast(getContext(), "Wrong Position");
                        }
                    } catch (Exception e) {
                        Operation.showToast(getContext(), "Wrong Location");
                    }

                    invalidate();
                }
            }
        } else {
            if (getContext() instanceof MainActivity) {
                MainActivity mainActivity =  (MainActivity) getContext();
                mainActivity.btnSendGridUpdate();
            }
        }

        return super.onTouchEvent(event);
    }
}
