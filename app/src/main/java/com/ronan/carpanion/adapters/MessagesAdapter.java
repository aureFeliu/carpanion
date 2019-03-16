package com.ronan.carpanion.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.ronan.carpanion.R;
import com.ronan.carpanion.entitites.Message;
import com.ronan.carpanion.viewlayouts.TripViewLayout;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder>
{
    public static final int ITEM_TYPE_SENT = 0;
    public static final int ITEM_TYPE_RECIEVED = 1;

    private List<Message> messages;
    private Context context;

    private String profileImage;
    private Bitmap image;

    @Override
    public int getItemViewType(int position)
    {
        if (messages.get(position).getFromUser().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
        {
            return ITEM_TYPE_SENT;
        }
        else
        {
            return ITEM_TYPE_RECIEVED;
        }
    }

    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = null;
        if (viewType == ITEM_TYPE_SENT)
        {
            v = LayoutInflater.from(context).inflate(R.layout.sent_message, null);
        }
        else if (viewType == ITEM_TYPE_RECIEVED)
        {
            v = LayoutInflater.from(context).inflate(R.layout.received_message, null);
        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/circular-book.otf");
        Message message = messages.get(position);
        holder.messageTextView.setText(message.getMessageText());
        holder.messageTextView.setTypeface(typeface);
        holder.userProfile.setImageBitmap(image);
    }

    @Override
    public int getItemCount()
    {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView messageTextView;
        public ImageView userProfile;
        public View layout;

        public ViewHolder(View v)
        {
            super(v);
            layout = v;
            messageTextView = (TextView) v.findViewById(R.id.chatMsgTextView);
            userProfile = (ImageView) v.findViewById(R.id.userProfile);
        }
    }

    public class ViewHolder2 extends RecyclerView.ViewHolder
    {
        public TextView messageTextView;
        public View layout;

        public ViewHolder2(View v)
        {
            super(v);
            layout = v;
            messageTextView = (TextView) v.findViewById(R.id.chatMsgTextView);
        }
    }

    public void add(int position, Message message)
    {
        messages.add(position, message);
        notifyItemInserted(position);
    }

    public void remove(int position)
    {
        messages.remove(position);
        notifyItemRemoved(position);
    }

    public MessagesAdapter(List<Message> myDataset, String profileImage, Context context)
    {
        messages = myDataset;
        this.profileImage = profileImage;
        this.context = context;

        try
        {
            image = new TripViewLayout.GetUserImage().execute(profileImage).get();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch (ExecutionException e)
        {
            e.printStackTrace();
        }
    }
}
