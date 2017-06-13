package ledsoon.astroweather;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

public class SettingsActivity extends AppCompatActivity implements SettingsFragment.IDataSender, FavouritesFragment.favouritesDataListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void sendToActivity(String a, String b, String c) {
        MainActivity.latitude = a;
        MainActivity.longitude = b;
        MainActivity.refreshingTime = c;
    }

    @Override
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
}
