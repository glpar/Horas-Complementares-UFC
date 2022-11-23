package com.robertson.unitransporte.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.robertson.unitransporte.MainActivity;
import com.robertson.unitransporte.R;
import com.robertson.unitransporte.adapters.AdapterViagem;
import com.robertson.unitransporte.objetos.Viagem;

import java.util.ArrayList;

public class MinhasViagensDisponiveisFragment extends Fragment implements  AdapterViagem.OnViagemListener{
    private ArrayList<Viagem> mDataSet;
    RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_minhas_viagens_disponiveis, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewMinhasViagens);

        Toolbar toolbar = view.findViewById(R.id.mToolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });

        mDataSet = MainActivity.mDataSetDisponiveis;

        AdapterViagem adapterViagem = new AdapterViagem(mDataSet,this);
        recyclerView.setAdapter(adapterViagem);

        return view;
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