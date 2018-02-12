package aban.chicago;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Button;

import static android.content.ContentValues.TAG;
import static java.lang.Math.abs;

/**
 * Created by Vicblivion on 18/01/2018.
 */

public class Station implements Parcelable{
    private static final String TAG = "Station";
    private String name = new String();
    private int bike = 0;
    private int dock = 0;
    private int number = -1;
    private double latitude = 0;
    private double longitude = 0;
    private double distance = 0;

    public Station(String name, int bike, int dock, int number, double latitude, double longitude){
        this.name = name;
        this.bike = bike;
        this.dock = dock;
        this.number = number;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static final Creator<Station> CREATOR = new Creator<Station>() {
        @Override
        public Station createFromParcel(Parcel in) {
            return new Station(in);
        }

        @Override
        public Station[] newArray(int size) {
            return new Station[size];
        }
    };

    public void setName(String name){this.name = name;}
    public void setBike(int bike){this.bike = bike;}
    public void setDock(int dock){this.dock = dock;}
    public void setDistance(double tlat, double tlong){distance = distance(tlat,tlong);}

    public String getName(){return this.name;}
    public int getBike(){return this.bike;}
    public int getDock(){return this.dock;}
    public int getNumber(){return this.number;}
    public double getLatitude(){return this.latitude;}
    public double getLongitude(){return this.longitude;}
    public double getDistance(){return distance;}

    public Station(Parcel in){
        String[] data = new String[6];

        in.readStringArray(data);
        this.name = data[0];
        this.bike = Integer.parseInt(data[1]);
        this.dock = Integer.parseInt(data[2]);
        this.number = Integer.parseInt(data[3]);
        this.latitude = Double.parseDouble(data[4]);
        this.longitude = Double.parseDouble(data[5]);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[]{this.name,""+this.bike,""+this.dock,""+this.number,""+this.latitude,""+this.longitude});
    }

    public double distance(double tLatitude, double tLongitude){
        double lat = (this.latitude-tLatitude) * (1000/9);
        //if(this.number==2){Log.e(TAG,""+lat);}
        double lon = (this.longitude-tLongitude) * (1000/9) * Math.cos(lat);
        //if(this.number==2){Log.e(TAG,""+lon);}
        return (double) Math.round((Math.sqrt(abs(Math.pow(lon,2)-Math.pow(lat,2))))*1000)/1000;
    }
}