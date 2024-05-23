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

/**
 * AdapterPredicciones es un adaptador para RecyclerView que maneja y muestra una lista de predicciones de objetos (detecciones).
 */
public class AdapterPredicciones extends RecyclerView.Adapter<AdapterPredicciones.ViewHolder> {
    private Context context; // Contexto de la aplicación
    private LayoutInflater inflater; // Inflador de vistas
    private List<Detection> listD; // Lista de detecciones
    View view; // Vista actual

    /**
     * Constructor para AdapterPredicciones.
     *
     * @param context Contexto de la aplicación.
     * @param listD Lista de detecciones.
     */
    public AdapterPredicciones(Context context, List<Detection> listD) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.listD = listD;
    }

    /**
     * Infla la vista del elemento de la lista y crea una instancia de ViewHolder.
     *
     * @param parent El ViewGroup padre al que se añadirá la nueva vista.
     * @param viewType Tipo de vista del nuevo elemento.
     * @return Una nueva instancia de ViewHolder.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = inflater.inflate(R.layout.itemprediccioneslayout, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Vincula los datos del elemento en la posición especificada con el ViewHolder.
     *
     * @param holder ViewHolder que debe actualizarse con los datos del elemento en la posición dada.
     * @param position Posición del elemento dentro del conjunto de datos del adaptador.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(listD.get(position));
    }

    /**
     * Devuelve el tamaño del conjunto de datos.
     *
     * @return Tamaño del conjunto de datos.
     */
    @Override
    public int getItemCount() {
        return listD.size();
    }

    /**
     * ViewHolder proporciona una referencia a las vistas para cada elemento de la lista.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvObjeto, tvProbabilidad; // TextViews para mostrar el objeto y su probabilidad

        /**
         * Constructor para ViewHolder.
         *
         * @param itemView Vista del elemento de la lista.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvObjeto = itemView.findViewById(R.id.tvObjeto);
            tvProbabilidad = itemView.findViewById(R.id.tvProbabilidad);
        }

        /**
         * Vincula los datos de un elemento de detección con las vistas.
         *
         * @param item Elemento de detección.
         */
        void bindData(final Detection item) {
            int porcentaje = (int) (item.getPresicion() * 100);
            tvObjeto.setText(item.getClase());
            tvProbabilidad.setText(porcentaje + "%");
        }
    }
}
