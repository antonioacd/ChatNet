package com.example.chatacd.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chatacd.R;
import com.example.chatacd.adapter.RecyclerAdapterConver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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

    private Thread hilo1;
    private Thread hilo2;

    RecyclerView rV;
    RecyclerAdapterConver recAdapter;

    private String nombre;
    private String ip;

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
                enviarMensaje(hilo2);
            }
        });
        recibirMensaje(hilo1);

        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ActionBar ac = getSupportActionBar();
        if(ac != null){
            ac.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER){
            enviarMensaje(hilo2);
        }
        return true;
    }

    public void recibirMensaje(Thread hilo1){

        hilo1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket servidor = new ServerSocket(2023);
                    while(true){
                        Socket misocket = servidor.accept();
                        DataInputStream dis = new DataInputStream(misocket.getInputStream());

                        mensaje = dis.readUTF();
                        recAdapter.listaMensajes.add("2" + mensaje);

                        misocket.close();
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

    public void enviarMensaje(Thread hilo2){

        hilo2 = new Thread(new Runnable() {
            @Override
            public void run() {
                Scanner sc = new Scanner(System.in);
                try {
                    Socket misocket = new Socket(ip,2023);
                    DataOutputStream dos = new DataOutputStream(misocket.getOutputStream());
                    String mensaje = mensaje_env.getText().toString();

                    recAdapter.listaMensajes.add("1" +mensaje);
                    dos.writeUTF(mensaje);

                    dos.close();

                    misocket.close();

                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
        hilo2.start();

        recAdapter.notifyDataSetChanged();
        rV.scrollToPosition(recAdapter.listaMensajes.size()+1);
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}