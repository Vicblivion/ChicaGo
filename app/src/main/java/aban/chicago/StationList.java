package aban.chicago;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.http.SslError;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Created by Vicblivion on 18/01/2018.
 */
//compile 'com.google.android.gms:play-services-location:10.+'

public class StationList extends AppCompatActivity {

    private static final String TAG = "ListeStation";
    private ListView stationList;
    private ArrayList<Station> stations = new ArrayList<Station>();
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            stations = bundle.getParcelableArrayList("Stations");
            Log.e(TAG,"Message reçu");
        }
    };
    private ParserStation parser = new ParserStation();
    private LocationManager service;
    private boolean enabled;
    private String provider;
    private Location location;
    private double latitude = 0;
    private double longitude = 0;
    private ListeStation listeView;
    private byte tri = 0;
    private Lander lander = new Lander();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_page);
        parser.start();
        lander.start();
    }

    public void affichageList(ArrayList<Station> l){
        setContentView(R.layout.activity_station_list);
        stationList = (ListView) findViewById(R.id.listView);
        majList(l);
        listeView = new ListeStation();
        listeView.start();
        lander.interrupt();
        lander = null;
    }

    public ArrayList<Station> trier(ArrayList<Station> listnt, byte type){
        Station station;
        int place;
        ArrayList<Station> sortedList = new ArrayList<Station>();

        tri = type;
        if (tri == 1){
            while(stations.size()!=0) {
                station = stations.get(0);
                place = 0;
                for (int i = 0; i < stations.size(); i++) {
                    if(stations.get(i).distance(latitude,longitude)<station.distance(latitude,longitude)){
                        station=stations.get(i);
                        place = i;
                    }
                }
                stations.remove(place);
                sortedList.add(station);
            }
        }
        if (tri == 2){
            while(stations.size()!=0) {
                station = stations.get(0);
                place = 0;
                for (int i = 0; i < stations.size(); i++) {
                    if(stations.get(i).getBike()>station.getBike()){
                        station=stations.get(i);
                        place = i;
                    }
                }
                stations.remove(place);
                sortedList.add(station);
            }
        }
        if(tri == 3){
            while(stations.size()!=0) {
                station = stations.get(0);
                place = 0;
                for (int i = 0; i < stations.size(); i++) {
                    if(stations.get(i).getDock()>station.getDock()){
                        station=stations.get(i);
                        place = i;
                    }
                }
                stations.remove(place);
                sortedList.add(station);
            }
        }
        stations = sortedList;
        return sortedList;
    }

    public void majList(ArrayList<Station> stations){
        if(stations.size()>0) {
            StationAdapter adapter = new StationAdapter(this, stations, location);
            stationList.setAdapter(adapter);
        }
    }

    class ParserStation extends Thread {
        private static final String TAG = "Parser";
        private ArrayList<Station> stations = new ArrayList<Station>();
        private Message msg;
        private Bundle bundle = new Bundle();

        public void run(){
            while (true){
                parsing();
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void parsing(){
            try {
                String response = null;
                URL json = new URL("https://feeds.divvybikes.com/stations/stations.json");
                HttpURLConnection connection = (HttpURLConnection) json.openConnection();
                connection.setRequestMethod("GET");
                InputStream in = new BufferedInputStream(connection.getInputStream());
                response = convertStreamToString(in);
                if(response != null){
                    JSONObject file = new JSONObject(response);
                    JSONArray list = file.getJSONArray("stationBeanList");
                    for(int i = 0;i<list.length();i++){
                        JSONObject station = list.getJSONObject(i);
                        if(station.getInt("statusKey") == 1) {
                            stations.add(new Station(
                                    station.getString("stationName"),
                                    station.getInt("availableBikes"),
                                    station.getInt("availableDocks"),
                                    station.getInt("id"),
                                    station.getDouble("latitude"),
                                    station.getDouble("longitude")
                            ));
                        }
                    }
                }
                else{stations.add(new Station("Error of connection",-1,-1,-1,-1,-1));}
            }
            catch (MalformedURLException e) {Log.e(TAG, "MalformedURLException: " + e.getMessage());}
            catch (ProtocolException e) {Log.e(TAG, "ProtocolException: " + e.getMessage());}
            catch (IOException e) {Log.e(TAG, "IOException: " + e.getMessage());}
            catch (Exception e) {Log.e(TAG, "Exception: " + e.getMessage());}

            bundle.putParcelableArrayList("Stations",stations);
            msg = handler.obtainMessage();
            msg.setData(bundle);
            handler.sendMessage(msg);
        }

        public String convertStreamToString(InputStream is) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            String line;
            try {while ((line = reader.readLine()) != null) {sb.append(line).append('\n');}}
            catch (IOException e) {e.printStackTrace();}
            finally {try {is.close();}
            catch (IOException e) {e.printStackTrace();}
            }
            return sb.toString();
        }
    }

    class ListeStation extends Thread{
        private static final String TAG = "Liste des stations";

        public void run(){
            Button blback = (Button) findViewById(R.id.StationListBack);
            blback.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    setContentView(R.layout.landing_page);
                    listeView.interrupt();
                    listeView = null;
                    lander = new Lander();
                    lander.start();
                }
            });

            while (true){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        majList(trier(stations,tri));
                    }
                });
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Lander extends Thread{
        private static final String TAG = "Landing page";

        public void run(){
            Button blkm = (Button) findViewById(R.id.SortByDistance);
            blkm.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    affichageList(trier(stations, (byte) 1));
                }
            });

            Button blbike = (Button) findViewById(R.id.SortByBike);
            blbike.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    affichageList(trier(stations, (byte) 2));
                }
            });

            Button bldock = (Button) findViewById(R.id.SortByDock);
            bldock.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    affichageList(trier(stations, (byte) 3));
                }
            });
        }
    }
}