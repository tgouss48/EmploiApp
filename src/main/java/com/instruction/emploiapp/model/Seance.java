package com.instruction.emploiapp.model;

public class Seance {
    private final String cie;
    private String matiere;
    private String instructeur;
    private String salle;
    private String horaire;

    public Seance(String cie, String matiere, String instructeur, String salle, String horaire) {
        this.cie = cie;
        this.matiere = matiere;
        this.instructeur = instructeur;
        this.salle = salle;
        this.horaire = horaire;
    }

    public void setHoraire(String horaire) {
        this.horaire = horaire;
    }

    public void setMatiere(String matiere) {
        this.matiere = matiere;
    }

    public void setInstructeur(String instructeur) {
        this.instructeur = instructeur;
    }

    public void setSalle(String salle) {
        this.salle = salle;
    }

    public String getCie() {
        return cie;
    }

    public String getMatiere() {
        return matiere;
    }

    public String getHoraire() {
        return horaire;
    }

    public String getSalle(){
        return salle;
    }

    public String getInstructeur(){
        return instructeur;
    }
}
