package com.example.appemergenciasmedicas;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Activity que muestra un mapa con la ubicación del usuario
 * y el lugar de salud (farmacia/clinica/hospital) más cercano
 * en Anserma usando Overpass API.
 */
public class Mapa extends AppCompatActivity {

    // ----------------- Variables principales -----------------
    private MapView mapView;
    private FusedLocationProviderClient fusedLocationClient;
    private double userLat, userLon;
    private Button btnMiUbicacion, btn_mapa_a_inicio;

    // ----------------- Ciclo de vida -----------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configuración de OSMDroid
        Configuration.getInstance().load(
                getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
        );

        setContentView(R.layout.activity_mapa);

        // Inicializar componentes
        inicializarMapa();
        inicializarBoton();
        inicializarGPS();
    }

    // ----------------- Inicializaciones -----------------

    private void inicializarMapa() {
        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
    }

    private void inicializarBoton() {
        btn_mapa_a_inicio = findViewById(R.id.btn_mapa_a_inicio);
        btn_mapa_a_inicio.setOnClickListener(v -> {
            Intent intent = new Intent(Mapa.this, MainActivity.class);
            startActivity(intent);
        });

        btnMiUbicacion = findViewById(R.id.btn_mi_ubicacion);
        btnMiUbicacion.setOnClickListener(v -> solicitarUbicacionUsuario());
    }

    private void inicializarGPS() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    // ----------------- Obtener ubicación del usuario -----------------

    private void solicitarUbicacionUsuario() {
        // Pedir permisos si no están concedidos
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1
            );
            return;
        }

        // Obtener última ubicación conocida
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                userLat = location.getLatitude();
                userLon = location.getLongitude();

                mostrarUbicacionUsuario();
                obtenerLugaresCercanos();
            } else {
                Toast.makeText(this, "No se pudo obtener tu ubicación", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarUbicacionUsuario() {
        // Limpiar marcadores previos
        mapView.getOverlays().clear();

        // Centrar mapa en el usuario
        GeoPoint startPoint = new GeoPoint(userLat, userLon);
        IMapController mapController = mapView.getController();
        mapController.setZoom(15.0);
        mapController.setCenter(startPoint);

        // Colocar marcador del usuario
        Marker userMarker = new Marker(mapView);
        userMarker.setPosition(startPoint);
        userMarker.setTitle("Tu ubicación");
        mapView.getOverlays().add(userMarker);
    }

    // ----------------- Consulta Overpass API -----------------

    private void obtenerLugaresCercanos() {
        // Consulta a Overpass
        String url = "https://overpass-api.de/api/interpreter?data=[out:json][timeout:25];"
                + "area[name=\"Anserma\"][boundary=administrative]->.searchArea;"
                + "("
                + "node[\"amenity\"=\"pharmacy\"](area.searchArea);"
                + "way[\"amenity\"=\"pharmacy\"](area.searchArea);"
                + "relation[\"amenity\"=\"pharmacy\"](area.searchArea);"
                + "node[\"amenity\"=\"clinic\"](area.searchArea);"
                + "way[\"amenity\"=\"clinic\"](area.searchArea);"
                + "relation[\"amenity\"=\"clinic\"](area.searchArea);"
                + "node[\"amenity\"=\"hospital\"](area.searchArea);"
                + "way[\"amenity\"=\"hospital\"](area.searchArea);"
                + "relation[\"amenity\"=\"hospital\"](area.searchArea);"
                + ");out body;>;out skel qt;";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) { e.printStackTrace(); }
            @Override public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    runOnUiThread(() -> procesarResultados(json));
                }
            }
        });
    }

    // ----------------- Procesar resultados -----------------

    private void procesarResultados(String json) {
        try {
            JSONObject root = new JSONObject(json);
            JSONArray elements = root.getJSONArray("elements");

            double minDist = Double.MAX_VALUE;
            double nearestLat = 0, nearestLon = 0;
            String nearestName = "Desconocido";

            // Buscar el lugar más cercano
            for (int i = 0; i < elements.length(); i++) {
                JSONObject elem = elements.getJSONObject(i);
                if (elem.has("lat") && elem.has("lon")) {
                    double lat = elem.getDouble("lat");
                    double lon = elem.getDouble("lon");

                    // calcular distancia con Haversine
                    double dist = haversine(userLat, userLon, lat, lon);

                    if (dist < minDist) {
                        minDist = dist;
                        nearestLat = lat;
                        nearestLon = lon;

                        if (elem.has("tags") && elem.getJSONObject("tags").has("name")) {
                            nearestName = elem.getJSONObject("tags").getString("name");
                        }
                    }
                }
            }

            // Mostrar marcador solo si se encontró uno
            if (nearestLat != 0 && nearestLon != 0) {
                GeoPoint nearestPoint = new GeoPoint(nearestLat, nearestLon);
                Marker marker = new Marker(mapView);
                marker.setPosition(nearestPoint);
                marker.setTitle("Más cercano: " + nearestName + " (" + (int) minDist + " m)");
                mapView.getOverlays().add(marker);

                mapView.invalidate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ----------------- Utilidad: distancia Haversine -----------------
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371000; // metros
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
