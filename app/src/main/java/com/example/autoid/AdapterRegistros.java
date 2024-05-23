package com.example.autoid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.autoid.Modelos.Placa;
import com.example.autoid.Modelos.Reporte;

import java.util.List;

public class AdapterRegistros extends RecyclerView.Adapter<AdapterRegistros.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    private List<Placa> listP;
    View view;
    private AdapterRegistros.OnItemDeleteListener deleteListener;
    public AdapterRegistros(Context context, List<Placa> listP) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.listP = listP;
    }

    @NonNull
    @Override
    public AdapterRegistros.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = inflater.inflate(R.layout.item_registro_layout, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterRegistros.ViewHolder holder, int position) {
        Placa item = listP.get(position);
        holder.bindData(item);
        holder.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteListener != null) {
                    deleteListener.onItemDelete(item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listP.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvplaca, tvfecha;
        Button btnEliminar;
        Context context;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvplaca = itemView.findViewById(R.id.tvPlaca);
            tvfecha = itemView.findViewById(R.id.tvFechaRegistro);
            btnEliminar = itemView.findViewById(R.id.btnEliminarRegistro);
            context = itemView.getContext();
        }
        void bindData(final Placa item){
            tvplaca.setText(item.getNumMatricula());
            tvfecha.setText(item.getCreated_at());
        }
    }
    public interface OnItemDeleteListener {
        void onItemDelete(Placa item);
    }
    public void setOnItemDeleteListener(AdapterRegistros.OnItemDeleteListener listener) {
        this.deleteListener = listener;
    }
}
