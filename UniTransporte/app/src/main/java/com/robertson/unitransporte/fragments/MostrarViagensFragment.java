package com.robertson.unitransporte.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.robertson.unitransporte.MainActivityOrg;
import com.robertson.unitransporte.R;
import com.robertson.unitransporte.adapters.AdapterViagem;
import com.robertson.unitransporte.objetos.Municipio;
import com.robertson.unitransporte.objetos.Organizador;
import com.robertson.unitransporte.objetos.Passageiro;
import com.robertson.unitransporte.objetos.Usuario;
import com.robertson.unitransporte.objetos.Viagem;

import java.util.ArrayList;
import java.util.Objects;


public class MostrarViagensFragment extends Fragment implements AdapterViagem.OnViagemListener {
    private ArrayList<Viagem> mDataSet = new ArrayList<>();
    private RecyclerView recyclerView;


    @Override
    public void onResume() {
        super.onResume();
        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Viagens")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            mDataSet.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.get("OrganizadorCriador").toString().equals(usuarioAtual.getUid())) {
                                    Viagem viagem = new Viagem();
                                    Gson gsonOrg = new GsonBuilder().setPrettyPrinting().create();;
                                    String aux = gsonOrg.toJson(document.get("Viagem"));
                                    viagem = gsonOrg.fromJson(aux, Viagem.class);
                                    viagem.setPassageiros(document.toObject(Viagem.class).getPassageiros());
                                    mDataSet.add(viagem);
                                }
                            }
                        }
                    }
                })
        ;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mostrar_viagens, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        mDataSet.clear();
        mDataSet.addAll(MainActivityOrg.mDataSet);

        AdapterViagem adapterViagem = new AdapterViagem(mDataSet,this);
        recyclerView.setAdapter(adapterViagem);

        return view;
    }

    @Override
    public void onViagemClick(int position) {
        DadosViagemFragment dadosViagemFragment = new DadosViagemFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("viagem", mDataSet.get(position));
        bundle.putInt("position", position);

        dadosViagemFragment.setArguments(bundle);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.frameLayoutFragment, dadosViagemFragment)
                .addToBackStack(null)
                .commit();
    }
}