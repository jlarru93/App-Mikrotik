package com.kael.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kael.bean.Servidor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kael on 29/08/2016.
 */
public class SQLServidor extends SQLiteOpenHelper {

    public SQLServidor(Context context) {
        super(context, DataBase.DATABASE_NAME, null, DataBase.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        DataBase.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DataBase.onUpgrade(db, oldVersion, newVersion);
    }

    public boolean registrarServidor(Servidor servidor){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();

        values.put(DataBase.servidor.KEY_SERVERNAME, servidor.getNameServer());
        values.put(DataBase.servidor.KEY_HOST, servidor.getHost());
        values.put(DataBase.servidor.KEY_PORT, new Integer(servidor.getPort()));
        values.put(DataBase.servidor.KEY_USER, servidor.getUser());
        values.put(DataBase.servidor.KEY_PASSWORD, servidor.getPassword());
        values.put(DataBase.servidor.KEY_SSLCONNECTION, new Boolean(servidor.isSSLConnection()));
        values.put(DataBase.servidor.KEY_SSLCERTIFICATE, new Boolean(servidor.isSSLCertificate()));

        boolean registrado=false;

        registrado = db.insert(DataBase.servidor.TABLE_NAME, null, values)>0;
        db.close();
        return registrado;
    }

    public boolean eliminarServidor(int codigo){
        SQLiteDatabase db=this.getWritableDatabase();
        String where=DataBase.servidor.KEY_COD+"=?";
        String[] argumentos= {Integer.toString(codigo)};

        boolean eliminado=false;

        eliminado=db.delete(DataBase.servidor.TABLE_NAME, where, argumentos)>0;

        db.close();

        return eliminado;
    }


    public List<Servidor> getServers(){
        SQLiteDatabase db=this.getWritableDatabase();
        List<Servidor> list=new ArrayList<Servidor>();

        String[] campos= {
                DataBase.servidor.KEY_COD,
                DataBase.servidor.KEY_SERVERNAME,
                DataBase.servidor.KEY_HOST,
                DataBase.servidor.KEY_PORT,
                DataBase.servidor.KEY_USER,
                DataBase.servidor.KEY_PASSWORD,
                DataBase.servidor.KEY_SSLCONNECTION,
                DataBase.servidor.KEY_SSLCERTIFICATE
        };

        String where=null;//codservidor=?
        String[] argumentos=null;//reemplaza los parametros ?

        Cursor cursor=db.query(DataBase.servidor.TABLE_NAME, campos, where, argumentos,null, null, null);

        //Nos aseguramos de que existe al menos un registro
        if(cursor.moveToFirst()) {
            Servidor obj=null;
            //Recorremos el cursor hasta que no haya más registros
            do {
                obj=new Servidor();
                obj.setCodServer(cursor.getInt(0));
                obj.setNameServer(cursor.getString(1));
                obj.setHost(cursor.getString(2));
                obj.setPort(cursor.getInt(3));
                obj.setUser(cursor.getString(4));
                obj.setPassword(cursor.getString(5));
                obj.setSSLConnection(cursor.getString(6).equals("1"));
                obj.setSSLCertificate(cursor.getString(7).equals("1"));

                list.add(obj);
            } while (cursor.moveToNext());
        }
        db.close();

        return list;
    }

}
