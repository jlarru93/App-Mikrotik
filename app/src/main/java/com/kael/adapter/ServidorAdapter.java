package com.kael.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kael.bean.Servidor;
import com.kael.mikrotik.R;

import java.util.List;

/**
 * Created by kael on 29/08/2016.
 */
public class ServidorAdapter extends RecyclerView.Adapter<ServidorAdapter.ServidorViewHolder> {
    private List<Servidor> list;
    private Context context;
    private OnItemClickListener clickListener;

    public ServidorAdapter(List<Servidor> list) {
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener clickListener){
        this.clickListener =  clickListener;
    }

    public Servidor getServidor(int position){
        return list.get(position);
    }



    @Override
    public ServidorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.servidor_card, parent, false);
        return new ServidorViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ServidorViewHolder holder, int position) {
        Servidor servidor = list.get(position);
        holder.lblNombreServidor.setText(servidor.getNameServer());
        Log.d("ELEMENTO", servidor.getNameServer());
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ServidorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView lblNombreServidor;
        public ServidorViewHolder(View v) {
            super(v);
            lblNombreServidor = (TextView) v.findViewById(R.id.lblServidor);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(this);
        }
    }


    public interface OnItemClickListener{
        void onClick(RecyclerView.ViewHolder v);
    }
}
