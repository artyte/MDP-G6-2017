package mdp.robotxplorer.beta;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class Beta_ArenaView extends View {
    /*public static int MAZE_VIEW = 1;
    public static int SELECT_POS_VIEW = 2;
    private int gridSize;
    private int viewMode = MAZE_VIEW;
    private int numCol = 20;
    private int numRow = 15;
    protected MazeGridMap mazeGridMap;
    private boolean forSelection = false;
    protected Beta_Arena arena;*/

    public Beta_ArenaView(Context context) {
        this(context, null);
    }

    public Beta_ArenaView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /*public void setupArena(Beta_Arena arena) {
        this.arena = arena;
        mazeGridMap = new MazeGridMap(arena);
        arena.getBetaRobot().exploreCurrentPosition();
    }

    public void setupArena(Beta_Arena arena, int viewMode) {
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
        arena.getBetaRobot().draw(canvas, gridSize, getContext());

    }

    public void move(Beta_Robot.Move move) {
        if (move == Beta_Robot.Move.UP) {
            arena.getBetaRobot().move(Beta_Robot.Move.UP);

        } else if (move == Beta_Robot.Move.LEFT) {
            arena.getBetaRobot().move(Beta_Robot.Move.LEFT);

        } else if (move == Beta_Robot.Move.RIGHT) {
            arena.getBetaRobot().move(Beta_Robot.Move.RIGHT);
        }

        invalidate();
    }

    public Beta_Arena getArena() {
        return arena;
    }

    //update explored
    public void gridUpdateMDF1( ) {
        String data=getArena().getMdf1BinaryData();
        Log.e(Config.log_id,"Grid Update MDF1: " + data);

        if (data != null) {
            arena.updateExplorationCellProperty(Operation.convertGridFormat(data));
        }
    }

    public void gridUpdateMDF2( ) {
        try {
            String convertedData = Operation.convertGridFormat(getArena().getMdf2BinaryData());
            arena.updateObstacleCellProperty(convertedData);
            Log.e(Config.log_id,"convertedData " + convertedData);

        } catch (Exception e) {
            Log.e(Config.log_id," gridupdate error "+ e.getMessage());
        }

    }
    public void gridUpdate(String gridDataInHex) {
        Log.e(Config.log_id, "grid hex " + gridDataInHex);
        String gridDataInBinary = "";
        for (int i = 0; i < gridDataInHex.length(); i++) {
            gridDataInBinary += Operation.HexToBinary(gridDataInHex.charAt(i) + "");
        }

        Log.e(Config.log_id, "gridDataInBinary: " + gridDataInBinary);
        arena.updateObstacleCellProperty(gridDataInBinary);
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
                    int y = (int) ((event.getX() - startX) / gridSize);
                    int x = (int) ((event.getY() - startY) / gridSize);

                    y = y + 1;
                    x = x - 1;
                    y = Math.abs(y - (numCol - 1));

                    Log.e(Config.log_id, "ROW X: " + x + "COL Y: " + y);

                    try {
                        if (y <= 17 && x <= 12) {
                            arena.getBetaRobot().setXPos(x);
                            arena.getBetaRobot().setYPos(y);

                            arena.getStartProperty().setX(x);
                            arena.getStartProperty().setY(y);

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
            if(getContext() instanceof MainActivity) {
                MainActivity mainActivity =  (MainActivity) getContext();
                mainActivity.btnSendGridUpdate();
            }
        }

        return super.onTouchEvent(event);
    }*/
}