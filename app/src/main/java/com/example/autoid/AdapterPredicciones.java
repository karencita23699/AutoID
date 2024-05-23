package com.example.autoid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autoid.Modelos.Detection;

import java.util.List;

public class AdapterPredicciones extends RecyclerView.Adapter<AdapterPredicciones.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    private List<Detection> listD;
    View view;

    public AdapterPredicciones(Context context, List<Detection> listD) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.listD = listD;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = inflater.inflate(R.layout.itemprediccioneslayout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(listD.get(position));
    }

    @Override
    public int getItemCount() {
        return listD.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvObjeto, tvProbabilidad;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvObjeto = itemView.findViewById(R.id.tvObjeto);
            tvProbabilidad = itemView.findViewById(R.id.tvProbabilidad);
        }
        void bindData(final Detection item){
            int porcentaje = (int) (item.getPresicion()*100);
            tvObjeto.setText(item.getClase());
            tvProbabilidad.setText(porcentaje+"%");
        }
    }
}
