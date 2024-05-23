package com.example.autoid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

public class PerfilFragment extends Fragment implements View.OnClickListener{

    View view;
    TextView tvNom, tvEma, tvTel, tvGen, tvUse;
    Button btnModificarParqueo, btnEditarUsuario;
    public PerfilFragment() {
        // Required empty public constructor
    }
    public static PerfilFragment newInstance(String param1, String param2) {
        PerfilFragment fragment = new PerfilFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_perfil, container, false);
        inicializar();
        return view;
    }
    private void inicializar() {
        tvNom = view.findViewById(R.id.tvNombreUser);
        tvEma = view.findViewById(R.id.tvCorreoElectronico);
        tvTel = view.findViewById(R.id.tvContacto);
        tvGen = view.findViewById(R.id.tvSexo);
        tvUse = view.findViewById(R.id.tvNombreUsuario);

        btnEditarUsuario = view.findViewById(R.id.btnEditar);

        btnEditarUsuario.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnEditar){
            EditarDatosUsuario();
        }
    }
    private void EditarDatosUsuario() {
        NavHostFragment.findNavController(PerfilFragment.this)
                .navigate(R.id.editarMiUsuario);
    }
    @Override
    public void onResume() {
        super.onResume();
        actualizarDatosDelPerfil();
    }
    private void actualizarDatosDelPerfil() {
        actualizarDatos();
    }
    public void actualizarDatos(){
        try{
            String nombres = ((MainActivity)getActivity()).getUsuario().getNombres();
            String primerape = ((MainActivity)getActivity()).getUsuario().getPrimerApellido();
            String segundoap = ((MainActivity)getActivity()).getUsuario().getSegundoApellido();
            String correo = ((MainActivity)getActivity()).getUsuario().getCorreo();
            String contacto = ((MainActivity)getActivity()).getUsuario().getTelefono();
            String genero = ((MainActivity)getActivity()).getUsuario().getGenero();
            String nomUser = ((MainActivity)getActivity()).getUsuario().getNomUsuario();

            String nombrecompleto = primerape+" "+segundoap;
            tvNom.setText(nombrecompleto+"\n"+nombres);
            tvEma.setText(correo);
            tvTel.setText(contacto);
            if(genero.equals("F"))
                tvGen.setText("Femenino");
            else
                tvGen.setText("Masculino");
            tvUse.setText(nomUser);
        }catch (Exception e){

        }
    }
}