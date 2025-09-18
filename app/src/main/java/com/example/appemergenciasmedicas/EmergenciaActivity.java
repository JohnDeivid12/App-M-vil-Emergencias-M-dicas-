package com.example.appemergenciasmedicas;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class EmergenciaActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergencia);

        // Hospital San José
        Button btnLlamarHospital = findViewById(R.id.btnLlamarHospital);
        btnLlamarHospital.setOnClickListener(v -> {
            String numero = "tel:123456789";
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(numero));
            startActivity(intent);
        });

        // Clínica Risaralda
        Button btnLlamarClinica = findViewById(R.id.btnLlamarClinica);
        btnLlamarClinica.setOnClickListener(v -> {
            String numero = "tel:987654321";
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(numero));
            startActivity(intent);
        });
    }
}
