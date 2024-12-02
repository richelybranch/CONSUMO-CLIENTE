package com.example.servicioweb;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> registros;
    ArrayList<String> ids;
    ArrayAdapter<String> adapter;
    String selectedId = "";

    Button btnInsertar, btnModificar, btnEliminar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        registros = new ArrayList<>();
        ids = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, registros);
        listView.setAdapter(adapter);

        btnInsertar = findViewById(R.id.btnInsertar);
        btnModificar = findViewById(R.id.btnModificar);
        btnEliminar = findViewById(R.id.btnEliminar);

        cargarRegistros();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                selectedId = ids.get(position);
                Toast.makeText(MainActivity.this, "ID seleccionado: " + selectedId, Toast.LENGTH_SHORT).show();
            }
        });

        btnInsertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, InsertarActivity.class);
                startActivity(intent);
            }
        });

        btnModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!selectedId.isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, ModificarActivity.class);
                    intent.putExtra("id", selectedId);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Seleccione un registro", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!selectedId.isEmpty()) {
                    eliminarRegistro(selectedId);
                } else {
                    Toast.makeText(MainActivity.this, "Seleccione un registro", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void cargarRegistros() {
        String URL = "https://ist17dejulio.000webhostapp.com/listar.php";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                registros.clear();
                ids.clear();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String id = jsonObject.getString("codigo");
                        String nombre = jsonObject.getString("nombre");
                        String apellido = jsonObject.getString("apellido");
                        String mail = jsonObject.getString("mail");

                        registros.add(id + ": " + nombre + " " + apellido + " (" + mail + ")");
                        ids.add(id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error al cargar registros: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private void eliminarRegistro(String id) {
        String URL = "https://ist17dejulio.000webhostapp.com/eliminar.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(MainActivity.this, "Registro eliminado", Toast.LENGTH_SHORT).show();
                cargarRegistros();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    // Procesar jsonArray y actualizar la interfaz de usuario
                } catch (JSONException e) {
                    // Manejar el error de análisis JSON
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error: " + error.toString(), Toast.LENGTH_SHORT).show();

                Toast.makeText(MainActivity.this, "Error al eliminar registro: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("codigo", id);
                return parametros;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
