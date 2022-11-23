package com.robertson.unitransporte.objetos;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

public class  Organizador implements Serializable, Parcelable, LocationListener {
    String email;
    String senha;
    String nome;
    Estado estado;
    Municipio municipio;
    double latitude;
    double longitude;



    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Organizador(String email, String senha, String nome, Estado estado, Municipio municipio, double latitude, double longitude) {
        this.email = email;
        this.senha = senha;
        this.nome = nome;
        this.estado = estado;
        this.municipio = municipio;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Organizador(){}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Municipio getMunicipio() {
        return municipio;
    }

    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        this.latitude  = location.getLatitude();
        this.longitude = location.getLongitude();
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }

    @Override
    public void onFlushComplete(int requestCode) {
        LocationListener.super.onFlushComplete(requestCode);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.email);
        dest.writeString(this.senha);
        dest.writeString(this.nome);
        dest.writeSerializable(this.estado);
        dest.writeSerializable(this.municipio);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
    }

    public void readFromParcel(Parcel source) {
        this.email = source.readString();
        this.senha = source.readString();
        this.nome = source.readString();
        this.estado = (Estado) source.readSerializable();
        this.municipio = (Municipio) source.readSerializable();
        this.latitude = source.readDouble();
        this.longitude = source.readDouble();
    }

    protected Organizador(Parcel in) {
        this.email = in.readString();
        this.senha = in.readString();
        this.nome = in.readString();
        this.estado = (Estado) in.readSerializable();
        this.municipio = (Municipio) in.readSerializable();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
    }

    public static final Creator<Organizador> CREATOR = new Creator<Organizador>() {
        @Override
        public Organizador createFromParcel(Parcel source) {
            return new Organizador(source);
        }

        @Override
        public Organizador[] newArray(int size) {
            return new Organizador[size];
        }
    };
}
