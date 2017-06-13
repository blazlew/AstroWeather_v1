package ledsoon.astroweather;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class FavouritesFragment extends Fragment implements SettingsFragment.IDataSender {

    EditText etLocalization;
    Button bPlus;
    ListView lvPlaces;

//    String XML_URL, concatedPlace;
//    ArrayList<String> arrayXML = new ArrayList<>(), placesList = new ArrayList<>();
//    ArrayAdapter<String> arrayAdapter;
//    boolean bFlag = true;

    ListView list;
    ArrayList<String> listItems = new ArrayList<String>(), parsedXML = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    String query, concat;
    boolean flag = true;
    private String location2;

    @Override
    public void sendToActivity(String a, String b, String c) {
        MainActivity.latitude = a;
        MainActivity.longitude =  b;
        MainActivity.refreshingTime = c;
    }

    public interface favouritesDataListener{
        public void sendFavouritesData(String city, String latitude, String longitude, String time, String temperature, String pressure,
                                       String weatherConditions, String windStrenth, String windDirection, String humidity, String visibility, String iconURL, String forecast);
    }

    favouritesDataListener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (favouritesDataListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement favouritesDataListener");
        }
    }

    public FavouritesFragment() {
    }

    public static FavouritesFragment newInstance() {
        FavouritesFragment fragment = new FavouritesFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favourites, container, false);
        etLocalization = (EditText) rootView.findViewById(R.id.etLocalization);
        bPlus = (Button) rootView.findViewById(R.id.bPlus);
//        bPlus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                XML_URL = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20geo.places(1)%20where%20text=" + '"' + etLocalization.getText().toString() + '"';
//                Thread thread = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        try {
//                            arrayXML.clear();
//                            URL url = new URL(XML_URL);
//                            URLConnection urlConnection = url.openConnection();
//
//                            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
//                            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
//                            Document document = documentBuilder.parse(urlConnection.getInputStream());
//
//                            NodeList nodes = document.getElementsByTagName("place");
//                            for (int i = 0; i < nodes.getLength(); i++) {
//                                Element element = (Element) nodes.item(i);
//                                NodeList title = element.getElementsByTagName("woeid");
//                                Element line = (Element) title.item(0);
//                                arrayXML.add(line.getTextContent());
//                            }
//                            concatedPlace = etLocalization.getText().toString() + "/" + arrayXML.get(0);
//                            MainActivity.sqLiteDatabase.execSQL("INSERT INTO "
//                                    + MainActivity.TableName
//                                    + " (Field1, Field2)"
//                                    + " VALUES ('" + etLocalization.getText().toString() + "', " + arrayXML.get(0) + ");");
//                            placesList.add(concatedPlace);
//                            getFiles();
//                        } catch (UnknownHostException e) {
//                            Toast.makeText(getContext(), "No internet connection, can't add!", Toast.LENGTH_SHORT).show();
//                        } catch (Exception e) {
//                            Toast.makeText(getContext(), "City name is wrong or already added!", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//        });
        bPlus.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location2 = etLocalization.getText().toString();
                etLocalization.setText("");
                query = "https://query.yahooapis.com/v1/public/yql?q=select * from geo.places(1) where text=" + '"' + location2 + '"';
                query = query.replaceAll(" ", "%20");
                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            parsedXML.clear();
                            URL url = new URL(query);
                            URLConnection conn = url.openConnection();

                            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                            DocumentBuilder builder = factory.newDocumentBuilder();
                            Document doc = builder.parse(conn.getInputStream());

                            NodeList nodes = doc.getElementsByTagName("place");
                            for (int i = 0; i < nodes.getLength(); i++) {
                                Element element = (Element) nodes.item(i);
                                NodeList title = element.getElementsByTagName("woeid");
                                Element line = (Element) title.item(0);
                                parsedXML.add(line.getTextContent());
                            }
                            concat = location2 + "/" + parsedXML.get(0);
                            MainActivity.sqLiteDatabase.execSQL("INSERT INTO "
                                    + MainActivity.TableName
                                    + " (Field1, Field2)"
                                    + " VALUES ('" + location2 + "', " + parsedXML.get(0) + ");");
                            listItems.add(concat);
                            getFiles();
                        } catch (UnknownHostException e) {
                            toast(getContext(), "No internet connection, can't add!");
                        } catch (Exception e) {
                            toast(getContext(), "City name is wrong or already added!");
                        }
                    }
                });
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        return rootView;
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
//                            PrintWriter out = new PrintWriter(MainActivity.dir + "/" + Integer.toString(cursor.getInt(Column2)) + ".xml");
//                            XML_URL = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid=" + Integer.toString(cursor.getInt(Column2)) + " and u=" + MainActivity.unit + ";";
//                            URL url = new URL(XML_URL);
//                            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
//                            String temp;
//                            while ((temp = in.readLine()) != null) {
//                                out.println(temp);
//                            }
//                            in.close();
//                            out.close();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                    } while (cursor.moveToNext());
//                }
//            }
//        });
//        thread.start();
//        thread.join();
//    }

    void getFiles() throws InterruptedException {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                /*retrieve data from database */
                Cursor c = MainActivity.sqLiteDatabase.rawQuery("SELECT * FROM " + MainActivity.TableName, null);

                int Column1 = c.getColumnIndex("Field1");
                int Column2 = c.getColumnIndex("Field2");

                // Check if our result was valid.
                c.moveToFirst();
                if (c != null) {
                    // Loop through all Results
                    do {
                        try {
                            PrintWriter out = new PrintWriter(MainActivity.dir + "/" + Integer.toString(c.getInt(Column2)) + ".xml");
                            query = "https://query.yahooapis.com/v1/public/yql?q=select * from weather.forecast where woeid=" + Integer.toString(c.getInt(Column2)) + " and u=" + MainActivity.unit + ";";
                            query = query.replaceAll(" ", "%20");
                            URL url = new URL(query);
                            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                            String str;
                            while ((str = in.readLine()) != null) {
                                out.println(str);
                            }
                            in.close();
                            out.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } while (c.moveToNext());
                }
            }
        });
        thread.start();
        thread.join();
    }

