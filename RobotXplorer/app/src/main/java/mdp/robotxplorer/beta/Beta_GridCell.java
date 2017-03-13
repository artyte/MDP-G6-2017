package mdp.robotxplorer.beta;

public class Beta_GridCell {
    private boolean explored, haveObstacle;
    private int x, y;

    public Beta_GridCell(int x, int y) {
        this.x = x;
        this.y = y;

        explored = false;
        haveObstacle = false;
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

    public boolean getExplored() {
        return explored;
    }

    public void setExplored(boolean explored) {
        this.explored = explored;
    }

    public boolean getHaveObstacle() {
        return haveObstacle;
    }

    public void setHaveObstacle(boolean haveObstacle) {
        this.haveObstacle = haveObstacle;
    }
}