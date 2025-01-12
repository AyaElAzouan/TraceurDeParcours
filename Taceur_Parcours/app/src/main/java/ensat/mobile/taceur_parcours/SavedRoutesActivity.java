package ensat.mobile.taceur_parcours;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SavedRoutesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_routes);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query("parcours", null, null, null, null, null, null);
        StringBuilder savedRoutes = new StringBuilder();
        while (cursor.moveToNext()) {
            @SuppressLint("Range") double distance = cursor.getDouble(cursor.getColumnIndex("distance"));
            @SuppressLint("Range") double duration = cursor.getDouble(cursor.getColumnIndex("duration"));
            @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex("date"));

            // Calculer les kilomètres et mètres
            long totalKilometers = (long) distance; // Distance en kilomètres (déjà en kilomètres, donc pas de conversion nécessaire)
            long remainingMeters = (long) (distance * 1000) % 1000; // Conversion de la distance restante en mètres

            // Convertir la durée en minutes et l'afficher sous forme de jours, heures, minutes et secondes
            long durationMillis = (long) (duration * 60 * 1000); // Conversion de la durée en millisecondes
            long days = durationMillis / (1000 * 60 * 60 * 24);
            long hours = (durationMillis % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
            long minutes = (durationMillis % (1000 * 60 * 60)) / (1000 * 60);
            long seconds = (durationMillis % (1000 * 60)) / 1000;

            // Affichage dans la vue
            String result = String.format(
                    "Distance : %d km et %d m\nDurée : %d jours, %d heures, %d minutes et %d secondes\nDate : %s\n\n",
                    totalKilometers, remainingMeters, days, hours, minutes, seconds, date
            );

            savedRoutes.append(result); // Ajoutez le résultat à la vue pour afficher les parcours sauvegardés
        }

        cursor.close();

        TextView savedRoutesTextView = findViewById(R.id.text_saved_routes);
        savedRoutesTextView.setText(savedRoutes.toString());


        // Configuration du bouton pour revenir à la première vue
        Button backToHomeButton = findViewById(R.id.button_back_to_home);
        backToHomeButton.setOnClickListener(v -> {
            // Crée un Intent pour revenir à l'écran principal
            Intent intent = new Intent(SavedRoutesActivity.this, MainActivity.class); // Assurez-vous que MainActivity est l'écran principal
            startActivity(intent);
            finish(); // Facultatif, pour fermer cette activité après avoir démarré la nouvelle activité
        });
    }

    }