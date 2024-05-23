package com.example.autoid.Modelos;

public class Caja {
    float x_min;
    float y_min;
    float x_max;
    float y_max;

    public Caja(float x_min, float y_min, float x_max, float y_max) {
        this.x_min = x_min;
        this.y_min = y_min;
        this.x_max = x_max;
        this.y_max = y_max;
    }

    public Caja() {
    }

    public float getX_min() {
        return x_min;
    }

    public void setX_min(float x_min) {
        this.x_min = x_min;
    }

    public float getY_min() {
        return y_min;
    }

    public void setY_min(float y_min) {
        this.y_min = y_min;
    }

    public float getX_max() {
        return x_max;
    }

    public void setX_max(float x_max) {
        this.x_max = x_max;
    }

    public float getY_max() {
        return y_max;
    }

    public void setY_max(float y_max) {
        this.y_max = y_max;
    }
}
