package ensat.mobile.taceur_parcours;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity {
    private MapView mapView; // Une seule instance de MapView
    private ArrayList<String> positions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configurer OSMDroid
        Configuration.getInstance().setUserAgentValue(getApplicationContext().getPackageName());
        setContentView(R.layout.activity_maps);

        // Initialiser la carte
        mapView = findViewById(R.id.mapview);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(15.0);

        // Récupérer les positions passées
        ArrayList<String> positions = getIntent().getStringArrayListExtra("positions");
        if (positions != null && !positions.isEmpty()) {
            ArrayList<GeoPoint> geoPoints = new ArrayList<>();
            for (String position : positions) {
                String[] latLon = position.split(",");
                double latitude = Double.parseDouble(latLon[0]);
                double longitude = Double.parseDouble(latLon[1]);
                geoPoints.add(new GeoPoint(latitude, longitude));
            }

            // Centrer la carte sur le premier point
            mapView.getController().setCenter(geoPoints.get(0));
            mapView.getController().setZoom(15);
            // Dessiner une ligne entre les points
            Polyline polyline = new Polyline();
            polyline.setPoints(geoPoints);
            polyline.getOutlinePaint().setColor(Color.BLUE);
            polyline.getOutlinePaint().setStrokeWidth(5.0f);
            mapView.getOverlayManager().add(polyline);


            // Ajouter un marqueur pour le début du trajet
            Marker startMarker = new Marker(mapView);
            startMarker.setPosition(geoPoints.get(0)); // Position initiale
            startMarker.setTitle("Début du trajet"); // Titre du marqueur
            startMarker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_start, null));
            mapView.getOverlays().add(startMarker);

            // Ajouter un marqueur pour la fin du trajet
            Marker endMarker = new Marker(mapView);
            endMarker.setPosition(geoPoints.get(geoPoints.size() - 1)); // Dernier point
            endMarker.setTitle("Fin du trajet"); // Titre du marqueur
            endMarker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_hand_end, null));
            mapView.getOverlays().add(endMarker);

            // Rafraîchir la carte
              mapView.invalidate();

        }
    }

}

