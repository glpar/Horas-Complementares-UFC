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
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.robertson.unitransporte.MainActivityOrg;
import com.robertson.unitransporte.R;
import com.robertson.unitransporte.adapters.AdapterPassageiro;
import com.robertson.unitransporte.objetos.Passageiro;
import com.robertson.unitransporte.objetos.Usuario;
import com.robertson.unitransporte.objetos.Viagem;
import com.robertson.unitransporte.services.FirebaseService;

public class DadosViagemPassageiroFragment extends Fragment {
    TextView textViewNomeViagem, textViewOrigemViagem, textViewDestinoViagem, textViewDiasSemana,
            textViewHorarioOrigemViagem, getTextViewHorarioDestinoViagem, textViewTotalPassageiros;
    RecyclerView recyclerViewPassageiros;
    Button btnSeInscrever, btnDesinscrever;
    Viagem viagem;
    EditText editTextInformeDestino;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viagem = getArguments().getParcelable("viagem");
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        btnSeInscrever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTextInformeDestino.getText().toString().isEmpty()){
                    mostraSnackbar(view, getString(R.string.preencha_destino));
                }
                else {
                    FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
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
                                            if (usuario.getEmail().equals(usuarioAtual.getEmail())) {
                                                Passageiro passageiro = new Passageiro();
                                                passageiro.setNome(usuario.getNome());
                                                passageiro.setEmail(usuario.getEmail());
                                                passageiro.setSenha(usuario.getSenha());
                                                passageiro.setDestino(editTextInformeDestino.getText().toString());
                                                if(viagem.contemPassageiro(passageiro)){
                                                    mostraSnackbar(view, getString(R.string.voce_ja_inscrito));
                                                }
                                                else{
                                                    FirebaseService.addPassageiroNaViagem(passageiro, viagem);
                                                    mostraSnackbar(view, getString(R.string.voce_inscreveu_sucesso));
                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            getParentFragmentManager().popBackStack();
                                                            getParentFragmentManager().popBackStack();
                                                        }
                                                    }, 2500);
                                                }
                                            }
                                        }
                                    }
                                }
                            })
                    ;
                }
            }
        });

        btnDesinscrever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
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
                                        if (usuario.getEmail().equals(usuarioAtual.getEmail())) {
                                            Passageiro passageiro = new Passageiro();
                                            passageiro.setNome(usuario.getNome());
                                            passageiro.setEmail(usuario.getEmail());
                                            passageiro.setSenha(usuario.getSenha());
                                            passageiro.setDestino(editTextInformeDestino.getText().toString());
                                            if(!viagem.contemPassageiro(passageiro)){
                                                mostraSnackbar(view, getString(R.string.voce_nao_esta_inscrito));
                                            }
                                            else{
                                                FirebaseService.removePassageiroDaViagem(passageiro, viagem);
                                                mostraSnackbar(view, getString(R.string.voce_se_desinscreveu_com_sucesso));
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        getParentFragmentManager().popBackStack();
                                                        getParentFragmentManager().popBackStack();
                                                    }
                                                }, 2500);
                                            }
                                        }
                                    }
                                }
                            }
                        })
                ;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dados_viagem_passageiro, container, false);
        textViewNomeViagem = view.findViewById(R.id.textViewNomeViagemDadosPassageiro);
        textViewOrigemViagem = view.findViewById(R.id.textViewOrigemViagemDadosPassageiro);
        textViewDestinoViagem = view.findViewById(R.id.textViewDestinoViagemDadosPassageiro);
        textViewDiasSemana = view.findViewById(R.id.textViewDiasSemanaViagemDadosPassageiro);
        textViewHorarioOrigemViagem = view.findViewById(R.id.textViewHorarioOrigemViagemDadosPassageiro);
        getTextViewHorarioDestinoViagem = view.findViewById(R.id.textViewHorarioDestinoViagemDadosPassageiro);
        recyclerViewPassageiros = view.findViewById(R.id.recyclerViewPassageirosViagemDadosPassageiro);
        btnSeInscrever = view.findViewById(R.id.buttonViagemDadosPassageiro);
        btnDesinscrever = view.findViewById(R.id.buttonDesinscreverViagemDadosPassageiro);
        editTextInformeDestino = view.findViewById(R.id.editTextInformeSeuDestino);
        textViewTotalPassageiros = view.findViewById(R.id.textViewTotalPassageirosInscritosViagemDadosPassageiro);

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



























