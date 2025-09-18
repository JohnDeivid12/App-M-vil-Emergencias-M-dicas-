package com.example.appemergenciasmedicas;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditarActivity extends AppCompatActivity {

    private EditText txtNombre, txtDireccion, txtTelefono;
    private Spinner spinnerTipo;
    private Button btnGuardarCambios, btnCancelar;

    private int idRegistro;
    private String nombre, tipo, direccion, telefono;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar);

        txtNombre = findViewById(R.id.txtEditarNombre);
        spinnerTipo = findViewById(R.id.spinnerEditarTipo);
        txtDireccion = findViewById(R.id.txtEditarDireccion);
        txtTelefono = findViewById(R.id.txtEditarTelefono); // 📞 nuevo campo
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);
        btnCancelar = findViewById(R.id.btnCancelar);

        // Opciones del Spinner
        String[] opciones = {"Hospital", "Clínica", "Farmacia"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, opciones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adapter);

        // 📥 Recibir datos del Intent
        idRegistro = getIntent().getIntExtra("id", -1);
        nombre = getIntent().getStringExtra("nombre");
        tipo = getIntent().getStringExtra("tipo");
        direccion = getIntent().getStringExtra("direccion");
        telefono = getIntent().getStringExtra("telefono"); // 📞 recibir teléfono

        // Mostrar datos
        txtNombre.setText(nombre);
        txtDireccion.setText(direccion);
        txtTelefono.setText(telefono);

        if (tipo != null) {
            int position = adapter.getPosition(tipo);
            if (position >= 0) {
                spinnerTipo.setSelection(position);
            }
        }

        // Guardar cambios
        btnGuardarCambios.setOnClickListener(v -> {
            String nuevoNombre = txtNombre.getText().toString().trim();
            String nuevoTipo = spinnerTipo.getSelectedItem().toString();
            String nuevaDireccion = txtDireccion.getText().toString().trim();
            String nuevoTelefono = txtTelefono.getText().toString().trim();

            if (!nuevoNombre.isEmpty() && !nuevoTipo.isEmpty() && !nuevaDireccion.isEmpty() && !nuevoTelefono.isEmpty()) {
                actualizarRegistro(nuevoNombre, nuevoTipo, nuevaDireccion, nuevoTelefono);
            } else {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            }
        });

        // Cancelar
        btnCancelar.setOnClickListener(v -> finish());
    }

    private void actualizarRegistro(String nuevoNombre, String nuevoTipo, String nuevaDireccion, String nuevoTelefono) {
        AdminSQLliteOpenHelper adminBD = new AdminSQLliteOpenHelper(this, "AdministradorBD", null, 3);        SQLiteDatabase baseDeDatos = adminBD.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put("nombre", nuevoNombre);
        valores.put("tipo", nuevoTipo);
        valores.put("direccion", nuevaDireccion);
        valores.put("telefono", nuevoTelefono); // 📞 actualizar teléfono

        int filas = baseDeDatos.update("CentrosMedicos", valores, "id=?", new String[]{String.valueOf(idRegistro)});
        baseDeDatos.close();

        if (filas > 0) {
            Toast.makeText(this, "Registro actualizado correctamente", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
        }
    }
}
