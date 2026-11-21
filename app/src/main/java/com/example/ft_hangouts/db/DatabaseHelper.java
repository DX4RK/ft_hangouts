package com.example.ft_hangouts.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.telephony.PhoneNumberUtils;

import com.example.ft_hangouts.model.Contact;
import com.example.ft_hangouts.model.Message;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ContactsDB";
    private static final int DATABASE_VERSION = 3;

    private static final String CONTACTS_TABLE = "contacts";
    private static final String CONTACT_ID = "id";
    private static final String CONTACT_FIRST_NAME = "firstName";
    private static final String CONTACT_LAST_NAME = "lastName";
    private static final String CONTACT_PHONE = "phone";
    private static final String CONTACT_EMAIL = "email";
    private static final String CONTACT_ADDRESS = "address";

    private static final String MESSAGES_TABLE = "messages";
    private static final String MSG_ID = "id";
    private static final String MSG_CONTACT_ID = "contactId";
    private static final String MSG_BODY = "body";
    private static final String MSG_TIMESTAMP = "timestamp";
    private static final String MSG_IS_SENT = "isSent";

    private static final String SQL_CREATE_CONTACTS = "CREATE TABLE " + CONTACTS_TABLE + " ("
            + CONTACT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CONTACT_FIRST_NAME + " TEXT, "
            + CONTACT_LAST_NAME + " TEXT, "
            + CONTACT_PHONE + " TEXT, "
            + CONTACT_EMAIL + " TEXT, "
            + CONTACT_ADDRESS + " TEXT)";

    private static final String SQL_CREATE_MESSAGES = "CREATE TABLE " + MESSAGES_TABLE + " ("
            + MSG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + MSG_CONTACT_ID + " INTEGER NOT NULL, "
            + MSG_BODY + " TEXT, "
            + MSG_TIMESTAMP + " INTEGER, "
            + MSG_IS_SENT + " INTEGER, "
            + "FOREIGN KEY(" + MSG_CONTACT_ID + ") REFERENCES "
            + CONTACTS_TABLE + "(" + CONTACT_ID + ") ON DELETE CASCADE)";

    private static final String SQL_INDEX_MESSAGES = "CREATE INDEX IF NOT EXISTS idx_messages_contact "
            + "ON " + MESSAGES_TABLE + "(" + MSG_CONTACT_ID + ", " + MSG_TIMESTAMP + ")";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CONTACTS);
        db.execSQL(SQL_CREATE_MESSAGES);
        db.execSQL(SQL_INDEX_MESSAGES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL(SQL_CREATE_MESSAGES);
        }
        if (oldVersion < 3) {
            db.execSQL(SQL_INDEX_MESSAGES);
        }
    }

    public long addContact(Contact contact) {
        return getWritableDatabase().insert(CONTACTS_TABLE, null, toValues(contact));
    }

    public int updateContact(Contact contact) {
        return getWritableDatabase().update(CONTACTS_TABLE, toValues(contact),
                CONTACT_ID + " = ?", new String[]{String.valueOf(contact.getId())});
    }

    public int deleteContact(int id) {
        return getWritableDatabase().delete(CONTACTS_TABLE,
                CONTACT_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public Contact getContact(int id) {
        try (Cursor cursor = getReadableDatabase().query(CONTACTS_TABLE, null,
                CONTACT_ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null)) {
            return cursor.moveToFirst() ? contactFromCursor(cursor) : null;
        }
    }

    public List<Contact> getAllContacts() {
        List<Contact> list = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query(CONTACTS_TABLE, null,
                null, null, null, null,
                CONTACT_FIRST_NAME + " COLLATE NOCASE ASC")) {
            while (cursor.moveToNext()) {
                list.add(contactFromCursor(cursor));
            }
        }
        return list;
    }

    public Contact getContactByPhone(String phone) {
        if (phone == null) return null;
        for (Contact c : getAllContacts()) {
            if (c.getPhone() != null && PhoneNumberUtils.compare(phone, c.getPhone())) {
                return c;
            }
        }
        return null;
    }

    public long addMessage(Message message) {
        return getWritableDatabase().insert(MESSAGES_TABLE, null, toValues(message));
    }

    public List<Message> getMessagesForContact(int contactId) {
        List<Message> list = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query(MESSAGES_TABLE, null,
                MSG_CONTACT_ID + " = ?", new String[]{String.valueOf(contactId)},
                null, null,
                MSG_TIMESTAMP + " ASC, " + MSG_ID + " ASC")) {
            while (cursor.moveToNext()) {
                list.add(messageFromCursor(cursor));
            }
        }
        return list;
    }

    private static ContentValues toValues(Contact c) {
        ContentValues values = new ContentValues();
        values.put(CONTACT_FIRST_NAME, c.getFirstName());
        values.put(CONTACT_LAST_NAME, c.getLastName());
        values.put(CONTACT_PHONE, c.getPhone());
        values.put(CONTACT_EMAIL, c.getEmail());
        values.put(CONTACT_ADDRESS, c.getAddress());
        return values;
    }

    private static ContentValues toValues(Message m) {
        ContentValues values = new ContentValues();
        values.put(MSG_CONTACT_ID, m.getContactId());
        values.put(MSG_BODY, m.getBody());
        values.put(MSG_TIMESTAMP, m.getTimestamp());
        values.put(MSG_IS_SENT, m.isSent() ? 1 : 0);
        return values;
    }

    private static Contact contactFromCursor(Cursor cursor) {
        return new Contact(
                cursor.getInt(cursor.getColumnIndexOrThrow(CONTACT_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(CONTACT_FIRST_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(CONTACT_LAST_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(CONTACT_PHONE)),
                cursor.getString(cursor.getColumnIndexOrThrow(CONTACT_EMAIL)),
                cursor.getString(cursor.getColumnIndexOrThrow(CONTACT_ADDRESS)));
    }

    private static Message messageFromCursor(Cursor cursor) {
        return new Message(
                cursor.getInt(cursor.getColumnIndexOrThrow(MSG_ID)),
                cursor.getInt(cursor.getColumnIndexOrThrow(MSG_CONTACT_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(MSG_BODY)),
                cursor.getLong(cursor.getColumnIndexOrThrow(MSG_TIMESTAMP)),
                cursor.getInt(cursor.getColumnIndexOrThrow(MSG_IS_SENT)) == 1);
    }
}