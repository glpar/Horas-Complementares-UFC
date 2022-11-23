package com.robertson.unitransporte.objetos;

import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;

import com.robertson.unitransporte.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Viagem implements Parcelable {
    String nome;
    Municipio origem = new Municipio();
    Municipio destino = new Municipio();
    ArrayList<Boolean> dias = new ArrayList<Boolean>();
    String horarioSaidaOrigem;
    String horarioSaidaDestino;
    ArrayList<Passageiro> passageiros = new ArrayList<>();

    public boolean contemPassageiro(Passageiro p){
        for(Passageiro passageiro : passageiros){
            if(passageiro.getEmail().equals(p.getEmail())){
                return true;
            }
        }
        return false;
    }

    public ArrayList<Passageiro> getPassageiros() {
        return passageiros;
    }

    public void setPassageiros(ArrayList<Passageiro> passageiros) {
        this.passageiros = passageiros;
    }

    public Viagem(){
        for (int i=0; i < 7; i++){
            dias.add(false);
        }
    }
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Municipio getOrigem() {
        return origem;
    }

    public void setOrigem(Municipio origem) {
        this.origem = origem;
    }

    public Municipio getDestino() {
        return destino;
    }

    public void setDestino(Municipio destino) {
        this.destino = destino;
    }

    public ArrayList<Boolean> getDias() {
        return dias;
    }

    public void setDias(ArrayList dias) {
        this.dias = dias;
    }

    public String diasSemanaToString(){
        StringBuilder res = new StringBuilder();
        for(int i=0; i < dias.size(); i++){
            if(dias.get(i)){
                switch (i){
                    case 0:{
                        res.append("dom ");
                        break;
                    }
                    case 1:{
                        res.append("seg ");
                        break;
                    }
                    case 2:{
                        res.append("ter ");
                        break;
                    }
                    case 3:{
                        res.append("qua ");
                        break;
                    }
                    case 4:{
                        res.append("qui ");
                        break;
                    }
                    case 5:{
                        res.append("sex ");
                        break;
                    }
                    case 6:{
                        res.append("sáb");
                        break;
                    }
                }
            }
        }
        return res.toString();
    }
    public String getHorarioSaidaOrigem() {
        return horarioSaidaOrigem;
    }

    public void setHorarioSaidaOrigem(String horarioSaidaOrigem) {
        this.horarioSaidaOrigem = horarioSaidaOrigem;
    }

    public String getHorarioSaidaDestino() {
        return horarioSaidaDestino;
    }

    public void setHorarioSaidaDestino(String horarioSaidaDestino) {
        this.horarioSaidaDestino = horarioSaidaDestino;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.nome);
        dest.writeSerializable(this.origem);
        dest.writeSerializable(this.destino);
        dest.writeList(this.dias);
        dest.writeString(this.horarioSaidaOrigem);
        dest.writeString(this.horarioSaidaDestino);
        dest.writeList(this.passageiros);
    }

    public void readFromParcel(Parcel source) {
        this.nome = source.readString();
        this.origem = (Municipio) source.readSerializable();
        this.destino = (Municipio) source.readSerializable();
        this.dias = new ArrayList<Boolean>();
        source.readList(this.dias, Boolean.class.getClassLoader());
        this.horarioSaidaOrigem = source.readString();
        this.horarioSaidaDestino = source.readString();
        this.passageiros = new ArrayList<Passageiro>();
        source.readList(this.passageiros, Passageiro.class.getClassLoader());
    }

    protected Viagem(Parcel in) {
        this.nome = in.readString();
        this.origem = (Municipio) in.readSerializable();
        this.destino = (Municipio) in.readSerializable();
        this.dias = new ArrayList<Boolean>();
        in.readList(this.dias, Boolean.class.getClassLoader());
        this.horarioSaidaOrigem = in.readString();
        this.horarioSaidaDestino = in.readString();
        this.passageiros = new ArrayList<Passageiro>();
        in.readList(this.passageiros, Passageiro.class.getClassLoader());
    }

    public static final Creator<Viagem> CREATOR = new Creator<Viagem>() {
        @Override
        public Viagem createFromParcel(Parcel source) {
            return new Viagem(source);
        }

        @Override
        public Viagem[] newArray(int size) {
            return new Viagem[size];
        }
    };
}
