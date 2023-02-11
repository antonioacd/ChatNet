package com.example.chatacd.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.content.Intent;
import android.os.Bundle;
import android.os.Trace;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
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
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    TextView txtNombre, txtIp;
    EditText mensaje_env;
    ImageView img;
    Button btnEnviar;
    Button btnAtras;
    String conversacion;
    String mensaje;

    private CircularProgressDrawable progressDrawable;

    boolean encendido;

    RecyclerView rV;
    RecyclerAdapterConver recAdapter;

    private String nombre;
    private String ip;
    private int puerto;
    ServerSocket servidor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rV = (RecyclerView) findViewById(R.id.rVMensajes);
        mensaje_env = (EditText) findViewById(R.id.txtMiTexto);
        btnEnviar = (Button) findViewById(R.id.btnEnviar);
        btnAtras = (Button) findViewById(R.id.btnAtras);
        txtNombre  = (TextView) findViewById(R.id.txtNombreChat);
        txtIp  = (TextView) findViewById(R.id.txtIp);
        img = (ImageView) findViewById(R.id.imgPerfilInd);

        conversacion = "";
        mensaje = null;
        puerto = 2023;
        Intent i = getIntent();

        nombre = i.getStringExtra("nombre");
        ip = i.getStringExtra("ip");

        txtNombre.setText(nombre);
        txtIp.setText(ip);

        progressDrawable = new CircularProgressDrawable(this);
        progressDrawable.setStrokeWidth(10f);
        progressDrawable.setStyle(CircularProgressDrawable.LARGE);
        progressDrawable.setCenterRadius(30f);
        progressDrawable.start();

        Glide.with(this)
                .load("https://www.softzone.es/app/uploads-softzone.es/2018/04/guest.png")
                .placeholder(progressDrawable)
                .error(R.mipmap.ic_launcher)
                .into(img);

        recAdapter = new RecyclerAdapterConver(this);

        //Creamos un LinearLayout para establecer el Layout del recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        rV.setLayoutManager(layoutManager);

        //Implementamos el recyclerAdapter en el recyclerView
        rV.setAdapter(recAdapter);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarMensaje();
            }
        });

        recibirMensaje();

        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });

        btnEnviar.setVisibility(View.GONE);

        mensaje_env.addTextChangedListener(new TextWatcher() {
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

    public void abridSocket(){
        encendido = true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER){
            enviarMensaje();
        }
        return true;
    }

    public void recibirMensaje(){

        encendido = true;

        Thread hilo1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    servidor = new ServerSocket(puerto);
                    while(encendido){
                        Socket serverSocket = servidor.accept();
                        DataInputStream dis = new DataInputStream(serverSocket.getInputStream());

                        mensaje = dis.readUTF();
                        Log.d("Mensajeee", "-" + mensaje + "-");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recAdapter.insertarItem("2" + mensaje);
                            }
                        });

                        serverSocket.close();

                    }

                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });

        hilo1.start();
        recAdapter.notifyDataSetChanged();
        rV.scrollToPosition(recAdapter.listaMensajes.size()+1);
    }

    public void enviarMensaje(){

        Thread hilo2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("prueba", "0");
                    Socket socketCli = new Socket(ip, puerto);
                    Log.d("prueba","1" + socketCli.toString());
                    DataOutputStream dos = new DataOutputStream(socketCli.getOutputStream());

                    Log.d("prueba","2" + dos.toString());
                    String mensaje = mensaje_env.getText().toString();
                    Log.d("prueba","3" + mensaje);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recAdapter.insertarItem("1" + mensaje);
                        }
                    });

                    dos.writeUTF(mensaje);
                    dos.close();
                    socketCli.close();

                } catch (UnknownHostException ex) {
                    Log.d("Prueba", "Fallo" + ex.getMessage());
                } catch (IOException ex) {
                    Log.d("Prueba", "Fallo" + ex.getMessage());
                }
            }
        });
        hilo2.start();
        rV.scrollToPosition(recAdapter.listaMensajes.size()+1);

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        encendido = false;
        try {
            if (servidor != null && !servidor.isClosed()) {
                servidor.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!encendido) {
            abridSocket();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}