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
        }
    };
    private ParserStation parser = new ParserStation();
    private LocationManager service;
    private boolean enabled;
    private String provider;
    private Location location;
    private double latitude = 0;
    private double longitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_page);
        parser.start();

        Button blkm = (Button) findViewById(R.id.SortByDistance);
        blkm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Station station;
                int place;
                ArrayList<Station> sortedList = new ArrayList<Station>();
                while(stations.size()!=0) {
                    station = stations.get(0);
                    place = 0;
                    for (int i = 0; i < stations.size(); i++) {
                        if(stations.get(i).distance(latitude,longitude)<station.distance(latitude,longitude)){
                            station=stations.get(i);
                            place = i;
                            stations.remove(i);
                        }
                    }
                    sortedList.add(station);
                }
            }
        });
    }
        

        /*service = (LocationManager) getSystemService(LOCATION_SERVICE);
        enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        Criteria criteria = new Criteria();
        provider = service.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {return;}
        location = service.getLastKnownLocation(provider);
        while(true){
            majList(stations);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/

    public void majList(ArrayList<Station> stations){
        StationAdapter adapter = new StationAdapter(this, stations, location);
        stationList.setAdapter(adapter);
    }

    class ParserStation extends Thread {
        private static final String TAG = "Parser";
        private ArrayList<Station> stations = new ArrayList<Station>();
        private Message msg = handler.obtainMessage();
        private Bundle bundle = new Bundle();

        public void run(){
            //while (true){
                parsing();
                /*try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
            //}
        }

        public void parsing(){
            try {
                String response = null;
                URL json = new URL("https://feeds.divvybikes.com/stations/stations.json");
                HttpURLConnection connection = (HttpURLConnection) json.openConnection();
                connection.setRequestMethod("GET");
                InputStream in = new BufferedInputStream(connection.getInputStream());
                response = convertStreamToString(in);
                Log.e(TAG,""+response);
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
}