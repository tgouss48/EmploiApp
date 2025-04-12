package com.instruction.emploiapp.model;

import javafx.beans.property.*;

public class Instructeur {

    private final IntegerProperty id;
    private final StringProperty nom;
    private final StringProperty grade;
    private final StringProperty matiere;
    private final StringProperty idCie;

    public Instructeur() {
        this.id = new SimpleIntegerProperty();
        this.nom = new SimpleStringProperty();
        this.grade = new SimpleStringProperty();
        this.matiere = new SimpleStringProperty();
        this.idCie = new SimpleStringProperty();
    }

    public Instructeur(int id, String nom, String grade, String matiere, String idCie) {
        this.id = new SimpleIntegerProperty(id);
        this.nom = new SimpleStringProperty(nom);
        this.grade = new SimpleStringProperty(grade);
        this.matiere = new SimpleStringProperty(matiere);
        this.idCie = new SimpleStringProperty(idCie);
    }

    public Instructeur(String nom, String grade, String matiere, String idCie) {
        this(0, nom, grade, matiere, idCie);
    }

    public int getId() {
        return id.get();
    }

    public void setID(int id) {
        this.id.set(id);
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

    public String getGrade() {
        return grade.get();
    }

    public StringProperty gradeProperty() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade.set(grade);
    }

    public String getMatiere() {
        return matiere.get();
    }

    public StringProperty matiereProperty() {
        return matiere;
    }

    public void setMatiere(String matiere) {
        this.matiere.set(matiere);
    }

    public String getIdCie() {
        return idCie.get();
    }

    public StringProperty idCieProperty() {
        return idCie;
    }

    public void setIdCie(String idCie) {
        this.idCie.set(idCie);
    }
}
