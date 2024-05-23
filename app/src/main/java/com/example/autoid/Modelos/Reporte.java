package com.example.autoid.Modelos;

public class Reporte {
    private int idReporte;
    private String resultadoObtenido;
    private String img;
    private int idUsuario;
    private int estado;
    private int eliminado;
    private String created_at;

    public int getIdReporte() {
        return idReporte;
    }

    public void setIdReporte(int idReporte) {
        this.idReporte = idReporte;
    }
    public Reporte(int idReporte,String resultadoObtenido, String img, String created_at, int idUsuario, int estado, int eliminado) {
        this.idReporte = idReporte;
        this.resultadoObtenido = resultadoObtenido;
        this.img = img;
        this.idUsuario = idUsuario;
        this.estado = estado;
        this.eliminado = eliminado;
        this.created_at = created_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getResultadoObtenido() {
        return resultadoObtenido;
    }

    public void setResultadoObtenido(String resultadoObtenido) {
        this.resultadoObtenido = resultadoObtenido;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public int getEliminado() {
        return eliminado;
    }

    public void setEliminado(int eliminado) {
        this.eliminado = eliminado;
    }
}
