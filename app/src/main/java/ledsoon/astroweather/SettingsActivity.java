package ledsoon.astroweather;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

public class SettingsActivity extends AppCompatActivity implements SettingsFragment.IDataSender {

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
}
