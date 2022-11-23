package com.robertson.unitransporte.fragments;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.robertson.unitransporte.MainActivity;
import com.robertson.unitransporte.R;
import com.robertson.unitransporte.adapters.AdapterViagem;
import com.robertson.unitransporte.objetos.Passageiro;
import com.robertson.unitransporte.objetos.Usuario;
import com.robertson.unitransporte.objetos.Viagem;

import java.util.ArrayList;
import java.util.Objects;

public class MinhasViagensFragment extends Fragment implements  AdapterViagem.OnViagemListener{
    private ArrayList<Viagem> mDataSet;
    RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_minhas_viagens, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewMinhasViagens);
        SwipeRefreshLayout refreshLayout = view.findViewById(R.id.refresh);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUsuarioAtual(new FirestoreCallback() {
                    @Override
                    public void onCallback(QueryDocumentSnapshot document2) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("Viagens")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            mDataSet.clear();
                                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                                Viagem viagem = new Viagem();
                                                Gson gsonOrg = new GsonBuilder().setPrettyPrinting().create();;
                                                String aux = gsonOrg.toJson(document.get("Viagem"));
                                                viagem = gsonOrg.fromJson(aux, Viagem.class);
                                                viagem.setPassageiros(document.toObject(Viagem.class).getPassageiros());

                                                ArrayList<Passageiro> passageiroArrayList = document.toObject(Viagem.class).getPassageiros();

                                                for(Passageiro p : passageiroArrayList){
                                                    Gson gsonOrg2 = new GsonBuilder().setPrettyPrinting().create();
                                                    String aux2 = gsonOrg2.toJson(document2.get("Usuário"));
                                                    Usuario user = gsonOrg2.fromJson(aux2, Usuario.class);

                                                    if(p.getEmail().equals(user.getEmail())){
                                                        mDataSet.add(viagem);
                                                        break;
                                                    }
                                                }
                                            }
                                            refreshLayout.setRefreshing(false);
                                        }
                                    }
                                })
                        ;
                    }
                });
            }
        });


        mDataSet = MainActivity.mDataSet;

        AdapterViagem adapterViagem = new AdapterViagem(mDataSet,this);
        recyclerView.setAdapter(adapterViagem);

        return view;
    }
    public void getUsuarioAtual(FirestoreCallback firestoreCallback){
        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Usuários")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Gson gsonOrg = new GsonBuilder().setPrettyPrinting().create();;
                                String aux = gsonOrg.toJson(document.get("Usuário"));
                                Usuario usuario = gsonOrg.fromJson(aux, Usuario.class);
                                if(usuario.getEmail().equals(usuarioAtual.getEmail())) {
                                    firestoreCallback.onCallback(document);
                                }
                            }
                        }
                    }
                })
        ;
    }
    private interface FirestoreCallback{
        void onCallback(QueryDocumentSnapshot user);
    }
    @Override
    public void onViagemClick(int position) {
        DadosViagemPassageiroFragment dadosViagemPassageiroFragment = new DadosViagemPassageiroFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("viagem", mDataSet.get(position));

        dadosViagemPassageiroFragment.setArguments(bundle);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.frameLayoutFragmentUsuario, dadosViagemPassageiroFragment)
                .addToBackStack(null)
                .commit();
    }
}