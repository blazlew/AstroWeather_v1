package ledsoon.astroweather;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherFragment extends Fragment {

    TextView tvCity;
    TextView tvLatitude;
    TextView tvLongitude;
    TextView tvTime;
    TextView tvTemperature;
    TextView tvPressure;
    TextView tvWeatherConditions;
    TextView tvWindStrength;
    TextView tvWindDirection;
    TextView tvHumidity;
    TextView tvVisibility;
    ImageView ivWeatherConditions;
    Bitmap bmp;



    public static WeatherFragment newInstance() {
        WeatherFragment fragment = new WeatherFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        tvCity = (TextView) rootView.findViewById(R.id.tvCity);
        tvLatitude = (TextView) rootView.findViewById(R.id.tvLatitude);
        tvLongitude = (TextView) rootView.findViewById(R.id.tvLongitude);
        tvTime = (TextView) rootView.findViewById(R.id.tvTime);
        tvTemperature = (TextView) rootView.findViewById(R.id.tvTemperature);
        tvPressure = (TextView) rootView.findViewById(R.id.tvPressure);
        tvWeatherConditions = (TextView) rootView.findViewById(R.id.tvWeatherConditions);
        tvWindStrength = (TextView) rootView.findViewById(R.id.tvWindStrength);
        tvWindDirection = (TextView) rootView.findViewById(R.id.tvWindDirection);
        tvHumidity = (TextView) rootView.findViewById(R.id.tvHumidity);
        tvVisibility = (TextView) rootView.findViewById(R.id.tvVisibility);
        ivWeatherConditions = (ImageView) rootView.findViewById(R.id.ivWeatherCondtions);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tvCity.setText(MainActivity.city);
        tvTemperature.setText(MainActivity.temp + MainActivity.unit);
        tvWeatherConditions.setText(MainActivity.desc);
        tvPressure.setText(MainActivity.pres + " mb");
        tvHumidity.setText(MainActivity.hum);
        tvVisibility.setText(MainActivity.vis);
        tvWindStrength.setText(MainActivity.windStr + " km/h");
        tvWindDirection.setText(MainActivity.windDir);
        tvLatitude.setText(MainActivity.lat);
        tvLongitude.setText(MainActivity.longi);
        final Handler handler = new Handler();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                try {
                    url = new URL(MainActivity.imgUrl);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    if (url != null) bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ivWeatherConditions.setImageBitmap(bmp);
    }

}
