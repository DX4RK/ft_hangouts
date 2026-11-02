package com.example.ft_hangouts.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.ft_hangouts.R;
import com.example.ft_hangouts.model.Contact;

import java.util.List;

public class ContactAdapter extends ArrayAdapter<Contact> {

    public ContactAdapter(Context context, List<Contact> contacts) {
        super(context, 0, contacts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null)
            row = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_contact, parent, false);

        Contact c = getItem(position);

        TextView avatar = row.findViewById(R.id.avatar);
        TextView name = row.findViewById(R.id.name);
        TextView phone = row.findViewById(R.id.phone);

        String first = c.getFirstName() == null ? "" : c.getFirstName();
        String last = c.getLastName() == null ? "" : c.getLastName();

        name.setText(first + " " + last);
        phone.setText(c.getPhone());
        avatar.setText(first.isEmpty() ? "?" : first.substring(0, 1).toUpperCase());

        return row;
    }
}