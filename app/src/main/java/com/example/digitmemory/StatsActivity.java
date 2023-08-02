package com.example.digitmemory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class StatsActivity extends AppCompatActivity {

    Button bbackmenustats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        bbackmenustats = findViewById(R.id.buttonbackmenustats);

        // Benutzernamen aus den SharedPreferences abrufen
        SharedPreferences preferences = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
        String username = preferences.getString("CURRENT_USER", "");

        // DatabaseHelper-Instanz initialisieren
        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        // Den Highscore des Benutzers aus der Datenbank abrufen
        int userHighScore = databaseHelper.getUserHighScore(username);

        // TextViews initialisieren
        TextView textViewUsername = findViewById(R.id.textViewUsernameSTATS);
        TextView textViewHighScore = findViewById(R.id.textViewHighScoreSTATS);
        TextView textViewLastAttempts = findViewById(R.id.textViewLastAttempts);
        TextView textViewAverage = findViewById(R.id.textViewAverage);

        // Die letzten Versuche des Benutzers aus der Datenbank abrufen
        String lastAttemptsString = databaseHelper.getLastAttempts(username);

        // Die letzten Versuche in eine Liste von Integern konvertieren
        List<Integer> lastAttemptsList = convertStringToList(lastAttemptsString);

        // Die letzten Versuche im TextView "textViewLastAttempts" anzeigen
        textViewLastAttempts.setText("Last 10 Attempts: \n" + lastAttemptsList.toString());

        // Den Durchschnitt der letzten Versuche mit der calculateAverage-Methode berechnen
        double average = calculateAverage(lastAttemptsList);

        // Den TextView "textViewAverage" im Layout finden und den Durchschnittswert setzen
        textViewAverage.setText("Average: " + String.format("%.2f", average));

        // Benutzername und Highscore in den TextViews anzeigen
        textViewUsername.setText("Username: " + username);
        textViewHighScore.setText("High Score: " + userHighScore);

        bbackmenustats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Zum Hauptmenü wechseln, wenn der "Back to Menu" Button geklickt wird
                Intent intent = new Intent(StatsActivity.this, DMmenu.class);
                startActivity(intent);
            }
        });
    }

    // Hilfsmethode zum Konvertieren des letzten Versuchs-Strings in eine Liste von Integern
    private List<Integer> convertStringToList(String lastAttemptsString) {
        List<Integer> lastAttemptsList = new ArrayList<>();
        if (lastAttemptsString != null && !lastAttemptsString.isEmpty()) {
            String[] attemptsArray = lastAttemptsString.split(",");
            for (String attempt : attemptsArray) {
                try {
                    int value = Integer.parseInt(attempt.trim());
                    lastAttemptsList.add(value);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return lastAttemptsList;
    }

    // Hilfsmethode zum Berechnen des Durchschnitts der letzten Versuche
    private double calculateAverage(List<Integer> lastAttemptsList) {
        if (lastAttemptsList == null || lastAttemptsList.isEmpty()) {
            return 0.0;
        }

        int sum = 0;
        for (int value : lastAttemptsList) {
            sum += value;
        }

        return (double) sum / lastAttemptsList.size();
    }
}
