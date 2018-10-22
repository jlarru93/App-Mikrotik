package com.kael.dao;

import android.content.Context;

import com.kael.bean.Servidor;
import com.kael.sqlite.SQLServidor;

import java.util.List;

/**
 * Created by kael on 29/08/2016.
 */
public class DAOServidor {
    private SQLServidor db;
    private Context context;

    public DAOServidor(Context context){
        this.context = context;
        db = new SQLServidor(context);
    }

    public boolean registrarServidor(Servidor servidor){
        return db.registrarServidor(servidor);
    }

    public boolean eliminarServidor(int codigo){
        return db.eliminarServidor(codigo);
    }

    public List<Servidor> getServers(){
        return db.getServers();
    }

}
