package mdp.robotxplorer.arena;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

import mdp.robotxplorer.R;
import mdp.robotxplorer.common.Config;

public class ArenaRenderer {
    protected static void renderArena(Canvas canvas, Arena arena, int gridSize, Context context) {
        canvas.drawColor(Color.WHITE);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);

        renderGridMap(canvas, paint, arena.getGridMap(), gridSize);
        renderStartZone(canvas, paint, gridSize);
        renderGoalZone(canvas, paint, gridSize);
        renderRobot(canvas, arena.getRobot(), gridSize, context);
    }

    private static void renderRobot(Canvas canvas, Robot robot, int gridSize, Context context) {
        Paint paint = new Paint();
        Bitmap b = BitmapFactory.decodeResource(context.getResources(), R.mipmap.robot);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, gridSize * 3, gridSize * 3, false);
        Matrix matrix = new Matrix();

        if (robot.getFacingDirection() == Robot.Direction.NORTH) {
            matrix.setRotate(0, scaledBitmap.getWidth() / 2, scaledBitmap.getHeight() / 2);

        } else if (robot.getFacingDirection() == Robot.Direction.EAST) {
            matrix.setRotate(90, scaledBitmap.getWidth() / 2, scaledBitmap.getHeight() / 2);

        } else if (robot.getFacingDirection() == Robot.Direction.SOUTH) {
            matrix.setRotate(180, scaledBitmap.getWidth() / 2, scaledBitmap.getHeight() / 2);

        } else if (robot.getFacingDirection() == Robot.Direction.WEST) {
            matrix.setRotate(270, scaledBitmap.getWidth() / 2, scaledBitmap.getHeight() / 2);
        }

        matrix.postTranslate(gridSize / 2 + gridSize * (robot.getYPos() - 1),
                gridSize / 2 + gridSize * (robot.getXPos() - 1));
        canvas.drawBitmap(scaledBitmap, matrix, paint);
    }

    private static void renderGridMap(Canvas canvas, Paint paint, Arena.GridCell[][] gridMap, int gridSize) {
        for (int x = 0; x < Config.ARENA_WIDTH; x ++) {
            for (int y = 0; y < Config.ARENA_LENGTH; y ++) {
                paint.setStyle(Paint.Style.FILL);

                float left   = gridSize / 2 + gridSize * y;
                float right  = left + gridSize;

                float top    = gridSize / 2 + gridSize * x;
                float bottom = top + gridSize;

                RectF rect = new RectF(left, top, right, bottom);

                switch (gridMap[x][y]) {
                    case UNEXPLORED:
                        rectFillColor(canvas, paint, rect, Config.UNEXPLORED);
                        break;

                    case FREE_SPACE:
                        rectFillColor(canvas, paint, rect, Config.FREE_SPACE);
                        break;

                    case OBSTACLE:
                        rectFillColor(canvas, paint, rect, Config.OBSTACLE);
                        break;
                }

                canvas.drawRect(rect, paint);
                rectDrawBorder(canvas, paint, rect);
            }
        }
    }

    private static void renderStartZone(Canvas canvas, Paint paint, int gridSize) {
        for (int x = 0; x < 3; x ++) {
            for (int y = 0; y < 3; y ++) {
                float left   = gridSize / 2 + gridSize * y;
                float right  = left + gridSize;

                float top    = gridSize / 2 + gridSize * x;
                float bottom = top + gridSize;

                RectF rect = new RectF(left, top, right, bottom);
                rectFillColor(canvas, paint, rect, Config.START);
                rectDrawBorder(canvas, paint, rect);
                canvas.drawText("S", left + gridSize / 2, top + gridSize / 2, paint);
            }
        }
    }

    private static void renderGoalZone(Canvas canvas, Paint paint, int gridSize) {
        for (int x = Config.ARENA_WIDTH - 3; x < Config.ARENA_WIDTH; x ++) {
            for (int y = Config.ARENA_LENGTH - 3; y < Config.ARENA_LENGTH; y ++) {
                float left   = gridSize / 2 + gridSize * y;
                float right  = left + gridSize;

                float top    = gridSize / 2 + gridSize * x;
                float bottom = top + gridSize;

                RectF rect = new RectF(left, top, right, bottom);
                rectFillColor(canvas, paint, rect, Config.GOAL);
                rectDrawBorder(canvas, paint, rect);
                canvas.drawText("G", left + gridSize / 2, top + gridSize / 2, paint);
            }
        }
    }

    private static void rectFillColor(Canvas canvas, Paint paint, RectF rect, int color) {
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(rect, paint);
    }

    private static void rectDrawBorder(Canvas canvas, Paint paint, RectF rect) {
        paint.setColor(Config.BORDER);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        canvas.drawRect(rect, paint);
    }
}