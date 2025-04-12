package com.instruction.emploiapp.model;

import javafx.beans.property.*;

public class Matiere {
    private final StringProperty nom;
    private final FloatProperty volumeHoraire;

    public Matiere() {
        this.nom = new SimpleStringProperty();
        this.volumeHoraire = new SimpleFloatProperty();
    }

    public Matiere(String nom, float volumeHoraire) {
        this.nom = new SimpleStringProperty(nom);
        this.volumeHoraire = new SimpleFloatProperty(volumeHoraire);
    }

    public String getNom() {
        return nom.get();
    }

    public StringProperty nomProperty() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom.set(nom);
    }

    public float getVolumeHoraire() {
        return volumeHoraire.get();
    }

    public FloatProperty volumeHoraireProperty() {
        return volumeHoraire;
    }

    public void setVolumeHoraire(float volumeHoraire) {
        this.volumeHoraire.set(volumeHoraire);
    }
}