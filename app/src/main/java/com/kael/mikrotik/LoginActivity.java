package com.kael.mikrotik;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.kael.bean.MessageDialog;
import com.kael.bean.Profile;
import com.kael.bean.Servidor;
import com.kael.dao.DAOServidor;
import com.kael.dao.PPP;
import com.kael.dao.System;
import com.kael.datos.ConexionMikrotik;
import com.kael.util.ConectividadRed;

import java.util.ArrayList;
import java.util.List;

import me.legrange.mikrotik.ApiConnection;
import me.legrange.mikrotik.ApiConnectionException;
import me.legrange.mikrotik.MikrotikApiException;

/**
 * Created by kael on 20/08/2016.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private Spinner spNodos;
    private Button btnConectarse;
    private Button btnServidores;
    private Context context;
    private MessageDialog dialog;

    private DAOServidor daoServidor;

    private Servidor[] serverList;

    private ArrayAdapter<String>  cleanAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        context = this;

        dialog = new MessageDialog(context);

        findControls();
        initialize();
        addListeners();
    }

    private void findControls(){
        spNodos = (Spinner) findViewById(R.id.spNodos);
        btnConectarse = (Button) findViewById(R.id.btnConectarse);
        btnServidores = (Button) findViewById(R.id.btnServidores);
    }

    private void initialize(){
        daoServidor = new DAOServidor(this);
        String[] cleanArraySpinner = {"Sin servidores"};
        cleanAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, cleanArraySpinner);
    }


    private void cargarSpinner(){
        List<Servidor> servidores = daoServidor.getServers();

        if(servidores!=null && servidores.size()>0){
            serverList =  serverListToArray(servidores);

            if(serverList!=null && serverList.length>0){
                ArrayAdapter r = new ArrayAdapter(this ,android.R.layout.simple_expandable_list_item_1, serverList);
                spNodos.setAdapter(r);
            }
        }

    }



    private void limpiarSpinner(){
        serverList = null;
        spNodos.setAdapter(cleanAdapter);
    }


    private Servidor[] serverListToArray(List<Servidor> list){
        Servidor[] array = null;

        if(list!=null && list.size()>0){
            array = new Servidor[list.size()];
            int i = 0;

            for(Servidor servidor : list){
                array[i] = servidor;
                i++;
            }
        }

        return array;
    }


    private void addListeners(){
        btnConectarse.setOnClickListener(this);
        btnServidores.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if( v == btnConectarse){

            if(serverList !=null && serverList.length>0){
                ConectividadRed red = new ConectividadRed(this);

                boolean connectedWifi = red.isConnectedToWifi();
                boolean connectedRedMobile = red.isConnectedToRedMobile();

                if(connectedWifi || connectedRedMobile){
                    Servidor servidor = serverList[spNodos.getSelectedItemPosition()];
                    MikrotikConnection task = new MikrotikConnection(servidor);
                    task.execute();
                }else{
                    String msg = "Tu dispositivo no tiene conexion a Internet.\n\n Habilite la red Wifi o de datos";

                    MessageDialog dialog = new MessageDialog(this);
                    dialog.showAlertDialog("Conexion a Internet", msg, false);
                }
            }else{
                MessageDialog dialog = new MessageDialog(this);
                dialog.showAlertDialog("No hay servidores", "Debe tener por lo menos un servidor registrado", false);
            }

        }
        else if( v == btnServidores){
            Intent intent = new Intent(this, ServidorActivity.class);
            startActivity(intent);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        limpiarSpinner();
        cargarSpinner();
    }

    private class MikrotikConnection extends AsyncTask<Void, Void, ApiConnection>{
        private Servidor servidor;
        private String mensajeError;
        private String uptimeMikrotik;
        private List<Profile> profiles;

        public MikrotikConnection(Servidor servidor){
            this.servidor = servidor;
        }


        @Override
        protected void onPreExecute() {
            dialog.showProgressBar("Conectando", "Estableciendo conexion...");
        }

        @Override
        protected ApiConnection doInBackground(Void... params) {
            ApiConnection cn = null;
            try {
                cn = ConexionMikrotik.getInstance().getConnection(servidor);
                System system = new System();
                uptimeMikrotik = system.getUptime(cn);
                PPP ppp = new PPP();
                profiles= ppp.getProfiles(cn);
            }
            catch (ApiConnectionException e){
                mensajeError = "No se pudo establecer la conexion al servidor";
                e.printStackTrace();
            }
            catch (MikrotikApiException e) {
                mensajeError = e.getMessage();
                e.printStackTrace();
            }
            return cn;
        }

        @Override
        protected void onPostExecute(ApiConnection cn) {
            dialog.closeProgressBar();
            if(cn !=null){
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("servidor", servidor);
                intent.putExtra("uptime", uptimeMikrotik);
                intent.putParcelableArrayListExtra("profiles", (ArrayList<? extends Parcelable>) profiles);
                startActivity(intent);
            }else{
                Toast.makeText(context, mensajeError, Toast.LENGTH_SHORT).show();
            }


        }
    }
}
