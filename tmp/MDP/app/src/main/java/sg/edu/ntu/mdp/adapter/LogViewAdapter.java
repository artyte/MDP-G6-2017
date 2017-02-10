package sg.edu.ntu.mdp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import org.w3c.dom.Text;

import java.util.ArrayList;

import sg.edu.ntu.mdp.R;

/**
 * Created by Eric on 22/10/2015.
 */
public class LogViewAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> logList;
    private LayoutInflater inflater = null;

    public LogViewAdapter(Context activityInstance, ArrayList<String> logList) {
        // TODO Auto-generated constructor stub
        this.logList = logList;
        context = activityInstance;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return logList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub


        View view = convertView;
            view = inflater.inflate(R.layout.item_list_log, null);
        TextView idTextViewText = (TextView) view.findViewById(R.id.idTextViewText);
        idTextViewText.setText(logList.get(position));

        return view;
    }
}
