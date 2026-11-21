package com.example.ft_hangouts;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class AvatarStore {
    private AvatarStore() {}

    public static File fileFor(Context context, long contactId) {
        return new File(new File(context.getFilesDir(), "avatars"),
                "contact_" + contactId + ".jpg");
    }

    public static void save(Context context, long contactId, Uri source) throws IOException {
        File file = fileFor(context, contactId);
        File dir = file.getParentFile();
        if (dir != null && !dir.exists() && !dir.mkdirs()) {
            throw new IOException("Could not create " + dir);
        }
        try (InputStream in = context.getContentResolver().openInputStream(source);
             OutputStream out = new FileOutputStream(file)) {
            if (in == null) throw new IOException("Cannot open " + source);
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) != -1) {
                out.write(buf, 0, n);
            }
        }
    }
}