package com.example.appemergenciasmedicas;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class EmergenciaActivity extends AppCompatActivity {

    private Button btn_emergencia_a_inicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergencia);

        btn_emergencia_a_inicio = findViewById(R.id.btn_emergencia_a_inicio);


        // Hospital San Vicente de Paul
        Button btnLlamarHospital = findViewById(R.id.btnLlamarHospital);
        btnLlamarHospital.setOnClickListener(v -> {
            String numero = "tel:606 8532377";
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(numero));
            startActivity(intent);
        });

        // Clínica Risaralda
        Button btnLlamarClinica = findViewById(R.id.btnLlamarClinica);
        btnLlamarClinica.setOnClickListener(v -> {
            String numero = "tel:3218455869";
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(numero));
            startActivity(intent);
        });

        // Abrir primeros auxilios
        btn_emergencia_a_inicio.setOnClickListener(v -> {
            Intent intent = new Intent(EmergenciaActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }
}
