package com.example.ft_hangouts.adapter;

import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import com.example.ft_hangouts.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.ft_hangouts.model.Contact;
import com.example.ft_hangouts.model.Message;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {

    private final Contact contact;
    public MessageAdapter(Context context, List<Message> messages, Contact contact) {
        super(context, android.R.layout.simple_list_item_2, messages);
        this.contact = contact;
    }

    /*public MessageAdapter(Context context, List<Message> messages) {
        super(context, android.R.layout.simple_list_item_2, messages);
    }*/

    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null)
            row = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_message, parent, false);

        Message m = getItem(position);
        if (m == null)
            return row;

        LinearLayout messageRow = row.findViewById(R.id.messageRow);
        LinearLayout bubble = row.findViewById(R.id.bubble);
        TextView body = row.findViewById(R.id.messageBody);
        TextView time = row.findViewById(R.id.messageTime);

        body.setText(m.getBody());
        time.setText(timeFormat.format(new Date(m.getTimestamp())));

        if (m.isSent()) {
            messageRow.setGravity(Gravity.END);
            bubble.setBackgroundResource(R.drawable.bubble_sent);
            body.setTextColor(Color.WHITE);
            time.setTextColor(Color.parseColor("#CCE0FF"));
        } else {
            messageRow.setGravity(Gravity.START);
            bubble.setBackgroundResource(R.drawable.bubble_received);
            body.setTextColor(Color.BLACK);
            time.setTextColor(Color.parseColor("#9CA3AF"));
        }

        return row;
    }
}