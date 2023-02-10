package com.example.chatacd.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.example.chatacd.R;
import com.example.chatacd.adapter.RecyclerAdapterChats;
import com.example.chatacd.model.Contacto;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ChatsActivity extends AppCompatActivity {

    private RecyclerView rVChats;

    private RecyclerAdapterChats recAdapterChat;

    private DBAccess dbController;

    private Button btnAgregar;

    private CircularProgressDrawable progressDrawable;

    AlertDialog dialog;

    int indice;

    ArrayList<Contacto> listaTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        ArrayList<Contacto> listaChats;

        btnAgregar = (Button) findViewById(R.id.btnAgregar);

        rVChats = (RecyclerView) findViewById(R.id.rvChats);

        recAdapterChat = new RecyclerAdapterChats(this);

        //Creamos un LinearLayout para establecer el Layout del recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(ChatsActivity.this);
        rVChats.setLayoutManager(layoutManager);

        //Implementamos el recyclerAdapter en el recyclerView
        rVChats.setAdapter(recAdapterChat);

        dbController = new DBAccess(this);

        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                agregarContacto();

            }
        });

        //Cargamos los contactos
        cargarContactos();

        recAdapterChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Capturamos el indice del elemento seleccionado
                indice = rVChats.getChildAdapterPosition(view);

                Log.d("Pulsado", ""+indice);

                //Iniciamos la nueva actividad, que seria la vista maestra del elemento
                Intent i = new Intent(ChatsActivity.this, MainActivity.class);
                //Introducimos comoo srting extra el id de elemento seleccionado, para mas tarde
                //en esta clase de vista maestra poder realizar una consulta a la appi sobre
                //este mismo elemento y no tener que cargar todos de nuevo
                i.putExtra("nombre", recAdapterChat.listaChats.get(indice).getNombre());
                i.putExtra("ip", recAdapterChat.listaChats.get(indice).getIp());
                startActivity(i);

                //Indicamos que se ha seleccionado un elemento de la vista
                view.setSelected(true);
            }
        });

    }

    public void cargarContactos(){

        recAdapterChat.listaChats = dbController.getAllContacts();
        recAdapterChat.notifyDataSetChanged();
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

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Contacto c = new Contacto(eIp.getText().toString(), eNombre.getText().toString(), "Hola", "https://www.softzone.es/app/uploads-softzone.es/2018/04/guest.png");

                //Llamamos al metodo insert para añadir el usuario a la base de datos
                if(dbController.insert(c.getNombre(), c.getUltimoMensaje(), c.getIp(), c.getImg()) != -1){

                    recAdapterChat.insertarItem(c);

                    recAdapterChat.notifyDataSetChanged();

                    dbController.insert(c.getNombre(), c.getUltimoMensaje(), c.getIp(), c.getImg());

                }else{
                    Toast.makeText(getApplicationContext(), "Esa id ya esta registrada", Toast.LENGTH_LONG).show();
                }

                dialog.dismiss();
            }
        });

        ventana.setView(v);

        dialog = ventana.create();

        dialog.show();
    }


}
