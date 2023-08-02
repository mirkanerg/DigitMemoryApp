package com.example.digitmemory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    TextView tv_level, tv_number;
    EditText et_number;
    Button confirm;

    Random rand;

    String generatedNumber;
    DatabaseHelper databaseHelper;

    private String username;

    int currentlevel = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Benutzernamen aus den SharedPreferences abrufen
        SharedPreferences preferences = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
        username = preferences.getString("CURRENT_USER", "");

        // Initialize the DatabaseHelper instance
        databaseHelper = new DatabaseHelper(this);

        // Aktuellen Highscore des Benutzers aus der Datenbank abrufen
        int userHighScore = databaseHelper.getUserHighScore(username);

        // Letzte Versuche des Benutzers aus der Datenbank abrufen
        String lastAttempts = databaseHelper.getLastAttempts(username);

        // Aktuelles Level auf 1 setzen, wenn die Aktivität gestartet wird
        currentlevel = 1;

        // Aktualisiere die letzten Versuche
        lastAttempts = updateLastAttemptsString(lastAttempts, currentlevel);

        // Aktualisiere die letzten Versuche in der Datenbank
        databaseHelper.updateLastAttempts(username, lastAttempts);

        // TextViews und Buttons initialisieren
        tv_level = findViewById(R.id.tvLevel);
        tv_number = findViewById(R.id.tvDigit);
        et_number = findViewById(R.id.etInput);
        confirm = findViewById(R.id.bConfirm);

        rand = new Random();

        // Zufallszahl je nach Level anzeigen, dann den Button und das Eingabefeld ausblenden
        et_number.setVisibility(View.GONE);
        confirm.setVisibility(View.GONE);
        tv_number.setVisibility(View.VISIBLE);

        generatedNumber = generateNumber(currentlevel);
        tv_number.setText(generatedNumber);

        // Timer für 3 Sekunden, dann das Eingabefeld und den Button anzeigen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                et_number.setVisibility(View.VISIBLE);
                confirm.setVisibility(View.VISIBLE);
                tv_number.setVisibility(View.GONE);

                et_number.requestFocus();
            }
        }, 3000);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Überprüfen, ob die eingegebene Zahl mit der generierten Zahl übereinstimmt
                if (generatedNumber.equals(et_number.getText().toString())) {

                    et_number.setVisibility(View.GONE);
                    confirm.setVisibility(View.GONE);
                    tv_number.setVisibility(View.VISIBLE);

                    // Eingabefeld leeren
                    et_number.setText("");

                    // Level um 1 erhöhen
                    currentlevel++;

                    // Aktuelles Level im TextView anzeigen
                    tv_level.setText("Level : " + currentlevel);

                    // Neue Zufallszahl je nach Level anzeigen
                    generatedNumber = generateNumber(currentlevel);
                    tv_number.setText(generatedNumber);

                    // Timer für 3 Sekunden, dann das Eingabefeld und den Button anzeigen
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            et_number.setVisibility(View.VISIBLE);
                            confirm.setVisibility(View.VISIBLE);
                            tv_number.setVisibility(View.GONE);

                            et_number.requestFocus();
                        }
                    }, 3000);
                } else {
                    // Keine Übereinstimmung = Game Over, Lösung anzeigen

                    // Letzte Versuche und Durchschnitt in der Datenbank aktualisieren
                    updateLastAttempts(username, currentlevel);

                    // Zu GameOver Activity wechseln und Informationen übergeben
                    Intent intent = new Intent(MainActivity.this, GameOver.class);
                    intent.putExtra("NUMBER_KEY", generatedNumber);
                    intent.putExtra("LEVEL_KEY", currentlevel);
                    startActivity(intent);
                }
            }
        });
    }

    // Methode zur Generierung einer Zufallszahl mit einer bestimmten Anzahl von Ziffern (digits)
    public String generateNumber(int digits) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < digits; i++) {
            int randomDigit = rand.nextInt(10);
            output.append(randomDigit);
        }
        //CHEAT-ON-v (#158 auskommentieren)
        //System.out.println(output.toString());
        return output.toString();
    }

    // Hilfsmethode zur Aktualisierung der Zeichenkette der letzten Versuche
    private String updateLastAttemptsString(String lastAttempts, int currentLevel) {
        List<Integer> lastAttemptsList = new ArrayList<>();

        if (lastAttempts != null && !lastAttempts.isEmpty()) {
            String[] attemptsArray = lastAttempts.split(",");
            for (String attempt : attemptsArray) {
                try {
                    int value = Integer.parseInt(attempt.trim());
                    lastAttemptsList.add(value);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }

        lastAttemptsList.add(currentLevel);

        // Halte nur die letzten 10 Versuche
        if (lastAttemptsList.size() > 10) {
            lastAttemptsList = lastAttemptsList.subList(lastAttemptsList.size() - 10, lastAttemptsList.size());
        }

        // Konvertiere die Liste zurück in eine kommaseparierte Zeichenkette
        StringBuilder lastAttemptsBuilder = new StringBuilder();
        for (int i = 0; i < lastAttemptsList.size(); i++) {
            lastAttemptsBuilder.append(lastAttemptsList.get(i));
            if (i < lastAttemptsList.size() - 1) {
                lastAttemptsBuilder.append(",");
            }
        }

        return lastAttemptsBuilder.toString();
    }

    // Hilfsmethode zur Aktualisierung der letzten Versuche und des Durchschnitts in der Datenbank
    private void updateLastAttempts(String username, int currentLevel) {
        // Die letzten Versuche des Benutzers aus der Datenbank abrufen
        String lastAttemptsString = databaseHelper.getLastAttempts(username);

        // Die letzten Versuche in eine Liste von Integers umwandeln
        List<Integer> lastAttemptsList = new ArrayList<>();
        if (lastAttemptsString != null && !lastAttemptsString.isEmpty()) {
            String[] lastAttemptsArray = lastAttemptsString.split(",");
            for (String attempt : lastAttemptsArray) {
                // Leere Zeichenfolgen und "1"-Werte ignorieren
                if (!attempt.trim().isEmpty() ) {
                    try {
                        int value = Integer.parseInt(attempt.trim());
                        lastAttemptsList.add(value);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        //Entfernen der fälschlichen "1" am Ende der array
        lastAttemptsList.remove(lastAttemptsList.size()-1);

        // Das aktuelle Level zur Liste der letzten Versuche hinzufügen, wenn es nicht 1 ist
        if (currentLevel != 1 || currentLevel==1) {
            lastAttemptsList.add(currentLevel);
        } else {
            // Wenn das aktuelle Level 1 ist, füge es nur zur Liste hinzu, wenn die Liste leer ist oder keinen Wert außer 1 enthält
            if (lastAttemptsList.isEmpty() || !lastAttemptsList.contains(currentLevel)) {
                lastAttemptsList.add(currentLevel);
            }
        }

        // Nur die letzten 10 Versuche in der Liste behalten
        if (lastAttemptsList.size() > 10) {
            lastAttemptsList = lastAttemptsList.subList(lastAttemptsList.size() - 10, lastAttemptsList.size());
        }

        // Die Liste in eine kommaseparierte Zeichenkette umwandeln
        StringBuilder lastAttemptsBuilder = new StringBuilder();
        for (int i = 0; i < lastAttemptsList.size(); i++) {
            lastAttemptsBuilder.append(lastAttemptsList.get(i));
            if (i < lastAttemptsList.size() - 1) {
                lastAttemptsBuilder.append(",");
            }
        }
        String newLastAttempts = lastAttemptsBuilder.toString();

        // Die letzten Versuche in der Datenbank aktualisieren
        databaseHelper.updateLastAttempts(username, newLastAttempts);
    }
}
