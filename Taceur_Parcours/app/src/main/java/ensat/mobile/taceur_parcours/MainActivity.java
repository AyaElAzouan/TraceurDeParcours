package ensat.mobile.taceur_parcours;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private boolean isTracking = false;
    private ArrayList<Location> locationsList = new ArrayList<>();
    private Button startButton, stopButton, showMapButton, calculateButton, showSavedRoutesButton,buttonRealtimeMap;
    private TextView positionsTextView;
    private ImageView backgroundImage;
    private TextView statusText;
    private double totalDistance = 0.0;
    private long startTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traceur_parcours);


// //////////////////////real time activité/////////////////////////////////////////////
        buttonRealtimeMap = findViewById(R.id.button_realtimemap);
        buttonRealtimeMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lancer RealtimeMapActivity
                Intent intent = new Intent(MainActivity.this, RealTimeMapActivity.class);
                startActivity(intent);
            }
        });
        ///////////////////////////////////
        // Initialisation des composants de l'interface utilisateur
        startButton = findViewById(R.id.button_start);
        stopButton = findViewById(R.id.button_stop);
        showMapButton = findViewById(R.id.button_show_map);
        calculateButton = findViewById(R.id.button_calculate);
        showSavedRoutesButton = findViewById(R.id.button_show_saved_routes);
        backgroundImage = findViewById(R.id.background_image);
        statusText = findViewById(R.id.status_text);
        positionsTextView = findViewById(R.id.text_positions);
        positionsTextView.setMovementMethod(new android.text.method.ScrollingMovementMethod());

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        initializeLocationListener();

        startButton.setOnClickListener(v -> {
            if (isTracking) {
                stopTracking(); // Arrêter le suivi en cours
                resetTracking(); // Réinitialiser pour un nouveau suivi
            }
            startTracking(); // Démarrer un nouveau suivi
            backgroundImage.setImageResource(R.drawable.ic_road); // Remplacez par l'image d'une route
            statusText.setText("En cours ...");
            statusText.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            startButton.setVisibility(View.GONE); // Cacher le bouton Démarrer
            stopButton.setVisibility(View.VISIBLE); // Montrer le bouton Arrêter
        });

        stopButton.setOnClickListener(v -> {
            stopTracking(); // Appel à la méthode d'arrêt du tracking
            // Mise à jour de l'image et du texte
            backgroundImage.setImageResource(R.drawable.end_trajet); // Remplacez par l'image vide
            statusText.setText("Trajet terminé !");
            statusText.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
            stopButton.setVisibility(View.GONE); // Cacher le bouton Arrêter
            showMapButton.setVisibility(View.VISIBLE); // Montrer le bouton "Afficher le chemin"
        });

        showMapButton.setOnClickListener(v -> {
            if (!locationsList.isEmpty()) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                ArrayList<String> positions = new ArrayList<>();
                for (Location location : locationsList) {
                    positions.add(location.getLatitude() + "," + location.getLongitude());
                }
                intent.putStringArrayListExtra("positions", positions);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Aucune position enregistrée", Toast.LENGTH_SHORT).show();
            }
        });

        calculateButton.setOnClickListener(v -> calculateDistanceAndDuration());

        showSavedRoutesButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SavedRoutesActivity.class);
            startActivity(intent);
        });
    }

    private void initializeLocationListener() {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                saveLocation(location.getLatitude(), location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(MainActivity.this, "GPS activé", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(MainActivity.this, "GPS désactivé. Veuillez l'activer.", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void startTracking() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            isTracking = true;
            locationsList.clear();
            totalDistance = 0.0;
            startTime = System.currentTimeMillis();
            ////visibilité de real time map
            buttonRealtimeMap.setVisibility(View.GONE);
            showMapButton.setVisibility(View.GONE);
            Toast.makeText(this, "Enregistrement démarré", Toast.LENGTH_SHORT).show();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, locationListener);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private void stopTracking() {
        if (isTracking) {
            isTracking = false;
            locationManager.removeUpdates(locationListener);
            long endTime = System.currentTimeMillis();

            // Calcul de la durée totale en millisecondes
            long durationMillis = endTime - startTime;

            // Conversion en jours, heures, minutes et secondes
            long days = durationMillis / (1000 * 60 * 60 * 24);
            long hours = (durationMillis % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
            long minutes = (durationMillis % (1000 * 60 * 60)) / (1000 * 60);
            long seconds = (durationMillis % (1000 * 60)) / 1000;

            // Sauvegarde de la durée en minutes pour le besoin actuel
            double durationMinutes = durationMillis / (1000.0 * 60); // Cette valeur peut rester flottante si nécessaire
            saveRouteToDatabase(durationMinutes);

            // Affichage des informations
            showMapButton.setVisibility(View.VISIBLE);
            ////visibilité de real time map
            buttonRealtimeMap.setVisibility(View.GONE);
            String durationMessage = String.format(
                    "Durée: %d jours, %d heures, %d minutes et %d secondes", // Formatage sans décimales
                    days, hours, minutes, seconds
            );
            Toast.makeText(this, "Enregistrement arrêté et sauvegardé.\n" + durationMessage, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Aucun suivi actif à arrêter.", Toast.LENGTH_SHORT).show();
        }

    }

    private void resetTracking() {
        locationsList.clear();
        totalDistance = 0.0;
        startTime = 0;
        positionsTextView.setText("");
    }

    private void saveLocation(double latitude, double longitude) {
        Location location = new Location("dummyprovider");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        if (!locationsList.isEmpty()) {
            Location lastLocation = locationsList.get(locationsList.size() - 1);
            totalDistance += lastLocation.distanceTo(location);
        }
        locationsList.add(location);
    }

    private void saveRouteToDatabase(double durationMinutes) {
        if (!locationsList.isEmpty()) {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            StringBuilder positions = new StringBuilder();
            for (Location location : locationsList) {
                positions.append(location.getLatitude()).append(",").append(location.getLongitude()).append(";");
            }
            String currentDate = java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());
            boolean isSaved = dbHelper.saveParcours(totalDistance / 1000, durationMinutes, positions.toString(), currentDate);
            if (isSaved) {
                Toast.makeText(this, "Parcours sauvegardé avec succès", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Erreur lors de la sauvegarde du parcours", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void calculateDistanceAndDuration() {
        if (!locationsList.isEmpty()) {
            // Calcul des kilomètres et des mètres
            long totalKilometers = (long) totalDistance / 1000; // Distance totale en kilomètres
            long remainingMeters = (long) totalDistance % 1000; // Distance restante en mètres

            // Calcul de la durée
            long endTime = System.currentTimeMillis();
            long durationMillis = endTime - startTime; // Durée en millisecondes

            // Conversion en jours, heures, minutes et secondes
            long days = durationMillis / (1000 * 60 * 60 * 24);
            long hours = (durationMillis % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
            long minutes = (durationMillis % (1000 * 60 * 60)) / (1000 * 60);
            long seconds = (durationMillis % (1000 * 60)) / 1000;

            // Formatage du résultat
            String result = String.format(
                    "Distance totale : %d km et %d m\nDurée : %d jours, %d heures, %d minutes et %d secondes",
                    totalKilometers, remainingMeters, days, hours, minutes, seconds
            );

            positionsTextView.setText(result);
        } else {
            Toast.makeText(this, "Aucune position enregistrée pour calculer.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (isTracking) {
                startTracking();
            }
        } else {
            Toast.makeText(this, "Permission de localisation refusée", Toast.LENGTH_SHORT).show();
        }
    }
}
