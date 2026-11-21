package com.example.ft_hangouts.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ft_hangouts.R;
import com.example.ft_hangouts.model.Contact;

import java.io.File;
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

        //TextView avatar = row.findViewById(R.id.avatar);
        TextView avatarLetter = row.findViewById(R.id.avatar);
        ImageView avatarImage = row.findViewById(R.id.avatarImage);
        TextView name = row.findViewById(R.id.name);
        TextView phone = row.findViewById(R.id.phone);

        File file = new File(new File(getContext().getFilesDir(), "avatars"),
                "contact_" + c.getId() + ".jpg");
        Bitmap bmp = file.exists() ? loadSmall(file, 144) : null;

        String first = c.getFirstName() == null ? "" : c.getFirstName();
        String last = c.getLastName() == null ? "" : c.getLastName();

        name.setText(first + " " + last);
        phone.setText(c.getPhone());
        //avatar.setText(first.isEmpty() ? "?" : first.substring(0, 1).toUpperCase());
        if (bmp != null) {
            avatarImage.setImageBitmap(bmp);
            avatarImage.setClipToOutline(true);
            avatarImage.setVisibility(View.VISIBLE);
            avatarLetter.setVisibility(View.GONE);
        } else {
            String firstN = c.getFirstName().trim();
            avatarLetter.setText(firstN.isEmpty() ? "?" : firstN.substring(0, 1).toUpperCase());
            avatarLetter.setVisibility(View.VISIBLE);
            avatarImage.setVisibility(View.GONE);
        }

        return row;
    }

    private static Bitmap loadSmall(File file, int targetPx) {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), o);

        int sample = 1;
        while (o.outWidth / (sample * 2) >= targetPx && o.outHeight / (sample * 2) >= targetPx) {
            sample *= 2;
        }
        o.inJustDecodeBounds = false;
        o.inSampleSize = sample;
        return BitmapFactory.decodeFile(file.getAbsolutePath(), o);
    }
}