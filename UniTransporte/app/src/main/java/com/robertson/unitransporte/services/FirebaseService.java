package com.robertson.unitransporte.services;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.robertson.unitransporte.objetos.Passageiro;
import com.robertson.unitransporte.objetos.Usuario;
import com.robertson.unitransporte.objetos.Viagem;

import java.util.HashMap;
import java.util.Map;

public class FirebaseService {
    static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void addPassageiroNaViagem(Passageiro passageiro, Viagem viagem){
        db.collection("Viagens")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot document : task.getResult()){
                            Gson gsonOrg = new GsonBuilder().setPrettyPrinting().create();;
                            String aux = gsonOrg.toJson(document.get("Viagem"));
                            Viagem viagemAux = gsonOrg.fromJson(aux, Viagem.class);
                            if(viagemAux.getNome().equals(viagem.getNome())){
                                viagem.getPassageiros().add(passageiro);
                                db.collection("Viagens")
                                        .document(document.getId())
                                        .update("passageiros", FieldValue.arrayUnion(passageiro));
                            }
                        }
                    }
                })
        ;
    }
    public static void removePassageiroDasViagens(String email){
        db.collection("Viagens")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot document : task.getResult()){
                            Gson gsonOrg = new GsonBuilder().setPrettyPrinting().create();
                            String aux = gsonOrg.toJson(document.get("Viagem"));
                            Viagem viagemAux = gsonOrg.fromJson(aux, Viagem.class);
                            viagemAux.setPassageiros(document.toObject(Viagem.class).getPassageiros());

                            for(Passageiro p : viagemAux.getPassageiros()){
                                if(p.getEmail().equals(email)){
                                    db.collection("Viagens")
                                            .document(document.getId())
                                            .update("passageiros", FieldValue.arrayRemove(p));
                                }
                            }
                        }
                    }
                })
        ;
    }
    public static void removePassageiroDaViagem(Passageiro passageiro, Viagem viagem){
        db.collection("Viagens")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot document : task.getResult()){
                            Gson gsonOrg = new GsonBuilder().setPrettyPrinting().create();
                            String aux = gsonOrg.toJson(document.get("Viagem"));
                            Viagem viagemAux = gsonOrg.fromJson(aux, Viagem.class);
                            viagemAux.setPassageiros(document.toObject(Viagem.class).getPassageiros());

                            if(viagemAux.getNome().equals(viagem.getNome())){
                                for(Passageiro p : viagemAux.getPassageiros()){
                                    if(p.getEmail().equals(passageiro.getEmail())){
                                        db.collection("Viagens")
                                                .document(document.getId())
                                                .update("passageiros", FieldValue.arrayRemove(p));
                                    }
                                }
                            }
                        }
                    }
                })
        ;
    }
    public static void apagaViagem(Viagem viagem){
        db.collection("Viagens")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot document : task.getResult()){
                            Gson gsonOrg = new GsonBuilder().setPrettyPrinting().create();
                            String aux = gsonOrg.toJson(document.get("Viagem"));
                            Viagem viagemAux = gsonOrg.fromJson(aux, Viagem.class);

                            if(viagemAux.getNome().equals(viagem.getNome())){
                                db.collection("Viagens")
                                        .document(document.getId())
                                        .delete()
                                ;
                            }
                        }
                    }
                })
        ;
    }
}






















