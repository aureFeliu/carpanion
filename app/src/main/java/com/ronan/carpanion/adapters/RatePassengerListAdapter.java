package com.ronan.carpanion.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ronan.carpanion.viewlayouts.PassengerViewLayout;
import com.ronan.carpanion.viewlayouts.RatePassengerViewLayout;

import java.util.ArrayList;
import java.util.HashMap;

public class RatePassengerListAdapter extends BaseAdapter
{
    private Context context;
    private ArrayList<String> passengers;

    public RatePassengerListAdapter(Context context, ArrayList<String> passengers)
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
        RatePassengerViewLayout ratePassengerViewLayout = null;
        String string = passengers.get(i);

        if(view == null)
        {
            ratePassengerViewLayout = new RatePassengerViewLayout(context, string);
        }
        else
        {
            ratePassengerViewLayout = (RatePassengerViewLayout) view;
            ratePassengerViewLayout.setPassenger(string);
        }
        return ratePassengerViewLayout;
    }
}
