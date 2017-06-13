package ledsoon.astroweather;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity implements SettingsFragment.IDataSender, FavouritesFragment.favouritesDataListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter sectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager viewPager;
    public static String latitude = "19.28";
    public static String longitude = "51.47";
    public static String refreshingTime = "1";
    static FragmentManager fragmentManager;
    public static boolean isTablet = false, networkAvailable;
    public static SQLiteDatabase sqLiteDatabase;
    public static String TableName = "woeids", dir, unit = "'c'";
//    String query;

    public static String lat = "0.0";
    public static String longi = "0.0";
    public static String time = "0.0";
    public static String temp = "0.0";
    public static String desc = "";
    public static String pres = "0.0";
    public static String hum = "0.0";
    public static String vis = "0.0";
    public static String windStr = "0.0";
    public static String windDir = "0.0";
    public static String imgUrl = "";
    public static String city = "";
    public static String forecast = "";
    String query;
    static boolean toastFlag = false;

//    static boolean toastFlag = false;
//    public static String temp = "0.0", desc = "", pres = "0.0",
//            hum = "0.0", vis = "0.0", windStr = "0.0", windDir = "0.0", imgUrl = "", unit = "'c'", dir, city = "", forecast = "",
//            lat = "0.0", longi = "0.0";
////    public static String TableName = "woeids";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    protected void onResume() {
        super.onResume();
        sectionsPagerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar coolToolbar = (Toolbar) findViewById(R.id.coolToobar);
        setSupportActionBar(coolToolbar);
        fragmentManager = getSupportFragmentManager();
        //ViewPager pager = (ViewPager) findViewById(R.id.mainViev);
        //pager.setOffscreenPageLimit(3);
        // Create the arrayAdapter that will return a fragment for each of the three
        // primary sections of the activity.
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        dir = getFilesDir().toString();
        final Handler handler=new Handler();
        final Runnable updateTask=new Runnable() {
            @Override
            public void run() {
                if(isNetworkAvailable(getApplicationContext())) networkAvailable = true;
                else networkAvailable = false;
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(updateTask, 1000);
        File file = new File(MainActivity.dir + '/' + "f.txt");
        if(file.exists())
            MainActivity.unit = "'f'";
        else {
            MainActivity.unit = "'c'";
            file = new File(MainActivity.dir + '/' + "c.txt");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            sqLiteDatabase = this.openOrCreateDatabase("woeidDB", MODE_PRIVATE, null);

   /* Create a Table in the Database. */
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS "
                    + TableName
                    + " (Field1 VARCHAR UNIQUE NOT NULL, Field2 INT(3));");
            getFiles();
        }
        catch (Exception e) {
        }

        // Set up the ViewPager with the sections arrayAdapter.
        viewPager = (ViewPager) findViewById(R.id.mainViev);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setCurrentItem(1);
        if (isTablet(this.getApplicationContext())) isTablet = true;
        else isTablet = false;
    }