//    protected boolean shouldRequestDisallowIntercept(ViewGroup scrollView, MotionEvent event) {
//        boolean disallowIntercept = true;
//        float yOffset = getYOffset(event);
//
//        if (scrollView instanceof ListView) {
//            ListView listView = (ListView) scrollView;
//            if (yOffset < 0 && listView.getFirstVisiblePosition() == 0 && listView.getChildAt(0).getTop() >= 0) {
//                disallowIntercept = false;
//            }
//            else if (yOffset > 0 && listView.getLastVisiblePosition() == listView.getAdapter().getCount() - 1 && listView.getChildAt(listView.getChildCount() - 1).getBottom() <= listView.getHeight()) {
//                disallowIntercept = false;
//            }
//        }
//        else {
//            float scrollY = scrollView.getScrollY();
//            disallowIntercept = !((scrollY == 0 && yOffset < 0) || (scrollView.getHeight() + scrollY == scrollView.getChildAt(0).getHeight() && yOffset >= 0));
//
//        }
//
//        return disallowIntercept;
//    }

    protected boolean shouldRequestDisallowIntercept(ViewGroup scrollView, MotionEvent event) {
        boolean disallowIntercept = true;
        float yOffset = getYOffset(event);

        if (scrollView instanceof ListView) {
            ListView listView = (ListView) scrollView;
            if (yOffset < 0 && listView.getFirstVisiblePosition() == 0 && listView.getChildAt(0).getTop() >= 0) {
                disallowIntercept = false;
            }
            else if (yOffset > 0 && listView.getLastVisiblePosition() == listView.getAdapter().getCount() - 1 && listView.getChildAt(listView.getChildCount() - 1).getBottom() <= listView.getHeight()) {
                disallowIntercept = false;
            }
        }
        else {
            float scrollY = scrollView.getScrollY();
            disallowIntercept = !((scrollY == 0 && yOffset < 0) || (scrollView.getHeight() + scrollY == scrollView.getChildAt(0).getHeight() && yOffset >= 0));

        }

        return disallowIntercept;
    }

