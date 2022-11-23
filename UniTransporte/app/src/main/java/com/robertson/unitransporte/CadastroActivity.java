package com.robertson.unitransporte;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Handler;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.robertson.unitransporte.objetos.ConectaApiIbge;
import com.robertson.unitransporte.objetos.Estado;
import com.robertson.unitransporte.objetos.Municipio;
import com.robertson.unitransporte.objetos.Organizador;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class CadastroActivity extends AppCompatActivity {
    Spinner spnEstados, spnMunicipios;
    EditText editTextEmail, editTextNomeComplet, editTextSenha, editTextSenhaConfirm;
    Button btnCadastrarOrgCadr, btnBuscaLocalizacao;
    Estado estado;
    Municipio municipio;
    Organizador organizador = new Organizador();
    LocationManager mLocManager;
    boolean buscouLoc = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        spnEstados = findViewById(R.id.spinnerEstados);
        spnMunicipios = findViewById(R.id.spinnerCidades);
        editTextEmail = findViewById(R.id.editTextEmailCadastroOrg);
        editTextNomeComplet = findViewById(R.id.editTextNomeCompOrg);
        editTextSenha = findViewById(R.id.editTextSenhaOrg);
        editTextSenhaConfirm = findViewById(R.id.editTextConfirmSenh);
        btnCadastrarOrgCadr = findViewById(R.id.buttonCadastrarOrgCadr);
        btnBuscaLocalizacao = findViewById(R.id.buttonBuscarLocalizacaoCadastroOrg);

        estado = new Estado();
        municipio = new Municipio();

        vinculaEstadosMunicipios();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)   != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(CadastroActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(CadastroActivity.this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            ActivityCompat.requestPermissions(CadastroActivity.this, new String[] {Manifest.permission.ACCESS_NETWORK_STATE}, 1);
            return;
        }
        mLocManager  = (LocationManager) getSystemService(CadastroActivity.this.LOCATION_SERVICE);
        mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, organizador);

        btnCadastrarOrgCadr.setOnClickListener(view -> {
            String strEmail = editTextEmail.getText().toString();
            String strNomeComplet = editTextNomeComplet.getText().toString();
            String strSenha = editTextSenha.getText().toString();
            String strSenhaConfirm = editTextSenhaConfirm.getText().toString();

            if(strEmail.isEmpty() || strNomeComplet.isEmpty() || strSenha.isEmpty() || strSenhaConfirm.isEmpty() || !buscouLoc){
                mostraSnackbar(view, getString(R.string.preencha_tudo));
            }else if(!strSenha.equals(strSenhaConfirm)){
                mostraSnackbar(view, getString(R.string.senhas_diferen));
            }else{
                cadastrarUsuario(view);
            }
        });
    }

    public void buscarInformacoesGPS(View v) {
        buscouLoc = true;
        if (mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            String texto = getString(R.string.latitude) + organizador.getLatitude() + "\n" +
                    getString(R.string.longitude) + organizador.getLongitude() + "\n";
            Toast.makeText(CadastroActivity.this, texto, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(CadastroActivity.this, getString(R.string.gps_off), Toast.LENGTH_LONG).show();
        }
        mostrarGoogleMaps(organizador.getLatitude(), organizador.getLongitude());
    }

    private void mostrarGoogleMaps(double latitude, double longitude) {
        WebView webViewMaps = (WebView) findViewById(R.id.webViewMaps);
        webViewMaps.getSettings().setJavaScriptEnabled(true);
        webViewMaps.loadUrl("https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude);
    }

    private void vinculaEstadosMunicipios() {
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

        ArrayAdapter<String> adapterEstados = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strEstados);

        spnEstados.setAdapter(adapterEstados);
        spnEstados.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                solicitaMunicipios(strSiglas.get(i));
                montaEstadoSelecionado(strEstados.get(i));
            }
            private void montaEstadoSelecionado(String nomeEstado) {
                for(Estado est : estados){
                    if(est.getNome().equals(nomeEstado)){
                        estado.setId(est.getId());
                        estado.setNome(est.getNome());
                        estado.setSigla(est.getSigla());
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void solicitaMunicipios(String siglaEstado) {
        String respostaIBGE = getRespostaIbge("municipio", siglaEstado);
        Gson gsonMunicipios = new GsonBuilder().setPrettyPrinting().create();
        Municipio[] municipios = gsonMunicipios.fromJson(respostaIBGE, Municipio[].class);
        ArrayList<String> strMunicipios = new ArrayList<String>();

        for(Municipio municipio : municipios){
            strMunicipios.add(municipio.getNome());
        }

        Collections.sort(strMunicipios);

        ArrayAdapter<String> adapterMunicipios = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strMunicipios);

        spnMunicipios.setAdapter(adapterMunicipios);
        spnMunicipios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                montaMunicipioSelecionado(strMunicipios.get(i));
            }
            private void montaMunicipioSelecionado(String nomeMunicipio) {
                for(Municipio est : municipios){
                    if(est.getNome().equals(nomeMunicipio)){
                        municipio.setId(est.getId());
                        municipio.setNome(est.getNome());
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

    private void mostraSnackbar(View view, String mensagem){
        Snackbar snackbar = Snackbar.make(view, mensagem, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(Color.WHITE);
        snackbar.setTextColor(Color.BLACK);
        snackbar.show();
    }

    private void cadastrarUsuario(View view){
        String strEmail = editTextEmail.getText().toString();
        String strSenha = editTextSenha.getText().toString();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(strEmail, strSenha).addOnCompleteListener(task ->{
            if(task.isSuccessful()){
                salvarDadosUsuario();
                mostraSnackbar(view, getString(R.string.sucesso_cadastro));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(CadastroActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
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
                    erro = getString(R.string.erro_cadastro);
                }
                mostraSnackbar(view, erro);
            }
        });
    }
    private void salvarDadosUsuario(){
        String strNomeComp = editTextNomeComplet.getText().toString();
        String strEmail = editTextEmail.getText().toString();
        String strSenha = editTextSenha.getText().toString();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        organizador.setEmail(strEmail);
        organizador.setSenha(strSenha);
        organizador.setEstado(estado);
        organizador.setMunicipio(municipio);
        organizador.setNome(strNomeComp);

        Map<String, Object> usuarios = new HashMap<>();
        usuarios.put("Organizador", organizador);

        String usuarioID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        DocumentReference documentReference = db.collection(getString(R.string.organizadores)).document(usuarioID);
        documentReference.set(usuarios).addOnSuccessListener(unused ->
                Log.d("db", "Sucesso ao salvar os dados.")).addOnFailureListener(e ->
                Log.d("db_error", "Erro ao salvar os dados " + e.toString()));

    }
}




















