package aban.chicago;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Vicblivion on 18/01/2018.
 */

public class Station implements Parcelable{
    private String name = new String();
    private int bike = 0;
    private int dock = 0;
    private int number = -1;
    private double latitude = 0;
    private double longitude = 0;

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

    public String getName(){return this.name;}
    public int getBike(){return this.bike;}
    public int getDock(){return this.dock;}
    public int getNumber(){return this.number;}
    public double getLatitude(){return this.latitude;}
    public double getLongitude(){return this.longitude;}

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
}