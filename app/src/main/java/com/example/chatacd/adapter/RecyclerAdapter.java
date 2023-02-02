package com.example.chatacd.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatacd.R;

import java.util.ArrayList;


/**
 *
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerHolder>{

    public ArrayList<String> listaMensajes;

    Context contexto;

    //Constructor de RecyclerAdapter
    public RecyclerAdapter(Context contexto) {
        this.contexto = contexto;
        listaMensajes = new ArrayList<String>();
    }

    @Override
    public int getItemViewType(int position) {
        String mensaje = listaMensajes.get(position);

        if (mensaje.substring(0,1).equals("1")) {
            return 1;
        } else {
            return 2;
        }
    }

    //Creamos la vista de nuestro RecyclerAdapter
    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerHolder recyclerHolder;

        if (viewType == 1) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_mensaje,parent, false);
            recyclerHolder = new RecyclerHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_mensaje2,parent, false);
            recyclerHolder = new RecyclerHolder(view);
        }

        return recyclerHolder;
    }

    //Introducimos los datos en el RecyclerAdapter
    @Override
    public void onBindViewHolder(@NonNull RecyclerHolder holder, int position) {

        String mensaje = listaMensajes.get(position);

        int viewType = getItemViewType(position);
        if (viewType == 1) {
            holder.mensajeEnviado.setText(mensaje.substring(1));
        } else {
            holder.mensajeRecibido.setText(mensaje.substring(1));
        }
    }

    @Override
    public int getItemCount() {
        return listaMensajes.size();
    }

    //Asignamos los elementos de nustro recycled holder a variables creadas
    public class RecyclerHolder extends RecyclerView.ViewHolder {

        TextView mensajeEnviado;
        TextView mensajeRecibido;

        public RecyclerHolder(@NonNull View itemView) {
            super(itemView);

            mensajeEnviado  = (TextView) itemView.findViewById(R.id.mensajeYo);
            mensajeRecibido  = (TextView) itemView.findViewById(R.id.mensajeEl);

        }
    }

}
