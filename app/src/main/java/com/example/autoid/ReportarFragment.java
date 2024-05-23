package com.example.autoid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autoid.Interfaces.ApiReporte;
import com.example.autoid.Modelos.Placa;
import com.example.autoid.Modelos.Reporte;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportarFragment extends Fragment {
    View view;
    RecyclerView rvReportes;
    List<Reporte> listR;
    int idUsuario;
    AdapterReportes adapterReportes;
    SearchView searchView;

    public ReportarFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reportar, container, false);
        inicializar();
        return view;
    }

    private void inicializar() {
        rvReportes = view.findViewById(R.id.recyclerViewReportes);
        searchView = view.findViewById(R.id.SearchViewReportes);
        rvReportes.setHasFixedSize(true);
        rvReportes.setLayoutManager(new LinearLayoutManager(view.getContext()));
        obtenerReportes();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                List<Reporte> itemsn = new ArrayList<>();
                for (Reporte item : listR) {
                    if (item.getResultadoObtenido().contains(query))
                        itemsn.add(item);
                }
                AdapterReportes listAdapter = new AdapterReportes(getActivity(), itemsn);
                rvReportes = view.findViewById(R.id.recyclerViewRegistros);
                rvReportes.setHasFixedSize(true);
                rvReportes.setLayoutManager(new LinearLayoutManager(getActivity()));
                rvReportes.setAdapter(listAdapter);
                listAdapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<Reporte> itemsn = new ArrayList<>();
                for (Reporte item : listR) {
                    if (item.getResultadoObtenido().contains(newText))
                        itemsn.add(item);
                }
                AdapterReportes listAdapter = new AdapterReportes(getActivity(), itemsn);
                rvReportes = view.findViewById(R.id.recyclerViewRegistros);
                rvReportes.setHasFixedSize(true);
                rvReportes.setLayoutManager(new LinearLayoutManager(getActivity()));
                rvReportes.setAdapter(listAdapter);
                listAdapter.notifyDataSetChanged();
                return false;
            }
        });
    }

    private void obtenerReportes() {
        listR = new ArrayList<>();
        try {
            idUsuario = ((MainActivity) getActivity()).getUserId();
            ApiClient apiClient=new ApiClient();
            ApiReporte apiReporte = apiClient.getRetrofit().create(ApiReporte.class);

            Call<List<Reporte>> call = apiReporte.obtenerReportes(idUsuario);
            call.enqueue(new Callback<List<Reporte>>() {
                @Override
                public void onResponse(Call<List<Reporte>> call, Response<List<Reporte>> response) {
                    if(response.isSuccessful() && response.code()==200){
                        listR = response.body();
                        actualizarAdapter();
                    }
                }

                @Override
                public void onFailure(Call<List<Reporte>> call, Throwable t) {

                }
            });
        } catch (Exception e) {

        }
    }
    private void actualizarAdapter() {
        adapterReportes = new AdapterReportes(getActivity(), listR);
        rvReportes.setAdapter(adapterReportes);
        adapterReportes.setOnItemDeleteListener(new AdapterReportes.OnItemDeleteListener() {
            @Override
            public void onItemDelete(Reporte item) {
                eliminarReporte(item);
                listR.remove(item);
                adapterReportes.notifyDataSetChanged();
            }
        });
    }

    private void eliminarReporte(Reporte item) {
        ApiClient apiClient = new ApiClient();
        ApiReporte apiReporte = apiClient.getRetrofit().create(ApiReporte.class);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("idReporte", item.getIdReporte());
        Call<Void> call = apiReporte.eliminarReporte(jsonObject);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful() && response.code()==200)
                    Toast.makeText(getContext(),"Reporte eliminado con exito", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }


}