package com.example.appemergenciasmedicas;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Listado extends AppCompatActivity {

    private ListView listaCentros;
    private SearchView searchView;
    private Button btnVolver;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> listaDatos;
    private ArrayList<Integer> listaIds;
    private ArrayList<String> listaNombres;
    private ArrayList<String> listaTipos;
    private ArrayList<String> listaDirecciones;
    private ArrayList<String> listaTelefonos;

    private String filtroTipo = null; // ✅ Para filtrar hospitales, clínicas o farmacias

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado);

        listaCentros = findViewById(R.id.lista_centros);
        searchView = findViewById(R.id.searchView);
        btnVolver = findViewById(R.id.btn_listado_a_inicio);

        // ✅ Revisamos si MainActivity envió filtro
        if (getIntent() != null && getIntent().hasExtra("tipo")) {
            filtroTipo = getIntent().getStringExtra("tipo");
        }

        cargarCentros();

        // 🔎 Filtro en tiempo real
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        // ✏️ Editar al hacer clic en un ítem
        listaCentros.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(Listado.this, EditarActivity.class);
            intent.putExtra("id", listaIds.get(position));
            intent.putExtra("nombre", listaNombres.get(position));
            intent.putExtra("tipo", listaTipos.get(position));
            intent.putExtra("direccion", listaDirecciones.get(position));
            intent.putExtra("telefono", listaTelefonos.get(position));
            startActivity(intent);
        });

        // 🔙 Botón volver
        btnVolver.setOnClickListener(v -> {
            Intent intent = new Intent(Listado.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void cargarCentros() {
        AdminSQLliteOpenHelper admin = new AdminSQLliteOpenHelper(this, "AdministradorBD", null, 3);
        SQLiteDatabase db = admin.getReadableDatabase();

        listaDatos = new ArrayList<>();
        listaIds = new ArrayList<>();
        listaNombres = new ArrayList<>();
        listaTipos = new ArrayList<>();
        listaDirecciones = new ArrayList<>();
        listaTelefonos = new ArrayList<>();

        // ✅ Si hay filtro, aplicamos WHERE
        String query;
        String[] args = null;

        if (filtroTipo != null) {
            query = "SELECT id, nombre, tipo, direccion, telefono FROM CentrosMedicos WHERE tipo=?";
            args = new String[]{filtroTipo};
        } else {
            query = "SELECT id, nombre, tipo, direccion, telefono FROM CentrosMedicos";
        }

        Cursor cursor = db.rawQuery(query, args);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String nombre = cursor.getString(1);
                String tipo = cursor.getString(2);
                String direccion = cursor.getString(3);
                String telefono = cursor.getString(4);

                listaIds.add(id);
                listaNombres.add(nombre);
                listaTipos.add(tipo);
                listaDirecciones.add(direccion);
                listaTelefonos.add(telefono);

                listaDatos.add(nombre + " - " + tipo + " - " + direccion + " - 📞 " + telefono);

            } while (cursor.moveToNext());
        }

        if (cursor != null) cursor.close();
        db.close();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaDatos);
        listaCentros.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 🔄 Recargar datos cuando regrese de editar
        cargarCentros();
    }
}
