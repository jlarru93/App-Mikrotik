package com.kael.mikrotik;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.kael.adapter.PPPInterfaceAdapter;
import com.kael.bean.PPPInterface;
import com.kael.bean.Servidor;
import com.kael.dao.PPP;
import com.kael.datos.ConexionMikrotik;

import java.util.ArrayList;
import java.util.List;

import me.legrange.mikrotik.ApiConnection;
import me.legrange.mikrotik.MikrotikApiException;

/**
 * Created by kael on 24/08/2016.
 */
public class PPPInterfaceFragment extends Fragment implements PPPInterfaceAdapter.OnItemClickListener, SearchView.OnQueryTextListener{
    private List<PPPInterface> list;
    private RecyclerView recycler;
    private RecyclerView.LayoutManager layoutManager;
    private PPPInterfaceAdapter adapter;

    private Context context;
    private Servidor servidor;

    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.secret_conectado_recycler, container, false);
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
        final MenuItem item2 = menu.findItem(R.id.menu_adduser);
        item2.setVisible(false);
        searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Busqueda por Nombre");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_refresh:
                refreshList();
                break;
        }
        return true;
    }


    private void refreshList(){
        listPPPInterfaces();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View v = getView();
        context = v.getContext();

        Bundle bundle = getArguments();

        if(bundle!=null){
            servidor = (Servidor) bundle.getSerializable("servidor");
        }

        recycler = (RecyclerView) v.findViewById(R.id.recyclerClienteConectado);
        recycler.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recycler.setLayoutManager(layoutManager);
        recycler.setItemAnimator(new DefaultItemAnimator());
        listPPPInterfaces();
    }

    private void listPPPInterfaces() {
        PPPInterfaceListTask task = new PPPInterfaceListTask();
        task.execute();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        List<PPPInterface> filteredList = filter(list, newText);
        adapter.setFilter(filteredList);
        return true;
    }

    private List<PPPInterface> filter(List<PPPInterface> list, String query){
        query = query.toLowerCase();
        List<PPPInterface> filteredList = new ArrayList<>();

        for(PPPInterface pppInterface : list){
            String text = pppInterface.getName().toLowerCase();

            if(text.contains(query)){
                filteredList.add(pppInterface);
            }
        }
        return filteredList;
    }

    @Override
    public void onClick(RecyclerView.ViewHolder v) {
        final PPPInterface pppInterface = adapter.getPPPInterface(v.getAdapterPosition());
        String IP = pppInterface.getRemoteAdress();
        OpenURLTask task = new OpenURLTask(getActivity());
        task.execute(IP);
    }

    private class PPPInterfaceListTask extends AsyncTask<ApiConnection, Void, List<PPPInterface> >{

        @Override
        protected List<PPPInterface> doInBackground(ApiConnection... params) {
            List<PPPInterface> list = null;
            ApiConnection cn = null;

            try{
                if(params!=null && params.length>0 && params[0]!=null){
                    cn = params[0];
                }else{
                    cn = ConexionMikrotik.getInstance().getConnection(servidor);
                }
                PPP ppp = new PPP();
                list = ppp.getInterfaces(cn);
                cn.close();

            } catch (MikrotikApiException e) {
                e.printStackTrace();
            }


            return list;
        }

        @Override
        protected void onPostExecute(List<PPPInterface> listInterfaces) {
            list = listInterfaces;
            adapter = new PPPInterfaceAdapter(list);
            adapter.setOnItemClickListener(PPPInterfaceFragment.this);
            recycler.setAdapter(adapter);

        }
    }

    private class OpenURLTask extends AsyncTask<String, Void, Void>{
        private Activity activity;

        public OpenURLTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected Void doInBackground(String... params) {

            if(params!=null && params.length>0 && params[0] != null){
                String url = "http://" + params[0];
                openURL(url);
            }

            return null;
        }

        private void openURL(String url){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            activity.startActivity(intent);
        }
    }
}
