package com.example.ft_hangouts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.LruCache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class AvatarStore {
    private static final LruCache<Long, Bitmap> cache =
            new LruCache<Long, Bitmap>(4 * 1024 * 1024) { // 4 MB
                @Override
                protected int sizeOf(Long key, Bitmap value) {
                    return value.getByteCount();
                }
            };

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
        cache.remove(contactId);
    }

    public static void delete(Context context, long contactId) {
        cache.remove(contactId);
        fileFor(context, contactId).delete();
    }

    public static Bitmap load(Context context, long contactId, int targetPx) {
        Bitmap cached = cache.get(contactId);
        if (cached != null) return cached;

        File file = fileFor(context, contactId);
        if (!file.exists()) return null;

        Bitmap bmp = decodeSampled(file, targetPx);
        if (bmp != null) cache.put(contactId, bmp);
        return bmp;
    }

    private static Bitmap decodeSampled(File file, int targetPx) {
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