package com.example.ft_hangouts;

import com.example.ft_hangouts.adapter.ContactAdapter;
import com.example.ft_hangouts.adapter.MessageAdapter;
import com.example.ft_hangouts.model.Contact;
import com.example.ft_hangouts.model.Message;
import com.example.ft_hangouts.db.DatabaseHelper;
import com.example.ft_hangouts.receiver.SmsReceiver;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.content.BroadcastReceiver;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
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
         /* TextView contactName = findViewById(R.id.contactName); */
        ImageButton sendButton = findViewById(R.id.buttonSend);
        ImageButton closeButton = findViewById(R.id.closeButton);
        EditText editMessage = findViewById(R.id.editMessage);

        TextView avatarLetter = findViewById(R.id.avatar);
        ImageView avatarImage = findViewById(R.id.avatarImage);
        //TextView avatar = findViewById(R.id.avatar);
        TextView name = findViewById(R.id.name);
        TextView phone = findViewById(R.id.phone);

        File file = new File(new File(getFilesDir(), "avatars"),
                "contact_" + contactId + ".jpg");
        Bitmap bmp = file.exists() ? loadSmall(file, 144) : null;

        String first = contact.getFirstName() == null ? "" : contact.getFirstName();
        String last = contact.getLastName() == null ? "" : contact.getLastName();

        name.setText(first + " " + last);
        phone.setText(contact.getPhone());
        if (bmp != null) {
            avatarImage.setImageBitmap(bmp);
            avatarImage.setClipToOutline(true);
            avatarImage.setVisibility(View.VISIBLE);
            avatarLetter.setVisibility(View.GONE);
        } else {
            String firstN = contact.getFirstName().trim();
            avatarLetter.setText(firstN.isEmpty() ? "?" : firstN.substring(0, 1).toUpperCase());
            avatarLetter.setVisibility(View.VISIBLE);
            avatarImage.setVisibility(View.GONE);
        }
        //avatar.setText(first.isEmpty() ? "?" : first.substring(0, 1).toUpperCase());

        messagesList.setAdapter(adapter);

        /*contactName.setText(contact.getFirstName() + " " + contact.getLastName());*/
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
        editMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean enterPressed = event != null
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                        && event.getAction() == KeyEvent.ACTION_DOWN;
                if (actionId == EditorInfo.IME_ACTION_SEND || enterPressed) {
                    sendButton.performClick();
                    return true;
                }
                return false;
            }
        });
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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

    @Override
    protected void onResume() {
        super.onResume();
        if (contact == null)
            return;
        IntentFilter filter = new IntentFilter(SmsReceiver.ACTION_NEW_MESSAGE);
        if (Build.VERSION.SDK_INT >= 33)
            registerReceiver(messageReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        else
            registerReceiver(messageReceiver, filter);
        refreshMessages();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (contact == null)
            return;
        unregisterReceiver(messageReceiver);
    }
}