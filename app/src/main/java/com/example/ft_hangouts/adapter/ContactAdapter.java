package com.example.ft_hangouts.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ft_hangouts.ContactViews;
import com.example.ft_hangouts.R;
import com.example.ft_hangouts.model.Contact;

import java.util.List;

public class ContactAdapter extends ArrayAdapter<Contact> {

    public ContactAdapter(Context context, List<Contact> contacts) {
        super(context, 0, contacts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_contact, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Contact c = getItem(position);
        holder.name.setText(ContactViews.fullName(getContext(), c));
        holder.phone.setText(c.getPhone());
        ContactViews.bindAvatar(c, holder.avatarLetter, holder.avatarImage);

        return convertView;
    }

    private static final class ViewHolder {
        final TextView avatarLetter;
        final ImageView avatarImage;
        final TextView name;
        final TextView phone;

        ViewHolder(View row) {
            avatarLetter = row.findViewById(R.id.avatar);
            avatarImage = row.findViewById(R.id.avatarImage);
            name = row.findViewById(R.id.name);
            phone = row.findViewById(R.id.phone);
        }
    }
}