package mdp.robotxplorer.beta;

public class Beta_Robot {
    private int xPos, yPos;
    private Direction facingDirection;
    private String status;

    public enum Direction {
        NORTH, SOUTH, EAST, WEST
    }

    public Beta_Robot(int xPos, int yPos, Direction direction) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.facingDirection = direction;
        this.status = "N/A";
    }

    public int getXPos() {
        return xPos;
    }

    public void setXPos(int xPos) {
        this.xPos = xPos;
    }

    public int getYPos() {
        return yPos;
    }

    public void setYPos(int yPos) {
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
                if (yPos > 1) yPos --;
                break;

            case SOUTH:
                if (yPos < 19) yPos ++;
                break;

            case EAST:
                if (xPos < 19) xPos ++;
                break;

            case WEST:
                if (xPos > 1) xPos --;
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
