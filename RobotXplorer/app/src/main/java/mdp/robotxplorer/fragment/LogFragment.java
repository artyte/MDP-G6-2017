package mdp.robotxplorer.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import mdp.robotxplorer.R;
import mdp.robotxplorer.adapter.LogViewAdapter;
import mdp.robotxplorer.common.Config;

public class LogFragment extends ListFragment implements AdapterView.OnItemLongClickListener {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log_list, container, false);
        return view;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

        if (logList != null) {
            ClipData clip = ClipData.newPlainText("a", logList.get(i).toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getActivity(), "Copied to Clipboard!", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    public void addLog(ArrayList<String> logList) {
        this.logList = logList;

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