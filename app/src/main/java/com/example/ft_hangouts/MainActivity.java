package com.example.ft_hangouts;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.ft_hangouts.adapter.ContactAdapter;
import com.example.ft_hangouts.db.DatabaseHelper;
import com.example.ft_hangouts.model.Contact;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    public static final String EXTRA_CONTACT_ID = "contactId";
    private static final int REQUEST_SMS_PERMISSION = 1;

    private final List<Contact> contacts = new ArrayList<>();
    private ContactAdapter adapter;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = DatabaseHelper.getInstance(this);
        adapter = new ContactAdapter(this, contacts);

        ListView contactList = findViewById(R.id.contacts);
        contactList.setAdapter(adapter);
        contactList.setOnItemClickListener((parent, view, position, id) ->
                openConversation(contacts.get(position)));

        ImageButton addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(v ->
                startActivity(new Intent(this, ContactEditActivity.class)));

        requestSmsPermissionIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshContacts();
    }

    private void refreshContacts() {
        contacts.clear();
        contacts.addAll(db.getAllContacts());
        adapter.notifyDataSetChanged();
    }

    private void openConversation(Contact contact) {
        Intent intent = new Intent(this, ConversationActivity.class);
        intent.putExtra(EXTRA_CONTACT_ID, contact.getId());
        startActivity(intent);
    }

    private void requestSmsPermissionIfNeeded() {
        if (checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS},
                    REQUEST_SMS_PERMISSION);
        }
    }
}