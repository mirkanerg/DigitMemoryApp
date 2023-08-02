package com.example.digitmemory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LogIn extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin, btnRegister;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialisiere die Views und Buttons
        etUsername = findViewById(R.id.editTextUsername);
        etPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.buttonLogin);
        btnRegister = findViewById(R.id.buttonRegister);

        // Erstelle eine Instanz von DatabaseHelper, um mit der Datenbank zu interagieren
        databaseHelper = new DatabaseHelper(this);

        // Setze den OnClickListener für den Login-Button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hole den eingegebenen Benutzernamen und das Passwort aus den EditText-Feldern
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                // Prüfe, ob entweder der Benutzername oder das Passwort leer sind
                if (username.isEmpty() || password.isEmpty()) {
                    // Zeige eine Toast-Nachricht an, wenn eines der Felder leer ist
                    Toast.makeText(LogIn.this, "Please enter both username and password.", Toast.LENGTH_SHORT).show();
                } else {
                    // Prüfe, ob der angegebene Benutzername und das Passwort mit einem vorhandenen Benutzer in der Datenbank übereinstimmen
                    if (databaseHelper.checkUser(username, password)) {
                        // Login erfolgreich, starte die DMmenu Activity.

                        // Speichere den Benutzernamen in den SharedPreferences für spätere Verwendung
                        SharedPreferences preferences = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("CURRENT_USER", username); // 'username' ist der aktuelle Benutzername
                        editor.apply();

                        // Starte die DMmenu Activity
                        Toast.makeText(LogIn.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LogIn.this, DMmenu.class);
                        startActivity(intent);
                    } else {
                        // Login fehlgeschlagen, zeige eine Fehlermeldung an.
                        Toast.makeText(LogIn.this, "Invalid credentials. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Setze den OnClickListener für den Register-Button
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hole den eingegebenen Benutzernamen und das Passwort aus den EditText-Feldern
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                // Prüfe, ob entweder der Benutzername oder das Passwort leer sind
                if (username.isEmpty() || password.isEmpty()) {
                    // Zeige eine Toast-Nachricht an, wenn eines der Felder leer ist
                    Toast.makeText(LogIn.this, "Please enter both username and password.", Toast.LENGTH_SHORT).show();
                } else {
                    // Prüfe, ob der Benutzername bereits in der Datenbank vorhanden ist
                    if (databaseHelper.checkUsernameExists(username)) {
                        // Zeige eine Toast-Nachricht an, wenn der Benutzername bereits existiert
                        Toast.makeText(LogIn.this, "Username already exists. Please choose a different one.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Registriere den neuen Benutzer, indem der Benutzername und das Passwort in der Datenbank gespeichert werden
                        long userId = databaseHelper.addUser(username, password);
                        if (userId != -1) {
                            // Registrierung erfolgreich, zeige eine Toast-Nachricht an
                            Toast.makeText(LogIn.this, "Registration successful. Please log in.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Zeige eine Toast-Nachricht an, wenn während der Registrierung ein Fehler aufgetreten ist
                            Toast.makeText(LogIn.this, "Error occurred during registration. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }
}
