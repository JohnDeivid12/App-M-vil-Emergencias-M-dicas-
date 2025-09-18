package com.example.appemergenciasmedicas;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.File;

public class Mapa extends AppCompatActivity {

    private static final int CODIGO_UBICACION = 100;
    private MapView mapView;
    private FusedLocationProviderClient fusedLocationClient;
    private Button btn_mi_ubicacion, btn_mapa_a_inicio;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configuración de osmdroid (caché y user agent)
        Configuration.getInstance().setUserAgentValue(getPackageName());
        Configuration.getInstance().setOsmdroidBasePath(new File(getCacheDir().getAbsolutePath(), "osmdroid"));
        Configuration.getInstance().setOsmdroidTileCache(new File(getCacheDir().getAbsolutePath(), "osmdroid/tiles"));

        setContentView(R.layout.activity_mapa);

        // Referencias UI
        mapView = findViewById(R.id.mapView);
        btn_mi_ubicacion = findViewById(R.id.btn_mi_ubicacion);
        btn_mapa_a_inicio = findViewById(R.id.btn_mapa_a_inicio);

        // Configuración mapa
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(15.0);

        // Inicializar servicios
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        requestQueue = Volley.newRequestQueue(this);

        // Botón mostrar ubicación
        btn_mi_ubicacion.setOnClickListener(v -> mostrarMiUbicacion());

        // Botón volver al inicio
        btn_mapa_a_inicio.setOnClickListener(v -> {
            Intent intent = new Intent(Mapa.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void mostrarMiUbicacion() {
        // Pedir permisos si no están otorgados
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET},
                    CODIGO_UBICACION);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(ubicacion -> {
            if (ubicacion != null) {
                double lat = ubicacion.getLatitude();
                double lon = ubicacion.getLongitude();

                // Centrar el mapa en mi ubicación
                GeoPoint punto = new GeoPoint(lat, lon);
                mapView.getController().setCenter(punto);

                // Agregar marcador de mi ubicación
                Marker marker = new Marker(mapView);
                marker.setPosition(punto);
                marker.setTitle("📍 Mi ubicación");
                mapView.getOverlays().clear();
                mapView.getOverlays().add(marker);

                // Buscar hospitales, clínicas y farmacias cercanos
                buscarCentrosSalud(lat, lon);

                mapView.invalidate();
            } else {
                Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buscarCentrosSalud(double lat, double lon) {
        // Query Overpass API: buscar hospitales, clínicas y farmacias en 2km alrededor
        String url = "https://overpass-api.de/api/interpreter?data=[out:json];"
                + "node(around:2000," + lat + "," + lon + ")[amenity~\"hospital|clinic|pharmacy\"];out;";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray elements = response.getJSONArray("elements");
                        for (int i = 0; i < elements.length(); i++) {
                            JSONObject element = elements.getJSONObject(i);
                            double latitud = element.getDouble("lat");
                            double longitud = element.getDouble("lon");

                            // Nombre del centro
                            String nombre = "Centro de salud";
                            if (element.has("tags") && element.getJSONObject("tags").has("name")) {
                                nombre = element.getJSONObject("tags").getString("name");
                            }

                            // Crear marcador
                            Marker marcador = new Marker(mapView);
                            marcador.setPosition(new GeoPoint(latitud, longitud));
                            marcador.setTitle("🏥 " + nombre);
                            mapView.getOverlays().add(marcador);
                        }
                        mapView.invalidate();
                    } catch (Exception e) {
                        Toast.makeText(this, "Error al procesar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error de conexión con Overpass API", Toast.LENGTH_SHORT).show()
        );

        requestQueue.add(request);
    }
}
