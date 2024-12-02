package com.example.servicioweb;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ModificarActivity extends AppCompatActivity {

    EditText txtCodigo, txtNombre, txtApellido, txtMail;
    Button btnActualizar;

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar);

        txtCodigo = findViewById(R.id.txtCodigoModif);
        txtNombre = findViewById(R.id.txtNombreModif);
        txtApellido = findViewById(R.id.txtApellidoModif);
        txtMail = findViewById(R.id.txtMailModif);
        btnActualizar = findViewById(R.id.btnActualizar);

        // Obtener el ID del registro a modificar desde MainActivity
        id = getIntent().getStringExtra("id");

        // Cargar los datos del registro en los EditText
        cargarDatosRegistro(id);

        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarRegistro(id);
            }
        });
    }

    private void cargarDatosRegistro(String id) {
        String URL = "https://ist17dejulio.000webhostapp.com/obtener_registro.php?id=" + id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // Parsear la respuesta JSON
                    JSONObject jsonObject = new JSONObject(response);
                    String codigo = jsonObject.getString("codigo");
                    String nombre = jsonObject.getString("nombre");
                    String apellido = jsonObject.getString("apellido");
                    String mail = jsonObject.getString("mail");

                    // Mostrar los datos en los EditText
                    txtCodigo.setText(codigo);
                    txtNombre.setText(nombre);
                    txtApellido.setText(apellido);
                    txtMail.setText(mail);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ModificarActivity.this, "Error al obtener datos del servidor", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ModificarActivity.this, "Error al cargar datos del registro", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void actualizarRegistro(String id) {
        String URL = "https://ist17dejulio.000webhostapp.com/actualizar.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(ModificarActivity.this, "Registro actualizado", Toast.LENGTH_SHORT).show();
                finish(); // Regresar a la actividad anterior después de actualizar
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ModificarActivity.this, "Error al actualizar registro", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("codigo", txtCodigo.getText().toString());
                params.put("nombre", txtNombre.getText().toString());
                params.put("apellido", txtApellido.getText().toString());
                params.put("mail", txtMail.getText().toString());
                params.put("id", id);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
