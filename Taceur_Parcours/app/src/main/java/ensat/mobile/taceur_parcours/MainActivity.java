package ensat.mobile.taceur_parcours;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    // Déclaration du LocationManager et du LocationListener
    private LocationManager locationManager;
    private LocationListener locationListener;

    // Variable pour savoir si le suivi est en cours
    private boolean isTracking = false;

    // Liste pour stocker les coordonnées GPS collectées
    private ArrayList<Location> locationsList = new ArrayList<>();

    // Boutons pour démarrer et arrêter l'enregistrement
    private Button startButton;
    private Button stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traceur_parcours);

        // Initialisation des boutons de l'interface utilisateur
        startButton = findViewById(R.id.button_start);
        stopButton = findViewById(R.id.button_stop);

        // Initialisation du LocationManager pour obtenir les informations GPS
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Initialisation du LocationListener pour recevoir les mises à jour de localisation
        initializeLocationListener();

        // Gestion des clics sur le bouton "Démarrer"
        startButton.setOnClickListener(v -> startTracking());

        // Gestion des clics sur le bouton "Arrêter"
        stopButton.setOnClickListener(v -> stopTracking());
    }

    // Fonction pour initialiser le LocationListener
    private void initializeLocationListener() {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Récupérer la latitude et la longitude
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                // Affichage dans les logs pour le suivi des coordonnées (commenté pour l'instant)
                // Log.d("Trajet", "Position enregistrée : Latitude = " + latitude + ", Longitude = " + longitude);

                // Sauvegarder la nouvelle position dans la liste
                saveLocation(latitude, longitude);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // Ne fait rien dans cet exemple, mais peut être utilisé pour surveiller l'état du fournisseur GPS
            }

            @Override
            public void onProviderEnabled(String provider) {
                // Cette méthode est appelée lorsque le GPS est activé
            }

            @Override
            public void onProviderDisabled(String provider) {
                // Afficher un message si le GPS est désactivé
                Toast.makeText(MainActivity.this, "GPS désactivé, veuillez l'activer.", Toast.LENGTH_SHORT).show();
            }
        };
    }

    // Fonction pour démarrer le suivi de localisation
    private void startTracking() {
        // Vérifier si la permission de localisation est accordée
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            isTracking = true;  // Le suivi commence
            locationsList.clear(); // Réinitialiser la liste des positions précédentes
            Toast.makeText(this, "Enregistrement démarré", Toast.LENGTH_SHORT).show();

            // Demander les mises à jour de la localisation
            try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 2, locationListener);
            } catch (SecurityException e) {
                // Si une erreur se produit lors de la demande de mise à jour, afficher un message
                e.printStackTrace();
                Toast.makeText(this, "Erreur lors de la demande de mise à jour de la localisation.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Si la permission n'est pas accordée, la demander à l'utilisateur
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    // Fonction pour arrêter le suivi de localisation
    private void stopTracking() {
        if (isTracking) {
            isTracking = false;  // Le suivi est arrêté
            try {
                // Supprimer les mises à jour de la localisation
                locationManager.removeUpdates(locationListener);
                Toast.makeText(this, "Enregistrement arrêté", Toast.LENGTH_SHORT).show();
            } catch (SecurityException e) {
                // Si une erreur se produit lors de l'arrêt des mises à jour, afficher un message
                e.printStackTrace();
                Toast.makeText(this, "Erreur lors de l'arrêt des mises à jour de localisation.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Si aucun suivi n'est actif, afficher un message d'erreur
            Toast.makeText(this, "Aucun suivi actif à arrêter.", Toast.LENGTH_SHORT).show();
        }
    }

    // Fonction pour sauvegarder les coordonnées GPS dans la liste
    private void saveLocation(double latitude, double longitude) {
        // Afficher un Toast avec les coordonnées
        Toast.makeText(this, "Latitude: " + latitude + ", Longitude: " + longitude, Toast.LENGTH_SHORT).show();

        // Créer un nouvel objet Location avec les coordonnées obtenues
        Location location = new Location("dummyprovider");
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        // Ajouter la position à la liste
        locationsList.add(location);
    }

    // Gérer le résultat de la demande de permission de localisation
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Si la permission est accordée, relancer le suivi si nécessaire
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (isTracking) {
                startTracking();
            }
        } else {
            // Si la permission est refusée, afficher un message d'erreur
            Toast.makeText(this, "Permission de localisation refusée", Toast.LENGTH_SHORT).show();
        }
    }
}
