package com.example.ft_hangouts;   // keep YOUR package line

import com.example.ft_hangouts.db.DatabaseHelper;
import com.example.ft_hangouts.model.Contact;
import android.util.Log;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);   // loads the XML
        android.widget.Toast.makeText(this, "NEW BUILD 123", android.widget.Toast.LENGTH_LONG).show();

        DatabaseHelper db = DatabaseHelper.getInstance(this);
        db.addContact(new Contact("Alice", "Martin", "0601020304", "a@x.com", "Paris"));
        for (Contact c : db.getAllContacts())
            Log.d("DB_TEST", c.getId() + " " + c.getFirstName() + " " + c.getPhone());

        TextView title = findViewById(R.id.titleText);
        Button addButton = findViewById(R.id.addButton);

        title.setText("Hello");

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open another screen
                Intent intent = new Intent(MainActivity.this, ContactEditActivity.class);
                startActivity(intent);
            }
        });
    }
}