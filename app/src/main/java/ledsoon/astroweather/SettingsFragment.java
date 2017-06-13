package ledsoon.astroweather;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsFragment extends Fragment{

    private Button bChange;
    private TextView tvLatitude;
    private TextView tvLongitude;
    private TextView tvRefreshingTime;

  //  @Override
    public void sendFavouritesData(String city, String latitude, String longitude, String time, String temperature, String pressure, String weatherConditions, String windStrenth, String windDirection, String humidity, String visibility, String iconURL, String forecast) {
        MainActivity.city = city;
        MainActivity.lat = latitude;
        MainActivity.longi = longitude;
        //MainActivity.time = time;
        MainActivity.temp = temperature;
        MainActivity.pres = pressure;
        MainActivity.desc = weatherConditions;
        MainActivity.windStr = windStrenth;
        MainActivity.windDir = windDirection;
        MainActivity.hum = humidity;
        MainActivity.vis = visibility;
        MainActivity.imgUrl = iconURL;
        MainActivity.forecast = forecast;
    }

    public interface IDataSender {
        void sendToActivity(String a, String b, String c);
    }

    IDataSender iDataSender;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            iDataSender = (IDataSender) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement IDataSender");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            outState.putString("tvLatitude", tvLatitude.getText().toString());
            outState.putString("tvLongitude", tvLongitude.getText().toString());
            outState.putString("tvRefreshingTime", tvRefreshingTime.getText().toString());
        } catch (NullPointerException e) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        this.bChange = (Button) rootView.findViewById(R.id.okButton);
        this.tvLatitude = (TextView) rootView.findViewById(R.id.tvLatitude);
        this.tvLongitude = (TextView) rootView.findViewById(R.id.tvLongitude);
        this.tvRefreshingTime = (TextView) rootView.findViewById(R.id.tvRefreshingTime);
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            try {
                tvLatitude.setText(savedInstanceState.getString("tvLatitude"));
                tvLongitude.setText(savedInstanceState.getString("tvLongitude"));
                tvRefreshingTime.setText(savedInstanceState.getString("tvRefreshingTime"));
            } catch (NullPointerException e) {
            }
        }
        bChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvRefreshingTime.getText().toString().length() >= 1 && isNumeric(tvRefreshingTime.getText().toString())) {
                    iDataSender.sendToActivity(MainActivity.latitude, MainActivity.longitude, tvRefreshingTime.getText().toString());
                }
                if(tvLatitude.getText().toString().length() >= 1 && isNumeric(tvLatitude.getText().toString())) {
                    iDataSender.sendToActivity(tvLatitude.getText().toString(), MainActivity.longitude, MainActivity.refreshingTime);
                }
                if(tvLongitude.getText().toString().length() >= 1 && isNumeric(tvLongitude.getText().toString())) {
                    iDataSender.sendToActivity(MainActivity.latitude, tvLongitude.getText().toString(), MainActivity.refreshingTime);
                }
                Toast.makeText(getActivity(), "Data has been changed", Toast.LENGTH_SHORT).show();
                tvLongitude.setText("");
                tvLatitude.setText("");
                tvRefreshingTime.setText("");
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public static boolean isNumeric(String str)
    {
        try
        {
            Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }
}