//    protected float getYOffset(MotionEvent ev) {
//        final int historySize = ev.getHistorySize();
//        final int pointerCount = ev.getPointerCount();
//
//        if (historySize > 0 && pointerCount > 0) {
//            float lastYOffset = ev.getHistoricalY(pointerCount - 1, historySize - 1);
//            float currentYOffset = ev.getY(pointerCount - 1);
//
//            float yOffset = lastYOffset - currentYOffset;
//
//            return yOffset;
//        }
//
//        return 0;
//    }

    protected float getYOffset(MotionEvent ev) {
        final int historySize = ev.getHistorySize();
        final int pointerCount = ev.getPointerCount();

        if (historySize > 0 && pointerCount > 0) {
            float lastYOffset = ev.getHistoricalY(pointerCount - 1, historySize - 1);
            float currentYOffset = ev.getY(pointerCount - 1);

            float dY = lastYOffset - currentYOffset;

            return dY;
        }

        return 0;
    }

//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        if (bFlag) {
//        /*retrieve data from database */
//            Cursor cursor = MainActivity.sqLiteDatabase.rawQuery("SELECT * FROM " + MainActivity.TableName, null);
//
//            int Column1 = cursor.getColumnIndex("Field1");
//            int Column2 = cursor.getColumnIndex("Field2");
//
//            // Check if our result was valid.
//            cursor.moveToFirst();
//            if (cursor != null) {
//                // Loop through all Results
//                do {
//                    try {
//                        placesList.add(cursor.getString(Column1) + '/' + Integer.toString(cursor.getInt(Column2)));
//                    } catch (Exception e) {
//                    }
//
//                } while (cursor.moveToNext());
//            }
//            bFlag = false;
//        }
//        arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, placesList);
//        lvPlaces = (ListView) getActivity().findViewById(R.id.lvPlaces);
//        lvPlaces.setAdapter(arrayAdapter);
//        lvPlaces.setOnTouchListener(new View.OnTouchListener() {
//            public boolean onTouch(View v, MotionEvent event) {
//                try {
//                    v.getParent().requestDisallowInterceptTouchEvent(shouldRequestDisallowIntercept((ViewGroup) v, event));
//                }
//                catch (NullPointerException e) {}
//                return false;
//            }
//        });
//        final Handler handler=new Handler();
//        final Runnable updateTask=new Runnable() {
//            @Override
//            public void run() {
//                arrayAdapter.notifyDataSetChanged();
//                handler.postDelayed(this, 1000);
//            }
//        };
//        handler.postDelayed(updateTask, 1000);
//
//        lvPlaces.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view,
//                                           int position, long arg3) {
//                MainActivity.sqLiteDatabase.execSQL("DELETE FROM "
//                        + MainActivity.TableName
//                        + " WHERE Field1=" + "'" + placesList.get(position).substring(0, placesList.get(position).indexOf('/')) + "';");
//                File file = new File(MainActivity.dir + "/" + placesList.get(position).substring(placesList.get(position).indexOf('/'), placesList.get(position).length()) + ".xml");
//                file.delete();
//                placesList.remove(position);
//                arrayAdapter.notifyDataSetChanged();
//                return false;
//            }
//
//        });
//
//        lvPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    final int position, long arg3) {
//                Thread thread = new Thread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        try {
//                            arrayXML.clear();
//                            String path = MainActivity.dir + "/" + placesList.get(position).substring(placesList.get(position).indexOf('/'), placesList.get(position).length()) + ".xml";
//                            FileReader filePath = new FileReader(path);
//                            BufferedReader buffer = new BufferedReader(filePath);
//                            String currentLine;
//                            String city = placesList.get(position).substring(0, placesList.get(position).indexOf('/'));
//                            city.replaceAll(" ", "%20");
//                            String lat = null, longi  = null,
//                                    temp = null, desc = null,
//                                    pres = null, hum = null,
//                                    vis = null, windStr = null,
//                                    windDir = null, imgUrl = null, forecast = null, forecast2 = null, forecast3 = null, forecast4 = null,
//                                    forecast5 = null, forecast6 = null, forecast7 = null;
//                            while((currentLine = buffer.readLine()) != null) {
//                                if (currentLine.contains("geo:lat")) {
//                                    lat = currentLine.substring(currentLine.indexOf("pos#") + 6, currentLine.indexOf("</geo:lat>"));
//                                }
//                                if (currentLine.contains("geo:long")) {
//                                    longi = currentLine.substring(currentLine.lastIndexOf("pos#") + 6, currentLine.indexOf("</geo:long>"));
//                                }
//                                if (currentLine.contains("yweather:condition")) {
//                                    temp = currentLine.substring(currentLine.indexOf("temp=") + 6, currentLine.indexOf("text=") - 2);
//                                    desc = currentLine.substring(currentLine.indexOf("text=") + 6, currentLine.indexOf("/><yweather:forecast", currentLine.indexOf("text=") + 6) - 1);
//                                }
//                                if (currentLine.contains("<yweather:atmosphere")) {
//                                    pres = currentLine.substring(currentLine.lastIndexOf("pressure=") + 10, currentLine.indexOf("rising=") - 2);
//                                    hum = currentLine.substring(currentLine.indexOf("humidity=") + 10, currentLine.lastIndexOf("pressure=") - 2);
//                                    vis = currentLine.substring(currentLine.indexOf("visibility=") + 12, currentLine.indexOf("<yweather:astronomy", currentLine.indexOf("visibility=") + 12) - 3);
//                                }
//                                if (currentLine.contains("<yweather:wind")) {
//                                    windDir = currentLine.substring(currentLine.indexOf("direction=") + 11, currentLine.lastIndexOf("speed=") - 2);
//                                    windStr = currentLine.substring(currentLine.lastIndexOf("speed=") + 7, currentLine.indexOf("<yweather:atmosphere", currentLine.lastIndexOf("speed=") + 7) - 3);
//                                }
//                                if (currentLine.contains("CDATA")) {
//                                    imgUrl = currentLine.substring(currentLine.indexOf("CDATA") + 19, currentLine.lastIndexOf(".gif") + 4);
//                                }
//                                if (currentLine.contains("<yweather:forecast")) {
//                                    forecast = currentLine.substring(currentLine.indexOf("<yweather:forecast xmlns:yweather=") + 86, currentLine.indexOf("/>", currentLine.indexOf("<yweather:forecast xmlns:yweather=") + 1));
//                                    forecast2 = currentLine.substring(currentLine.lastIndexOf(forecast) + forecast.length() + 2);
//                                    forecast2 = forecast2.substring(forecast2.indexOf("<yweather:forecast xmlns:yweather=") + 86, forecast2.indexOf("/>", forecast2.indexOf("<yweather:forecast xmlns:yweather=") + 1));
//                                    forecast3 = currentLine.substring(currentLine.lastIndexOf(forecast2) + forecast2.length() + 2);
//                                    forecast3 = forecast3.substring(forecast3.indexOf("<yweather:forecast xmlns:yweather=") + 86, forecast3.indexOf("/>", forecast3.indexOf("<yweather:forecast xmlns:yweather=") + 1));
//                                    forecast4 = currentLine.substring(currentLine.lastIndexOf(forecast3) + forecast3.length() + 2);
//                                    forecast4 = forecast4.substring(forecast4.indexOf("<yweather:forecast xmlns:yweather=") + 86, forecast4.indexOf("/>", forecast4.indexOf("<yweather:forecast xmlns:yweather=") + 1));
//                                    forecast5 = currentLine.substring(currentLine.lastIndexOf(forecast4) + forecast4.length() + 2);
//                                    forecast5 = forecast5.substring(forecast5.indexOf("<yweather:forecast xmlns:yweather=") + 86, forecast5.indexOf("/>", forecast5.indexOf("<yweather:forecast xmlns:yweather=") + 1));
//                                    forecast6 = currentLine.substring(currentLine.lastIndexOf(forecast5) + forecast5.length() + 2);
//                                    forecast6 = forecast6.substring(forecast6.indexOf("<yweather:forecast xmlns:yweather=") + 86, forecast6.indexOf("/>", forecast6.indexOf("<yweather:forecast xmlns:yweather=") + 1));
//                                    forecast7 = currentLine.substring(currentLine.lastIndexOf(forecast6) + forecast6.length() + 2);
//                                    forecast7 = forecast7.substring(forecast7.indexOf("<yweather:forecast xmlns:yweather=") + 86, forecast7.indexOf("/>", forecast7.indexOf("<yweather:forecast xmlns:yweather=") + 1));
//                                    forecast += "\n\n" + forecast2 + "\n\n" + forecast3 + "\n\n" + forecast4 + "\n\n" + forecast5 + "\n\n" + forecast6 + "\n\n" + forecast7;
//                                    forecast = forecast.replaceAll("date=", "\nDate: ");
//                                    forecast = forecast.replaceAll("day=", "\nDay: ");
//                                    forecast = forecast.replaceAll("high=", "\nHigh: ");
//                                    forecast = forecast.replaceAll("low=", "\nLow: ");
//                                    forecast = forecast.replaceAll("text=", "\n");
//                                }
//                            }
//                            filePath.close();
//                            listener.sendFavouritesData(city, lat, longi, "", temp, pres, desc, windStr, windDir, hum, vis, imgUrl, forecast);
//                            sendToActivity(lat, longi, "1");
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//                thread.start();
//                try {
//                    thread.join();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (flag) {
        /*retrieve data from database */
            Cursor c = MainActivity.sqLiteDatabase.rawQuery("SELECT * FROM " + MainActivity.TableName, null);

            int Column1 = c.getColumnIndex("Field1");
            int Column2 = c.getColumnIndex("Field2");

            // Check if our result was valid.
            c.moveToFirst();
            if (c != null) {
                // Loop through all Results
                do {
                    try {
                        listItems.add(c.getString(Column1) + '/' + Integer.toString(c.getInt(Column2)));
                    } catch (Exception e) {
                    }

                } while (c.moveToNext());
            }
            flag = false;
        }
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listItems);
        list = (ListView) getActivity().findViewById(R.id.lvPlaces);
        list.setAdapter(adapter);
        list.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    v.getParent().requestDisallowInterceptTouchEvent(shouldRequestDisallowIntercept((ViewGroup) v, event));
                }
                catch (NullPointerException e) {}
                return false;
            }
        });
        final Handler handler=new Handler();
        final Runnable updateTask=new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(updateTask, 1000);

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long arg3) {
                MainActivity.sqLiteDatabase.execSQL("DELETE FROM "
                        + MainActivity.TableName
                        + " WHERE Field1=" + "'" + listItems.get(position).substring(0, listItems.get(position).indexOf('/')) + "';");
                File file = new File(MainActivity.dir + "/" + listItems.get(position).substring(listItems.get(position).indexOf('/'), listItems.get(position).length()) + ".xml");
                file.delete();
                listItems.remove(position);
                adapter.notifyDataSetChanged();
                return false;
            }

        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long arg3) {
                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            parsedXML.clear();
                            String path = MainActivity.dir + "/" + listItems.get(position).substring(listItems.get(position).indexOf('/'), listItems.get(position).length()) + ".xml";
                            FileReader filePath = new FileReader(path);
                            BufferedReader buffer = new BufferedReader(filePath);
                            String currentLine;
                            String city = listItems.get(position).substring(0, listItems.get(position).indexOf('/'));
                            city.replaceAll(" ", "%20");
                            String lat = null, longi  = null,
                                    temp = null, desc = null,
                                    pres = null, hum = null,
                                    vis = null, windStr = null,
                                    windDir = null, imgUrl = null, forecast = null, forecast2 = null, forecast3 = null, forecast4 = null,
                                    forecast5 = null, forecast6 = null, forecast7 = null;
                            while((currentLine = buffer.readLine()) != null) {
                                if (currentLine.contains("geo:lat")) {
                                    lat = currentLine.substring(currentLine.indexOf("pos#") + 6, currentLine.indexOf("</geo:lat>"));
                                }
                                if (currentLine.contains("geo:long")) {
                                    longi = currentLine.substring(currentLine.lastIndexOf("pos#") + 6, currentLine.indexOf("</geo:long>"));
                                }
                                if (currentLine.contains("yweather:condition")) {
                                    temp = currentLine.substring(currentLine.indexOf("temp=") + 6, currentLine.indexOf("text=") - 2);
                                    desc = currentLine.substring(currentLine.indexOf("text=") + 6, currentLine.indexOf("/><yweather:forecast", currentLine.indexOf("text=") + 6) - 1);
                                }
                                if (currentLine.contains("<yweather:atmosphere")) {
                                    pres = currentLine.substring(currentLine.lastIndexOf("pressure=") + 10, currentLine.indexOf("rising=") - 2);
                                    hum = currentLine.substring(currentLine.indexOf("humidity=") + 10, currentLine.lastIndexOf("pressure=") - 2);
                                    vis = currentLine.substring(currentLine.indexOf("visibility=") + 12, currentLine.indexOf("<yweather:astronomy", currentLine.indexOf("visibility=") + 12) - 3);
                                }
                                if (currentLine.contains("<yweather:wind")) {
                                    windDir = currentLine.substring(currentLine.indexOf("direction=") + 11, currentLine.lastIndexOf("speed=") - 2);
                                    windStr = currentLine.substring(currentLine.lastIndexOf("speed=") + 7, currentLine.indexOf("<yweather:atmosphere", currentLine.lastIndexOf("speed=") + 7) - 3);
                                }
                                if (currentLine.contains("CDATA")) {
                                    imgUrl = currentLine.substring(currentLine.indexOf("CDATA") + 19, currentLine.lastIndexOf(".gif") + 4);
                                }
                                if (currentLine.contains("<yweather:forecast")) {
                                    forecast = currentLine.substring(currentLine.indexOf("<yweather:forecast xmlns:yweather=") + 86, currentLine.indexOf("/>", currentLine.indexOf("<yweather:forecast xmlns:yweather=") + 1));
                                    forecast2 = currentLine.substring(currentLine.lastIndexOf(forecast) + forecast.length() + 2);
                                    forecast2 = forecast2.substring(forecast2.indexOf("<yweather:forecast xmlns:yweather=") + 86, forecast2.indexOf("/>", forecast2.indexOf("<yweather:forecast xmlns:yweather=") + 1));
                                    forecast3 = currentLine.substring(currentLine.lastIndexOf(forecast2) + forecast2.length() + 2);
                                    forecast3 = forecast3.substring(forecast3.indexOf("<yweather:forecast xmlns:yweather=") + 86, forecast3.indexOf("/>", forecast3.indexOf("<yweather:forecast xmlns:yweather=") + 1));
                                    forecast4 = currentLine.substring(currentLine.lastIndexOf(forecast3) + forecast3.length() + 2);
                                    forecast4 = forecast4.substring(forecast4.indexOf("<yweather:forecast xmlns:yweather=") + 86, forecast4.indexOf("/>", forecast4.indexOf("<yweather:forecast xmlns:yweather=") + 1));
                                    forecast5 = currentLine.substring(currentLine.lastIndexOf(forecast4) + forecast4.length() + 2);
                                    forecast5 = forecast5.substring(forecast5.indexOf("<yweather:forecast xmlns:yweather=") + 86, forecast5.indexOf("/>", forecast5.indexOf("<yweather:forecast xmlns:yweather=") + 1));
                                    forecast6 = currentLine.substring(currentLine.lastIndexOf(forecast5) + forecast5.length() + 2);
                                    forecast6 = forecast6.substring(forecast6.indexOf("<yweather:forecast xmlns:yweather=") + 86, forecast6.indexOf("/>", forecast6.indexOf("<yweather:forecast xmlns:yweather=") + 1));
                                    forecast7 = currentLine.substring(currentLine.lastIndexOf(forecast6) + forecast6.length() + 2);
                                    forecast7 = forecast7.substring(forecast7.indexOf("<yweather:forecast xmlns:yweather=") + 86, forecast7.indexOf("/>", forecast7.indexOf("<yweather:forecast xmlns:yweather=") + 1));
                                    forecast += "\n\n" + forecast2 + "\n\n" + forecast3 + "\n\n" + forecast4 + "\n\n" + forecast5 + "\n\n" + forecast6 + "\n\n" + forecast7;
                                    forecast = forecast.replaceAll("date=", "\nDate: ");
                                    forecast = forecast.replaceAll("day=", "\nDay: ");
                                    forecast = forecast.replaceAll("high=", "\nHigh: ");
                                    forecast = forecast.replaceAll("low=", "\nLow: ");
                                    forecast = forecast.replaceAll("text=", "\n");
                                }
                            }
                            filePath.close();
                            listener.sendFavouritesData(city, lat, longi, "", temp, pres, desc, windStr, windDir, hum, vis, imgUrl, forecast);
                            sendToActivity(lat, longi, "1");
                        } catch (Exception e) {
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
            }
        });
    }

    public void toast(final Context context, final String text) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
