package mdp.robotxplorer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mdp.robotxplorer.R;
import mdp.robotxplorer.arena.Arena;
import mdp.robotxplorer.arena.ArenaView;
import mdp.robotxplorer.arena.Robot;

public class ArenaFragment extends Fragment {
    private ArenaView arenaView;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("arena", arenaView.getArena());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        View mView = inflater.inflate(R.layout.fragment_arena, container, false);
        arenaView = (ArenaView) mView.findViewById(R.id.arenaView);

        if (savedInstanceState != null) {
            Arena arena = (Arena) savedInstanceState.getSerializable("arena");

            if (arena != null)
                arenaView.setupArena(arena);
        } else
            arenaView.setupArena(new Arena());

        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public ArenaView getArenaView() {
        return arenaView;
    }

    public Arena getArena() {
        return arenaView.getArena();
    }

    public void moveRobot(int x, int y, int direction) {
        arenaView.getArena().getRobot().setPosition(x, y);

        switch (direction) {
            case 0:
                arenaView.getArena().getRobot().setDirection(Robot.Direction.EAST);
                break;

            case 90:
                arenaView.getArena().getRobot().setDirection(Robot.Direction.SOUTH);
                break;

            case 180:
                arenaView.getArena().getRobot().setDirection(Robot.Direction.WEST);
                break;

            case 270:
                arenaView.getArena().getRobot().setDirection(Robot.Direction.NORTH);
                break;
        }

        arenaView.invalidate();
    }

    public void moveForward() {
        arenaView.getArena().getRobot().moveForward();
        arenaView.invalidate();
    }

    public void turnLeft() {
        arenaView.getArena().getRobot().turnLeft();
        arenaView.invalidate();
    }

    public void turnRight() {
        arenaView.getArena().getRobot().turnRight();
        arenaView.invalidate();
    }

    public void gridUpdate(String gridData) {
        arenaView.getArena().updateGridMap(gridData);
        arenaView.invalidate();
    }

    public void statusUpdate(String status) {
        arenaView.getArena().getRobot().setStatus(status);
    }

    public void resetGrid() {
        arenaView.getArena().resetArena();
        arenaView.invalidate();
    }

    public Robot getRobot() {
        return arenaView.getArena().getRobot();
    }

    public String getMDF1() {
        return arenaView.getArena().getMDF1();
    }

    public String getMDF2() {
        return arenaView.getArena().getMDF2();
    }
}