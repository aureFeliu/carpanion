package com.ronan.carpanion.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ronan.carpanion.viewlayouts.MessagesViewLayout;
import com.ronan.carpanion.viewlayouts.PassengerViewLayout;

import java.util.ArrayList;
import java.util.Set;

public class MessagesListAdapter extends BaseAdapter
{
    private Context context;
    private ArrayList<String> users;

    public MessagesListAdapter(Context context, ArrayList<String> users)
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
        MessagesViewLayout messagesViewLayout = null;
        String string = users.get(i);

        if(view == null)
        {
            messagesViewLayout = new MessagesViewLayout(context, string);
        }
        else
        {
            messagesViewLayout = (MessagesViewLayout) view;
            messagesViewLayout.setUser(string);
        }
        return messagesViewLayout;
    }
}
