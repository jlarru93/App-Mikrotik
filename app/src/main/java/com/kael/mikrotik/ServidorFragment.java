package com.kael.mikrotik;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.kael.adapter.ServidorAdapter;
import com.kael.bean.Servidor;
import com.kael.dao.DAOServidor;

import java.util.List;

/**
 * Created by kael on 29/08/2016.
 */
public class ServidorFragment extends Fragment implements ServidorAdapter.OnItemClickListener{
    private static final int TYPE_TEXT_VARIATION_HIDDEN_PASSWORD = 129;
    private Context context;
    private RecyclerView recycler;
    private LinearLayoutManager layoutManager;
    private ServidorAdapter adapter;


    private DAOServidor dao;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.servidor_recycler, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.servidor_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_add:
                createAddServerDialog();
                break;
        }
        return true;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View v = getView();
        context = v.getContext();
        dao = new DAOServidor(context);

        recycler = (RecyclerView) v.findViewById(R.id.recyclerServidor);
        recycler.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recycler.setLayoutManager(layoutManager);
        recycler.setItemAnimator(new DefaultItemAnimator());
        getServers();
    }


    private void getServers(){
        List<Servidor> servers = dao.getServers();
        adapter = new ServidorAdapter(servers);
        adapter.setOnItemClickListener(this);
        recycler.setAdapter(adapter);
    }


    private void createAddServerDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.agregar_servidor, null);
        builder.setView(v);

        final EditText txtNombreServidor = (EditText) v.findViewById(R.id.txtNombreServidor);
        final EditText txtHOST = (EditText) v.findViewById(R.id.txtHOST);
        final EditText txtPORT = (EditText) v.findViewById(R.id.txtPORT);
        final EditText txtUsuario = (EditText) v.findViewById(R.id.txtUsuario);
        final EditText txtPassword = (EditText) v.findViewById(R.id.txtPassword);
        final CheckBox chkMostrarClave = (CheckBox) v.findViewById(R.id.chkMostrarClave);
        final CheckBox chkConexionTLS = (CheckBox) v.findViewById(R.id.chkConexionTLS);
        final RadioButton rbtSSLCertificado = (RadioButton) v.findViewById(R.id.rbtSSLCertificado);
        final RadioButton rbtSSLAnonimo = (RadioButton) v.findViewById(R.id.rbtSSLAnonimo);
        final Button btnRegistrar = (Button) v.findViewById(R.id.btnRegistrarServidor);
        final RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.radioGroup);



        chkMostrarClave.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    txtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    txtPassword.setSelection(txtPassword.getText().length());
                }else{
                    txtPassword.setInputType(TYPE_TEXT_VARIATION_HIDDEN_PASSWORD);
                    txtPassword.setSelection(txtPassword.getText().length());
                }
            }
        });

        chkConexionTLS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    radioGroup.setVisibility(View.VISIBLE);
                }else{
                    radioGroup.setVisibility(View.INVISIBLE);
                }
            }
        });


        final AlertDialog alertDialog= builder.show();

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(txtNombreServidor.getText().toString().trim().isEmpty()){
                    txtNombreServidor.setError("Ingrese un nombre");
                    txtNombreServidor.requestFocus();
                    return;
                }

                if(txtHOST.getText().toString().trim().isEmpty()){
                    txtHOST.setError("Ingrese la IP del servidor");
                    txtHOST.requestFocus();
                    return;
                }

                if(txtPORT.getText().toString().trim().isEmpty()){
                    txtPORT.setError("Ingrese un numero de puerto");
                    txtPORT.requestFocus();
                    return;
                }


                if(txtUsuario.getText().toString().trim().isEmpty()){
                    txtUsuario.setError("Ingrese un usuario");
                    txtUsuario.requestFocus();
                    return;
                }

                if(txtPassword.getText().toString().trim().isEmpty()){
                    txtPassword.setError("Ingrese una clave");
                    txtPassword.requestFocus();
                    return;
                }



                String nombreServidor = txtNombreServidor.getText().toString();
                String host = txtHOST.getText().toString();
                int port = Integer.parseInt(txtPORT.getText().toString());
                String usuario = txtUsuario.getText().toString();
                String password = txtPassword.getText().toString();
                boolean SSLConnection = false;
                boolean SSLCertificate = false;

                if(chkConexionTLS.isChecked()){
                    SSLConnection = true;

                    if(rbtSSLCertificado.isSelected()){
                        SSLCertificate = true;
                    }

                }

                Servidor servidor = new Servidor();
                servidor.setNameServer(nombreServidor);
                servidor.setHost(host);
                servidor.setPort(port);
                servidor.setUser(usuario);
                servidor.setPassword(password);
                servidor.setSSLConnection(SSLConnection);
                servidor.setSSLCertificate(SSLCertificate);

                String resultado = null;

                if(dao.registrarServidor(servidor)){
                    resultado = "¡Servidor registrado exitosamente!";
                    getServers();
                }else{
                    resultado = "¡Error al registrar el servidor!";
                }

                alertDialog.dismiss();
                Toast.makeText(context, resultado, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onClick(RecyclerView.ViewHolder v) {
        final Servidor servidor = adapter.getServidor(v.getAdapterPosition());

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Eliminar");


        builder.setMessage("¿Desea eliminar el servidor "+servidor.getNameServer()+"?");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(dao.eliminarServidor(servidor.getCodServer())){
                    Toast.makeText(context, "¡Servidor eliminado exitosamente", Toast.LENGTH_SHORT).show();
                    getServers();
                }else{
                    Toast.makeText(context, "¡Error al eliminar el servidor", Toast.LENGTH_SHORT).show();
                }

            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }
}
