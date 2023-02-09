package com.example.chatacd.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.example.chatacd.R;
import com.example.chatacd.adapter.RecyclerAdapterChats;
import com.example.chatacd.adapter.RecyclerAdapterConver;
import com.example.chatacd.model.Contacto;

public class ChatsActivity extends AppCompatActivity {

    private RecyclerView rVChats;

    private RecyclerAdapterChats recAdapterChat;

    private DBAccess dbController;

    private Button btnAgregar;

    private CircularProgressDrawable progressDrawable;

    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        btnAgregar = (Button) findViewById(R.id.btnAgregar);

        rVChats = (RecyclerView) findViewById(R.id.rvChats);

        recAdapterChat = new RecyclerAdapterChats(this);

        //Creamos un LinearLayout para establecer el Layout del recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(ChatsActivity.this);
        rVChats.setLayoutManager(layoutManager);

        //Implementamos el recyclerAdapter en el recyclerView
        rVChats.setAdapter(recAdapterChat);

        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    public void agregarContacto(){

        AlertDialog.Builder ventana = new AlertDialog.Builder(this);

        ventana.setTitle("Agregar Contacto");

        View v = getLayoutInflater().inflate(R.layout.layout_agregarcontacto, null);

        EditText eNombre = v.findViewById(R.id.etNombre);
        EditText eIp = v.findViewById(R.id.etIp);
        Button aceptar = v.findViewById(R.id.btnConfirmar);
        Button cancelar = v.findViewById(R.id.btnCancelar);

        Context context = this;

        progressDrawable = new CircularProgressDrawable(this);
        progressDrawable.setStrokeWidth(10f);
        progressDrawable.setStyle(CircularProgressDrawable.LARGE);
        progressDrawable.setCenterRadius(30f);
        progressDrawable.start();

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Contacto c = new Contacto(eIp.getText().toString(), eNombre.getText().toString(), null, "https://www.softzone.es/app/uploads-softzone.es/2018/04/guest.png");

                recAdapterChat.listaChats.add(c);

                recAdapterChat.notifyDataSetChanged();

                dbController.insert(c.getNombre(), c.getUltimoMensaje(), c.getIp(), c.getImg());

                dialog.dismiss();
            }
        });

        ventana.setView(v);

        dialog = ventana.create();

        dialog.show();
    }


}
