package com.example.chatacd.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatacd.R;
import com.example.chatacd.adapter.RecyclerAdapterConver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * <h1>MainActivity</h1>
 *
 *  Esta clase sera la encargada de gestionar el envio y la recepcion
 *  de los mensajes en nuestros chats
 */

public class MainActivity extends AppCompatActivity {

    static private int PUERTO = 2023;
    static private String FOTO_PERFIL = "https://www.softzone.es/app/uploads-softzone.es/2018/04/guest.png";

    private TextView txtNombre, txtIp;
    private EditText eTmensajeEnv;
    private ImageView img;
    private Button btnEnviar;
    private Button btnAtras;
    private String mensaje;

    //Creamos el circulo de progreso para despues cargar la imagen usando glide
    private CircularProgressDrawable progressDrawable;

            //Declaracion de variables

    //Esta variable nos servira para abrir o cerrar los hilos
    boolean abierto;

    //Recycler View y el Recycler Adapter, donde iran los mensajes de nuestra conversación
    private RecyclerView recV;
    private RecyclerAdapterConver recA;

    //Nombre del contacto y su ip
    private String nombre;
    private String ip;

    //Socket del servidor, lo declaro aqui para poder cerrarlo en el onDestroy()
    ServerSocket servidor;

    /**
     * El onCreate sera el encargado de inicializar las variables
     * , asi como de implementar los listener
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inicializamos las variables de la vista
        recV = (RecyclerView) findViewById(R.id.rVMensajes);
        eTmensajeEnv = (EditText) findViewById(R.id.txtMiTexto);
        btnEnviar = (Button) findViewById(R.id.btnEnviar);
        btnAtras = (Button) findViewById(R.id.btnAtras);
        txtNombre  = (TextView) findViewById(R.id.txtNombreChat);
        txtIp  = (TextView) findViewById(R.id.txtIp);
        img = (ImageView) findViewById(R.id.imgPerfilInd);

        //El mensaje estara vacio hasta que se sobreescriba
        mensaje = null;

        //Cogemos el intent para recibir los datos de la conversacion
        Intent i = getIntent();

        //Inicializamos el nombre y la y ip con los dtos obtenidos
        nombre = i.getStringExtra("nombre");
        ip = i.getStringExtra("ip");

        //Ponemos el nomrbe y la ip en sus correspondientes campos
        txtNombre.setText(nombre);
        txtIp.setText(ip);

        //Asi como la foto utilizando la libreria glide
        progressDrawable = new CircularProgressDrawable(this);
        progressDrawable.setStrokeWidth(10f);
        progressDrawable.setStyle(CircularProgressDrawable.LARGE);
        progressDrawable.setCenterRadius(30f);
        progressDrawable.start();

        Glide.with(this)
                .load(FOTO_PERFIL)
                .placeholder(progressDrawable)
                .error(R.mipmap.ic_launcher)
                .into(img);

        //Inicializamos el RecyclerAdapter
        recA = new RecyclerAdapterConver(this);

        //Creamos un LinearLayout para establecer el Layout del recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recV.setLayoutManager(layoutManager);

        //Implementamos el recyclerAdapter en el recyclerView
        recV.setAdapter(recA);

        //Llamamos al metodo recibirMensaje(), que abrira el hilo para recibir mensajes
        recibirMensaje();

        //Creamos un listener del boton enviar para que cuando se pulse, llame al metodo enviarMensaje()
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarMensaje();
            }
        });

        //El boton atras volvera a la anterior actividad
        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //Establecemos el boton enviar como invisible para que no se puedan enviar mensajes vacios
        btnEnviar.setVisibility(View.GONE);

        //Implementando un listener al EditText, podemos controlar diferentes acciones, como por ejemplo
        // lo que ocurre antes de escribir, despues y durante, asi que cuando se comienze a escribir,
        // aparecera el boton de enviar
        eTmensajeEnv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().isEmpty()) {
                    btnEnviar.setVisibility(View.GONE);
                } else {
                    btnEnviar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    /**
     * <h1>recibirMensaje</h1>
     *
     * Este metodo abre un hilo, que se queda en un bucle infinito para recibir
     * los mensajes
     */

