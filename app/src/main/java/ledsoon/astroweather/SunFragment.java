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

    TextView sunrise, wazymut, sunset, zazymut, zmierzch, swit;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public SunFragment() {
    }

    public static SunFragment newInstance(int sectionNumber) {
        SunFragment fragment = new SunFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sun, container, false);
        this.sunrise = (TextView) rootView.findViewById(R.id.tvSunrise);
        this.wazymut = (TextView) rootView.findViewById(R.id.wazymut);
        this.sunset = (TextView) rootView.findViewById(R.id.tvSunset);
        this.zazymut = (TextView) rootView.findViewById(R.id.zazymut);
        this.zmierzch = (TextView) rootView.findViewById(R.id.zmierzch);
        this.swit = (TextView) rootView.findViewById(R.id.swit);
        calculateSun();
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        calculateSun();
        final Handler handler=new Handler();
        final Runnable updateTask=new Runnable() {
            @Override
            public void run() {
                calculateSun();
                handler.postDelayed(this, Long.parseLong(MainActivity.refreshingTime) * 1000);
            }
        };
        handler.postDelayed(updateTask, Long.parseLong(MainActivity.refreshingTime) * 1000);
    }

    void calculateSun() {
        AstroCalculator.Location loc = new AstroCalculator.Location(Double.parseDouble(MainActivity.latitude), Double.parseDouble(MainActivity.longitude));
        Calendar mCalendar = new GregorianCalendar();
        TimeZone mTimeZone = mCalendar.getTimeZone();
        AstroDateTime datetime = new AstroDateTime(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH) + 1,
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH), Calendar.getInstance().get(Calendar.HOUR), Calendar.getInstance().get(Calendar.MINUTE),
                Calendar.getInstance().get(Calendar.SECOND), getOffset(), mTimeZone.inDaylightTime(new Date()));
        AstroCalculator calc = new AstroCalculator(datetime, loc);
        AstroCalculator.SunInfo sun = calc.getSunInfo();
        this.sunrise.setText("Sunrise: " + sun.getSunrise().toString());
        this.wazymut.setText("Azimuth: " + Double.toString(sun.getAzimuthRise()));
        this.sunset.setText("Sunset: " + sun.getSunset().toString());
        this.zazymut.setText("Azimuth: " + Double.toString(sun.getAzimuthSet()));
        AstroDateTime datetime2 = sun.getTwilightEvening();
        String temp = Integer.toString(datetime2.getHour()) + ":" + Integer.toString(datetime2.getMinute());
        AstroDateTime datetime3 = sun.getTwilightMorning();
        String temp2 = Integer.toString(datetime3.getHour()) + ":" + Integer.toString(datetime3.getMinute());
        this.zmierzch.setText("Dusk: " + temp);
        this.swit.setText("Dawn: " + temp2);
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


