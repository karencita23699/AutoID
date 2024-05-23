package com.example.autoid;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.autoid.Modelos.Reporte;

import java.util.List;

public class AdapterReportes extends RecyclerView.Adapter<AdapterReportes.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    private List<Reporte> listD;
    View view;
    private OnItemDeleteListener deleteListener;

    public AdapterReportes(Context context, List<Reporte> listD) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.listD = listD;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = inflater.inflate(R.layout.item_reporte_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reporte item = listD.get(position);
        holder.bindData(item);
        if (context != null && listD.get(position) != null && listD.get(position).getImg() != null) {
            String imageUrl = listD.get(position).getImg().replace("\\/", "/");
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.imgcargando)
                    .error(R.drawable.notfound)
                    .into(holder.img);
        }
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
        return listD.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView img, imgestado;
        TextView tvPlaca, tvfecha, tvestado;
        Context context;
        Button btnEliminar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgItem);
            tvPlaca = itemView.findViewById(R.id.tvPlaca);
            tvfecha = itemView.findViewById(R.id.tvFechaReporte);
            tvestado = itemView.findViewById(R.id.tvActivo);
            imgestado = itemView.findViewById(R.id.imgActivo);
            context = itemView.getContext();
            btnEliminar = itemView.findViewById(R.id.btnEliminarReporte);
        }
        void bindData(final Reporte item){
            tvPlaca.setText(item.getResultadoObtenido());
            tvfecha.setText(item.getCreated_at());
            tvestado.setText(item.getEstado()==0? "Sin Revisar" : "Revisado");
            Drawable drawable = imgestado.getDrawable();
            if(item.getEstado()==1)
                drawable.setColorFilter(ContextCompat.getColor(context, R.color.Activo), PorterDuff.Mode.SRC_IN);
            else
                drawable.setColorFilter(ContextCompat.getColor(context, R.color.Inactivo), PorterDuff.Mode.SRC_IN);
        }
    }
    public interface OnItemDeleteListener {
        void onItemDelete(Reporte item);
    }
    public void setOnItemDeleteListener(OnItemDeleteListener listener) {
        this.deleteListener = listener;
    }
}
