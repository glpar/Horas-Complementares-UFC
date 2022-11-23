package com.robertson.unitransporte.objetos;

import java.io.Serializable;

public class Municipio implements Serializable {
    Integer id;
    String nome;

    public Municipio(Integer id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Municipio() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
