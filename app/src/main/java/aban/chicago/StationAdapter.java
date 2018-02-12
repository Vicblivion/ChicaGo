package aban.chicago;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Vicblivion on 19/01/2018.
 */

public class StationAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Station> mStationSource;
    private Location location;

    public StationAdapter(Context context, ArrayList<Station> stations, Location location){
        mContext = context;
        mStationSource = stations;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount(){return mStationSource.size();}

    @Override
    public Object getItem(int position){return mStationSource.get(position);}

    @Override
    public long getItemId(int position){return position;}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get view for row item
        View rowView = mInflater.inflate(R.layout.station, parent, false);
        //Get all elements to display from a station
        TextView stationName = (TextView) rowView.findViewById(R.id.StationName);
        TextView bikeNumber = (TextView) rowView.findViewById(R.id.NumberBike);
        TextView dockNumber = (TextView) rowView.findViewById(R.id.NumberDock);
        Button button = (Button) rowView.findViewById(R.id.Map);

        Station station = (Station) getItem(position);

        stationName.setText(station.getName());
        bikeNumber.setText(""+station.getBike());
        dockNumber.setText(""+station.getDock());
        if(station.getDistance()<1){button.setText((int) (station.getDistance()*1000)+" m");}
        else{button.setText(station.getDistance()+" Km");}
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });
        return rowView;
    }
}