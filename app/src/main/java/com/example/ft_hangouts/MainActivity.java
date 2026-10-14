package com.example.ft_hangouts;   // keep YOUR package line

import java.util.List;
import android.widget.ArrayAdapter;
import com.example.ft_hangouts.db.DatabaseHelper;
import com.example.ft_hangouts.model.Contact;
import android.util.Log;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);   // loads the XML
        android.widget.Toast.makeText(this, "NEW BUILD 123", android.widget.Toast.LENGTH_LONG).show();

        TextView title = findViewById(R.id.titleText);
        Button addButton = findViewById(R.id.addButton);
        ListView contactList = findViewById(R.id.contacts);

        DatabaseHelper db = DatabaseHelper.getInstance(this);
        List<Contact> contacts = db.getAllContacts();

        ArrayAdapter<Contact> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, contacts);
        contactList.setAdapter(adapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ContactEditActivity.class);
                startActivity(intent);
            }
        });
    }
}