package com.robertson.unitransporte.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.robertson.unitransporte.MainActivityOrg;
import com.robertson.unitransporte.R;
import com.robertson.unitransporte.objetos.ConectaApiIbge;
import com.robertson.unitransporte.objetos.Estado;
import com.robertson.unitransporte.objetos.Municipio;
import com.robertson.unitransporte.objetos.Viagem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class CriaViagemFragment extends Fragment {

    Viagem novaViagem;
    EditText editTextNomeViagem, editTextHoraSaidaOrigem, editTextHoraSaidaDestino;
    Spinner spnOrigemEstado, spnOrigemMunicipio, spnDestinoEstado, spnDestinoMunicipio;
    Button buttonDiasSemana, buttonConfirmar;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cria_viagem, container, false);

        Toolbar toolbar = view.findViewById(R.id.mToolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });

        editTextNomeViagem = view.findViewById(R.id.editTextNomeCriaViagem);
        editTextHoraSaidaOrigem = view.findViewById(R.id.editTextHoraSaidaOrigemCriaViagem);
        editTextHoraSaidaDestino = view.findViewById(R.id.editTextHoraSaidaDestinoCriaViagem);
        spnOrigemEstado = view.findViewById(R.id.spinnerEstados);
        spnOrigemMunicipio = view.findViewById(R.id.spinnerCidades);
        spnDestinoEstado = view.findViewById(R.id.spinnerEstadosDestino);
        spnDestinoMunicipio = view.findViewById(R.id.spinnerCidadesDestino);
        buttonDiasSemana = view.findViewById(R.id.buttonDiasSemanaCriViagem);
        buttonConfirmar = view.findViewById(R.id.buttonCadastrarCriaViagem);
        progressBar = view.findViewById(R.id.progressBarCriaViagem);

        novaViagem = new Viagem();

        vinculaEstadosMunicipios(spnOrigemEstado, spnOrigemMunicipio, "origem");
        vinculaEstadosMunicipios(spnDestinoEstado, spnDestinoMunicipio, "destino");
        constroiSelecDiasSemana();



        buttonConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                novaViagem.setNome(editTextNomeViagem.getText().toString());
                novaViagem.setHorarioSaidaOrigem(editTextHoraSaidaOrigem.getText().toString());
                novaViagem.setHorarioSaidaDestino(editTextHoraSaidaDestino.getText().toString());
                if (novaViagem.getNome().isEmpty() || novaViagem.getHorarioSaidaOrigem().isEmpty() || novaViagem.getHorarioSaidaDestino().isEmpty() || !marcouDiaSemana()) {
                   mostraSnackbar(view, getString(R.string.preencha_tudo));
                }else{
                    cadastraViagem(view);
                }
            }
        });


        // Inflate the layout for this fragment
        return view;



    }

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

    private void cadastraViagem(View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> viagens = new HashMap<>();
        viagens.put("Viagem", novaViagem);
        viagens.put("OrganizadorCriador", FirebaseAuth.getInstance().getCurrentUser().getUid());

        db.collection(getString(R.string.viagens))
            .add(viagens)
            .addOnSuccessListener(unused -> {
                progressBar.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mostraSnackbar(view, getString(R.string.sucesso_criar_viagem));
                        MainActivityOrg.mDataSet.add(novaViagem);
                        irTelaInicial();
                    }
                }, 3000);
            })
            .addOnFailureListener(e ->{
                Log.d("db_error", "Erro ao salvar os dados " + e);
                mostraSnackbar(view, getString(R.string.erro_salvar_dados_db));
        });
    }
    private void irTelaInicial(){
        getParentFragmentManager().popBackStack();
    }
    private void mostraSnackbar(View view, String mensagem){
        Snackbar snackbar = Snackbar.make(view, mensagem, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(Color.WHITE);
        snackbar.setTextColor(Color.BLACK);
        snackbar.show();
    }
    public boolean marcouDiaSemana(){
        boolean res = false;
        for(boolean b : novaViagem.getDias()){
            if (b) {
                res = true;
                break;
            }
        }
        return res;
    }
    private void vinculaEstadosMunicipios(Spinner spnEstado, Spinner spnMunicipio, String flag) {
        String respostaIBGE = getRespostaIbge("estado");

        Gson gsonEstados = new GsonBuilder().setPrettyPrinting().create();
        Estado[] estados = gsonEstados.fromJson(respostaIBGE, Estado[].class);
        ArrayList<String> strEstados = new ArrayList<String>();
        ArrayList<String> strSiglas = new ArrayList<String>();

        for(Estado estado : estados){
            strEstados.add(estado.getNome());
        }
        Collections.sort(strEstados);

        for(String str : strEstados){
            for(Estado estado : estados){
                if(str.equals(estado.getNome())){
                    strSiglas.add(estado.getSigla());
                }
            }
        }

        ArrayAdapter<String> adapterEstados = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, strEstados);

        spnEstado.setAdapter(adapterEstados);
        spnEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                solicitaMunicipios(strSiglas.get(i), spnMunicipio, flag);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    private void solicitaMunicipios(String siglaEstado, Spinner spnMunicipio, String flag) {
        String respostaIBGE = getRespostaIbge("municipio", siglaEstado);
        Gson gsonMunicipios = new GsonBuilder().setPrettyPrinting().create();
        Municipio[] municipios = gsonMunicipios.fromJson(respostaIBGE, Municipio[].class);
        ArrayList<String> strMunicipios = new ArrayList<String>();

        for(Municipio municipio : municipios){
            strMunicipios.add(municipio.getNome());
        }

        Collections.sort(strMunicipios);

        ArrayAdapter<String> adapterMunicipios = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, strMunicipios);

        spnMunicipio.setAdapter(adapterMunicipios);
        spnMunicipio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                montaMunicipioSelecionado(strMunicipios.get(i));
            }
            private void montaMunicipioSelecionado(String nomeMunicipio) {
                for(Municipio est : municipios){
                    if(est.getNome().equals(nomeMunicipio)){
                        if(flag.equals("origem")){
                            novaViagem.getOrigem().setId(est.getId());
                            novaViagem.getOrigem().setNome(est.getNome());
                        }else if(flag.equals("destino")){
                            novaViagem.getDestino().setId(est.getId());
                            novaViagem.getDestino().setNome(est.getNome());
                        }

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    @Nullable
    private String getRespostaIbge (String ...parametroBuscado) {
        String respostaIBGE = null;
        ConectaApiIbge conectaApiIbge = new ConectaApiIbge();
        try {
            respostaIBGE = (String)conectaApiIbge.execute(parametroBuscado).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return respostaIBGE;
    }
    private void constroiSelecDiasSemana(){
        ArrayList<Integer> diasList = new ArrayList<>();
        String[] diasArray = {getString(R.string.domingo), getString(R.string.segunda),
                getString(R.string.terca), getString(R.string.quarta), getString(R.string.quinta),
                getString(R.string.sexta), getString(R.string.sabado)};

        buttonDiasSemana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Initialize alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                // set title
                builder.setTitle(R.string.selec_dias_semana);

                // set dialog non cancelable
                builder.setCancelable(false);

                boolean booleanAux[] = new boolean[7];
                for(int i=0; i < 7; i++){
                    booleanAux[i] = novaViagem.getDias().get(i);
                }
                builder.setMultiChoiceItems(diasArray, booleanAux, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        // check condition
                        if (b) {
                            // when checkbox selected
                            // Add position  in lang list
                            diasList.add(i);
                            // Sort array list
                            Collections.sort(diasList);
                        } else {
                            // when checkbox unselected
                            // Remove position from diasList
                            diasList.remove(Integer.valueOf(i));
                        }
                        for(int i2=0; i2 < 7; i2++){
                            novaViagem.getDias().set(i2, booleanAux[i2]);
                        }
                    }
                });

                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Initialize string builder
                        StringBuilder stringBuilder = new StringBuilder();
                        // use for loop
                        for (int j = 0; j < diasList.size(); j++) {
                            // concat array value
                            stringBuilder.append(diasArray[diasList.get(j)]);
                            // check condition
                            if (j != diasList.size() - 1) {
                                // When j value  not equal
                                // to lang list size - 1
                                // add comma
                                stringBuilder.append(", ");
                            }
                        }
                        // set text on textView
                        if(stringBuilder.length() > 0){
                            buttonDiasSemana.setText(stringBuilder.toString());
                        }

                    }
                });

                builder.setNegativeButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // dismiss dialog
                        dialogInterface.dismiss();
                    }
                });
                builder.setNeutralButton(getString(R.string.limpar_tudo), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // use for loop
                        for (int j = 0; j < novaViagem.getDias().size(); j++) {
                            // remove all selection
                            novaViagem.getDias().set(j, false);
                            // clear language list
                            diasList.clear();
                            // clear text view value
                            buttonDiasSemana.setText(getString(R.string.selec_dias_semana));
                        }
                    }
                });
                // show dialog
                builder.show();
            }
        });
    }
}