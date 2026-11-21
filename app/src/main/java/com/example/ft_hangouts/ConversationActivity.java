package com.example.ft_hangouts;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ft_hangouts.adapter.MessageAdapter;
import com.example.ft_hangouts.db.DatabaseHelper;
import com.example.ft_hangouts.model.Contact;
import com.example.ft_hangouts.model.Message;
import com.example.ft_hangouts.receiver.SmsReceiver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConversationActivity extends Activity {
    private static final int AVATAR_SIZE_PX = 144;

    private final List<Message> messages = new ArrayList<>();
    private Contact contact;
    private MessageAdapter adapter;
    private DatabaseHelper db;
    private ListView messagesList;

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

        db = DatabaseHelper.getInstance(this);
        contact = db.getContact(getIntent().getIntExtra(MainActivity.EXTRA_CONTACT_ID, -1));
        if (contact == null) {
            Toast.makeText(this, R.string.contact_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        adapter = new MessageAdapter(this, messages);
        messagesList = findViewById(R.id.listMessages);
        messagesList.setAdapter(adapter);

        bindHeader();
        setupInput();
        findViewById(R.id.closeButton).setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(SmsReceiver.ACTION_NEW_MESSAGE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(messageReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(messageReceiver, filter);
        }
        refreshMessages();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(messageReceiver);
    }

    private void refreshMessages() {
        messages.clear();
        messages.addAll(db.getMessagesForContact(contact.getId()));
        adapter.notifyDataSetChanged();
        messagesList.setSelection(adapter.getCount() - 1);
    }

    private void bindHeader() {
        TextView name = findViewById(R.id.name);
        TextView phone = findViewById(R.id.phone);
        TextView avatarLetter = findViewById(R.id.avatar);
        ImageView avatarImage = findViewById(R.id.avatarImage);

        String first = nullToEmpty(contact.getFirstName()).trim();
        String last = nullToEmpty(contact.getLastName()).trim();
        name.setText((first + " " + last).trim());
        phone.setText(contact.getPhone());

        File file = new File(new File(getFilesDir(), "avatars"),
                "contact_" + contact.getId() + ".jpg");
        Bitmap bmp = file.exists() ? loadSmall(file, AVATAR_SIZE_PX) : null;

        if (bmp != null) {
            avatarImage.setImageBitmap(bmp);
            avatarImage.setClipToOutline(true);
            avatarImage.setVisibility(View.VISIBLE);
            avatarLetter.setVisibility(View.GONE);
        } else {
            avatarLetter.setText(first.isEmpty() ? "?" : first.substring(0, 1).toUpperCase());
            avatarLetter.setVisibility(View.VISIBLE);
            avatarImage.setVisibility(View.GONE);
        }
    }

    private void setupInput() {
        EditText editMessage = findViewById(R.id.editMessage);
        ImageButton sendButton = findViewById(R.id.buttonSend);

        sendButton.setOnClickListener(v -> {
            String text = editMessage.getText().toString().trim();
            if (text.isEmpty()) return;

            db.addMessage(new Message(contact.getId(), text, System.currentTimeMillis(), true));
            editMessage.setText("");
            refreshMessages();
        });

        editMessage.setOnEditorActionListener((v, actionId, event) -> {
            boolean enterPressed = event != null
                    && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                    && event.getAction() == KeyEvent.ACTION_DOWN;
            if (actionId == EditorInfo.IME_ACTION_SEND || enterPressed) {
                sendButton.performClick();
                return true;
            }
            return false;
        });
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
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