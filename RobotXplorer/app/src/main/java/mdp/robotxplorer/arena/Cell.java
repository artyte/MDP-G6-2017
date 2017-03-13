package mdp.robotxplorer.arena;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.io.Serializable;
import java.util.ArrayList;

import mdp.robotxplorer.common.Config;

public class Cell implements Serializable {
    boolean isExplored = false, haveObstacle = false;

    private int x;
    private int y;

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
        float left = gridSize / 2 + gridSize * x;
        float top =  gridSize / 2 + gridSize * y;//(19 - y);// y1
        float right = left + gridSize; // width (distance from X1 to X2)
        float bottom = top + gridSize; // height (distance from Y1 to Y2)
        RectF rect = new RectF(left, top, right, bottom);

        if (isExplored && haveObstacle) {
            paint.setColor(Config.OBSTACLE);

        } else if (isExplored && !haveObstacle) {
            paint.setColor(Config.EXPLORED);

        } else if (!isExplored && !haveObstacle) {
            paint.setColor(Config.UNEXPLORED);
        }

        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(rect, paint);
        // border
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Config.BORDER);
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
