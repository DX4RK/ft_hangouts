package com.example.ft_hangouts;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ft_hangouts.model.Contact;

public final class ContactViews {
    public static final int AVATAR_SIZE_PX = 144;

    private ContactViews() {}

    public static String fullName(Context context, Contact c) {
        return context.getString(R.string.contact_full_name,
                nullToEmpty(c.getFirstName()).trim(),
                nullToEmpty(c.getLastName()).trim()).trim();
    }

    public static void bindAvatar(Contact c, TextView letter, ImageView image) {
        Bitmap bmp = AvatarStore.load(image.getContext(), c.getId(), AVATAR_SIZE_PX);
        if (bmp != null) {
            image.setImageBitmap(bmp);
            image.setClipToOutline(true);
            image.setVisibility(View.VISIBLE);
            letter.setVisibility(View.GONE);
        } else {
            String first = nullToEmpty(c.getFirstName()).trim();
            if (first.isEmpty()) {
                letter.setText(R.string.avatar_placeholder);
            } else {
                letter.setText(first.substring(0, 1).toUpperCase());
            }
            letter.setVisibility(View.VISIBLE);
            image.setVisibility(View.GONE);
        }
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}