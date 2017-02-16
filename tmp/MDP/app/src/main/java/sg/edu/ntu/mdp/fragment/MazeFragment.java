package sg.edu.ntu.mdp.fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import sg.edu.ntu.mdp.R;
import sg.edu.ntu.mdp.common.Config;
import sg.edu.ntu.mdp.model.arena.Arena;
import sg.edu.ntu.mdp.model.arena.ArenaView;
import sg.edu.ntu.mdp.model.arena.GoalProperty;
import sg.edu.ntu.mdp.model.arena.Robot;
import sg.edu.ntu.mdp.model.arena.StartProperty;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MazeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MazeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MazeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private View mView;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ArenaView arenaView;
    Arena arena;
    private OnFragmentInteractionListener mListener;
    public MazeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("arena",arenaView.getArena());


    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MazeFragment newInstance(String param1, String param2) {
        MazeFragment fragment = new MazeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        mView = inflater.inflate(R.layout.fragment_maze, container, false);
         arenaView = (ArenaView) mView.findViewById(R.id.arenaView);

        if(savedInstanceState!=null)
        {
            if(savedInstanceState.getSerializable("arena")!=null)
            {
                arena = (Arena) savedInstanceState.getSerializable("arena");
                arenaView.setupArena(arena);
                mListener.onUiUpdate(arena);

            }
        }
        else {
            arena= new Arena(Config.DEAFULT_NO_OF_ROW,Config.DEAFULT_NO_OF_COL, new GoalProperty(Config.DEAFULT_GOAL_X,Config.DEAFULT_GOAL_Y),new StartProperty(Config.DEAFULT_START_X,Config.DEAFULT_START_Y) ,Config.DEAFULT_START_X,Config.DEAFULT_START_Y,Config.DEAFULT_ROBOT_HEAD );
            Log.e(Config.log_id,"robot x "+arena.getRobot().getX());
            arenaView.setupArena(arena);
            mListener.onUiUpdate(arena);
        }
        return mView;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }


    public void moveRobot(int x, int y,int direction)
    {
        arena.getRobot().moveRobot(x,y,direction);
        arenaView.invalidate();
        mListener.onUiUpdate(arena);
    }
    public void btnMove(Robot.Move direction)
    {
        if (direction== Robot.Move.UP)
        {
            arenaView.move(Robot.Move.UP);

        }else
        if (direction== Robot.Move.LEFT)
        {
           arenaView.move(Robot.Move.LEFT);

        }else
        if (direction== Robot.Move.RIGHT)
        {
           arenaView.move(Robot.Move.RIGHT);
        }
        mListener.onUiUpdate(arenaView.getArena());
    }

    public void gridUpdate(String gridData) {
        arenaView.gridUpdate(gridData);
        arenaView.invalidate();
        mListener.onUiUpdate(arena);
    }
        //mdf1
    public void gridUpdateMDF1(String gridData) {
        arenaView.gridUpdateMDF1();
        arenaView.invalidate();
        mListener.onUiUpdate(arena);
    }
    //mdf2
    public void gridUpdateMDF2(String gridData) {
        arenaView.gridUpdateMDF2();
        arenaView.invalidate();
        mListener.onUiUpdate(arena);
    }
    public void statusUpdate(String status) {
        if(arena!=null && arena.getRobot()!=null)
        {
            arena.getRobot().setStatus(status);
            mListener.onUiUpdate(arena);
        }
    }

    public Arena getArena() {
        return arena;
    }



    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onUiUpdate(Arena arena);
    }


    public void resetArenaView()
    {
        arena= new Arena(Config.DEAFULT_NO_OF_ROW,Config.DEAFULT_NO_OF_COL, new GoalProperty(Config.DEAFULT_GOAL_X,Config.DEAFULT_GOAL_Y),new StartProperty(Config.DEAFULT_START_X,Config.DEAFULT_START_Y) ,Config.DEAFULT_START_X,Config.DEAFULT_START_Y,Config.DEAFULT_ROBOT_HEAD );
        Log.e(Config.log_id,"robot x "+arena.getRobot().getX());
        arenaView.setupArena(arena);
        moveRobot(Config.DEAFULT_START_X,Config.DEAFULT_START_Y,Config.DEAFULT_ROBOT_HEAD);
        mListener.onUiUpdate(arena);
        arenaView.invalidate();
    }
}