    //Metodo para abrir el servidor y asi poder recibir mensajes
    public void recibirMensaje(){

        //Establecemos la variable abierto, mencionada anteriormente para ejecutar el bucle
        abierto = true;

        //Creamos el hilo
        Thread hilo1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Asignamos un puerto al servidor
                    servidor = new ServerSocket(PUERTO);
                    //Mientras que este abierto, recibira informacion
                    while(abierto){
                        //Creamos el socket que acepta la conexion con el servidor
                        Socket serverSocket = servidor.accept();
                        //Creamos un DataInputStream que se conecta al flujo de entrada del socket
                        DataInputStream dis = new DataInputStream(serverSocket.getInputStream());
                        //Leemos el mensaje desde el flujo de entrada
                        mensaje = dis.readUTF();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Llamamos al metodo insertarItem, para añadir nuestro mensaje al RecyclerView
                                // lo realizamos dentro de runOnUIThread para poder tocar el RecyclerView ya que
                                // no lo podemos realizar desde hilos secundarios

                                //El "2",  nos servira para identificar si el mensaje esta siendo enviado o recibido, y asi colocarlo
                                // en un TextView o en otro
                                recA.insertarItem("2" + mensaje);

                                //Subimos los mensajes hasta la ultima posicion
                                recV.scrollToPosition(recA.listaMensajes.size()-1);
                            }
                        });

                        //Cerramos el socket del serveidor
                        serverSocket.close();
                    }

                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });

        //Iniciamos el hilo
        hilo1.start();
        //Notificamos al Recycler Adapter que se han realizado cambios
        recA.notifyDataSetChanged();

    }
    /**
     * <h1>enviarMensaje</h1>
     *
     * Metodo para enviar mensajes al servidor, el cual abre un hilo por cada mensaje que se envia,
     * pasandole la ip obtenida de la actividad anterior
     */
    //Metodo para enviar mensajes al servidor
    public void enviarMensaje(){

        //Creamos el hilo
        Thread hilo2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Creamos el socket y lo inicializamos dandole una Ip y un puerto
                    Socket socketCli = new Socket(ip, PUERTO);
                    //Crea un DataOutputStream que sera usado para escribir en el socket
                    DataOutputStream dos = new DataOutputStream(socketCli.getOutputStream());
                    //Cogemos el mensaje de la vista
                    String mensaje = eTmensajeEnv.getText().toString();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Llamamos al metodo insertarItem, para añadir nuestro mensaje al RecyclerView
                            // lo realizamos dentro de runOnUIThread para poder tocar el RecyclerView ya que
                            // no lo podemos realizar desde hilos secundarios

                            //El "1",  nos servira para identificar si el mensaje esta siendo enviado o recibido, y asi colocarlo
                            // en un TextView o en otro
                            recA.insertarItem("1" + mensaje);
                            eTmensajeEnv.setText("");

                            //Subimos los mensajes hasta la ultima posicion
                            recV.scrollToPosition(recA.listaMensajes.size()-1);
                        }
                    });

                    //Escribimos el mensaje
                    dos.writeUTF(mensaje);
                    //Cerramos el DataOutputStream
                    dos.close();
                    //Cerramos el socket
                    socketCli.close();

                } catch (UnknownHostException ex) {
                    //En el caso de que el socket no establezca conexion, porque la ip o el puerto no sean validos
                    // nos mostrara un toast de que la ip no es valida
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {Toast.makeText(getApplicationContext(), "Direccion Ip no valida", Toast.LENGTH_SHORT).show();}
                    });

                } catch (IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {Toast.makeText(getApplicationContext(), "Direccion Ip no valida", Toast.LENGTH_SHORT).show();}
                    });
                }
            }
        });
        hilo2.start();
        //Subimos los mensajes hasta la ultima posicion
        recV.scrollToPosition(recA.listaMensajes.size()+1);
    };

    /**
     * <h1>onDestroy</h1>
     *
     * El metodo onDestroy permite que cuando se elimine la actividad
     * se cierre el socket del servidor en caso de que no se haya cerrado
     * para evitar posibles errores
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Cuando se cierre la actividad la variable que controla el bucle del socket del servidor
        // el cual recibe los mensajes, se pondra en false, para que se pare
        abierto = false;
        //Y si el servidor esta activo o es diferente a null se cerrara
        try {
            if (servidor != null && !servidor.isClosed()) {
                servidor.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * <h1>onResume</h1>
     *
     * Cuando se vuelva a la actividad, volvera a estar activada,
     * realmente no hace falta ya que se vuelve a pasar por el onCreate, ya que se cierra
     * y luego se abre pero he decidido ponerlo por posibles fallos
     */

    @Override
    protected void onResume() {
        super.onResume();
        if (!abierto) {
            abierto = true;
        }
    }

}