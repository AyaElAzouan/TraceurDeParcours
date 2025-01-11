package ensat.mobile.taceur_parcours;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private LocationManager locationManager;
    private LocationListener locationListener;
    private boolean isTracking = false;
    private Button startButton;
    private Button stopButton;

    // Liste pour stocker les trajets (coordonnées GPS)
    private List<String> trajetList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traceur_parcours);

        startButton = findViewById(R.id.button_start);
        stopButton = findViewById(R.id.button_stop);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Créer un LocationListener pour obtenir les mises à jour de la localisation
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Log.d("Trajet", "Position enregistrée : Latitude = " + latitude + ", Longitude = " + longitude);

                // Ajouter la position à la liste
                saveLocation(latitude, longitude);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        // Démarrer le suivi
        startButton.setOnClickListener(v -> startTracking());

        // Arrêter le suivi
        stopButton.setOnClickListener(v -> stopTracking());
    }

    // Démarre le suivi de la localisation
    private void startTracking() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            isTracking = true;
            Toast.makeText(this, "Enregistrement démarré", Toast.LENGTH_SHORT).show();

            // Demander les mises à jour de la localisation à l'aide de LocationManager
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, locationListener);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    // Arrêter le suivi de la localisation
    private void stopTracking() {
        if (isTracking) {
            isTracking = false;
            locationManager.removeUpdates(locationListener);
            Toast.makeText(this, "Enregistrement arrêté", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Aucun suivi actif à arrêter.", Toast.LENGTH_SHORT).show();
        }
    }

    // Sauvegarder la localisation dans la liste
    private void saveLocation(double latitude, double longitude) {
        String locationString = "Lat: " + latitude + ", Lon: " + longitude;
        trajetList.add(locationString); // Ajouter la localisation à la liste
        Log.d("Trajet", "Position ajoutée à la liste: " + locationString);
        updateUI(locationString); // Mettre à jour l'UI avec la dernière position
    }

    // Mise à jour de l'UI avec les coordonnées
    private void updateUI(String locationString) {
        Log.d("Trajet", "Mise à jour du TextView avec la nouvelle position: " + locationString);
        runOnUiThread(() -> {
            TextView locationsTextView = findViewById(R.id.locations_text_view);
            if (locationsTextView != null) {
                // Afficher la liste complète des trajets enregistrés
                StringBuilder trajetDisplay = new StringBuilder("Trajet enregistré :\n");
                for (String traj : trajetList) {
                    trajetDisplay.append(traj).append("\n");
                }
                locationsTextView.setText(trajetDisplay.toString());
            } else {
                Log.e("Trajet", "TextView locations_text_view introuvable !");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (isTracking) {
                startTracking();
            }
        } else {
            Toast.makeText(this, "Permission de localisation refusée", Toast.LENGTH_SHORT).show();
        }
    }
}
