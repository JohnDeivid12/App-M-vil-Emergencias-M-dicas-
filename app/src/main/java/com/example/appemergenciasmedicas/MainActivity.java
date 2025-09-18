package com.example.appemergenciasmedicas;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText txtNombre, txtDireccion, txtTelefono;
    private Spinner spinnerTipo;
    private Button btnRegistrar, btnVerListado, btnHospitales, btnClinicas, btnFarmacias, btnEmergencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtNombre = findViewById(R.id.txtNombre);
        spinnerTipo = findViewById(R.id.spinnerTipo);
        txtDireccion = findViewById(R.id.txtDireccion);
        txtTelefono = findViewById(R.id.txtTelefono); // ✅ Nuevo campo
        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnVerListado = findViewById(R.id.btnVerListado);

        // Botones de filtro
        btnHospitales = findViewById(R.id.btn_hospitales);
        btnClinicas = findViewById(R.id.btn_clinicas);
        btnFarmacias = findViewById(R.id.btn_farmacias);

        // 🚑 Botón de emergencia
        btnEmergencia = findViewById(R.id.btn_emergencia);

        // Opciones para el Spinner
        String[] opciones = {"Hospital", "Clínica", "Farmacia"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, opciones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adapter);

        // Registrar centro
        btnRegistrar.setOnClickListener(v -> {
            String nombre = txtNombre.getText().toString().trim();
            String tipo = spinnerTipo.getSelectedItem().toString();
            String direccion = txtDireccion.getText().toString().trim();
            String telefono = txtTelefono.getText().toString().trim(); // ✅ Capturamos el teléfono

            Registrar(nombre, tipo, direccion, telefono);
        });

        // Ver listado completo
        btnVerListado.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Listado.class);
            startActivity(intent);
        });

        // Filtrar por Hospitales
        btnHospitales.setOnClickListener(v -> abrirListadoFiltrado("Hospital"));

        // Filtrar por Clínicas
        btnClinicas.setOnClickListener(v -> abrirListadoFiltrado("Clínica"));

        // Filtrar por Farmacias
        btnFarmacias.setOnClickListener(v -> abrirListadoFiltrado("Farmacia"));

        // 🚑 Abrir vista de emergencias
        btnEmergencia.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EmergenciaActivity.class);
            startActivity(intent);
        });
    }

    private void abrirListadoFiltrado(String tipo) {
        Intent intent = new Intent(MainActivity.this, Listado.class);
        intent.putExtra("tipo", tipo); // Enviamos el filtro
        startActivity(intent);
    }

    // ✅ Método para registrar un centro médico con teléfono
    public void Registrar(String nombre, String tipo, String direccion, String telefono) {
        AdminSQLliteOpenHelper adminBD = new AdminSQLliteOpenHelper(this, "AdministradorBD", null, 3); // ⬅️ Cambié versión a 3
        SQLiteDatabase baseDeDatos = adminBD.getWritableDatabase();

        if (!nombre.isEmpty() && !tipo.isEmpty() && !direccion.isEmpty() && !telefono.isEmpty()) {
            Cursor fila = baseDeDatos.rawQuery(
                    "SELECT nombre FROM CentrosMedicos WHERE nombre=?",
                    new String[]{nombre}
            );

            if (fila.moveToFirst()) {
                Toast.makeText(this, "Ese centro ya está registrado", Toast.LENGTH_SHORT).show();
                fila.close();
                baseDeDatos.close();
                return;
            }

            ContentValues registro = new ContentValues();
            registro.put("nombre", nombre);
            registro.put("tipo", tipo);
            registro.put("direccion", direccion);
            registro.put("telefono", telefono); // ✅ Guardamos teléfono

            baseDeDatos.insert("CentrosMedicos", null, registro);
            baseDeDatos.close();

            txtNombre.setText("");
            txtDireccion.setText("");
            txtTelefono.setText(""); // ✅ Limpiamos campo
            spinnerTipo.setSelection(0);

            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Debes llenar todos los campos", Toast.LENGTH_LONG).show();
        }
    }
}
