package com.example.autoid.Modelos;

import com.example.autoid.Modelos.BoundingBox;

public class Detection {
    private float confidence;
    private int classIndex;
    private String clase;
    private BoundingBox boundingBox;

    public String getClase() {
        return clase;
    }

    public void setClase(String clase) {
        this.clase = clase;
    }

    private float presicion;

    // Constructor
    public Detection(float confidence, int classIndex, BoundingBox boundingBox, float presicion) {
        this.confidence = confidence;
        this.classIndex = classIndex;
        this.boundingBox = boundingBox;
        this.presicion = presicion;
    }

    public float getPresicion() {
        return presicion;
    }

    public void setPresicion(float presicion) {
        this.presicion = presicion;
    }

    public Detection() {
    }

    // Getters y Setters
    public float getConfidence() {
        return confidence;
    }

    public void setConfidence(float confidence) {
        this.confidence = confidence;
    }

    public int getClassIndex() {
        return classIndex;
    }

    public void setClassIndex(int classIndex) {
        this.classIndex = classIndex;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }
}
