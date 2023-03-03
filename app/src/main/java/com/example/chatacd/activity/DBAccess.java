package com.example.chatacd.activity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.chatacd.model.Contacto;

import java.io.PushbackReader;
import java.util.ArrayList;


/**
 * <h1>DBAccess</h1>
 *
 *  Esta clase sera la encargada de crear la base de datos,
 *  y contendra los metodos para manipularla
 */

public class DBAccess extends SQLiteOpenHelper {

    //Todo 1. Extendemos la clase con SQLiteOpenHelper para tener acceso a los métodos que gestiona
    //todo-> la base de datos.

    //Nombre de la base de datos
    private static final String DB_NAME = "ChatApp";

    //Nombre de la tabla
    private static final String DB_TABLE_NAME = "contactos";

    //Version de la base de datos
    private static final int DB_VERSION = 1;

    //Columnas de la tabla
    private static final String IP_COLUMN = "ip";

    private static final String NOMBRE_COLUMN = "nombre";

    private static final String UMENSAJE_COLUMN = "mensaje";

    private static final String IMAGEN_COLUMN = "imagen";

    private Context mContext;

    public DBAccess(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

        mContext = context;
    }

    /**
     * El onCreate es el encargado de crear las tablas de la base de datos
     * @param sqLiteDatabase
     */

    //Todo 2. Sobrecargamos onCreate, encargado de crear las tablas asociadas a la base de datos.
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //Creamos la tabla
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " +DB_TABLE_NAME+ "(" +
                NOMBRE_COLUMN + " TEXT," +
                UMENSAJE_COLUMN + " TEXT," +
                IP_COLUMN + " TEXT PRIMARY KEY," +
                IMAGEN_COLUMN + " TEXT)";

        //Ejecutamos la consulta
        sqLiteDatabase.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }

    /**
     * <h1>insert</h1>
     *
     * Método para insertar un dato en la base de datos
     * @param nombre    Sera el nombre del contacto
     * @param mensaje   Seria el ultimo mensaje del contacto el cual siempre sera el mismo
     * @param ip        Numero de ip del contacto
     * @param imgPerfil Url de la imagen de perfil del usuario la cual sera siempre la misma
     * @return          Devuelve un numero entero el cual indicara si la consulta se ha realizado
     *                  o si no ha sido posible
     */

    public long insert(String nombre, String mensaje, String ip, String imgPerfil){

        //Pedimos acceso a la base de datos
        SQLiteDatabase db = this.getWritableDatabase();
        long result = -1;

        ContentValues values = new ContentValues();

        //Introducimos los valores mediante clave valor en el contenedor creado en la linea anterior
        values.put(NOMBRE_COLUMN, nombre);
        values.put(UMENSAJE_COLUMN, mensaje);
        values.put(IP_COLUMN, ip);
        values.put(IMAGEN_COLUMN, imgPerfil);

        //Recogemos el resultado de la operacion
        result = db.insert(DB_TABLE_NAME,null,values);

        //Se cierra la conexión de la base de datos
        db.close();

        return result;
    }


    /**
     * <h1>getAllContacts</h1>
     *
     * Este metodo se encarga de obtener los datos de cada contacto,
     * crearlo e introducirlo en una lista
     *
     * @return Lista con todos los contactos
     */

    public ArrayList<Contacto> getAllContacts(){

        //Pedimos acceso a la base de datos
        SQLiteDatabase db = this.getReadableDatabase();
        //Creamos la lista de contactos y la inicializamos como vacia
        ArrayList<Contacto> listaContactos = new ArrayList<>();

        //Creamos una array de string para introducirlo al cursor y saber que datos obtener
        String[] cols = new String[]{NOMBRE_COLUMN ,UMENSAJE_COLUMN ,IP_COLUMN, IMAGEN_COLUMN};

        //Creamos el cursor que recorrera la tabla
        Cursor c = db.query(DB_TABLE_NAME,cols,null,null,null,null,null);

        if(c.moveToFirst()){
            do{
                Contacto cont = new Contacto(c.getString(2), c.getString(0), c.getString(1), c.getString(3));
                listaContactos.add(cont);
            }while(c.moveToNext());
        }

        return listaContactos;
    }

}
