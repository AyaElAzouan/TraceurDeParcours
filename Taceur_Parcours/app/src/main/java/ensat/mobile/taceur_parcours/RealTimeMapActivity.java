package ensat.mobile.taceur_parcours;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
public class RealTimeMapActivity extends AppCompatActivity{
    private LocationManager locationManager;
    private LocationListener locationListener;
    private boolean isTracking = false;
    private ArrayList<Location> locationsList = new ArrayList<>();
    private Button startButton, stopButton,Enregistrer;
    private TextView positionsTextView;

    private MapView mapView; // Carte
    private Polyline polyline;
    private GeoPoint lastPoint = null;
    private IMapController mapController;
    private double totalDistance = 0.0;
    private long startTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtimemap);

        // Initialisation des composants de l'interface utilisateur
        startButton = findViewById(R.id.button_start);
        stopButton = findViewById(R.id.button_stop);
        Enregistrer = findViewById(R.id.button_enregistrer);

        // Initialisation de la carte
        Configuration.getInstance().setUserAgentValue(getPackageName());
        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true); // Permet de zoomer et de se déplacer
        // Source de tuiles
        mapController = mapView.getController();

        // Définir un zoom plus raisonnable pour tester
        mapController.setZoom(12.0); // Zoom initial plus raisonnable

        // Positionner la carte sur un point de départ (coordonnées de Paris)
        GeoPoint startPoint = new GeoPoint(35.7742, -5.8131); // Coordonnées de Tanger, Maroc
        mapController.setCenter(startPoint);



        // Initialisation du Polyline pour afficher le chemin
        polyline = new Polyline();
        polyline.setColor(Color.BLUE);
        mapView.getOverlays().add(polyline); // Ajouter le polyline à la carte

        // Initialisation du LocationManager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Initialisation du LocationListener
        initializeLocationListener();

        // Gestion des clics sur les boutons
        startButton.setOnClickListener(v -> startTracking());
        stopButton.setOnClickListener(v -> stopTracking());
        Enregistrer.setOnClickListener(v -> enregistrer());

    }

    private void initializeLocationListener() {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                saveLocation(location.getLatitude(), location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // Rien à faire ici
            }

            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(RealTimeMapActivity.this, "GPS activé", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(RealTimeMapActivity.this, "GPS désactivé. Veuillez l'activer.", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void startTracking() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startTime = System.currentTimeMillis();
            isTracking = true;
            locationsList.clear();
            totalDistance = 0.0;
            Toast.makeText(this, "Enregistrement démarré", Toast.LENGTH_SHORT).show();
            startButton.setEnabled(false); // Désactiver le bouton start
            stopButton.setEnabled(true);  // Activer le bouton stop

            try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 1, locationListener);
            } catch (SecurityException e) {
                e.printStackTrace();
                Toast.makeText(this, "Erreur lors du démarrage de la localisation.", Toast.LENGTH_SHORT).show();
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private void stopTracking() {
        if (isTracking) {
            isTracking = false;
            try {
                locationManager.removeUpdates(locationListener);
                Toast.makeText(this, "Enregistrement arrêté", Toast.LENGTH_SHORT).show();
                stopButton.setEnabled(false);  // Désactiver le bouton stop
                startButton.setEnabled(true);  // Réactiver le bouton start

                // Marquer la fin du trajet (ajouter un point final si nécessaire)
                if (lastPoint != null) {
                    GeoPoint endPoint = new GeoPoint(lastPoint.getLatitude(), lastPoint.getLongitude());
                    polyline.addPoint(endPoint);
                    mapView.invalidate();
                }
            } catch (SecurityException e) {
                e.printStackTrace();
                Toast.makeText(this, "Erreur lors de l'arrêt de la localisation.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Aucun suivi actif à arrêter.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveLocation(double latitude, double longitude) {
        // Ajouter la position à la liste
        Location location = new Location("dummyprovider");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        if (!locationsList.isEmpty()) {
            Location lastLocation = locationsList.get(locationsList.size() - 1);
            totalDistance += lastLocation.distanceTo(location);
        }
        locationsList.add(location);

        // Ajouter le point à la polyline
        GeoPoint geoPoint = new GeoPoint(latitude, longitude);
        polyline.addPoint(geoPoint); // Ajouter un point à la polyline
        lastPoint = geoPoint; // Sauvegarder le dernier point
        mapView.invalidate(); // Redessiner la carte
        if (locationsList.size() == 1) {
            Marker startMarker = new Marker(mapView);
            startMarker.setPosition(geoPoint);
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            startMarker.setTitle("Point de départ");
            mapView.getOverlays().add(startMarker);
        }
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
    private void enregistrer() {
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

            String durationMessage = String.format(
                    "Durée: %d jours, %d heures, %d minutes et %d secondes", // Formatage sans décimales
                    days, hours, minutes, seconds
            );
            Toast.makeText(this, "Enregistrement arrêté et sauvegardé.\n" + durationMessage, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RealTimeMapActivity.this, SavedRoutesActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Aucun suivi actif à arrêter.", Toast.LENGTH_SHORT).show();
        }

    }

}
