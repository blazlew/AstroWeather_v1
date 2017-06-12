package ledsoon.astroweather;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class FavouritesFragment extends Fragment implements SettingsFragment.IDataSender {

    EditText etLocalization;
    Button bPlus;

    @Override
    public void sendToActivity(String a, String b, String c) {
        MainActivity.latitude = a;
        MainActivity.longitude =  b;
        MainActivity.refreshingTime = c;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favourites, container, false);
        etLocalization = (EditText) rootView.findViewById(R.id.etLocalization);
        bPlus = (Button) rootView.findViewById(R.id.bPlus);
        return rootView;
    }

}
