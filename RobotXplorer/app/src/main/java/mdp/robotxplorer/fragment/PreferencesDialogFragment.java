package mdp.robotxplorer.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mdp.robotxplorer.R;

public class PreferencesDialogFragment extends DialogFragment {
    private View preferencesView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        preferencesView = inflater.inflate(R.layout.dialog_fragment_preferences, container, false);
        return preferencesView;
    }
}
