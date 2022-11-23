package com.robertson.unitransporte.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.robertson.unitransporte.R;
import com.robertson.unitransporte.objetos.Passageiro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AdapterPassageiro extends RecyclerView.Adapter<AdapterPassageiro.PassageiroViewRolder>{
    ArrayList<Passageiro> mDataSetPassageiros;
    int contDestinos = 1;

    public AdapterPassageiro(ArrayList<Passageiro> mDataSetPassageiros) {
        Collections.sort(mDataSetPassageiros, new Comparator<Passageiro>() {
            @Override
            public int compare(Passageiro o1, Passageiro o2) {
                return o1.getDestino().compareTo(o2.getDestino());
            }
        });
        this.mDataSetPassageiros = mDataSetPassageiros;
    }

    @NonNull
    @Override
    public PassageiroViewRolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.passageiro_unidade, parent, false);
        return new PassageiroViewRolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PassageiroViewRolder holder, int position) {
        if(position > 0 && !mDataSetPassageiros.get(position).getDestino().equalsIgnoreCase(mDataSetPassageiros.get(position-1).getDestino())){
            contDestinos = 1;
        }
        String s = contDestinos + ". " + mDataSetPassageiros.get(position).getNome() + " - " + mDataSetPassageiros.get(position).getDestino();
        contDestinos++;
        holder.linha.setText(s);
    }

    @Override
    public int getItemCount() {
        return mDataSetPassageiros.size();
    }

    public static class PassageiroViewRolder extends RecyclerView.ViewHolder{
        TextView linha;
        public PassageiroViewRolder(@NonNull View itemView) {
            super(itemView);
            linha = itemView.findViewById(R.id.textViewPassageiroUnidade);
        }
    }
}
