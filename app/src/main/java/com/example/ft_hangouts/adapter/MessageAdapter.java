package com.example.ft_hangouts.adapter;

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null)
            row = LayoutInflater.from(getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);

        Message m = getItem(position);
        if (m == null)
            return row;

        String author = m.isSent() ? "Me" : contact.getFirstName();

        TextView line1 = row.findViewById(android.R.id.text1);
        TextView line2 = row.findViewById(android.R.id.text2);

        line1.setText(author);
        line2.setText(m.getBody());

        return row;
    }
}