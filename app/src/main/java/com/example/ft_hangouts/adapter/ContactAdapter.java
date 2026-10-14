package com.example.ft_hangouts.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.ft_hangouts.model.Contact;

import java.util.List;

public class ContactAdapter extends ArrayAdapter<Contact> {

    public ContactAdapter(Context context, List<Contact> contacts) {
        super(context, android.R.layout.simple_list_item_2, contacts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null)
            row = LayoutInflater.from(getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);

        Contact c = getItem(position);

        TextView line1 = row.findViewById(android.R.id.text1);
        TextView line2 = row.findViewById(android.R.id.text2);

        line1.setText(c.getFirstName() + " " + c.getLastName());
        line2.setText(c.getPhone());

        return row;
    }
}