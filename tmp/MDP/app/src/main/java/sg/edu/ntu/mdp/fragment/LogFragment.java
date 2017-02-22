package sg.edu.ntu.mdp.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import sg.edu.ntu.mdp.R;
import sg.edu.ntu.mdp.adapter.LogViewAdapter;
import sg.edu.ntu.mdp.common.Config;

public class LogFragment extends android.support.v4.app.ListFragment implements AdapterView.OnItemLongClickListener {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    ArrayList logList;
    public LogFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static LogFragment newInstance(int columnCount) {
        LogFragment fragment = new LogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log_list, container, false);


        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        if (logList != null) {

        ClipData clip = ClipData.newPlainText("a",logList.get(i).toString());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getActivity(), "Copied to Clipboard!", Toast.LENGTH_SHORT).show();

        }
        return false;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
    }

    public void addLog(ArrayList<String> logList) {
        this.logList=logList;
        if (logList.size() >= 500) {
            try {
                logList.remove(499);
            } catch (Exception e) {
                Log.e(Config.log_id,e.getMessage());
            }
        }
        ListView lv = getListView();
        lv.setOnItemLongClickListener(this);
        LogViewAdapter adapter = new LogViewAdapter(getActivity(), logList);
        setListAdapter(null);
        setListAdapter(adapter);
    }

}
