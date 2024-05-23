package com.example.autoid;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.autoid.Interfaces.ApiUsuario;
import com.example.autoid.Modelos.Usuario;
import com.google.android.material.navigation.NavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * La clase MainActivity maneja la interfaz principal de la aplicación.
 * Implementa el menú de navegación y las funcionalidades asociadas a las acciones del usuario.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    NavigationView navigationView; // Vista de navegación
    NavController navController; // Controlador de navegación
    TextView tvnomuser; // Texto para mostrar el nombre del usuario
    DrawerLayout drawerLayout; // Layout para el menú de navegación
    ImageView btnMenu, btnCamera; // Botones para el menú y la cámara
    View headerView; // Cabecera de la vista de navegación
    int userId; // ID del usuario
    Menu navMenu; // Menú de navegación
    private static final int REQUEST_CAMERA_PERMISSION = 8; // Código de solicitud para el permiso de la cámara
    Usuario usuario; // Usuario actual

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inicializar(); // Inicializar componentes de la interfaz

        Intent intent = getIntent();
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }

        if (intent.hasExtra("idUsuario")) {
            userId = intent.getIntExtra("idUsuario", -1);
            recuperarUsuario(userId); // Recuperar datos del usuario
        }

        SharedPreferences preferences = getSharedPreferences("sesion", Context.MODE_PRIVATE);
        if (preferences.getBoolean("estado_usu", false)) {
            userId = preferences.getInt("idUsuario", -1);
            recuperarUsuario(userId); // Recuperar datos del usuario
        }

        navigationView.setItemIconTintList(null);
        navMenu = navigationView.getMenu();
        navController = Navigation.findNavController(this, R.id.navFragment);
        NavigationUI.setupWithNavController(navigationView, navController);
        
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.CapturarPlaca:
                    btnCamera.setVisibility(View.GONE);
                    navController.navigate(R.id.CapturarPlaca);
                    break;
                case R.id.Reportes:
                    navController.navigate(R.id.Reportes);
                    break;
                case R.id.Registros:
                    navController.navigate(R.id.Registros);
                    break;
                case R.id.Perfil:
                    navController.navigate(R.id.Perfil);
                    break;
                case R.id.CerrarSesion:
                    cerrarSesion(); // Cerrar sesión
                    break;
                default:
                    break;
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    /**
     * Cierra la sesión del usuario y redirige a la pantalla de inicio de sesión.
     */
    private void cerrarSesion() {
        SharedPreferences preferences = getSharedPreferences("sesion", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.remove("estado_usu");
        editor.remove("idUsuario");

        editor.apply();
        
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        finish();
    }

    /**
     * Inicializa los componentes de la interfaz.
     */
    private void inicializar() {
        navigationView = findViewById(R.id.NavigationView);
        headerView = navigationView.getHeaderView(0);
        tvnomuser = headerView.findViewById(R.id.tvNombreUsuario);
        btnCamera = findViewById(R.id.btnCamara);
        drawerLayout = findViewById(R.id.drawerLayout1);
        btnMenu = findViewById(R.id.btnMenu);

        btnMenu.setOnClickListener(this);
        btnCamera.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnMenu:
                drawerLayout.openDrawer(GravityCompat.START);
                recuperarUsuario(userId); // Recuperar datos del usuario
                break;
            case R.id.btnCamara:
                navController.navigate(R.id.CapturarPlaca);
                break;
            default:
                break;
        }
    }

    /**
     * Recupera los datos del usuario desde el servidor.
     * @param id ID del usuario.
     */
    private void recuperarUsuario(int id) {
        ApiClient apiClient = new ApiClient();
        ApiUsuario apiUsuario = apiClient.getRetrofit().create(ApiUsuario.class);
        Call<Usuario> call = apiUsuario.obtenerUsuario(id);
        
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    usuario = response.body();
                    if (usuario != null) {
                        String nombre = usuario.getPrimerApellido() + " " + usuario.getSegundoApellido() + " " + usuario.getNombres();
                        if (usuario.getSegundoApellido().isEmpty()) {
                            nombre = usuario.getPrimerApellido() + " " + usuario.getNombres();
                        }
                        tvnomuser.setText(nombre);
                    } else {
                        Log.e("Usuario", "nulo");
                    }
                } else {
                    Log.e("retrofitError", "" + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.i("Error", t.getMessage());
            }
        });
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public int getUserId() {
        return userId;
    }
}
