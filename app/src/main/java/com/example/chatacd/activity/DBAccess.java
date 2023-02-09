package com.example.chatacd.activity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.Html;
import android.util.Log;

import com.example.chatacd.model.Contacto;

import java.util.ArrayList;


public class DBAccess extends SQLiteOpenHelper {

    //Todo 1. Extendemos la clase con SQLiteOpenHelper para tener acceso a los métodos que gestiona
    //todo-> la base de datos.

    //Database name
    private static final String DB_NAME = "ChatApp";

    //Table name
    private static final String DB_TABLE_NAME = "contactos";

    //Database version must be >= 1
    private static final int DB_VERSION = 1;

    //Columns
    private static final String IP_COLUMN = "ip";

    private static final String NOMBRE_COLUMN = "nombre";

    private static final String UMENSAJE_COLUMN = "mensaje";

    private static final String IMAGEN_COLUMN = "imagen";

    //Application Context
    private Context mContext;


    /**
     * Constructor de la base de datos, si no existe la base de datos la crea, sino se conecta.
     *  En el caso de que se hiciese una actualización y se cambiase la versión,
     *  el constructor llamaría al método onUpgrade para actualizar los cambios de la base de datos.
     * @param context Contexto de la aplicación
     */
    public DBAccess(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

        mContext = context;

    }

    //Todo 2. Sobrecargamos onCreate, encargado de crear las tablas asociadas a la base de datos.
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //Version 1
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " +DB_TABLE_NAME+ "(" +
                NOMBRE_COLUMN + " TEXT," +
                UMENSAJE_COLUMN + " TEXT," +
                IP_COLUMN + " TEXT PRIMARY KEY," +
                IMAGEN_COLUMN + " TEXT)";

        //Todo 2.1. Lanzamos la consulta con execSQL

        sqLiteDatabase.execSQL(CREATE_CONTACTS_TABLE);

       // Los mensajes LOG sirven para que el programación, durante el desarrollo pueda recibir mensajes
       // tecnicos sobre el funcionamiento del programa sin que el usuario las pueda ver.
       // Estos mensajes aparecen en la pestaña Logcat de Android studio.  
       Log("Tablas creadas");

    }

    // Todo 3. Sobrecargamos onUpgrade, encargado de actualizar la base de datos y las tablas asociadas.

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }

    public void getVersionDB(){
        Log(Integer.toString(this.getReadableDatabase().getVersion()));
    }

    //Todo 4. Creamos un método para insertar un dato en la BD.
    public long insert(String nombre, String mensaje, String ip, String imgPerfil){

        //Todo 4.1. Pedimos acceso de escritura en la base de datos.
        SQLiteDatabase db = this.getWritableDatabase();
        long result = -1;

        // Contenedor clave,valor -> columna, valor de entrada registro
        ContentValues values = new ContentValues();

        values.put(NOMBRE_COLUMN, nombre);
        values.put(UMENSAJE_COLUMN, mensaje);
        values.put(IP_COLUMN, ip);
        values.put(IMAGEN_COLUMN, imgPerfil);
        Log("Datos insertados");
        //Todo 4.2. Insertamos a través del método insert, cuyos parametro son:
        //todo -> nombre de la tabla
        //todo -> nullColumnHack permite indicar si hay una columna cuyo valor pueda ser nulo.
        //todo -> valores asociados a la inserción.

        result = db.insert(DB_TABLE_NAME,null,values);

        //Se cierra la conexión de la base de datos
        db.close();

        return result;

    }
/*
    public Usuario getDataUser(String usuario){

        SQLiteDatabase db = this.getReadableDatabase();
        Usuario u = null;
        //Todo 5.2. Realizamos la consulta a través del método 'query', cuyo significado de los
        // todo-> parámetros tenemos en los apuntes. Este método devuelve un cursor que nos
        // todo-> permite recorrer las tuplas del resultado.
        String[] cols = new String[]{NOMBRE_COLUMN ,UMENSAJE_COLUMN ,IP_COLUMN, };

        //Un cursor es un tipo de dato que se mueve entre los registros devueltos por una consulta de una base de datos.
        Cursor c = db.query(DB_TABLE_NAME,cols,IP_COLUMN +" ='"+usuario+"'",null,null,null,null);

        if(c.moveToFirst()) {

            u = new Usuario(c.getString(0), c.getString(1), c.getString(2), c.getString(3));

        }

        return u;

    }*/

    public int getUser(String ip, String contrasenia){

        int result = -1;

        SQLiteDatabase db = this.getReadableDatabase();

        String[] cols = new String[]{NOMBRE_COLUMN ,UMENSAJE_COLUMN ,IP_COLUMN, IMAGEN_COLUMN};

        //Un cursor es un tipo de dato que se mueve entre los registros devueltos por una consulta de una base de datos.
        Cursor c = db.query(DB_TABLE_NAME,cols,IP_COLUMN +" ='"+ip+"'",null,null,null,null);

        if(c.moveToFirst()) {

            String contraseniaCogida = c.getString(3);

            if (contraseniaCogida == null || contraseniaCogida.equals(null) || contraseniaCogida == "" || !contraseniaCogida.equals(contrasenia)){
                result  = 1;
            }

            if (contrasenia.equals(contraseniaCogida)){
                result = 0;
            }

        }

        if(c != null) {
            c.close();
        }

        db.close();

        return result;
        //-1 = no este registrado, 0 = bien, 1 = contraseña incorrecta

    }

        public ArrayList<Contacto> getAllUsers(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Contacto> listaContactos = new ArrayList<>();

        String[] cols = new String[]{NOMBRE_COLUMN ,UMENSAJE_COLUMN ,IP_COLUMN, IMAGEN_COLUMN};

        //Un cursor es un tipo de dato que se mueve entre los registros devueltos por una consulta de una base de datos.
        Cursor c = db.query(DB_TABLE_NAME,cols,null,null,null,null,null);

        if(c.moveToFirst()){

            do{

                Contacto cont = new Contacto(c.getString(0), c.getString(1), c.getString(2), c.getString(3));

                listaContactos.add(cont);

            }while(c.moveToNext());
        }

        return listaContactos;
    }

    public void Log(String msg){
        Log.d("DB", msg);
    }


}
