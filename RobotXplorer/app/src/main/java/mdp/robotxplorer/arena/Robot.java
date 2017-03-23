package mdp.robotxplorer.arena;

import java.io.Serializable;

public class Robot implements Serializable {
    private int xPos, yPos;
    private Direction facingDirection;
    private String status;

    public enum Direction {
        NORTH, SOUTH, EAST, WEST
    }

    public Robot(int xPos, int yPos, Direction direction) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.facingDirection = direction;
        this.status = "N/A";
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }

    public void setPosition(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public Direction getFacingDirection() {
        return facingDirection;
    }

    public void setFacingDirection(Direction direction) {
        facingDirection = direction;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void moveForward() {
        switch (facingDirection) {
            case NORTH:
                if (xPos > 1) xPos --;
                break;

            case SOUTH:
                if (xPos < 13) xPos ++;
                break;

            case EAST:
                if (yPos < 18) yPos ++;
                break;

            case WEST:
                if (yPos > 1) yPos --;
                break;
        }
    }

    public void turnLeft() {
        switch (facingDirection) {
            case NORTH:
                facingDirection = Direction.WEST;
                break;

            case SOUTH:
                facingDirection = Direction.EAST;
                break;

            case EAST:
                facingDirection = Direction.NORTH;
                break;

            case WEST:
                facingDirection = Direction.SOUTH;
                break;
        }
    }

    public void turnRight() {
        switch (facingDirection) {
            case NORTH:
                facingDirection = Direction.EAST;
                break;

            case SOUTH:
                facingDirection = Direction.WEST;
                break;

            case EAST:
                facingDirection = Direction.SOUTH;
                break;

            case WEST:
                facingDirection = Direction.NORTH;
                break;
        }
    }
}
