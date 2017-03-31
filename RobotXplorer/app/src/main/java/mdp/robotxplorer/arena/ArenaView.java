package mdp.robotxplorer.arena;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import mdp.robotxplorer.common.Config;
import mdp.robotxplorer.common.Operation;

public class ArenaView extends View {
    private Arena arena;
    private boolean inputPositionEnabled;
    private int gridSize;

    public ArenaView(Context context) {
        this(context, null);
    }

    public ArenaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inputPositionEnabled = false;
    }

    public void setupArena(Arena arena) {
        this.arena = arena;
    }

    public void setInputPositionEnabled(boolean b) {
        inputPositionEnabled = b;
    }

    @Override
    public void onDraw(Canvas canvas) {
        gridSize = canvas.getWidth() / (Config.ARENA_LENGTH + 1);
        ArenaRenderer.renderArena(canvas, arena, gridSize, getContext());
    }

    public Arena getArena() {
        return arena;
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        super.setOnTouchListener(l);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (inputPositionEnabled) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                float startX = gridSize / 2;
                float startY = gridSize / 2;

                float endX = gridSize / 2 + gridSize * Config.ARENA_LENGTH;
                float endY = gridSize / 2 + gridSize * Config.ARENA_WIDTH;

                if (event.getX() > startX && event.getX() < endX
                        && event.getY() > startY && event.getY() < endY) {

                    int y = (int) ((event.getX() - startX) / gridSize);
                    int x = (int) ((event.getY() - startY) / gridSize);

                    try {
                        if (x >= 1 && x <= Config.ARENA_WIDTH - 2 &&
                                y >= 1 && y <= Config.ARENA_LENGTH - 2) {
                            arena.getRobot().setPosition(x, y);

                        } else {
                            Operation.showToast(getContext(), "Invalid Position");
                        }
                    } catch (Exception e) {
                        Operation.showToast(getContext(), "Invalid Position");
                    }

                    invalidate();
                }
            }
        }

        return super.onTouchEvent(event);
    }
}