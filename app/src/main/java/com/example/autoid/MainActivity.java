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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    NavigationView navigationView;
    NavController navController;
    TextView tvnomuser;
    DrawerLayout drawerLayout;
    ImageView btnMenu, btnCamera;
    View headerView;
    int userId;
    Menu navMenu;
    private static final int REQUEST_CAMERA_PERMISSION = 8;
    Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inicializar();
        Intent intent = getIntent();
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
        if (intent.hasExtra("idUsuario")) {
            userId = intent.getIntExtra("idUsuario", -1);
            recuperarUsuario(userId);
        }
        SharedPreferences preferences = getSharedPreferences("sesion", Context.MODE_PRIVATE);
        if (preferences.getBoolean("estado_usu", false) == true) {
            userId = preferences.getInt("idUsuario", -1);
            recuperarUsuario(userId);
        }

        navigationView.setItemIconTintList(null);
        navMenu = navigationView.getMenu();
        navController = Navigation.findNavController(this, R.id.navFragment);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.CapturarPlaca) {
                    btnCamera.setVisibility(View.GONE);
                    navController.navigate(R.id.CapturarPlaca);
                }
                if (item.getItemId() == R.id.Reportes) {
                    navController.navigate(R.id.Reportes);
                }
                if (item.getItemId() == R.id.Registros) {
                    navController.navigate(R.id.Registros);
                }
                if (item.getItemId() == R.id.Perfil) {
                    navController.navigate(R.id.Perfil);
                }
                if (item.getItemId() == R.id.CerrarSesion) {
                    cerrarSesion();
                }
                drawerLayout.closeDrawer(GravityCompat.START);

                return true;
            }

        });
    }

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

    private void inicializar() {
        navigationView = findViewById(R.id.NavigationView);
        headerView = navigationView.getHeaderView(0);
        tvnomuser = headerView.findViewById(R.id.tvNombreUsuario);
        btnCamera = findViewById(R.id.btnCamara);
        navigationView = findViewById(R.id.NavigationView);
        drawerLayout = findViewById(R.id.drawerLayout1);
        btnMenu = findViewById(R.id.btnMenu);

        btnMenu.setOnClickListener(this);
        btnCamera.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnMenu) {
            drawerLayout.openDrawer(GravityCompat.START);
            recuperarUsuario(userId);
        }
        if (v.getId() == R.id.btnCamara) {
            navController.navigate(R.id.CapturarPlaca);
        }
    }

    private void recuperarUsuario(int id) {
        ApiClient apiClient = new ApiClient();
        ApiUsuario apiUsuario = apiClient.getRetrofit().create(ApiUsuario.class);
        Call<Usuario> call = apiUsuario.obtenerUsuario(id);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                try {
                    if (response.isSuccessful()) {
                        usuario = response.body();
                        if (usuario != null) {
                            String nombre = usuario.getPrimerApellido() + " " + usuario.getSegundoApellido() + " " + usuario.getNombres();
                            if (usuario.getSegundoApellido().equals(""))
                                nombre = usuario.getPrimerApellido() + " " + usuario.getNombres();
                            tvnomuser.setText(nombre);
                        } else {
                            Log.e("Usuario", "nulo");
                        }
                    } else {
                        Log.e("retrofitError", "" + response.errorBody());
                    }
                } catch (Exception e) {
                    Log.e("Exception", e.getMessage() + "");
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