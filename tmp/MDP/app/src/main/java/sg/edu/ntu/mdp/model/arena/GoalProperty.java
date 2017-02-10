package sg.edu.ntu.mdp.model.arena;

import java.io.Serializable;

/**
 * Created by ericl on 16/09/2016.
 */
public class GoalProperty implements Serializable{



    int x,y;

    public GoalProperty(int x, int y) {
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
}
