package com.example.ft_hangouts;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ft_hangouts.db.DatabaseHelper;
import com.example.ft_hangouts.model.Contact;

import java.io.IOException;

public class ContactEditActivity extends Activity {
    private static final String TAG = "ContactEdit";
    private static final String STATE_PHOTO = "selectedPhoto";
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText firstName;
    private EditText lastName;
    private EditText phone;
    private EditText email;
    private EditText address;
    private ImageView avatar;
    private Uri selectedPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_contact);

        firstName = findViewById(R.id.firstNameInput);
        lastName = findViewById(R.id.lastNameInput);
        phone = findViewById(R.id.phoneInput);
        email = findViewById(R.id.emailInput);
        address = findViewById(R.id.addressInput);
        avatar = findViewById(R.id.avatarImage);

        if (savedInstanceState != null) {
            selectedPhoto = savedInstanceState.getParcelable(STATE_PHOTO);
            if (selectedPhoto != null) avatar.setImageURI(selectedPhoto);
        }

        findViewById(R.id.closeButton).setOnClickListener(v -> finish());
        findViewById(R.id.addPhoto).setOnClickListener(v -> pickPhoto());
        findViewById(R.id.addContact).setOnClickListener(v -> saveContact());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_PHOTO, selectedPhoto);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            selectedPhoto = data.getData();
            avatar.setImageURI(selectedPhoto);
        }
    }

    private void pickPhoto() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        try {
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.no_image_picker, Toast.LENGTH_SHORT).show();
        }
    }

    private void saveContact() {
        if (!validateFields()) return;

        Contact contact = new Contact(
                text(firstName), text(lastName), text(phone), text(email), text(address));

        long id = DatabaseHelper.getInstance(this).addContact(contact);
        if (id == -1) {
            Toast.makeText(this, R.string.contact_add_error, Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedPhoto != null) {
            try {
                AvatarStore.save(this, id, selectedPhoto);
            } catch (IOException e) {
                Log.e(TAG, "Failed to save avatar", e);
                Toast.makeText(this, R.string.avatar_save_error, Toast.LENGTH_SHORT).show();
            }
        }

        Toast.makeText(this, R.string.contact_added, Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean validateFields() {
        boolean valid = true;

        if (text(firstName).isEmpty()) {
            firstName.setError(getString(R.string.error_first_name_required));
            valid = false;
        }
        if (text(lastName).isEmpty()) {
            lastName.setError(getString(R.string.error_last_name_required));
            valid = false;
        }

        String phoneText = text(phone);
        if (phoneText.isEmpty()) {
            phone.setError(getString(R.string.error_phone_required));
            valid = false;
        } else if (!isValidPhone(phoneText)) {
            phone.setError(getString(R.string.error_phone_invalid));
            valid = false;
        }

        String emailText = text(email);
        if (!emailText.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            email.setError(getString(R.string.error_email_invalid));
            valid = false;
        }

        return valid;
    }

    private static boolean isValidPhone(String phone) {
        if (!phone.matches("\\+?[0-9() .-]+")) return false;
        int digits = phone.replaceAll("\\D", "").length();
        return digits >= 7 && digits <= 15;
    }

    private static String text(EditText field) {
        return field.getText().toString().trim();
    }
}