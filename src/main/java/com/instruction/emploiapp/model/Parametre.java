package com.instruction.emploiapp.model;

import java.io.Serial;
import java.io.Serializable;

public class Parametre implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int nbCompagnies;
    private int nbCompagniesNonDisponibles;
    private String periode;

    public Parametre(int nbCompagnies, int nbCompagniesNonDisponibles, String periode) {
        this.nbCompagnies = nbCompagnies;
        this.nbCompagniesNonDisponibles = nbCompagniesNonDisponibles;
        this.periode = periode;
    }

    public int getNbCompagnies() {
        return nbCompagnies;
    }

    public void setNbCompagnies(int nbCompagnies) {
        this.nbCompagnies = nbCompagnies;
    }

    public int getNbCompagniesNonDisponibles() {
        return nbCompagniesNonDisponibles;
    }

    public void setNbCompagniesNonDisponibles(int nbCompagniesNonDisponibles) {
        this.nbCompagniesNonDisponibles = nbCompagniesNonDisponibles;
    }

    public String getPeriode() {
        return periode;
    }

    public void setPeriode(String periode) {
        this.periode = periode;
    }
}