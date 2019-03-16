package com.ronan.carpanion.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ronan.carpanion.viewlayouts.TripViewLayout;
import com.ronan.carpanion.entitites.Trip;

import java.util.ArrayList;

public class TripListAdapter extends BaseAdapter
{
    private Context context;
    private ArrayList<Trip> trips;

    public TripListAdapter(Context context, ArrayList<Trip> trips)
    {
        this.context = context;
        this.trips = trips;
    }

    @Override
    public int getCount()
    {
        return trips.size();
    }

    @Override
    public Object getItem(int i)
    {
        return trips.get(i);
    }

    @Override
    public long getItemId(int i)
    {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        TripViewLayout tripViewLayout = null;
        Trip trip = trips.get(i);

        if(view == null)
        {
            tripViewLayout = new TripViewLayout(context, trip);
        }
        else
        {
            tripViewLayout = (TripViewLayout) view;
            tripViewLayout.setTrip(trip);
        }
        return tripViewLayout;
    }
}
