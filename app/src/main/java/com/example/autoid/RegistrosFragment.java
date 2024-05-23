package com.example.autoid;

import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.autoid.Interfaces.ApiPlaca;
import com.example.autoid.Interfaces.ApiReporte;
import com.example.autoid.Modelos.Placa;
import com.example.autoid.Modelos.Reporte;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrosFragment extends Fragment {
    View view;
    List<Placa> listP;
    AdapterRegistros adapterRegistros;
    RecyclerView rvRegistros;
    SearchView searchView;
    int idUsuario;
    public RegistrosFragment() {

    }
    public static RegistrosFragment newInstance(String param1, String param2) {
        RegistrosFragment fragment = new RegistrosFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_registros, container, false);
        inicializar();

        return view;
    }

    private void inicializar() {
        rvRegistros = view.findViewById(R.id.recyclerViewRegistros);
        rvRegistros.setHasFixedSize(true);
        rvRegistros.setLayoutManager(new LinearLayoutManager(view.getContext()));
        obtenerRegistros();
        searchView = view.findViewById(R.id.SearchViewRegistros);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                List<Placa> itemsn = new ArrayList<>();
                for (Placa item : itemsn) {
                    if (item.getNumMatricula().contains(query))
                        itemsn.add(item);
                }
                AdapterRegistros listAdapter = new AdapterRegistros(getActivity(), itemsn);
                rvRegistros = view.findViewById(R.id.recyclerViewRegistros);
                rvRegistros.setHasFixedSize(true);
                rvRegistros.setLayoutManager(new LinearLayoutManager(getActivity()));
                rvRegistros.setAdapter(listAdapter);
                listAdapter.notifyDataSetChanged();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                List<Placa> itemsn = new ArrayList<>();
                for (Placa item : listP) {
                    if (item.getNumMatricula().contains(newText))
                        itemsn.add(item);
                }
                AdapterRegistros listAdapter = new AdapterRegistros(getActivity(), itemsn);
                rvRegistros = view.findViewById(R.id.recyclerViewRegistros);
                rvRegistros.setHasFixedSize(true);
                rvRegistros.setLayoutManager(new LinearLayoutManager(getActivity()));
                rvRegistros.setAdapter(listAdapter);
                listAdapter.notifyDataSetChanged();
                return false;
            }
        });
    }

    private void obtenerRegistros() {
        listP = new ArrayList<>();
        try{
            idUsuario = ((MainActivity)getActivity()).getUserId();
            ApiClient apiClient = new ApiClient();
            ApiPlaca apiPlaca = apiClient.getRetrofit().create(ApiPlaca.class);

            Call<List<Placa>> call = apiPlaca.obtenerRegistros(idUsuario);
            call.enqueue(new Callback<List<Placa>>() {
                @Override
                public void onResponse(Call<List<Placa>> call, Response<List<Placa>> response) {
                    if(response.isSuccessful() && response.code()==200){
                        listP = response.body();
                        actualizarAdapter();
                    }
                }

                @Override
                public void onFailure(Call<List<Placa>> call, Throwable throwable) {

                }
            });
        }catch (Exception e){

        }
    }

    private void actualizarAdapter() {
        adapterRegistros = new AdapterRegistros(getActivity(), listP);
        rvRegistros.setAdapter(adapterRegistros);
        adapterRegistros.setOnItemDeleteListener(new AdapterRegistros.OnItemDeleteListener() {
            @Override
            public void onItemDelete(Placa item) {
                eliminarRegistro(item);
                listP.remove(item);
                adapterRegistros.notifyDataSetChanged();
            }
        });
    }
    private void eliminarRegistro(Placa item) {
        ApiClient apiClient = new ApiClient();
        ApiPlaca apiPlaca = apiClient.getRetrofit().create(ApiPlaca.class);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("idPlaca", item.getIdPlaca());
        Call<Void> call = apiPlaca.eliminarRegistro(jsonObject);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful() && response.code()==200)
                    Toast.makeText(getContext(),"Numero de placa eliminado con exito", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }
}