//    void getFiles() throws InterruptedException {
//        Thread thread = new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                /*retrieve data from database */
//                Cursor cursor = MainActivity.sqLiteDatabase.rawQuery("SELECT * FROM " + MainActivity.TableName, null);
//
//                int Column1 = cursor.getColumnIndex("Field1");
//                int Column2 = cursor.getColumnIndex("Field2");
//
//                // Check if our result was valid.
//                cursor.moveToFirst();
//                if (cursor != null) {
//                    // Loop through all Results
//                    do {
//                        try {
//                            query = "https://query.yahooapis.com/v1/public/yql?q=select * from weather.forecast where woeid=" + Integer.toString(cursor.getInt(Column2)) + " and u=" + unit + ";";
//                            query = query.replaceAll(" ", "%20");
//                            URL url = new URL(query);
//                            String dir2 = dir + "/" + Integer.toString(cursor.getInt(Column2)) + ".xml";
//                            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
//                            PrintWriter out = new PrintWriter(dir2);
//                            String str;
//                            while ((str = in.readLine()) != null) {
//                                out.println(str);
//                            }
//                            in.close();
//                            out.close();
//                        } catch (UnknownHostException e) {
//                            if (!toastFlag) {
//                                toast(getApplicationContext(), "No internet access, cannot update. Using files from last launch (you can update from settings after enabling an internet connection).");
//                                toastFlag = true;
//                            }
//
//                        } catch(Exception e) {
//                            e.printStackTrace();
//                        }
//
//                    } while (cursor.moveToNext());
//                }
//            }
//        });
//        thread.start();
//        thread.join();
//        if (!isNetworkAvailable(this.getApplicationContext())) {
//            toast(getApplicationContext(), "No internet access, cannot update. Using files from last launch (you can update from settings after enabling an internet connection).");
//        }
//    }

    void getFiles() throws InterruptedException {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                /*retrieve data from database */
                Cursor cursor = MainActivity.sqLiteDatabase.rawQuery("SELECT * FROM " + MainActivity.TableName, null);

                int columnOne = cursor.getColumnIndex("Field1");
                int columnTwo = cursor.getColumnIndex("Field2");

                // Check if our result was valid.
                cursor.moveToFirst();
                if (cursor != null) {
                    // Loop through all Results
                    do {
                        try {
                            query = "https://query.yahooapis.com/v1/public/yql?q=select * from weather.forecast where woeid=" + Integer.toString(cursor.getInt(columnTwo)) + " and u=" + unit + ";";
                            query = query.replaceAll(" ", "%20");
                            URL url = new URL(query);
                            String secondDir = dir + "/" + Integer.toString(cursor.getInt(columnTwo)) + ".xml";
                            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                            PrintWriter out = new PrintWriter(secondDir);
                            String str;
                            while ((str = in.readLine()) != null) {
                                out.println(str);
                            }
                            in.close();
                            out.close();
                        } catch (UnknownHostException e) {
                            if (!toastFlag) {
                                toast(getApplicationContext(), "No internet access, cannot update. Using files from last launch (you can update from settings after enabling an internet connection).");
                                toastFlag = true;
                            }

                        } catch(Exception e) {
                            e.printStackTrace();
                        }

                    } while (cursor.moveToNext());
                }
            }
        });
        thread.start();
        thread.join();
        if (!isNetworkAvailable(this.getApplicationContext())) {
            toast(getApplicationContext(), "No internet access, cannot update. Using files from last launch (you can update from settings after enabling an internet connection).");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                MainActivity.this.startActivity(intent);
                return true;
        }
        return false;
    }

    @Override

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void sendFavouritesData(String city, String latitude, String longitude, String time, String temperature, String pressure, String weatherConditions, String windStrenth, String windDirection, String humidity, String visibility, String iconURL, String forecast) {

        this.city = city;
        this.lat = latitude;
        this.longi = longitude;
        this.time = time;
        this.temp = temperature;
        this.pres = pressure;
        this.desc = weatherConditions;
        this.windStr = windStrenth;
        this.windDir = windDirection;
        this.hum = humidity;
        this.vis = visibility;
        this.imgUrl = iconURL;
        this.forecast = forecast;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public float getPageWidth(int position) {
            if(isTablet && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) return(0.34f);
            else if(isTablet && getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) return(0.5f);
            else return(1.0f);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 1) {
                return HomeFragment.newInstance();
            }
            if (position == 0) {
                return SunFragment.newInstance();
            }
            if (position == 2) {
                return MoonFragment.newInstance();
            }
            if (position == 3) {
                return WeatherFragment.newInstance();
            }
            if (position == 4) {
                return ForecastFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    @Override
    public void sendToActivity(String a, String b, String c) {
        this.latitude = a;
        this.longitude = b;
        this.refreshingTime = c;
    }

    public void toast(final Context context, final String text) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(context, text, Toast.LENGTH_LONG).show();
            }
        });
    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}