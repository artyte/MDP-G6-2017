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

    public void gridUpdate(String gridData) { arena.updateObstacleCellProperty(gridData); }

    /*
    public String transpose(String gridData) {
        int gridlen = gridData.length();
        String[] gridRow = new String[(int)Math.ceil((double)gridlen/(double)20)];
        for (int i=0; i<gridRow.length; i++)
            gridRow[i] = gridData.substring(i*20, Math.min(gridlen, (i+1)*20));

        String transposed = "";
        for(int i = 19; i >= 0; i--) {
            for (int j = 0; j <= 14; j++) {
                transposed += gridRow[j].charAt(i);
            }
        }
        return transposed;
    }*/

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

                    y = Math.abs(y - (numCol - 1));

                    try {
                        Operation.showToast(getContext(), "Coordinates: " + x + ", " + (0-y+19));

                        x += -1;
                        y += -1;

                        arena.getRobot().setX(x);
                        arena.getRobot().setY(y);
                        arena.getStartProperty().setX(x);
                        arena.getStartProperty().setY(y);
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
