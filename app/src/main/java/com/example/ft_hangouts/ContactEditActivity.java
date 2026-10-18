package com.example.ft_hangouts;

import com.example.ft_hangouts.model.Contact;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ContactEditActivity extends Activity {
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

        Button addButton = findViewById(R.id.addContactButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!parseFields()) {
                    return;
                }

            }
        });
    }
}