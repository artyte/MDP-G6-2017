package sg.edu.ntu.mdp.model.arena;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import java.io.Serializable;
import sg.edu.ntu.mdp.common.Config;

public class MazeGridMap implements Serializable {
    private Canvas tempCanvas;
    private Arena arena;

    public MazeGridMap(Arena arena) {
        this.arena = arena;
        Log.e(Config.log_id, "row " + arena.getNumRow() + "");
        Log.e(Config.log_id, "colm " + arena.getNumCol() + "");
        //initialize the cell
    }

    public void draw(Canvas canvas, int gridSize) {
        this.tempCanvas = canvas;

        //cell bg
        for (int i = 0; i < arena.getNumRow(); i++) {
            for (int j = 0; j < arena.getNumCol(); j++) {
               arena.getCellArray()[i][j].draw(canvas, gridSize);
            }
        }

        //draw start
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                drawStart(canvas, gridSize, arena.getStartProperty().getX() + x, arena.getStartProperty().getY() + y);
            }
        }

        //draw goal
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                drawGoal(canvas, gridSize, arena.getGoalProperty().x + x, arena.getGoalProperty().y + y);
            }
        }
    }

    private void drawGoal(Canvas canvas, int gridSize, int x, int y) {
        int xPos = gridSize * y;
        int yPos = gridSize * x;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(Config.COLOR_GOAL);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(1);

        //drawing the rectangle
        float left = gridSize / 2 + gridSize * y; // x1
        float top = gridSize / 2 + gridSize * x; // y1
        float right = left + gridSize; // width (distance from X1 to X2)
        float bottom = top + gridSize; // height (distance from Y1 to Y2)
        RectF myRect = new RectF(left, top, right, bottom);
        canvas.drawRect(myRect, paint);

        // border
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(1);
        canvas.drawRect(myRect, paint);

        //draw text
        canvas.save();
        canvas.drawText("G", xPos - 10 + gridSize, yPos + gridSize, paint);
        canvas.restore();


    }

    private void drawStart(Canvas canvas, int gridSize, int x, int y) {
        //drawing the rectangle
        float left = gridSize / 2 + gridSize * (20-1-y);
        float top = gridSize / 2 + gridSize * (x); // y1
        float right = left + gridSize; // width (distance from X1 to X2)
        float bottom = top + gridSize; // height (distance from Y1 to Y2)

        RectF rect = new RectF(left, top, right, bottom);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(Config.COLOR_START);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(1);
        canvas.drawRect(rect, paint);

        // border
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(1);
        canvas.drawRect(rect, paint);
        //draw text

        canvas.save();
        canvas.drawText("S", left +gridSize/2-10, top + gridSize/2, paint);
        canvas.restore();
    }






}
