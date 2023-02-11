package com.example.chatacd.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatacd.R;
import com.example.chatacd.model.Contacto;

import java.util.ArrayList;


/**
 *
 */

public class RecyclerAdapterConver extends RecyclerView.Adapter<RecyclerAdapterConver.RecyclerHolder>{

    public ArrayList<String> listaMensajes;

    Context contexto;

    //Constructor de RecyclerAdapter
    public RecyclerAdapterConver(Context contexto) {
        this.contexto = contexto;
        listaMensajes = new ArrayList<String>();
    }

    //Creamos la vista de nuestro RecyclerAdapter
    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerHolder recyclerHolder;

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_mensaje,parent, false);
        recyclerHolder = new RecyclerHolder(view);

        return recyclerHolder;
    }

    //Metodo para añadir un Item a la lista y al recyclerAdapter
    public void insertarItem(String s){
        listaMensajes.add(s);
        this.notifyDataSetChanged();
    }

    //Introducimos los datos en el RecyclerAdapter
    @Override
    public void onBindViewHolder(@NonNull RecyclerHolder holder, int position) {

        String mensaje = listaMensajes.get(position);

        if (mensaje.substring(0,1).equals("1")) {
            holder.mensajeEnviado.setText(mensaje.substring(1));
            holder.mensajeRecibido.setVisibility(View.GONE);
        } else {
            holder.mensajeRecibido.setText(mensaje.substring(1));
            holder.mensajeEnviado.setVisibility(View.GONE);
        }

        holder.setIsRecyclable(false);
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
