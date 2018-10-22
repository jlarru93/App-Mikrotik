package com.kael.mikrotik;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.kael.adapter.SecretAdapter;
import com.kael.bean.Secret;
import com.kael.bean.MessageDialog;
import com.kael.bean.Servidor;
import com.kael.dao.PPP;
import com.kael.datos.ConexionMikrotik;

import java.util.ArrayList;
import java.util.List;

import me.legrange.mikrotik.ApiConnection;
import me.legrange.mikrotik.ApiConnectionException;
import me.legrange.mikrotik.MikrotikApiException;

/**
 * Created by kael on 17/08/2016.
 */
public class SecretFragment extends Fragment implements SecretAdapter.OnItemClickListener, SearchView.OnQueryTextListener {

    private List<Secret> secretList;
    private RecyclerView recycler;
    private RecyclerView.LayoutManager layoutManager;
    private SecretAdapter adapter;
    boolean adapter_linked = false;


    private Context context;
    SearchView searchView;


    Servidor servidor;
    ArrayList<Parcelable> profiles;

    ArrayAdapter adapterSpinner;

    private static int TYPE_TEXT_VARIATION_HIDDEN_PASSWORD = 129;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.secret_recycler, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        final MenuItem item = menu.findItem(R.id.menu_search);
        final MenuItem item2 = menu.findItem(R.id.menu_refresh);
        item2.setVisible(false);

        searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Busqueda por Nombre");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_adduser:
                createAddUserDialog();
                break;
        }
        return true;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View v = getView();
        context = v.getContext();

        Bundle bundle = getArguments();

        if(bundle!=null){
            servidor = (Servidor) bundle.getSerializable("servidor");
            profiles = bundle.getParcelableArrayList("profiles");
            adapterSpinner = new ArrayAdapter(context, android.R.layout.simple_list_item_1, profiles);
        }




        recycler = (RecyclerView) v.findViewById(R.id.recyclerCliente);
        recycler.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recycler.setLayoutManager(layoutManager);
        recycler.setItemAnimator(new DefaultItemAnimator());

        getAllSecrets();
    }

    public void getAllSecrets(){
        SecretListTask task = new SecretListTask();
        task.execute();
    }

    @Override
    public void onClick(RecyclerView.ViewHolder v) {
        final Secret secret = adapter.getSecret(v.getAdapterPosition());

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Activación");

        String message = "DESHABILITAR";
        if(secret.isDisabled()){
            message = "HABILITAR";
        }

        builder.setMessage("¿Desea "+message+" el cliente "+ secret.getName()+"?");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ChangeStatusTask task = new ChangeStatusTask();
                task.execute(secret);
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<Secret> filterList = filter(secretList, newText);
        adapter.setFilter(filterList);
        return true;
    }

    private List<Secret> filter(List<Secret> list, String query){
        query = query.toLowerCase();

        final List<Secret> filteredList = new ArrayList<>();
        for(Secret secret : list){
            final String text = secret.getName().toLowerCase();
            final String _id = secret.getId();
            if(text.contains(query) | _id.equalsIgnoreCase(query)){
                filteredList.add(secret);
            }
        }
        return filteredList;
    }


    private void createAddUserDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.agregar_pppoe, null);
        builder.setView(v);

        final EditText txtUsuario = (EditText) v.findViewById(R.id.txtUsuario);
        final EditText txtPassword = (EditText) v.findViewById(R.id.txtPassword);
        final Spinner spPerfil = (Spinner) v.findViewById(R.id.spPerfil);
        final Button btnRegistrar = (Button) v.findViewById(R.id.btnRegistrarPPPOE);
        final CheckBox chkMostrarClave = (CheckBox) v.findViewById(R.id.chkMostrarClave);
        spPerfil.setAdapter(adapterSpinner);

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

        final AlertDialog alertDialog= builder.show();

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(txtUsuario.getText().toString().trim().isEmpty()){
                    txtUsuario.setError("Ingrese un nombre");
                    txtUsuario.requestFocus();
                    return;
                }

                if(txtPassword.getText().toString().trim().isEmpty()){
                    txtPassword.setError("Ingrese una clave");
                    txtPassword.requestFocus();
                    return;
                }

                Secret secret = new Secret();

                String usuario = txtUsuario.getText().toString();
                String password = txtPassword.getText().toString();
                String perfil = spPerfil.getSelectedItem().toString();

                secret.setName(usuario);
                secret.setPassword(password);
                secret.setProfile(perfil);

                RegisterSecretTask task = new RegisterSecretTask(alertDialog);
                task.execute(secret);

            }
        });

    }





    private class SecretListTask extends AsyncTask<ApiConnection, Void, List<Secret>>{
        private String query;

        public SecretListTask(){}

        public SecretListTask(String query){
            this.query = query;
        }

        @Override
        protected void onPreExecute() {
            //Toast.makeText(context, "Conectando con Mikrotik", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected List<Secret> doInBackground(ApiConnection... params) {
            List<Secret> secretList = null;
            ApiConnection cn = null;
            try {
                if(params!=null && params.length>0 && params[0]!=null){
                    cn = params[0];
                }else{
                    cn = ConexionMikrotik.getInstance().getConnection(servidor);
                }

                PPP ppp = new PPP();
                secretList = ppp.getSecrets(cn);
                cn.close();

            } catch (MikrotikApiException e) {
                e.printStackTrace();
            }

            return secretList;
        }

        @Override
        protected void onPostExecute(List<Secret> secretList) {
            SecretFragment.this.secretList = secretList;

            if(!adapter_linked){
                adapter = new SecretAdapter(secretList);
                adapter.setOnItemClickListener(SecretFragment.this);
                recycler.setAdapter(adapter);
                adapter_linked = true;
            }else{
                adapter.setSecretList(secretList);

                if(searchView.getQuery().length()>0){
                    onQueryTextChange(searchView.getQuery().toString());
                }else if(query!=null && !query.isEmpty()){
                    onQueryTextChange(query);
                    query = null;
                }
            }

        }
    }


    private class ChangeStatusTask extends AsyncTask<Secret, Void, ApiConnection> {
        private boolean error = false;
        @Override
        protected ApiConnection doInBackground(Secret... secret) {
            ApiConnection cn = null;
            try {
                cn = ConexionMikrotik.getInstance().getConnection(servidor);
                PPP ppp = new PPP();
                String id = secret[0].getId();
                boolean estadoCambiado = !secret[0].isDisabled();
                ppp.changeStatus(id, estadoCambiado, cn);
                //cn.close();
            } catch (ApiConnectionException e) {
                error = true;
                cn = null;
                e.printStackTrace();
            } catch (MikrotikApiException e) {
                error = true;
                cn = null;
                e.printStackTrace();
            }
            return cn;
        }

        @Override
        protected void onPostExecute(ApiConnection cn) {
            if(!error){
                SecretListTask task = new SecretListTask();
                task.execute(cn);
            }
        }
    }

    private class RegisterSecretTask extends AsyncTask<Secret, Void, ApiConnection> {
        private boolean error = false;
        private MessageDialog dialog;
        private AlertDialog alertDialog;
        private String id;


        public RegisterSecretTask(AlertDialog alertDialog){
            this.alertDialog = alertDialog;
        }

        @Override
        protected void onPreExecute() {
            dialog = new MessageDialog(context);
            dialog.showProgressBar("Registro", "Procesando registro...");
        }

        @Override
        protected ApiConnection doInBackground(Secret... secret) {
            ApiConnection cn = null;
            try {
                cn = ConexionMikrotik.getInstance().getConnection(servidor);
                PPP ppp = new PPP();
                id = ppp.addSecret(secret[0], cn);

                //cn.close();
            } catch (ApiConnectionException e) {
                error = true;
                cn = null;
                e.printStackTrace();
            } catch (MikrotikApiException e) {
                error = true;
                cn = null;
                e.printStackTrace();
            }
            return cn;
        }

        @Override
        protected void onPostExecute(ApiConnection cn) {
            dialog.closeProgressBar();
            alertDialog.dismiss();
            if(!error){

                if(id!=null){
                    Toast.makeText(context, "¡Registrado exitosamente!", Toast.LENGTH_SHORT).show();
                    SecretListTask task = new SecretListTask(id);
                    task.execute(cn);
                }else{
                    Toast.makeText(context, "¡Error al registrar!", Toast.LENGTH_SHORT).show();
                }


            }else{
                Toast.makeText(context, "El cliente ya existe", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
