package com.example.ft_hangouts.adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ft_hangouts.R;
import com.example.ft_hangouts.model.Message;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {
    private final DateFormat timeFormat;
    private final DateFormat dateFormat;

    private final int sentTextColor;
    private final int sentTimeColor;
    private final int receivedTextColor;
    private final int receivedTimeColor;

    public MessageAdapter(Context context, List<Message> messages) {
        super(context, 0, messages);
        timeFormat = android.text.format.DateFormat.getTimeFormat(context);
        dateFormat = android.text.format.DateFormat.getDateFormat(context);

        sentTextColor = context.getColor(R.color.bubble_sent_text);
        sentTimeColor = context.getColor(R.color.bubble_sent_time);
        receivedTextColor = context.getColor(R.color.bubble_received_text);
        receivedTimeColor = context.getColor(R.color.bubble_received_time);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_message, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Message m = getItem(position);
        if (m == null) return convertView;

        holder.body.setText(m.getBody());
        holder.time.setText(formatTimestamp(m.getTimestamp()));

        if (m.isSent()) {
            holder.messageRow.setGravity(Gravity.END);
            holder.bubble.setBackgroundResource(R.drawable.bubble_sent);
            holder.body.setTextColor(sentTextColor);
            holder.time.setTextColor(sentTimeColor);
        } else {
            holder.messageRow.setGravity(Gravity.START);
            holder.bubble.setBackgroundResource(R.drawable.bubble_received);
            holder.body.setTextColor(receivedTextColor);
            holder.time.setTextColor(receivedTimeColor);
        }

        return convertView;
    }

    private String formatTimestamp(long timestamp) {
        Date date = new Date(timestamp);
        return DateUtils.isToday(timestamp) ? timeFormat.format(date) : dateFormat.format(date);
    }

    private static final class ViewHolder {
        final LinearLayout messageRow;
        final LinearLayout bubble;
        final TextView body;
        final TextView time;

        ViewHolder(View row) {
            messageRow = row.findViewById(R.id.messageRow);
            bubble = row.findViewById(R.id.bubble);
            body = row.findViewById(R.id.messageBody);
            time = row.findViewById(R.id.messageTime);
        }
    }
}