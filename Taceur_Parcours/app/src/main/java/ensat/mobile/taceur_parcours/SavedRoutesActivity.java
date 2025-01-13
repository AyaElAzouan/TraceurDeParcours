package ensat.mobile.taceur_parcours;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Arrays;
/**

 * Cette classe représente une activité qui affiche une liste des parcours sauvegardés dans la base de données SQLite.
 * Chaque parcours contient des informations telles que la distance, la durée, la date, et les positions GPS.

 */

public class SavedRoutesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_routes);
        // Initialiser la base de données en lecture seule
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // Obtenir tous les parcours enregistrés dans la table "parcours"
        Cursor cursor = db.query("parcours", null, null, null, null, null, null);
        // Récupérer le conteneur principal où les parcours seront affichés
        LinearLayout routesContainer = findViewById(R.id.routes_container);

        // Vérifier si la base contient des données
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Lire les données de la base
                @SuppressLint("Range") double distance = cursor.getDouble(cursor.getColumnIndex("distance"));
                @SuppressLint("Range") double duration = cursor.getDouble(cursor.getColumnIndex("duration"));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex("date"));
                @SuppressLint("Range") String positionsString = cursor.getString(cursor.getColumnIndex("positions"));


                // Calculer les kilomètres et mètres
                long totalKilometers = (long) distance; // Distance en kilomètres (déjà en kilomètres, donc pas de conversion nécessaire)
                long remainingMeters = (long) (distance * 1000) % 1000; // Conversion de la distance restante en mètres

                // Convertir la durée en minutes et l'afficher sous forme de jours, heures, minutes et secondes
                long durationMillis = (long) (duration * 60 * 1000); // Conversion de la durée en millisecondes
                long days = durationMillis / (1000 * 60 * 60 * 24);
                long hours = (durationMillis % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
                long minutes = (durationMillis % (1000 * 60 * 60)) / (1000 * 60);
                long seconds = (durationMillis % (1000 * 60)) / 1000;

                // Convertir positionsString en ArrayList<String>
                ArrayList<String> positionsList = new ArrayList<>();
                if (positionsString != null && !positionsString.isEmpty()) {
                    String[] positionArray = positionsString.split(";");
                    for (String pos : positionArray) {
                        if (!pos.isEmpty()) {
                            positionsList.add(pos);
                        }
                    }
                }

                // Créer un TextView pour chaque parcours

                String routeText = String.format(
                        "Distance : %d km et %d m\nDurée : %d jours, %d heures, %d minutes et %d secondes\nDate : %s\n\n",
                        totalKilometers, remainingMeters, days, hours, minutes, seconds, date
                );

                // Créer un TextView pour chaque parcours et ajouter le texte formaté
                TextView routeView = new TextView(this);
                routeView.setText(routeText);
                //creer un boutton
                Button runButton = new Button(this);
                runButton.setText("Afficher sur carte");
                // Appliquer un style personnalisé au bouton
                runButton.setBackgroundColor(Color.parseColor("#1E88E5")); // Couleur bleue de fond
                runButton.setTextColor(Color.WHITE); // Couleur blanche du texte

                // Ajouter des coins arrondis au bouton
                GradientDrawable shape = new GradientDrawable();
                shape.setCornerRadius(25); // Coins arrondis
                shape.setColor(Color.parseColor("#1E88E5")); // Fond bleu
                runButton.setBackground(shape);
                // Définir l'action du bouton "Run"
                runButton.setOnClickListener(v -> {
                    if (!positionsList.isEmpty()) {
                    Intent intent = new Intent(SavedRoutesActivity.this, MapsActivity.class);
                    intent.putStringArrayListExtra("positions", positionsList);
                   startActivity(intent);
                } else {
                   Toast.makeText(SavedRoutesActivity.this, "Aucune position enregistrée", Toast.LENGTH_SHORT).show();
                }
                    });

                LinearLayout routeLayout = new LinearLayout(this);
                routeLayout.setOrientation(LinearLayout.VERTICAL);  // Orientation verticale
                routeLayout.setPadding(16, 16, 16, 16);
                routeLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                // Ajouter le TextView et le bouton "Run" dans le layout
                routeLayout.addView(routeView);
                routeLayout.addView(runButton);

                // Ajouter le layout dans le conteneur principal
                routesContainer.addView(routeLayout);
            } while (cursor.moveToNext());
        }

        // Fermer le curseur pour libérer les ressources
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        // Configuration du bouton pour revenir à la première vue
        Button backToHomeButton = findViewById(R.id.button_back_to_home);
        backToHomeButton.setOnClickListener(v -> {
            // Crée un Intent pour revenir à l'écran principal
            Intent intent = new Intent(SavedRoutesActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Facultatif, pour fermer cette activité après avoir démarré la nouvelle activité
        });

    }

    }