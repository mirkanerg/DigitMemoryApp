package com.example.digitmemory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DMmenu extends AppCompatActivity {
    Button bplay;
    Button bstats;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Initialisiere die Buttons
        bplay = findViewById(R.id.buttonplay);
        bstats = findViewById(R.id.buttonstats);

        // Lies den Benutzernamen aus den SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
        username = prefs.getString("CURRENT_USER", null);

        // Setze den OnClickListener für den "Play"-Button
        bplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Starte die MainActivity, wenn der "Play"-Button geklickt wird
                Intent intent = new Intent(DMmenu.this, MainActivity.class);
                intent.putExtra("USERNAME_EXTRA", username); // Übergebe den Benutzernamen als Extra
                startActivity(intent);
            }
        });

        // Setze den OnClickListener für den "Stats"-Button
        bstats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Starte die StatsActivity, wenn der "Stats"-Button geklickt wird
                Intent intent = new Intent(DMmenu.this, StatsActivity.class);
                intent.putExtra("USERNAME_EXTRA", username); // Übergebe den Benutzernamen als Extra
                startActivity(intent);
            }
        });
    }
}
