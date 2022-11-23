package com.robertson.unitransporte;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.robertson.unitransporte.fragments.MinhasViagensDisponiveisFragment;
import com.robertson.unitransporte.fragments.MinhasViagensFragment;
import com.robertson.unitransporte.fragments.MostrarViagensFragment;
import com.robertson.unitransporte.fragments.OrgPerfilFragment;
import com.robertson.unitransporte.fragments.UsuarioPerfilFragment;
import com.robertson.unitransporte.objetos.Passageiro;
import com.robertson.unitransporte.objetos.Usuario;
import com.robertson.unitransporte.objetos.Viagem;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public static ArrayList<Viagem> mDataSet = new ArrayList<>();
    public static ArrayList<Viagem> mDataSetDisponiveis = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(savedInstanceState == null){
            mostrarMinhasViagens();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
    public void mostrarMinhasViagens(){
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
                                    MinhasViagensFragment minhasViagensFragment = new MinhasViagensFragment();
                                    getSupportFragmentManager().beginTransaction()
                                            .add(R.id.frameLayoutFragmentUsuario, minhasViagensFragment)
                                            .commit();
                                }
                            }
                        })
                ;
            }
        });
    }
    public void mostrarViagensDisponiveis(){
        QueryDocumentSnapshot[] usuario = new QueryDocumentSnapshot[1];
        getUsuarioAtual(new FirestoreCallback() {
            @Override
            public void onCallback(QueryDocumentSnapshot document2) {
                usuario[0] = document2;
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Viagens")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    mDataSetDisponiveis.clear();
                                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                        if(Objects.requireNonNull(document.get("OrganizadorCriador")).toString().equals(
                                                Objects.requireNonNull(usuario[0].get("OrganizadorCriador")).toString())) {
                                            Viagem viagem = new Viagem();
                                            Gson gsonOrg = new GsonBuilder().setPrettyPrinting().create();;
                                            String aux = gsonOrg.toJson(document.get("Viagem"));
                                            viagem = gsonOrg.fromJson(aux, Viagem.class);
                                            viagem.setPassageiros(document.toObject(Viagem.class).getPassageiros());
                                            mDataSetDisponiveis.add(viagem);
                                        }
                                    }
                                    MinhasViagensDisponiveisFragment minhasViagensDisponiveisFragment = new MinhasViagensDisponiveisFragment();
                                    getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.frameLayoutFragmentUsuario, minhasViagensDisponiveisFragment)
                                            .addToBackStack(null)
                                            .commit();
                                }
                            }
                        })
                ;
            }
        });
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemViagensDisponiveis:
                mostrarViagensDisponiveis();
                return true;
            case R.id.itemMeuPefilUsuario:
                irPerfilUsuario();
                return true;
            case R.id.itemFazerLogoutUsuario:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void irPerfilUsuario(){
        UsuarioPerfilFragment userPerfilFragment = new UsuarioPerfilFragment();
        getUsuarioAtual(new FirestoreCallback() {
            @Override
            public void onCallback(QueryDocumentSnapshot user) {
                Gson gsonOrg = new GsonBuilder().setPrettyPrinting().create();;
                String aux = gsonOrg.toJson(user.get("Usuário"));
                Usuario usuario = gsonOrg.fromJson(aux, Usuario.class);

                Bundle bundle = new Bundle();
                bundle.putString("nomeCompleto", usuario.getNome());
                bundle.putString("email", usuario.getEmail());
                bundle.putString("cidade", usuario.getMunicipio().getNome());

                userPerfilFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayoutFragmentUsuario, userPerfilFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0 ) {
            getFragmentManager().popBackStack();
        }
        else {
            super.onBackPressed();
        }
    }
}