package ledsoon.astroweather;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HomeFragment extends Fragment {

    TextView tvTime;
    TextView tvLatitude;
    TextView tvLongitude;
    TextView tvRefreshingTime;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        this.tvTime = (TextView) rootView.findViewById(R.id.tvTime);
        this.tvLatitude = (TextView) rootView.findViewById(R.id.tvLatitude);
        this.tvLongitude = (TextView) rootView.findViewById(R.id.tvLongitude);
        this.tvRefreshingTime = (TextView) rootView.findViewById(R.id.tvRefreshingTime);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Handler handler = new Handler();

        final Runnable updateTask = new Runnable() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                tvTime.setText(simpleDateFormat.format(calendar.getTime()));
                tvLatitude.setText(MainActivity.latitude);
                tvLongitude.setText(MainActivity.longitude);
                tvRefreshingTime.setText(MainActivity.refreshingTime + "s");
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(updateTask, 1000);

    }
}


