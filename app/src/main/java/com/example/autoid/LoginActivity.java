package com.example.autoid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.autoid.Interfaces.ApiUsuario;
import com.example.autoid.Modelos.Usuario;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    EditText etUsuario, etContrasena;
    TextView tvCrearCuenta;
    CheckBox cbRecordarme;
    Button btnIniciarSesion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        inicializar();

    }

    private void inicializar() {
        etUsuario = findViewById(R.id.etNomUsuario);
        etContrasena = findViewById(R.id.etPassword);
        tvCrearCuenta = findViewById(R.id.btnCreateAcount);
        cbRecordarme = findViewById(R.id.chRecordarme);
        btnIniciarSesion = findViewById(R.id.btnSignIn);

        btnIniciarSesion.setOnClickListener(this);
        tvCrearCuenta.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnCreateAcount)
            crearCuenta();
        if (v.getId() == R.id.btnSignIn)
            iniciarSesion();
    }

    private void iniciarSesion() {
        String usuario = etUsuario.getText().toString();
        String contrasena = etContrasena.getText().toString();
        Boolean rec = cbRecordarme.isChecked();
        if (!usuario.equals("") && !contrasena.equals("")) {
            ApiClient apiClient = new ApiClient();
            ApiUsuario apiUsuario = apiClient.getRetrofit().create(ApiUsuario.class);

            Call<Usuario> call = apiUsuario.login(usuario, contrasena);
            call.enqueue(new Callback<Usuario>() {
                @Override
                public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                    if (response.isSuccessful() && response.code() == 200) {
                        Toast.makeText(getApplicationContext(), "Bienvenido", Toast.LENGTH_SHORT).show();
                        limpiar();
                        finish();
                        Usuario usuario = response.body();
                        if (rec)
                            guardarestado(usuario);
                        Intent principal = new Intent(LoginActivity.this, MainActivity.class);
                        principal.putExtra("idUsuario", usuario.getIdUsuario());
                        startActivity(principal);
                    } else if (response.code() == 401) {
                        Toast.makeText(getApplicationContext(), "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                        etContrasena.setText("");
                    } else if (response.code() == 402) {
                        limpiar();
                        Toast.makeText(getApplicationContext(), "Usuario no existente", Toast.LENGTH_SHORT).show();
                        limpiar();
                    }
                }
                @Override
                public void onFailure(Call<Usuario> call, Throwable t) {

                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Rellene los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void crearCuenta() {
        Intent intent = new Intent(LoginActivity.this, RegistrarActivity.class);
        startActivity(intent);
    }

    private void limpiar() {
        etUsuario.setText("");
        etContrasena.setText("");
    }

    private void guardarestado(Usuario usuario) {
        SharedPreferences preferences = getSharedPreferences("sesion", Context.MODE_PRIVATE);
        boolean estado = true;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("estado_usu", estado);
        editor.putInt("idUsuario", usuario.getIdUsuario());
        editor.commit();
    }
}