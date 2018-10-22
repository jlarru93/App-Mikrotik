package com.kael.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kael.bean.PPPInterface;
import com.kael.mikrotik.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kael on 24/08/2016.
 */
public class PPPInterfaceAdapter extends RecyclerView.Adapter<PPPInterfaceAdapter.PPPInterfaceViewHolder> {
    private List<PPPInterface> list;
    private Context context;
    private OnItemClickListener clickListener;


    public PPPInterfaceAdapter(List<PPPInterface> list){
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener clickListener){
        this.clickListener =  clickListener;
    }

    public PPPInterface getPPPInterface(int position){
        return list.get(position);
    }


    @Override
    public PPPInterfaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.secret_conectado_card, parent, false);
        return new PPPInterfaceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PPPInterfaceViewHolder holder, int position) {
        PPPInterface pppInterface = list.get(position);
        holder.lblNombre.setText(pppInterface.getName());
        holder.lblIP.setText(pppInterface.getRemoteAdress());
        holder.lblUptime.setText(pppInterface.getUptime());
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class PPPInterfaceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView lblNombre, lblIP, lblUptime;
        public PPPInterfaceViewHolder(View v) {
            super(v);
            lblNombre = (TextView) v.findViewById(R.id.lblCliente);
            lblIP = (TextView) v.findViewById(R.id.lblIP);
            lblUptime = (TextView) v.findViewById(R.id.lblUptime);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(this);
        }
    }

    public void setFilter(List<PPPInterface> list){
        this.list = new ArrayList<>();
        this.list.addAll(list);
        notifyDataSetChanged();
    }


    public interface OnItemClickListener{
        void onClick(RecyclerView.ViewHolder v);
    }
}
