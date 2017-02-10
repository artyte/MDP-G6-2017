package sg.edu.ntu.mdp.model.arena;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import java.io.Serializable;

import sg.edu.ntu.mdp.R;
import sg.edu.ntu.mdp.common.CommonOperation;
import sg.edu.ntu.mdp.common.Config;

/**
 * Created by ericl on 09/09/2016.
 */
public class Robot implements Serializable {

    int x;
    int y;
    int direction;
    Arena arena;
    String status;


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

    public Robot(int x, int y, int direction, Arena arena
    ) {

        this.status = "na";
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.arena = arena;
    }

    public int getDirection() {
        return direction;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    private void turnRight() {
        direction = (direction + 1) % 4;
    }

    private void turnLeft() {

        direction = (direction - 1) % 4;
        if (direction < 0)
            direction = 3;
    }

    public void move(Move move) {
        if (move == Move.UP) {
            moveForward();
        } else if (move == Move.RIGHT) {
            turnRight();
        } else if (move == Move.LEFT) {
            turnLeft();
        }
        if(arena!=null)
        exploreCurrentPosition();
    }

    public boolean checkNewPosition(int newX, int newY) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (arena.getCellArray() != null) {
                    arena.getCellArray()[newX + j][newY + i].setExplored(true);
                }
            }
        }
    return true;
    }

    public void exploreCurrentPosition() {
        /*
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (arena.getCellArray() != null) {
                    arena.getCellArray()[x + j][y + i].setExplored(true);
                }
            }
        }
        */

    }

    private void moveForward() {
        if (direction == 0) {
            x = x - 1;
        } else if (direction == 1) {
            y = y - 1;
        } else if (direction == 2) {
            x = x + 1;
        } else if (direction == 3) {
            y = y + 1;
        }

    }


    public void draw(Canvas canvas, int gridSize, Context context) {
        Paint paint = new Paint();
        Bitmap b = BitmapFactory.decodeResource(context.getResources(), R.mipmap.robot);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, gridSize * 3, gridSize * 3, false);
        Matrix matrix = new Matrix();
        if (direction == 0) {
            matrix.setRotate(0, scaledBitmap.getWidth() / 2, scaledBitmap.getHeight() / 2);
        } else if (direction == 1) {
            matrix.setRotate(90, scaledBitmap.getWidth() / 2, scaledBitmap.getHeight() / 2);

        } else if (direction == 2) {
            matrix.setRotate(180, scaledBitmap.getWidth() / 2, scaledBitmap.getHeight() / 2);

        } else if (direction == 3) {
            matrix.setRotate(270, scaledBitmap.getWidth() / 2, scaledBitmap.getHeight() / 2);

        }

        matrix.postTranslate(gridSize / 2 + gridSize * (17 - y), gridSize / 2 + gridSize * x);
        canvas.drawBitmap(scaledBitmap, matrix, paint);

    }

    public void moveRobot(int x, int y, int direction) {
        setX(x);
        setY(y);
        setDirection(direction);
        if (arena.isStarted == false) {
            arena.reset();
            arena.setStartProperty(new StartProperty(x, y));
        }
        exploreCurrentPosition();
    }


    public enum Direction implements Serializable {
        UP(0), RIGHT(1), DOWN(2), LEFT(3);

        private final int number;

        private Direction(int number) {
            this.number = number;
        }

        public int getNumber() {
            return number;
        }
    }

    public enum Move implements Serializable {
        UP(0), RIGHT(1), LEFT(2);

        private final int number;

        private Move(int number) {
            this.number = number;
        }

        public int getNumber() {
            return number;
        }
    }
}
