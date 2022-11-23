package com.robertson.unitransporte.fragments;

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
import android.widget.Spinner;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.robertson.unitransporte.MainActivityOrg;
import com.robertson.unitransporte.R;
import com.robertson.unitransporte.objetos.ConectaApiIbge;
import com.robertson.unitransporte.objetos.Estado;
import com.robertson.unitransporte.objetos.Municipio;

import com.robertson.unitransporte.objetos.Usuario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class NovoPassageiroFragment extends Fragment {
    EditText editTextNome, editTextEmail, editTextSenha;
    Spinner spnEstado, spnMunicipio;
    Button btnConfirmar;
    Usuario usuario = new Usuario();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_novo_passageiro, container, false);
        editTextNome = view.findViewById(R.id.editTextNomeNovoPassageiro);
        editTextEmail = view.findViewById(R.id.editTextEmailNovoPassageiro);
        editTextSenha = view.findViewById(R.id.editTextSenhaNovoPassageiro);
        spnEstado = view.findViewById(R.id.spinnerEstadosNovoPassageiro);
        spnMunicipio = view.findViewById(R.id.spinnerCidadesNovoPassageiro);
        btnConfirmar = view.findViewById(R.id.buttonConfirmarNovoPassageiro);

        Toolbar toolbar = view.findViewById(R.id.mToolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });


        vinculaEstadosMunicipios(spnEstado, spnMunicipio);

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usuario.setNome(editTextNome.getText().toString());
                usuario.setEmail(editTextEmail.getText().toString());
                usuario.setSenha(editTextSenha.getText().toString());
                if(usuario.getNome().isEmpty() || usuario.getEmail().isEmpty() || usuario.getSenha().isEmpty()){
                    mostraSnackbar(view, getString(R.string.preencha_tudo));
                }else{
                    cadastrarUsuario(view);
                }
            }
        });

        return view;
    }
    private void mostraSnackbar(View view, String mensagem){
        Snackbar snackbar = Snackbar.make(view, mensagem, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(Color.WHITE);
        snackbar.setTextColor(Color.BLACK);
        snackbar.show();
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

    private void cadastrarUsuario(View view){
        String strEmail = editTextEmail.getText().toString();
        String strSenha = editTextSenha.getText().toString();

        String user = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(strEmail, strSenha).addOnCompleteListener(task ->{
            if(task.isSuccessful()){
                salvarDadosUsuario(user);
                mostraSnackbar(view, getString(R.string.sucesso_cadastro_passageiro));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FirebaseAuth.getInstance().signOut();
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(MainActivityOrg.organizador.getEmail(), MainActivityOrg.organizador.getSenha());
                        getParentFragmentManager().popBackStack();
                    }
                }, 1500);
            }else {
                String erro;
                try {
                    throw Objects.requireNonNull(task.getException());
                } catch (FirebaseAuthWeakPasswordException e) {
                    erro = getString(R.string.senha_minino6);
                } catch (FirebaseAuthUserCollisionException e) {
                    erro = getString(R.string.email_ja_cadastrado);
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    erro = getString(R.string.email_invalido);
                }catch(Exception e){
                    erro = getString(R.string.erro_cadastro_passageiro);
                }
                mostraSnackbar(view, erro);
            }
        });
    }
    private void salvarDadosUsuario(String user){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> usuarios = new HashMap<>();

        String usuarioID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        usuarios.put("Usuário", usuario);
        usuarios.put("OrganizadorCriador", user);
        Log.d("linha136", user);

        DocumentReference documentReference = db.collection("Usuários").document(usuarioID);
        documentReference.set(usuarios).addOnSuccessListener(unused ->
                Log.d("db", "Sucesso ao salvar os dados.")).addOnFailureListener(e ->
                Log.d("db_error", "Erro ao salvar os dados " + e.toString()));

    }
    private void vinculaEstadosMunicipios(Spinner spnEstado, Spinner spnMunicipio) {
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
                for(Estado e : estados){
                    if(e.getSigla().equals(strSiglas.get(i))){
                        usuario.setEstado(e);
                    }
                }
                solicitaMunicipios(strSiglas.get(i), spnMunicipio);
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
    private void solicitaMunicipios(String siglaEstado, Spinner spnMunicipio) {
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
                for(Municipio m : municipios){
                    if(m.getNome().equals(strMunicipios.get(i))){
                        usuario.setMunicipio(m);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}