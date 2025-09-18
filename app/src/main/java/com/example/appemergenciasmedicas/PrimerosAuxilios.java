package com.example.appemergenciasmedicas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class PrimerosAuxilios extends AppCompatActivity {

    private TextView txtInfo;
    private Button btnInicio;
    private String url = "https://raw.githubusercontent.com/JohnDeivid12/primeros-auxilios-api./refs/heads/main/primeros_auxilios.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primeros_auxilios);

        txtInfo = findViewById(R.id.txtInfo);
        btnInicio = findViewById(R.id.btn_auxilios_a_inicio);

        cargarDatos();

        btnInicio.setOnClickListener(v -> {
            Intent intent = new Intent(PrimerosAuxilios.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void cargarDatos() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> mostrarDatos(response),
                error -> txtInfo.setText("Error al cargar: " + error.toString())
        );

        queue.add(request);
    }

    private void mostrarDatos(JSONArray response) {
        try {
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < response.length(); i++) {
                JSONObject obj = response.getJSONObject(i);

                sb.append("📌 ").append(obj.getString("nombre")).append("\n");
                sb.append("Descripción: ").append(obj.getString("descripcion")).append("\n\n");

                // pasos
                JSONArray pasos = obj.getJSONArray("pasos");
                sb.append("Pasos:\n");
                for (int j = 0; j < pasos.length(); j++) {
                    sb.append(" - ").append(pasos.getString(j)).append("\n");
                }

                // advertencias
                JSONArray adv = obj.getJSONArray("advertencias");
                sb.append("\nAdvertencias:\n");
                for (int j = 0; j < adv.length(); j++) {
                    sb.append(" ⚠ ").append(adv.getString(j)).append("\n");
                }

                sb.append("\nUrgencia: ").append(obj.getString("urgencia")).append("\n");
                sb.append("\n---------------------------------\n\n");
            }

            txtInfo.setText(sb.toString());

        } catch (Exception e) {
            txtInfo.setText("Error al procesar JSON: " + e.getMessage());
        }
    }
}
