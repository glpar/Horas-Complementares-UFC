package com.robertson.unitransporte.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.api.LogDescriptor;
import com.robertson.unitransporte.MainActivityOrg;
import com.robertson.unitransporte.R;
import com.robertson.unitransporte.adapters.AdapterPassageiro;
import com.robertson.unitransporte.objetos.Municipio;
import com.robertson.unitransporte.objetos.Passageiro;
import com.robertson.unitransporte.objetos.Viagem;
import com.robertson.unitransporte.services.FirebaseService;

import java.util.ArrayList;

public class DadosViagemFragment extends Fragment {

    TextView textViewNomeViagem, textViewOrigemViagem, textViewDestinoViagem, textViewDiasSemana,
        textViewHorarioOrigemViagem, getTextViewHorarioDestinoViagem, textViewTotalPassageiros;
    RecyclerView recyclerViewPassageiros;
    Viagem viagem = new Viagem();
    Button btnApagarViagem;
    int pos;
    FloatingActionButton btnEditarViagem;

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        Viagem viagemAux = getArguments().getParcelable("viagem");
        pos = getArguments().getInt("position");
        viagem.setNome(viagemAux.getNome());
        Municipio m = new Municipio();
        m.setId(viagemAux.getOrigem().getId());
        m.setNome(viagemAux.getOrigem().getNome());
        viagem.setOrigem(m);
        Municipio m2 = new Municipio();
        m2.setId(viagemAux.getDestino().getId());
        m2.setNome(viagemAux.getDestino().getNome());
        viagem.setDestino(m2);
        viagem.setPassageiros(viagemAux.getPassageiros());
        viagem.setHorarioSaidaOrigem(viagemAux.getHorarioSaidaOrigem());
        viagem.setHorarioSaidaDestino(viagemAux.getHorarioSaidaDestino());
        ArrayList<Boolean> b = new ArrayList<Boolean>(viagemAux.getDias());
        viagem.setDias(b);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dados_viagem, container, false);
        textViewNomeViagem = view.findViewById(R.id.textViewNomeViagemDados);
        textViewOrigemViagem = view.findViewById(R.id.textViewOrigemViagemDados);
        textViewDestinoViagem = view.findViewById(R.id.textViewDestinoViagemDados);
        textViewDiasSemana = view.findViewById(R.id.textViewDiasSemanaViagemDados);
        textViewHorarioOrigemViagem = view.findViewById(R.id.textViewHorarioOrigemViagemDados);
        getTextViewHorarioDestinoViagem = view.findViewById(R.id.textViewHorarioDestinoViagemDados);
        recyclerViewPassageiros = view.findViewById(R.id.recyclerViewPassageirosViagemDados);
        btnApagarViagem = view.findViewById(R.id.buttonApagarViagem);
        textViewTotalPassageiros = view.findViewById(R.id.textViewTotalPassageirosInscritosViagemDados);
        btnEditarViagem = view.findViewById(R.id.floatingBtnEditarViagem);

        btnEditarViagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditarViagemFragment editarViagemFragment = new EditarViagemFragment();

                Bundle bundle = new Bundle();

                bundle.putParcelable("viagem", viagem);
                bundle.putInt("position", pos);

                editarViagemFragment.setArguments(bundle);

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frameLayoutFragment, editarViagemFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        btnApagarViagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnApagarViagem.setActivated(false);
                FirebaseService.apagaViagem(viagem);
                mostraSnackbar(view, getString(R.string.viagem_apagada));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("linha antes de remover", String.valueOf(MainActivityOrg.mDataSet.size()));
                        MainActivityOrg.mDataSet.remove(viagem);
                        Log.d("linha depois de remover", String.valueOf(MainActivityOrg.mDataSet.size()));
                        getParentFragmentManager().popBackStack();
                    }
                }, 1500);
            }
        });


        Toolbar toolbar = view.findViewById(R.id.mToolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });

        textViewNomeViagem.setText(viagem.getNome());
        textViewOrigemViagem.setText(viagem.getOrigem().getNome());
        textViewDestinoViagem.setText(viagem.getDestino().getNome());
        textViewDiasSemana.setText(viagem.diasSemanaToString());
        textViewHorarioOrigemViagem.setText(viagem.getHorarioSaidaOrigem());
        getTextViewHorarioDestinoViagem.setText(viagem.getHorarioSaidaDestino());

        textViewTotalPassageiros.setText(new StringBuilder().append(getString(R.string.total_passageiros)).append(" "+ viagem.getPassageiros().size()).toString());

        AdapterPassageiro adapterPassageiro = new AdapterPassageiro(viagem.getPassageiros());

        recyclerViewPassageiros.setAdapter(adapterPassageiro);


        return view;
    }
    private void mostraSnackbar(View view, String mensagem){
        Snackbar snackbar = Snackbar.make(view, mensagem, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(Color.WHITE);
        snackbar.setTextColor(Color.BLACK);
        snackbar.show();
    }
}