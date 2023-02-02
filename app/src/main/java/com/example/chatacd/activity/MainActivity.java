package com.example.chatacd.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.chatacd.R;
import com.example.chatacd.adapter.RecyclerAdapter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    TextView mensajes;
    EditText mensaje_env;
    Button btnEnviar;
    String conversacion;
    String mensaje;

    RecyclerView rV;
    RecyclerAdapter recAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rV = (RecyclerView) findViewById(R.id.rVMensajes);
        mensaje_env = (EditText) findViewById(R.id.txtMiTexto);
        btnEnviar = (Button) findViewById(R.id.btnEnviar);
        conversacion = "";
        mensaje = "";

        recAdapter = new RecyclerAdapter(this);

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
                        rV.scrollToPosition(recAdapter.listaMensajes.size() - 1);

                        misocket.close();
                    }
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });

        hilo1.start();
        recAdapter.notifyDataSetChanged();
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

                    Log.d("Mensaje", "Entra");
                    dos.writeUTF(mensaje);

                    //conversacion = mensajes.getText() + " \n" + "Mensaje enviado: " + mensaje;

                    recAdapter.listaMensajes.add("1" +mensaje);
                    rV.scrollToPosition(recAdapter.listaMensajes.size() - 1);
                    //mensajes.setText(conversacion);

                    dos.close();

                    misocket.close();

                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
        hilo2.start();
        recAdapter.notifyDataSetChanged();
    };


}