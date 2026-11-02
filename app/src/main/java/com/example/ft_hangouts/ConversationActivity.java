package com.example.ft_hangouts;

import com.example.ft_hangouts.adapter.ContactAdapter;
import com.example.ft_hangouts.adapter.MessageAdapter;
import com.example.ft_hangouts.model.Contact;
import com.example.ft_hangouts.model.Message;
import com.example.ft_hangouts.db.DatabaseHelper;

import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Context;
import android.content.Intent;

import android.content.BroadcastReceiver;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

public class ConversationActivity extends Activity {
    private Contact contact;
    private List<Message> messages;
    private MessageAdapter adapter;
    private DatabaseHelper db;

    public void refreshMessages() {
        messages.clear();
        messages.addAll(db.getMessagesForContact(contact.getId()));
        adapter.notifyDataSetChanged();
    }

    private final BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshMessages();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message);

        int contactId = getIntent().getIntExtra("contactId", -1);

        db = DatabaseHelper.getInstance(this);
        contact = db.getContact(contactId);

        if (contact == null) {
            Toast.makeText(this, "Contact not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        messages = db.getMessagesForContact(contact.getId());
        adapter = new MessageAdapter(this, messages, contact);

        ListView messagesList = findViewById(R.id.listMessages);
        TextView contactName = findViewById(R.id.contactName);
        Button sendButton = findViewById(R.id.buttonSend);
        Button closeButton = findViewById(R.id.closeButton);
        EditText editMessage = findViewById(R.id.editMessage);

        messagesList.setAdapter(adapter);

        contactName.setText(contact.getFirstName() + " " + contact.getLastName());
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editMessage.getText().toString().trim();
                if (text.isEmpty())
                    return;

                Message message = new Message(contact.getId(), text,
                        System.currentTimeMillis(), true);

                db.addMessage(message);
                editMessage.setText("");
                refreshMessages();
            }
        });
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}