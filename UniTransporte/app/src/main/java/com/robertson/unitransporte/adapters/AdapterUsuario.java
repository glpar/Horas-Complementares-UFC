package com.robertson.unitransporte.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.robertson.unitransporte.MainActivityOrg;
import com.robertson.unitransporte.R;
import com.robertson.unitransporte.fragments.ListarMeusPassageirosFragment;
import com.robertson.unitransporte.objetos.Passageiro;
import com.robertson.unitransporte.objetos.Usuario;
import com.robertson.unitransporte.objetos.Viagem;
import com.robertson.unitransporte.services.FirebaseService;

import java.util.ArrayList;

public class AdapterUsuario extends RecyclerView.Adapter<AdapterUsuario.UsuarioViewHolder> {
    ArrayList<Usuario> mDataSet;

    public AdapterUsuario(ArrayList<Usuario> mDataSet) {
        this.mDataSet = mDataSet;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.passageiro_lista_passageiros, parent, false);
        return new UsuarioViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        holder.textViewNome.setText(mDataSet.get(position).getNome());
        holder.textViewEmail.setText(mDataSet.get(position).getEmail());
        holder.textViewSenha.setText(mDataSet.get(position).getSenha());
        holder.btnRemover.setOnClickListener(view ->{
            removeUsuarioFirebase(mDataSet.get(position));
        });
    }

    public void removeUsuarioFirebase(Usuario user){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Usuários")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Gson gsonOrg = new GsonBuilder().setPrettyPrinting().create();
                                String aux = gsonOrg.toJson(document.get("Usuário"));
                                Usuario usuario = gsonOrg.fromJson(aux, Usuario.class);
                                if (user.getEmail().equals(usuario.getEmail())) {
                                    db.collection("Usuários").document(document.getId()).delete();
                                    mDataSet.remove(user);
                                    notifyDataSetChanged();

                                    FirebaseService.removePassageiroDasViagens(usuario.getEmail());
                                }
                            }
                        }
                    }
                })
        ;
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public static class UsuarioViewHolder extends RecyclerView.ViewHolder{
        TextView textViewNome, textViewEmail, textViewSenha;
        FloatingActionButton btnRemover;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNome = itemView.findViewById(R.id.textViewNomeListaPassageiro);
            textViewEmail = itemView.findViewById(R.id.textViewEmailListaPassageiro);
            textViewSenha = itemView.findViewById(R.id.textViewSenhaListaPassageiro);
            btnRemover = itemView.findViewById(R.id.buttonApagarPassageiroListaPassageiro);
        }
    }
}
