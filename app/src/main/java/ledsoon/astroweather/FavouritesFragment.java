package ledsoon.astroweather;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FavouritesFragment extends Fragment implements SettingsFragment.IDataSender {

    @Override
    public void sendToActivity(String a, String b, String c) {
        MainActivity.latitude = a;
        MainActivity.longitude =  b;
        MainActivity.refreshingTime = c;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourites, container, false);
    }

}
