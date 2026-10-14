package com.example.ft_hangouts.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ft_hangouts.model.Contact;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper instance;

    private static final String DATABASE_NAME = "ContactsDB";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "contacts";
    private static final String ID_FIELD = "id";
    private static final String FIRST_NAME_FIELD = "firstName";
    private static final String LAST_NAME_FIELD = "lastName";
    private static final String PHONE_FIELD = "phone";
    private static final String EMAIL_FIELD = "email";
    private static final String ADDRESS_FIELD = "address";

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null)
            instance = new DatabaseHelper(context.getApplicationContext());
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
                + ID_FIELD + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FIRST_NAME_FIELD + " TEXT, "
                + LAST_NAME_FIELD + " TEXT, "
                + PHONE_FIELD + " TEXT, "
                + EMAIL_FIELD + " TEXT, "
                + ADDRESS_FIELD + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    private ContentValues toValues(Contact c) {
        ContentValues values = new ContentValues();
        values.put(FIRST_NAME_FIELD, c.getFirstName());
        values.put(LAST_NAME_FIELD, c.getLastName());
        values.put(PHONE_FIELD, c.getPhone());
        values.put(EMAIL_FIELD, c.getEmail());
        values.put(ADDRESS_FIELD, c.getAddress());
        return values;
    }

    private Contact fromCursor(Cursor cursor) {
        return new Contact(
                cursor.getInt(cursor.getColumnIndexOrThrow(ID_FIELD)),
                cursor.getString(cursor.getColumnIndexOrThrow(FIRST_NAME_FIELD)),
                cursor.getString(cursor.getColumnIndexOrThrow(LAST_NAME_FIELD)),
                cursor.getString(cursor.getColumnIndexOrThrow(PHONE_FIELD)),
                cursor.getString(cursor.getColumnIndexOrThrow(EMAIL_FIELD)),
                cursor.getString(cursor.getColumnIndexOrThrow(ADDRESS_FIELD)));
    }

    public long addContact(Contact contact) {
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABLE_NAME, null, toValues(contact));
    }

    public List<Contact> getAllContacts() {
        List<Contact> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null,
                FIRST_NAME_FIELD + " COLLATE NOCASE ASC");
        while (cursor.moveToNext())
            list.add(fromCursor(cursor));
        cursor.close();
        return list;
    }

    public Contact getContact(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, ID_FIELD + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        Contact contact = null;
        if (cursor.moveToFirst())
            contact = fromCursor(cursor);
        cursor.close();
        return contact;
    }

    public int updateContact(Contact contact) {
        SQLiteDatabase db = getWritableDatabase();
        return db.update(TABLE_NAME, toValues(contact), ID_FIELD + " = ?",
                new String[]{String.valueOf(contact.getId())});
    }

    public int deleteContact(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_NAME, ID_FIELD + " = ?",
                new String[]{String.valueOf(id)});
    }
}