package com.example.ft_hangouts.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsMessage;

import com.example.ft_hangouts.db.DatabaseHelper;
import com.example.ft_hangouts.model.Contact;
import com.example.ft_hangouts.model.Message;

public class SmsReceiver extends BroadcastReceiver {
    public static final String ACTION_NEW_MESSAGE = "com.example.ft_hangouts.NEW_MESSAGE";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction()))
            return;

        SmsMessage[] parts = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        if (parts == null || parts.length == 0)
            return;

        String sender = parts[0].getOriginatingAddress();
        StringBuilder body = new StringBuilder();
        for (SmsMessage part : parts)
            body.append(part.getMessageBody());

        DatabaseHelper db = DatabaseHelper.getInstance(context);
        Contact contact = findContact(db, sender);

        if (contact == null) {
            long id = db.addContact(new Contact(sender, "", sender, "", ""));
            if (id == -1)
                return;
            contact = db.getContact((int) id);
        }

        db.addMessage(new Message(contact.getId(), body.toString(),
                System.currentTimeMillis(), false));

        Intent update = new Intent(ACTION_NEW_MESSAGE);
        update.setPackage(context.getPackageName());
        context.sendBroadcast(update);
    }

    private Contact findContact(DatabaseHelper db, String phone) {
        for (Contact c : db.getAllContacts()) {
            if (PhoneNumberUtils.compare(c.getPhone(), phone))
                return c;
        }
        return null;
    }
}
