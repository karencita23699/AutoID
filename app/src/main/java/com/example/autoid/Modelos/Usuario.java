package com.example.autoid.Modelos;

public class Usuario {
    public int idUsuario;
    public String primerApellido;
    public String segundoApellido;
    public String nombres;
    public String correo;
    public String telefono;
    public String nomUsuario;
    public String contrasena;
    public String genero;

    public Usuario(int idUsuario, String primerApellido, String segundoApellido, String nombres, String correo, String telefono, String nomUsuario, String contrasena, String genero) {
        this.idUsuario = idUsuario;
        this.primerApellido = primerApellido;
        this.segundoApellido = segundoApellido;
        this.nombres = nombres;
        this.correo = correo;
        this.telefono = telefono;
        this.nomUsuario = nomUsuario;
        this.contrasena = contrasena;
        this.genero = genero;
    }
    public Usuario(int idUsuario, String primerApellido, String segundoApellido, String nombres, String correo, String nomUsuario, String telefono, String genero) {
        this.idUsuario = idUsuario;
        this.primerApellido = primerApellido;
        this.segundoApellido = segundoApellido;
        this.nombres = nombres;
        this.correo = correo;
        this.telefono = telefono;
        this.nomUsuario = nomUsuario;
        this.contrasena = contrasena;
        this.genero = genero;
    }

    public Usuario(String primerApellido, String segundoApellido, String nombres, String correo, String telefono, String nomUsuario, String contrasena, String genero) {
        this.primerApellido = primerApellido;
        this.segundoApellido = segundoApellido;
        this.nombres = nombres;
        this.correo = correo;
        this.telefono = telefono;
        this.nomUsuario = nomUsuario;
        this.contrasena = contrasena;
        this.genero = genero;
    }


    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getPrimerApellido() {
        return primerApellido;
    }

    public void setPrimerApellido(String primerApellido) {
        this.primerApellido = primerApellido;
    }

    public String getSegundoApellido() {
        return segundoApellido;
    }

    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getNomUsuario() {
        return nomUsuario;
    }

    public void setNomUsuario(String nomUsuario) {
        this.nomUsuario = nomUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }
}
