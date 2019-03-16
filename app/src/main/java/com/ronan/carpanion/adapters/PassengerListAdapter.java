package com.ronan.carpanion.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ronan.carpanion.viewlayouts.PassengerViewLayout;

import java.util.ArrayList;

public class PassengerListAdapter extends BaseAdapter
{
    private Context context;
    private ArrayList<String> passengers;

    public PassengerListAdapter(Context context, ArrayList<String> passengers)
    {
        this.context = context;
        this.passengers = passengers;
    }

    @Override
    public int getCount()
    {
        return passengers.size();
    }

    @Override
    public Object getItem(int i)
    {
        return passengers.get(i);
    }

    @Override
    public long getItemId(int i)
    {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        PassengerViewLayout passengerViewLayout = null;
        String string = passengers.get(i);

        if(view == null)
        {
            passengerViewLayout = new PassengerViewLayout(context, string);
        }
        else
        {
            passengerViewLayout = (PassengerViewLayout) view;
            passengerViewLayout.setPassenger(string);
        }
        return passengerViewLayout;
    }
}
