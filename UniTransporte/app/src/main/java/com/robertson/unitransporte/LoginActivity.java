package com.robertson.unitransporte;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.robertson.unitransporte.objetos.Estado;
import com.robertson.unitransporte.objetos.Organizador;
import com.robertson.unitransporte.objetos.Passageiro;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    Button btnCadastro, btnLogin;
    EditText editTextEmail, editTextSenha;
    ProgressBar progressBar;
    Organizador organizador = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        if(usuarioAtual != null){
            organizadorOuPassageiro();
        }else {

            setContentView(R.layout.activity_login);

            editTextEmail = findViewById(R.id.editTextTextEmailAddress);
            editTextSenha = findViewById(R.id.editTextTextPassword);
            btnLogin = findViewById(R.id.button);
            progressBar = findViewById(R.id.progressBar);
            btnCadastro = findViewById(R.id.buttonCadastrarOrg);

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String strEmail = editTextEmail.getText().toString();
                    String strSenha = editTextSenha.getText().toString();
                    if (strEmail.isEmpty() || strSenha.isEmpty()) {
                        mostraSnackbar(view, getString(R.string.preencha_tudo));
                    } else {
                        autenticarUsuario(view);
                    }
                }
            });


            btnCadastro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    irCadastroActivity();
                }
            });
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    private void organizadorOuPassageiro(){
        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Organizadores")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getId().equals(usuarioAtual.getUid())){
                                    organizador = new Organizador();
                                    Gson gsonOrg = new GsonBuilder().setPrettyPrinting().create();;
                                    String aux = gsonOrg.toJson(document.get("Organizador"));
                                    organizador = gsonOrg.fromJson(aux, Organizador.class);

                                    telaPrincipalOrg();
                                    return;
                                }
                            }
                            telaPrincipal();
                        }
                    }
                });
    }

    private void telaPrincipalOrg() {
        Intent intent = new Intent(LoginActivity.this, MainActivityOrg.class);
        intent.putExtra("organizador", (Parcelable) organizador);
        startActivity(intent);
        finish();
    }

    void irCadastroActivity(){
        Intent intent = new Intent(this, CadastroActivity.class);
        startActivity(intent);
    }
    private void mostraSnackbar(View view, String mensagem){
        Snackbar snackbar = Snackbar.make(view, mensagem, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(Color.WHITE);
        snackbar.setTextColor(Color.BLACK);
        snackbar.show();
    }
    private void autenticarUsuario(View view) {
        String strEmail = editTextEmail.getText().toString();
        String strSenha = editTextSenha.getText().toString();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(strEmail, strSenha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressBar.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            organizadorOuPassageiro();
                        }
                    }, 3000);
                }else{
                    try{
                        throw task.getException();
                    }
                    catch(Exception e){
                        mostraSnackbar(view, getString(R.string.email_senha_incorretos));
                    }
                }
            }
        });
    }

    private void telaPrincipal() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}