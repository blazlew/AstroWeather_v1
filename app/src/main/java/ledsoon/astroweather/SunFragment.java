package ledsoon.astroweather;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.astrocalculator.AstroCalculator;
import com.astrocalculator.AstroDateTime;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class SunFragment extends Fragment {

    TextView tvSunrise, tvSunriseAzimuth, tvSunset, tvSunsetAzimuth, tvDusk, tvDawn;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public static SunFragment newInstance() {
        SunFragment fragment = new SunFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sun, container, false);
        this.tvSunrise = (TextView) rootView.findViewById(R.id.tvSunrise);
        this.tvSunriseAzimuth = (TextView) rootView.findViewById(R.id.tvSunriseAzimuth);
        this.tvSunset = (TextView) rootView.findViewById(R.id.tvSunset);
        this.tvSunsetAzimuth = (TextView) rootView.findViewById(R.id.tvSunsetAzimuth);
        this.tvDusk = (TextView) rootView.findViewById(R.id.tvDusk);
        this.tvDawn = (TextView) rootView.findViewById(R.id.tvDawn);
        generateSunInfo();
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        generateSunInfo();
        final Handler handler=new Handler();
        final Runnable updateTask=new Runnable() {
            @Override
            public void run() {
                generateSunInfo();
                handler.postDelayed(this, Long.parseLong(MainActivity.refreshingTime) * 1000);
            }
        };
        handler.postDelayed(updateTask, Long.parseLong(MainActivity.refreshingTime) * 1000);
    }

    void generateSunInfo() {
        AstroCalculator.Location location = new AstroCalculator.Location(Double.parseDouble(MainActivity.latitude), Double.parseDouble(MainActivity.longitude));
        Calendar mCalendar = new GregorianCalendar();
        TimeZone mTimeZone = mCalendar.getTimeZone();
        AstroDateTime datetime = new AstroDateTime(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH) + 1,
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH), Calendar.getInstance().get(Calendar.HOUR), Calendar.getInstance().get(Calendar.MINUTE),
                Calendar.getInstance().get(Calendar.SECOND), getOffset(), mTimeZone.inDaylightTime(new Date()));
        AstroCalculator astroCalculator = new AstroCalculator(datetime, location);
        AstroCalculator.SunInfo sunInfo = astroCalculator.getSunInfo();
        this.tvSunrise.setText(sunInfo.getSunrise().toString());
        this.tvSunriseAzimuth.setText(Double.toString(Math.round(sunInfo.getAzimuthRise() * 100d)/100d));
        this.tvSunset.setText(sunInfo.getSunset().toString());
        this.tvSunsetAzimuth.setText(Double.toString(Math.round(sunInfo.getAzimuthSet() * 100d)/100d));
        this.tvDusk.setText(Integer.toString(sunInfo.getTwilightEvening().getHour()) + ":" + Integer.toString(sunInfo.getTwilightEvening().getMinute()));
        this.tvDawn.setText(Integer.toString(sunInfo.getTwilightMorning().getHour()) + ":" + Integer.toString(sunInfo.getTwilightMorning().getMinute()));
    }

    public int getOffset(){
        TimeZone timezone = TimeZone.getDefault();
        int seconds = timezone.getOffset(Calendar.ZONE_OFFSET)/1000;
        double minutes = seconds/60;
        double hours = minutes/60;
        int finalHours = (int) hours;
        return finalHours;
    }
}


