package com.example.digitmemory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameOver extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameover);

        // Die Extras aus der MainActivity abrufen
        Intent intent = getIntent();
        String generatedNumber = intent.getStringExtra("NUMBER_KEY");
        int currentLevel = intent.getIntExtra("LEVEL_KEY", 1);

        // TextViews initialisieren
        TextView textViewGameOver = findViewById(R.id.textView2);
        TextView textViewGeneratedNumber = findViewById(R.id.textViewCorrectNumber);
        TextView textViewLevel = findViewById(R.id.textViewLevel);
        TextView textViewHighScore = findViewById(R.id.textViewHighscore);

        // Texte für die TextViews setzen
        textViewGameOver.setText("GAME OVER!");
        textViewGeneratedNumber.setText("Correct Number: " + generatedNumber);
        textViewLevel.setText("Level: " + currentLevel);

        // Benutzernamen aus den SharedPreferences abrufen
        SharedPreferences preferences = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
        String username = preferences.getString("CURRENT_USER", "");

        // DatabaseHelper-Instanz initialisieren
        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        // Den aktuellen Highscore des Benutzers aus der Datenbank abrufen
        int userHighScore = databaseHelper.getUserHighScore(username);

        // Überprüfen, ob das aktuelle Level höher als der Highscore des Benutzers ist
        if (currentLevel > userHighScore) {
            textViewHighScore.setText("New Highscore: " + currentLevel);
            // Den Highscore des Benutzers in der Datenbank aktualisieren
            databaseHelper.updateUserHighScore(username, currentLevel);
        } else {
            textViewHighScore.setText("Highscore: " + userHighScore);

        }

        // "Zurück zum Menü" Button initialisieren
        Button buttonBackToMenu = findViewById(R.id.buttonbackmenu);
        buttonBackToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Zurück zum Menü wechseln, wenn der "Zurück zum Menü" Button geklickt wird
                Intent menuIntent = new Intent(GameOver.this, DMmenu.class);
                startActivity(menuIntent);
            }
        });
    }
}
