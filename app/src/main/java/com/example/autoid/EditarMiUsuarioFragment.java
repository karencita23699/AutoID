package com.example.autoid;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.autoid.Interfaces.ApiUsuario;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarMiUsuarioFragment extends Fragment implements View.OnClickListener {

    TextView tvprimer, tvsegundo, tvnombres, tvcorreo, tvcontacto, tvgenero, tvnombreuser;
    View view;
    Button btnCambiarContraseña;
    Dialog dialog;
    ImageView imgprimer, imgsegundo, imgnombres, imgcontacto, imggenero, imgnombreuser;
    EditText etdato, etcontraseña, etcontraseña2;
    TextView tvtitle;
    int idUsuario = 0;
    //Botones del Dialog
    Button btnCancelar, btnGuardar;
    String campo = "";
    RadioButton rbFem, rbMas;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_editar_mi_usuario, container, false);
        inicializar();
        try {
            idUsuario = ((MainActivity) getActivity()).getUsuario().getIdUsuario();
            String primerapellido = ((MainActivity) getActivity()).getUsuario().getPrimerApellido();
            String segundoapellido = ((MainActivity) getActivity()).getUsuario().getSegundoApellido();
            String nombres = ((MainActivity) getActivity()).getUsuario().getNombres();
            String correo = ((MainActivity) getActivity()).getUsuario().getCorreo();
            String contacto = ((MainActivity) getActivity()).getUsuario().getTelefono();
            String genero = ((MainActivity) getActivity()).getUsuario().getGenero().equals("F") ? "Femenino" : "Masculino";
            String nombreUsuario = ((MainActivity) getActivity()).getUsuario().getNomUsuario();

            tvprimer.setText(primerapellido);
            tvsegundo.setText(segundoapellido);
            tvnombres.setText(nombres);
            tvcorreo.setText(correo);
            tvcontacto.setText(contacto);
            tvgenero.setText(genero);
            tvnombreuser.setText(nombreUsuario);
        } catch (Exception e) {

        }
        return view;
    }

    private void inicializar() {
        tvprimer = view.findViewById(R.id.tvPrimerApellido);
        tvsegundo = view.findViewById(R.id.tvSegundoApellido);
        tvnombres = view.findViewById(R.id.tvNombres);
        tvcorreo = view.findViewById(R.id.tvCorreoElectronico);
        tvcontacto = view.findViewById(R.id.tvNumeroContacto);
        tvgenero = view.findViewById(R.id.tvGenero);
        tvnombreuser = view.findViewById(R.id.tvNombreUsuario);

        imgprimer = view.findViewById(R.id.imgEditarPrimerApellido1);
        imgsegundo = view.findViewById(R.id.imgEditarSegundoApellido1);
        imgnombres = view.findViewById(R.id.imgEditarNombres1);
        imgcontacto = view.findViewById(R.id.imgEditarContacto1);
        imggenero = view.findViewById(R.id.imgEditarGenero1);

        btnCambiarContraseña = view.findViewById(R.id.btnCambiarContraseña);

        imgprimer.setOnClickListener(this);
        imgsegundo.setOnClickListener(this);
        imgnombres.setOnClickListener(this);
        imgcontacto.setOnClickListener(this);
        imggenero.setOnClickListener(this);
        btnCambiarContraseña.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imgEditarPrimerApellido1)
            editarPrimerApellido();
        if (v.getId() == R.id.imgEditarSegundoApellido1)
            editarSegundoApellido();
        if (v.getId() == R.id.imgEditarNombres1)
            editarNombres();
        if (v.getId() == R.id.imgEditarContacto1)
            editarContacto();
        if (v.getId() == R.id.imgEditarGenero1)
            editarGenero();
        if (v.getId() == R.id.btnCancelar1)
            dialog.dismiss();
        if (v.getId() == R.id.btnCambiarContraseña)
            editarContraseña();

        //dialog
        if (v.getId() == R.id.btnGuardar1)
            modificarUsuario(campo);
        if (v.getId() == R.id.btnGuardarGenero)
            modificarUsuarioGenero(campo);
        if (v.getId() == R.id.btnGuardarContraseña)
            modificarContraseña(campo);
    }

    private void modificarUsuario(String campo) {
        String dato = etdato.getText().toString().trim();
        ApiClient apiClient = new ApiClient();
        ApiUsuario apiUser = apiClient.getRetrofit().create(ApiUsuario.class);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("idUsuario", idUsuario);
        jsonObject.addProperty("campo", campo);
        jsonObject.addProperty("valor", dato.trim());

        Call<Void> call = apiUser.modificarUsuario(jsonObject);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    if (campo.equals("primerApellido")) {
                        tvprimer.setText(etdato.getText().toString().trim());
                        ((MainActivity) getActivity()).getUsuario().setPrimerApellido(etdato.getText().toString().trim());
                    }
                    if (campo.equals("segundoApellido")) {
                        tvsegundo.setText(etdato.getText().toString().trim());
                        ((MainActivity) getActivity()).getUsuario().setSegundoApellido(etdato.getText().toString().trim());
                    }
                    if (campo.equals("nombres")) {
                        tvnombres.setText(etdato.getText().toString().trim());
                        ((MainActivity) getActivity()).getUsuario().setNombres(etdato.getText().toString().trim());
                    }
                    if (campo.equals("telefono")) {
                        tvcontacto.setText(etdato.getText().toString().trim());
                        ((MainActivity) getActivity()).getUsuario().setTelefono(etdato.getText().toString().trim());
                    }
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });

    }

    private void modificarUsuarioGenero(String campo) {
        String dato = rbFem.isChecked() ? "F" : "M";
        ApiClient apiClient = new ApiClient();
        ApiUsuario apiUser = apiClient.getRetrofit().create(ApiUsuario.class);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("idUsuario", idUsuario);
        jsonObject.addProperty("campo", campo);
        jsonObject.addProperty("valor", dato.trim());

        Call<Void> call = apiUser.modificarUsuario(jsonObject);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    tvgenero.setText(dato.equals("F") ? "Femenino" : "Masculino");
                    ((MainActivity) getActivity()).getUsuario().setGenero(dato);
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });

    }

    private void modificarContraseña(String campo) {
        String patronMayuscula = ".*[A-Z].*";
        String patronMinuscula = ".*[a-z].*";
        String patronNumeros = ".*[0-9].*";
        String dato1 = etcontraseña.getText().toString();
        String dato2 = etcontraseña2.getText().toString();
        if (dato1.equals(dato2)) {
            if (dato1.length() > 8) {
                boolean contieneMayuscula = dato1.matches(patronMayuscula);
                boolean contieneMinuscula = dato1.matches(patronMinuscula);
                boolean contieneNumeros = dato1.matches(patronNumeros);
                if (contieneMayuscula) {
                    if (contieneMinuscula) {
                        if (contieneNumeros) {
                            ApiClient apiClient = new ApiClient();
                            ApiUsuario apiUser = apiClient.getRetrofit().create(ApiUsuario.class);

                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("idUsuario", idUsuario);
                            jsonObject.addProperty("campo", campo);
                            jsonObject.addProperty("valor", dato1.trim());

                            Call<Void> call = apiUser.modificarUsuario(jsonObject);
                            call.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if (response.isSuccessful() && response.code() == 200) {
                                        Toast.makeText(dialog.getContext(), "Contraseña cambiada exitosamente", Toast.LENGTH_SHORT).show();
                                    }
                                    dialog.dismiss();
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {

                                }
                            });
                        } else {
                            Toast.makeText(dialog.getContext(), "La contraseña debe contener al menos un numero", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(dialog.getContext(), "La contraseña debe contener al menos una minuscula", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(dialog.getContext(), "La contraseña debe contener al menos una mayuscula", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(dialog.getContext(), "La contraseña debe tener minimamente 8 caracteres", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(dialog.getContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
        }
    }

    private void editarPrimerApellido() {
        iniciarDialog();
        tvtitle.setText("Escribe tu Primer Apellido ");
        campo = "primerApellido";
    }

    private void editarSegundoApellido() {
        iniciarDialog();
        tvtitle.setText("Escribe tu Segundo Apellido ");
        campo = "segundoApellido";
    }

    private void editarNombres() {
        iniciarDialog();
        tvtitle.setText("Escribe tus Nombres");
        campo = "nombres";
    }

    private void editarContacto() {
        iniciarDialog();
        tvtitle.setText("Escribe tu Numero de Telefono");
        campo = "telefono";
    }

    private void editarGenero() {
        iniciarDialogGenero();
        campo = "genero";
    }

    private void editarContraseña() {
        iniciarDialogContraseña();
        campo = "Contrasena";
    }

    private void iniciarDialog() {
        dialog = new Dialog(view.getContext(), R.style.CustomDialog);
        dialog.setContentView(R.layout.dialog_editar_usuario);

        configuracion();

        tvtitle = dialog.findViewById(R.id.tvEditarTitulo1);
        etdato = dialog.findViewById(R.id.etEntrada1);
        btnCancelar = dialog.findViewById(R.id.btnCancelar1);
        btnGuardar = dialog.findViewById(R.id.btnGuardar1);
        if (etdato != null) {
            etdato.requestFocus();
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }

        btnCancelar.setOnClickListener(this);
        btnGuardar.setOnClickListener(this);
    }

    private void configuracion() {
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.gravity = Gravity.BOTTOM;
            window.setAttributes(params);
        }
        dialog.show();
    }

    private void iniciarDialogGenero() {
        dialog = new Dialog(view.getContext(), R.style.CustomDialog);
        dialog.setContentView(R.layout.dialog_editar_genero_usuario);

        configuracion();

        rbFem = dialog.findViewById(R.id.rbFem);
        rbMas = dialog.findViewById(R.id.rbMas);
        btnCancelar = dialog.findViewById(R.id.btnCancelar1);
        btnGuardar = dialog.findViewById(R.id.btnGuardarGenero);

        String genero = tvgenero.getText().toString();
        if (genero.equals("Femenino")) {
            rbFem.setChecked(true);
            rbMas.setChecked(false);
        } else {
            rbFem.setChecked(false);
            rbMas.setChecked(true);
        }

        btnCancelar.setOnClickListener(this);
        btnGuardar.setOnClickListener(this);
    }

    private void iniciarDialogContraseña() {
        dialog = new Dialog(view.getContext(), R.style.CustomDialog);
        dialog.setContentView(R.layout.dialog_editar_password_usuario);

        configuracion();

        etcontraseña = dialog.findViewById(R.id.etEntrada1);
        etcontraseña2 = dialog.findViewById(R.id.etEntrada2);
        btnCancelar = dialog.findViewById(R.id.btnCancelar1);
        btnGuardar = dialog.findViewById(R.id.btnGuardarContraseña);

        if (etcontraseña != null) {
            etcontraseña.requestFocus();
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
        btnCancelar.setOnClickListener(this);
        btnGuardar.setOnClickListener(this);
    }

}