package com.robertson.unitransporte.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.robertson.unitransporte.R;
import com.robertson.unitransporte.objetos.Viagem;

import java.util.ArrayList;

public class AdapterViagem extends RecyclerView.Adapter<AdapterViagem.ViagemViewRolder> {

    private ArrayList<Viagem> mDataSet;
    private OnViagemListener onViagemListener;

    public AdapterViagem(ArrayList<Viagem> dataSet, OnViagemListener onViagemListener){
        mDataSet = dataSet;
        this.onViagemListener = onViagemListener;
    }
    @NonNull
    @Override
    public ViagemViewRolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viagem_unidade, parent, false);

        return new ViagemViewRolder(v, onViagemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViagemViewRolder holder, int position) {
        holder.nomeViagem.setText(mDataSet.get(position).getNome());
        holder.localOrigem.setText(mDataSet.get(position).getOrigem().getNome());
        holder.localDestino.setText(mDataSet.get(position).getDestino().getNome());
        holder.horaSaidaOrigem.setText(mDataSet.get(position).getHorarioSaidaOrigem());
        holder.horaSaidaDestino.setText(mDataSet.get(position).getHorarioSaidaDestino());
        holder.diasSemana.setText(mDataSet.get(position).diasSemanaToString());
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public static class ViagemViewRolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView nomeViagem, localOrigem, localDestino, horaSaidaOrigem, horaSaidaDestino, diasSemana;
        OnViagemListener onViagemListener;
        public ViagemViewRolder(View v, OnViagemListener onViagemListener){
             super(v);
             nomeViagem = v.findViewById(R.id.textViewNomeViagemMostra);
             localOrigem = v.findViewById(R.id.textViewOrigemViagemMostra);
             localDestino = v.findViewById(R.id.textViewDestinoViagemMostra);
             horaSaidaOrigem = v.findViewById(R.id.textViewHoraSaidaOrigemViagemMostra);
             horaSaidaDestino = v.findViewById(R.id.textViewHoraSaideDestinoViagemMostra);
             diasSemana = v.findViewById(R.id.textViewDiasSemanaViagemMostra);
             this.onViagemListener = onViagemListener;

             v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onViagemListener.onViagemClick(getAdapterPosition());
        }
    }
     public interface OnViagemListener{
        void onViagemClick(int position);
     }
}
