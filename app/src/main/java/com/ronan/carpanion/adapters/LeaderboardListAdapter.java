package com.ronan.carpanion.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ronan.carpanion.entitites.Trip;
import com.ronan.carpanion.entitites.User;
import com.ronan.carpanion.viewlayouts.LeaderboardViewLayout;
import com.ronan.carpanion.viewlayouts.TripViewLayout;

import java.util.ArrayList;

public class LeaderboardListAdapter extends BaseAdapter
{
    private Context context;
    private ArrayList<User> users;

    public LeaderboardListAdapter(Context context, ArrayList<User> users)
    {
        this.context = context;
        this.users = users;
    }

    @Override
    public int getCount()
    {
        return users.size();
    }

    @Override
    public Object getItem(int i)
    {
        return users.get(i);
    }

    @Override
    public long getItemId(int i)
    {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        LeaderboardViewLayout leaderboardViewLayout = null;
        User user = users.get(i);
        int position = i + 1;

        if(view == null)
        {
            leaderboardViewLayout = new LeaderboardViewLayout(context, user, position);
        }
        else
        {
            leaderboardViewLayout = (LeaderboardViewLayout) view;
            leaderboardViewLayout.setUser(user, position);
        }
        return leaderboardViewLayout;
    }
}
