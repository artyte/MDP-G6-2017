package sg.edu.ntu.mdp.model.arena;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import java.io.Serializable;
import java.util.ArrayList;
import sg.edu.ntu.mdp.common.Config;

public class Cell implements Serializable{
    Boolean isExplored=false;
    Boolean haveObstacle=false;

    private int x;
    private int y;
    private Canvas tempCanvas;
    private ArrayList obstacleArray;
    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }



    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void draw(Canvas canvas, int gridSize) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        //drawing the rectangle
        float left = gridSize / 2 + gridSize * (19-y);
        float top = gridSize / 2 + gridSize * (x); // y1
        float right = left + gridSize; // width (distance from X1 to X2)
        float bottom = top + gridSize; // height (distance from Y1 to Y2)
        RectF rect = new RectF(left, top, right, bottom);
        if (isExplored) {
            paint.setColor(Config.COLOR_CELL_EXPLORED);
        }else
        {
            paint.setColor(Config.COLOR_CELL_UNEXPLORED);
        }
        if (haveObstacle) {
            paint.setColor(Config.COLOR_OBSTACLE);
        }
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(rect, paint);
        // border
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Config.COLOR_CELL_BORDER);
        paint.setStrokeWidth(1);
        canvas.drawRect(rect, paint);

    }

    public void setExplored(Boolean explored) {
        isExplored = explored;
    }

    public void setHaveObstacle(Boolean haveObstacle) {
        this.haveObstacle = haveObstacle;
    }
 }
