package com.instruction.emploiapp.model;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Suivi {

    private final IntegerProperty idCie;
    private final StringProperty matiere;
    private final FloatProperty volumeHoraire;

    public Suivi(int idCie, String matiere, float volumeHoraire) {
        this.idCie = new SimpleIntegerProperty(idCie);
        this.matiere = new SimpleStringProperty(matiere);
        this.volumeHoraire = new SimpleFloatProperty(volumeHoraire);
    }

    public int getIdCie() {
        return idCie.get();
    }

    public IntegerProperty idCieProperty() {
        return idCie;
    }

    public String getMatiere() {
        return matiere.get();
    }

    public StringProperty matiereProperty() {
        return matiere;
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
