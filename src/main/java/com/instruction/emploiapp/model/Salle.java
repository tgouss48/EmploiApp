package com.instruction.emploiapp.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Salle {

    private final IntegerProperty idSalle;
    private final StringProperty nom;
    private final StringProperty type;

    public Salle(String nom,String type) {
        this.idSalle = new SimpleIntegerProperty();
        this.nom = new SimpleStringProperty(nom);
        this.type = new SimpleStringProperty(type);
    }

    public int getIdSalle() {
        return idSalle.get();
    }

    public void setIdSalle(int idSalle) {
        this.idSalle.set(idSalle);
    }

    public IntegerProperty idSalleProperty() {
        return idSalle;
    }

    public String getNom() {
        return nom.get();
    }

    public void setNom(String nom) {
        this.nom.set(nom);
    }

    public StringProperty nomProperty() {
        return nom;
    }

    public String getType() {
        return type.get();
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public StringProperty typeProperty() {
        return type;
    }
}