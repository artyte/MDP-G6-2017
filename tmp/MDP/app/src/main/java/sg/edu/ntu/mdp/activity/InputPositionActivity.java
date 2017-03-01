package sg.edu.ntu.mdp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import sg.edu.ntu.mdp.R;
import sg.edu.ntu.mdp.common.Config;
import sg.edu.ntu.mdp.model.arena.Arena;
import sg.edu.ntu.mdp.model.arena.ArenaView;
import sg.edu.ntu.mdp.model.arena.GoalProperty;
import sg.edu.ntu.mdp.model.arena.Robot;
import sg.edu.ntu.mdp.model.arena.StartProperty;

public class InputPositionActivity extends AppCompatActivity {

    ArenaView arenaView;

    Arena arena;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_position);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        invalidateOptionsMenu();
        setTitle("Select the position of the robot");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        arenaView = (ArenaView) findViewById(R.id.arenaView);
        if(savedInstanceState!=null)
        {
            if(savedInstanceState.getSerializable("arena")!=null )
            {
                arena = (Arena) savedInstanceState.getSerializable("arena");
                arenaView.setupArena(arena,ArenaView.SELECT_POS_VIEW);
            }
        }
        else {
            arena= new Arena(Config.DEFAULT_NO_OF_ROW,Config.DEFAULT_NO_OF_COL, new GoalProperty(Config.DEFAULT_GOAL_X,Config.DEFAULT_GOAL_Y),new StartProperty(Config.DEFAULT_START_X,Config.DEFAULT_START_Y) ,Config.DEFAULT_START_X,Config.DEFAULT_START_Y,Config.DEFAULT_ROBOT_HEAD);
            Log.e(Config.log_id,"robot x "+arena.getRobot().getX());
            arenaView.setupArena(arena,ArenaView.SELECT_POS_VIEW);
        }


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.input_pos, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        }
        else if (item.getItemId() == R.id.menu_done) {
            returnResult(arenaView.getArena().getRobot());
        }
        else if (item.getItemId() == R.id.menu_rotate) {

            arenaView.move(Robot.Move.RIGHT);
            //arenaView.getArena().reset();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("arena",arenaView.getArena());


    }

    public void returnResult(Robot robot)
    {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("robotInput",robot);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }
}
