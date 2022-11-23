package com.robertson.unitransporte;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.robertson.unitransporte.fragments.CriaViagemFragment;
import com.robertson.unitransporte.fragments.ListarMeusPassageirosFragment;
import com.robertson.unitransporte.fragments.MostrarViagensFragment;
import com.robertson.unitransporte.fragments.NovoPassageiroFragment;
import com.robertson.unitransporte.fragments.OrgPerfilFragment;
import com.robertson.unitransporte.objetos.Organizador;
import com.robertson.unitransporte.objetos.Usuario;
import com.robertson.unitransporte.objetos.Viagem;

import java.util.ArrayList;

public class MainActivityOrg extends AppCompatActivity {
    public static Organizador organizador;
    public static ArrayList<Viagem> mDataSet = new ArrayList<>();
    public static ArrayList<Usuario> mDataSetUsuarios = new ArrayList<>();
    public static FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_org);

        fragmentManager = getSupportFragmentManager();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        organizador = new Organizador();
        organizador = (Organizador) getIntent().getSerializableExtra("organizador");

        if(savedInstanceState == null){
            mostraViagens();
        }
    }
    public void mostraViagens(){
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
                            MostrarViagensFragment mostrarViagensFragment = new MostrarViagensFragment();
                            getSupportFragmentManager().beginTransaction()
                            .add(R.id.frameLayoutFragment, mostrarViagensFragment)
                            .commit();
                        }
                    }
                })
        ;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_org, menu);
        return true;
    }

    public void realizarLogout(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivityOrg.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.itemListarPassageiros:
                irListarPassageiros();
                return true;
            case R.id.itemNovoPassageiro:
                irNovoPassageiro();
                return true;
            case R.id.itemCriarViagem:
                irCriarViagem();
                return true;
            case R.id.itemMeuPefil:
                irPerfil();
                return true;
            case R.id.itemSair:
                realizarLogout();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
    public void irListarPassageiros(){
        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Usuários")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            mDataSetUsuarios.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.contains("OrganizadorCriador") &&
                                        document.get("OrganizadorCriador").toString().equals(usuarioAtual.getUid())) {
                                    Usuario usuario = new Usuario();
                                    Gson gsonOrg = new GsonBuilder().setPrettyPrinting().create();
                                    String aux = gsonOrg.toJson(document.get("Usuário"));
                                    usuario = gsonOrg.fromJson(aux, Usuario.class);
                                    mDataSetUsuarios.add(usuario);
                                }
                            }
                            ListarMeusPassageirosFragment listarMeusPassageirosFragment = new ListarMeusPassageirosFragment();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.frameLayoutFragment, listarMeusPassageirosFragment)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    }
                })
        ;
    }
    public void irNovoPassageiro(){
        NovoPassageiroFragment novoPassageiroFragment = new NovoPassageiroFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayoutFragment, novoPassageiroFragment)
                .addToBackStack(null)
                .commit();
    }
    public void irCriarViagem(){
        CriaViagemFragment criaViagemFragment = new CriaViagemFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayoutFragment, criaViagemFragment)
                .addToBackStack(null)
                .commit();
    }
    public void irPerfil(){
        OrgPerfilFragment orgPerfilFragment = new OrgPerfilFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("organizador", organizador);

        orgPerfilFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayoutFragment, orgPerfilFragment)
                .addToBackStack(null)
                .commit();

    }
}