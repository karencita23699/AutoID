package com.example.autoid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.autoid.Interfaces.ApiUsuario;
import com.example.autoid.Modelos.Usuario;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrarActivity extends AppCompatActivity implements View.OnClickListener{

    EditText etprimer, etsegundo, etnombres, etcorreo, ettelefono, etnomusuario, etcontrasena1, etcontrasena2;
    Button btnRegistrar;
    RadioButton rbFem, rbMas;
    TextView tvcorreo, tvnomUsuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);
        inicializar();
    }

    private void inicializar() {
        etprimer = findViewById(R.id.etPrimerApellido);
        etsegundo = findViewById(R.id.etSegundoApellido);
        etnombres = findViewById(R.id.etNombres);
        etcorreo = findViewById(R.id.etCorreo);
        ettelefono = findViewById(R.id.etTelefono);
        etnomusuario = findViewById(R.id.etNomUsuario);
        etcontrasena1 = findViewById(R.id.etContrasena1);
        etcontrasena2 = findViewById(R.id.etContrasena2);
        rbFem = findViewById(R.id.rbFem);
        rbMas = findViewById(R.id.rbMas);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        tvcorreo = findViewById(R.id.tvCorreoNoDisponible);
        tvnomUsuario = findViewById(R.id.tvNombreUsuarioNoDisponible);
        btnRegistrar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnRegistrar)
            verificarDatos();
    }

    private void RegistrarUsuario() {
        String contrasena1 = etcontrasena1.getText().toString();
        String contrasena2 = etcontrasena2.getText().toString();

        String patronMayuscula = ".*[A-Z].*";
        String patronMinuscula = ".*[a-z].*";
        String patronNumeros = ".*[0-9].*";

        if(contrasena1.equals(contrasena2)){
            if (contrasena1.length() >= 8) {
                boolean contieneMayuscula = contrasena1.matches(patronMayuscula);
                if (contieneMayuscula) {
                    boolean contieneMinuscula = contrasena1.matches(patronMinuscula);
                    if (contieneMinuscula) {
                        boolean contieneNumeros = contrasena1.matches(patronNumeros);
                        if (contieneNumeros) {
                            String primer = etprimer.getText().toString().trim();
                            String segundo = etsegundo.getText().toString().trim();
                            String nombres = etnombres.getText().toString().trim();
                            String correo = etcorreo.getText().toString().trim();
                            String telefono = ettelefono.getText().toString().trim();
                            String nomUsuario = etnomusuario.getText().toString().trim();
                            String genero = rbFem.isChecked()? "F" : rbMas.isChecked()? "M": "";
                            Usuario usuario = new Usuario(primer,segundo,nombres,correo,telefono,nomUsuario,contrasena1,genero);

                            ApiClient apiClient = new ApiClient();
                            ApiUsuario apiUsuario = apiClient.getRetrofit().create(ApiUsuario.class);

                            Call<Integer> call = apiUsuario.crearUsuario(usuario);
                            call.enqueue(new Callback<Integer>() {
                                @Override
                                public void onResponse(Call<Integer> call, Response<Integer> response) {
                                    if(response.code()==201 && response.isSuccessful()){
                                        Toast.makeText(getApplicationContext(),"Usuario creado con exito",Toast.LENGTH_SHORT).show();
                                        int user = response.body();
                                        guardarestado(user);
                                        finish();
                                        Intent intent = new Intent(RegistrarActivity.this, MainActivity.class);
                                        intent.putExtra("idUsuario", user);
                                        startActivity(intent);
                                    } else if (response.code()==500) {
                                        Toast.makeText(getApplicationContext(),"Error al crear el usuario",Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Integer> call, Throwable t) {

                                }
                            });
                        }else{
                            Toast.makeText(this,"La contraseña debe contener al menos un numero",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(this,"La contraseña debe contener al menos una minuscula",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this,"La contraseña debe contener al menos una mayuscula",Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this,"La contraseña debe tener minimamente 8 caracteres",Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this,"La contraseña no coincide",Toast.LENGTH_SHORT).show();
        }
    }

    private void verificarDatos() {
        String correo = etcorreo.getText().toString();

        ApiClient apiClient = new ApiClient();
        ApiUsuario apiUsuario = apiClient.getRetrofit().create(ApiUsuario.class);

        Call<Void> call = apiUsuario.verificarEmail(correo);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    if(response.code()==200){
                        verificarNomUsuario();
                        tvcorreo.setVisibility(View.GONE);
                    }
                }else if (response.code()==401) {
                    etcorreo.setText("");
                    tvcorreo.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    private void verificarNomUsuario() {
        String nomUsuario = etnomusuario.getText().toString();
        ApiClient apiClient = new ApiClient();
        ApiUsuario apiUsuario = apiClient.getRetrofit().create(ApiUsuario.class);
        Call<Void> call = apiUsuario.verificarNomUsuario(nomUsuario);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    if(response.code()==200){
                        RegistrarUsuario();
                        tvnomUsuario.setVisibility(View.GONE);
                    }
                }else if (response.code()==401) {
                    etnomusuario.setText("");
                    tvnomUsuario.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }
    private void guardarestado(int usuario) {
        SharedPreferences preferences = getSharedPreferences("sesion", Context.MODE_PRIVATE);
        boolean estado = true;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("estado_usu", estado);
        editor.putInt("idUsuario", usuario);
        editor.commit();
    }

}