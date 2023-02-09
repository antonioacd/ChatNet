package com.example.chatacd.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.chatacd.R;
import com.example.chatacd.adapter.RecyclerAdapterConver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    EditText mensaje_env;
    Button btnEnviar;
    String conversacion;
    String mensaje;

    RecyclerView rV;
    RecyclerAdapterConver recAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rV = (RecyclerView) findViewById(R.id.rVMensajes);
        mensaje_env = (EditText) findViewById(R.id.txtMiTexto);
        btnEnviar = (Button) findViewById(R.id.btnEnviar);
        conversacion = "";
        mensaje = null;

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

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER){
            enviarMensaje();
        }
        return true;
    }

    public void recibirMensaje(){

        Thread hilo1 = new Thread(new Runnable() {
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

    public void enviarMensaje(){

        Thread hilo2 = new Thread(new Runnable() {
            @Override
            public void run() {
                Scanner sc = new Scanner(System.in);
                try {
                    Socket misocket = new Socket("localhost",2023);
                    DataOutputStream dos = new DataOutputStream(misocket.getOutputStream());
                    String mensaje = mensaje_env.getText().toString();

                    recAdapter.listaMensajes.add("1" +mensaje);
                    dos.writeUTF(mensaje);

                    mensaje_env.setText(" ");

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


}