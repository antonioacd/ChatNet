package com.example.chatacd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatacd.R;
import com.example.chatacd.adapter.RecyclerAdapterChats;
import com.example.chatacd.model.Contacto;

import java.util.ArrayList;


/**
 * <h1>ChatsActivity</h1>
 *
 *  Esta clase sera la pantalla de chats,
 *  Donde se podran agregar contactos y al pulsar un contacto nos introducira en la conversacion
 */

public class ChatsActivity extends AppCompatActivity {

    /**
     * Declaramos las variables
     */

    private RecyclerView rVChats;

    private RecyclerAdapterChats recAdapterChat;

    private DBAccess dbController;

    private Button btnAgregar;

    AlertDialog dialog;

    int indice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        /**
         * Inicializamos los componentes de la interfaz
         */
        btnAgregar = (Button) findViewById(R.id.btnAgregar);
        rVChats = (RecyclerView) findViewById(R.id.rvChats);

        /**
         * Inicializamos el recicler adapter de los chats
         */
        recAdapterChat = new RecyclerAdapterChats(this);

        /**
         * Creamos un LinearLayout para establecer el Layout del recyclerView
         */
        LinearLayoutManager layoutManager = new LinearLayoutManager(ChatsActivity.this);
        rVChats.setLayoutManager(layoutManager);

        /**
         * Implementamos el recyclerAdapter de los chats en el recyclerView
         */
        rVChats.setAdapter(recAdapterChat);

        /**
         * Inicializamos la variable que da acceso a los metodos de la base de datos
         */
        dbController = new DBAccess(this);

        /**
         * Implementamos el listener del boton Agregar para añadir un contacto nuevo
         */
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarContacto();
            }
        });

        /**
         * Llamamos al metodo cargarContactos que medante un metodo de la clase DBAcces
         * obtendra los datos de los conctactos
         */
        cargarContactos();

        /**
         * Implmentamos un listener para el recicler adapter, de manera que
         * cuando se pulse, se enviara a otra actividad, que sera el chat
         * individual del contaco
         */

        recAdapterChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Capturamos el indice del elemento seleccionado
                indice = rVChats.getChildAdapterPosition(view);

                Intent i = new Intent(ChatsActivity.this, MainActivity.class);
                i.putExtra("nombre", recAdapterChat.listaChats.get(indice).getNombre());
                i.putExtra("ip", recAdapterChat.listaChats.get(indice).getIp());
                startActivity(i);

                //Indicamos que se ha seleccionado un elemento de la vista
                view.setSelected(true);
            }
        });

    }

    /**
     * <h1>cargarContactos</h1>
     *
     * Este metodo actualiza la lista de los chats del recicler adapter
     * obteniendo los datos de los contactos
     */
    public void cargarContactos(){
        recAdapterChat.listaChats = dbController.getAllContacts();
        recAdapterChat.notifyDataSetChanged();
    }

    /**
     * <h1>agregarContacto</h1>
     *
     * Este metodo crea un AlertDialog personalizado en el cual podremos introducir un nombre
     * y su ip para enviar los mensajes a esa ip
     */
    public void agregarContacto(){

        AlertDialog.Builder ventana = new AlertDialog.Builder(this);

        ventana.setTitle("Agregar Contacto");

        View v = getLayoutInflater().inflate(R.layout.layout_agregarcontacto, null);

        EditText eNombre = v.findViewById(R.id.etNombre);
        EditText eIp = v.findViewById(R.id.etIp);
        Button aceptar = v.findViewById(R.id.btnConfirmar);
        Button cancelar = v.findViewById(R.id.btnCancelar);

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Comprobamos que los campos se hayan rellenado correctamente
                if (eIp.getText().toString().isEmpty() || eNombre.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Debe rellenar todos los campos", Toast.LENGTH_LONG).show();
                }else{

                    Contacto c = new Contacto(eIp.getText().toString(), eNombre.getText().toString(), "Hola", "https://www.softzone.es/app/uploads-softzone.es/2018/04/guest.png");

                    //Llamamos al metodo insert para añadir el usuario a la base de datos
                    if(dbController.insert(c.getNombre(), c.getUltimoMensaje(), c.getIp(), c.getImg()) != -1){

                        recAdapterChat.listaChats.add(c);

                        recAdapterChat.notifyDataSetChanged();

                        dbController.insert(c.getNombre(), c.getUltimoMensaje(), c.getIp(), c.getImg());

                    }else{
                        Toast.makeText(getApplicationContext(), "Esa id ya esta registrada", Toast.LENGTH_SHORT).show();
                    }

                    dialog.dismiss();
                }
            }
        });

        /**
         * Si pulsa el boton cancelar se cerrara el alert dialog
         */
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        ventana.setView(v);

        dialog = ventana.create();

        dialog.show();
    }

}
