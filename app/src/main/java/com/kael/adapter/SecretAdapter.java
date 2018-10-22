package com.kael.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kael.bean.Secret;
import com.kael.mikrotik.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kael on 17/08/2016.
 */
public class SecretAdapter extends RecyclerView.Adapter<SecretAdapter.ClienteViewHolder> {
    private List<Secret> secretList;
    private Context context;
    private OnItemClickListener clickListener;

    public SecretAdapter(List<Secret> secretList) {
        this.secretList = secretList;
    }

    public void setSecretList(List<Secret> secretList) {
        this.secretList = secretList;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener clickListener){
        this.clickListener =  clickListener;
    }


    public Secret getSecret(int position){
        return secretList.get(position);
    }


    @Override
    public SecretAdapter.ClienteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.secret_card, parent, false);
        return new ClienteViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SecretAdapter.ClienteViewHolder holder, int position) {
        Secret secret = secretList.get(position);
        holder.lblCliente.setText(secret.getName());

        String estado = "DESHABILITADO";
        if(!secret.isDisabled()){
            estado = "HABILITADO";
            holder.lblEstado.setTextColor(ContextCompat.getColor(context, R.color.buttonColor));
        }else{
            holder.lblEstado.setTextColor(Color.RED);
        }

        holder.lblEstado.setText(estado);
    }

    @Override
    public int getItemCount() {
        return secretList ==null?0: secretList.size();
    }

    public class ClienteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView lblCliente, lblEstado;


        public ClienteViewHolder(View v) {
            super(v);
            lblCliente = (TextView) v.findViewById(R.id.lblCliente);
            lblEstado = (TextView) v.findViewById(R.id.lblEstado);
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

    public void setFilter(List<Secret> secretList){
        this.secretList = new ArrayList<>();
        this.secretList.addAll(secretList);
        notifyDataSetChanged();
    }
}
