package com.example.ft_hangouts;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ContactEditActivity extends Activity {
    private boolean parseFields() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_contact);

        Button addButton = findViewById(R.id.addContactButton);


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}