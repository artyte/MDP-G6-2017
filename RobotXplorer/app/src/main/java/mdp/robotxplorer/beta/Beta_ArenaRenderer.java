package mdp.robotxplorer.beta;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

import mdp.robotxplorer.R;
import mdp.robotxplorer.common.Config;

public class Beta_ArenaRenderer {
    public static void renderArena(Beta_Arena betaArena) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.FILL);

        //Canvas canvas = renderGridMap(canvas, betaArena.getGridMap(), 10);
    }

    private static void renderRobot(Canvas canvas, Beta_Robot betaRobot, int gridSize, Context context) {
        Paint paint = new Paint();
        Bitmap b = BitmapFactory.decodeResource(context.getResources(), R.mipmap.robot);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, gridSize * 3, gridSize * 3, false);
        Matrix matrix = new Matrix();

        if (betaRobot.getFacingDirection() == Beta_Robot.Direction.EAST) {
            matrix.setRotate(0, scaledBitmap.getWidth() / 2, scaledBitmap.getHeight() / 2);

        } else if (betaRobot.getFacingDirection() == Beta_Robot.Direction.SOUTH) {
            matrix.setRotate(90, scaledBitmap.getWidth() / 2, scaledBitmap.getHeight() / 2);

        } else if (betaRobot.getFacingDirection() == Beta_Robot.Direction.WEST) {
            matrix.setRotate(180, scaledBitmap.getWidth() / 2, scaledBitmap.getHeight() / 2);

        } else if (betaRobot.getFacingDirection() == Beta_Robot.Direction.NORTH) {
            matrix.setRotate(270, scaledBitmap.getWidth() / 2, scaledBitmap.getHeight() / 2);
        }

        matrix.postTranslate(gridSize / 2 + gridSize * (17 - betaRobot.getYPos()), gridSize / 2 + gridSize * betaRobot.getXPos());
        canvas.drawBitmap(scaledBitmap, matrix, paint);
    }

    private static void renderGridMap(Canvas canvas, Beta_GridCell[][] gridMap, int gridSize) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);

        for (int i = 0; i < gridMap.length; i ++) {
            for (int j = 0; j < gridMap[i].length; j ++) {
                paint.setStyle(Paint.Style.FILL);

                //float left = gridSize / 2 + gridSize * (19 - gridMap[i][j].getY());
                //float top  = gridSize / 2 + gridSize * gridMap[i][j].getX();

                float left   = gridSize / 2 + gridSize * j;
                float right  = left + gridSize; // width (distance from X1 to X2)

                float top    = gridSize / 2 + gridSize * i;
                float bottom = top + gridSize; // height (distance from Y1 to Y2)

                if (gridMap[i][j].getHaveObstacle()) {
                    paint.setColor(Config.OBSTACLE);
                } else {
                    paint.setColor(gridMap[i][j].getExplored() ?
                            Config.EXPLORED : Config.UNEXPLORED);
                }

                // Rendering the individual grid cell
                RectF rect = new RectF(left, top, right, bottom);
                canvas.drawRect(rect, paint);

                // Rendering the border
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Config.BORDER);
                paint.setStrokeWidth(1);
                canvas.drawRect(rect, paint);
            }
        }
    }

    private static void renderStartZone(Canvas canvas, int gridSize, int x, int y) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);

        float left = gridSize / 2 + gridSize * (20-1-y);
        float top = gridSize / 2 + gridSize * (x); // y1
        float right = left + gridSize; // width (distance from X1 to X2)
        float bottom = top + gridSize; // height (distance from Y1 to Y2)

        RectF rect = new RectF(left, top, right, bottom);
        rectFillColor( canvas, rect, Config.START);
        rectDrawBorder(canvas, rect, 1);
        //draw text

        canvas.save();
        canvas.drawText("S", left + gridSize/2-10, top + gridSize/2, paint);
        canvas.restore();
    }

    private static void renderGoalZone(Canvas canvas, int gridSize, int x, int y) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);

        int xPos = gridSize * y;
        int yPos = gridSize * x;

        //drawing the rectangle
        float left = gridSize / 2 + gridSize * y; // x1
        float top = gridSize / 2 + gridSize * x; // y1
        float right = left + gridSize; // width (distance from X1 to X2)
        float bottom = top + gridSize; // height (distance from Y1 to Y2)

        RectF rect = new RectF(left, top, right, bottom);
        rectFillColor( canvas, rect, Config.GOAL);
        rectDrawBorder(canvas, rect, 1);

        //draw text
        canvas.save();
        canvas.drawText("G", xPos - 10 + gridSize, yPos + gridSize, paint);
        canvas.restore();
    }

    private static void rectFillColor(Canvas canvas, RectF rect, int color) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);

        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(rect, paint);
    }

    private static void rectDrawBorder(Canvas canvas, RectF rect, int strokeWidth) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        canvas.drawRect(rect, paint);
    }
}