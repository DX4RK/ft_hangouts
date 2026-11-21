package com.example.ft_hangouts;

import com.example.ft_hangouts.model.Contact;
import com.example.ft_hangouts.db.DatabaseHelper;
import android.content.ActivityNotFoundException;

import android.net.Uri;
import android.widget.ImageView;
import android.content.Intent;
import android.widget.Toast;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ContactEditActivity extends Activity {
    private Uri selectedPhoto;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int TAKE_PHOTO_REQUEST = 2;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            selectedPhoto = data.getData();

            ImageView avatar = findViewById(R.id.avatarImage);
            avatar.setImageURI(selectedPhoto);
        }
    }

    private boolean parseFields() {
        EditText firstName = findViewById(R.id.firstNameInput);
        EditText lastName = findViewById(R.id.lastNameInput);
        EditText phone = findViewById(R.id.phoneInput);
        EditText email = findViewById(R.id.emailInput);
        EditText address = findViewById(R.id.addressInput);

        boolean valid = true;

        if (firstName.getText().toString().trim().isEmpty()) {
            firstName.setError("First name is required");
            valid = false;
        }

        if (lastName.getText().toString().trim().isEmpty()) {
            lastName.setError("Last name is required");
            valid = false;
        }

        if (phone.getText().toString().trim().isEmpty()) {
            phone.setError("Phone number is required");
            valid = false;
        } else if (!isValidPhone(phone.getText().toString())) {
            phone.setError("Invalid phone number");
            valid = false;
        }

        String emailText = email.getText().toString().trim();
        if (!emailText.isEmpty()
                && !android.util.Patterns.EMAIL_ADDRESS
                .matcher(emailText).matches()) {
            email.setError("Invalid email address");
            valid = false;
        }

        return valid;
    }

    private boolean isValidPhone(String phone) {
        phone = phone.trim();
        if (!phone.matches("\\+?[0-9() .-]+")) {
            return false;
        }

        String digits = phone.replaceAll("\\D", "");
        return digits.length() >= 7 && digits.length() <= 15;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_contact);

        Button addButton = findViewById(R.id.addContact);
        ImageButton closeButton = findViewById(R.id.closeButton);
        Button addPhoto = findViewById(R.id.addPhoto);
        EditText firstName = findViewById(R.id.firstNameInput);
        EditText lastName = findViewById(R.id.lastNameInput);
        EditText phone = findViewById(R.id.phoneInput);
        EditText email = findViewById(R.id.emailInput);
        EditText address = findViewById(R.id.addressInput);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                try {
                    Log.d("TEST", "hey");
                    startActivityForResult(intent, PICK_IMAGE_REQUEST);
                } catch (ActivityNotFoundException e) {
                    Log.d("TEST", "no");
                }
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!parseFields()) {
                    return;
                }

                try {
                    Contact contact = new Contact(
                            firstName.getText().toString(),
                            lastName.getText().toString(),
                            phone.getText().toString(),
                            email.getText().toString(),
                            address.getText().toString()
                    );
                    DatabaseHelper db = DatabaseHelper.getInstance(ContactEditActivity.this);
                    long id = db.addContact(contact);
                    if (id != -1) {
                        Toast.makeText(ContactEditActivity.this,
                                "Contact added!", Toast.LENGTH_SHORT).show();

                        File dir = new File(getFilesDir(), "avatars");
                        if (!dir.exists() && !dir.mkdirs()) {
                            Log.e("AVATAR", "Could not create avatars folder");
                            return;
                        }

                        File file = new File(dir, "contact_" + id + ".jpg");

                        try (InputStream in = getContentResolver().openInputStream(selectedPhoto);
                             OutputStream out = new FileOutputStream(file)) {
                            if (in == null) throw new IOException("Cannot open " + selectedPhoto);
                            byte[] buf = new byte[8192];
                            int n;
                            while ((n = in.read(buf)) != -1) {
                                out.write(buf, 0, n);
                            }
                        } catch (IOException e) {
                            Log.e("AVATAR", "Failed to save avatar", e);
                        }

                        Log.d("AVATAR", "Saved: " + file.getAbsolutePath()
                                + " exists=" + file.exists()
                                + " size=" + file.length() + " bytes");
                        finish();
                    } else {
                        Toast.makeText(ContactEditActivity.this,
                                "Error adding contact", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.d("CONTACT_DEBUG", e.getMessage().toString());
                }
            }
        });
    }
}