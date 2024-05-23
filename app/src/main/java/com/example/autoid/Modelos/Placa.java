package com.example.autoid.Modelos;

public class Placa {
    private int idPlaca;
    private String numMatricula;
    private String created_at;

    public Placa(int idPlaca, String numMatricula, String created_at) {
        this.idPlaca = idPlaca;
        this.numMatricula = numMatricula;
        this.created_at = created_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public Placa(int idPlaca, String numMatricula) {
        this.idPlaca = idPlaca;
        this.numMatricula = numMatricula;
    }
    public Placa(String numMatricula) {
        this.numMatricula = numMatricula;
    }

    public int getIdPlaca() {
        return idPlaca;
    }

    public void setIdPlaca(int idPlaca) {
        this.idPlaca = idPlaca;
    }

    public String getNumMatricula() {
        return numMatricula;
    }

    public void setNumMatricula(String numMatricula) {
        this.numMatricula = numMatricula;
    }
}
