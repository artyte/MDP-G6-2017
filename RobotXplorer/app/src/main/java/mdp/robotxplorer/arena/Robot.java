package mdp.robotxplorer.arena;

import java.io.Serializable;

public class Robot implements Serializable {
    private int xPos, yPos;
    private Direction direction;
    private String status;

    public enum Direction {
        NORTH, SOUTH, EAST, WEST
    }

    public Robot(int xPos, int yPos, Direction direction) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.direction = direction;
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

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void moveForward() {
        switch (direction) {
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
        switch (direction) {
            case NORTH:
                direction = Direction.WEST;
                break;

            case SOUTH:
                direction = Direction.EAST;
                break;

            case EAST:
                direction = Direction.NORTH;
                break;

            case WEST:
                direction = Direction.SOUTH;
                break;
        }
    }

    public void turnRight() {
        switch (direction) {
            case NORTH:
                direction = Direction.EAST;
                break;

            case SOUTH:
                direction = Direction.WEST;
                break;

            case EAST:
                direction = Direction.SOUTH;
                break;

            case WEST:
                direction = Direction.NORTH;
                break;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Robot) {
            Robot robotObj = (Robot) obj;

            boolean xPosEqual = (xPos == robotObj.getXPos());
            boolean yPosEqual = (yPos == robotObj.getYPos());
            boolean directionEqual = (direction == robotObj.getDirection());
            boolean statusEqual = status.equals(robotObj.getStatus());

            return xPosEqual && yPosEqual && directionEqual && statusEqual;
        }

        return false;
    }
}