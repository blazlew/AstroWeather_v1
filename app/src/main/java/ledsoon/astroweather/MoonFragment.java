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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class MoonFragment extends Fragment {

    TextView tvSunrise;
    TextView tvSunset;
    TextView tvNewMoon;
    TextView tvFullMoon;
    TextView tvMoonPhaze;
    TextView tvSynodicDay;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public MoonFragment() {
    }

    public static MoonFragment newInstance() {
        MoonFragment moonFragment = new MoonFragment();
        return moonFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_moon, container, false);
        this.tvSunrise = (TextView) rootView.findViewById(R.id.tvSunrise);
        this.tvSunset = (TextView) rootView.findViewById(R.id.tvSunset);
        this.tvNewMoon = (TextView) rootView.findViewById(R.id.tvNewMoon);
        this.tvFullMoon = (TextView) rootView.findViewById(R.id.tvFullMoon);
        this.tvMoonPhaze = (TextView) rootView.findViewById(R.id.tvMoonPhaze);
        this.tvSynodicDay = (TextView) rootView.findViewById(R.id.tvSynodicDay);
        generateMoonInfo();
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        generateMoonInfo();
        final Handler handler=new Handler();
        final Runnable updateTask=new Runnable() {
            @Override
            public void run() {
                generateMoonInfo();
                handler.postDelayed(this, Long.parseLong(MainActivity.refreshingTime) * 1000);
            }
        };
        handler.postDelayed(updateTask, Long.parseLong(MainActivity.refreshingTime) * 1000);
    }

    void generateMoonInfo() {
        AstroCalculator.Location location = new AstroCalculator.Location(Double.parseDouble(MainActivity.latitude), Double.parseDouble(MainActivity.longitude));
        Calendar calendar = new GregorianCalendar();
        TimeZone timeZone = calendar.getTimeZone();
        AstroDateTime datetime = new AstroDateTime(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH) + 1,
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH), Calendar.getInstance().get(Calendar.HOUR), Calendar.getInstance().get(Calendar.MINUTE),
                Calendar.getInstance().get(Calendar.SECOND), getOffset(), timeZone.inDaylightTime(new Date()));
        AstroCalculator astroCalculator = new AstroCalculator(datetime, location);
        AstroCalculator.MoonInfo moonInfo = astroCalculator.getMoonInfo();
        this.tvSunrise.setText(moonInfo.getMoonrise().toString());
        this.tvSunset.setText(moonInfo.getMoonset().toString());
        this.tvNewMoon.setText(moonInfo.getNextNewMoon().toString());
        this.tvFullMoon.setText(moonInfo.getNextFullMoon().toString());
        this.tvMoonPhaze.setText(Math.round(moonInfo.getIllumination() * 100d)/100d + "%");
        try {
            Date dateOfLastNewMoon = new SimpleDateFormat("yyyy-mm-dd").parse((Integer.toString(datetime.getYear()) + "-" +
                    Integer.toString(datetime.getMonth()) + "-" + Integer.toString(datetime.getDay())));
            Date dateOfNextNewMoon = new SimpleDateFormat("yyyy-mm-dd").parse((Integer.toString(moonInfo.getNextNewMoon().getYear()) + "-" +
                    Integer.toString(moonInfo.getNextNewMoon().getMonth()) + "-" + Integer.toString(moonInfo.getNextNewMoon().getDay())));
            long dateDifference = Math.abs(dateOfNextNewMoon.getTime() - dateOfLastNewMoon.getTime());
            long dateDifferenceInDays = dateDifference / (24 * 60 * 60 * 1000);
            this.tvSynodicDay.setText("Synod day: " + Long.toString(dateDifferenceInDays));
        } catch (ParseException e) {
            e.printStackTrace();
        }
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


