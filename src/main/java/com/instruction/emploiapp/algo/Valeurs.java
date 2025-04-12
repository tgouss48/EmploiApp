package com.instruction.emploiapp.algo;

import com.instruction.emploiapp.model.Parametre;
import com.instruction.emploiapp.serialisation.SerialisationManager;

import java.util.*;

public class Valeurs {

    public static final Map<String, int[]> Matrice_FCB = new HashMap<>();
    static {
        Matrice_FCB.put("ARTS MARTIAUX", new int[]{1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0});
        Matrice_FCB.put("EPS", new int[]{4, 5, 5, 5, 4, 5, 5, 5, 5, 5, 5, 5, 5});
        Matrice_FCB.put("ARMEMENT", new int[]{3, 4, 4, 4, 3, 4, 4, 4, 3, 4, 4, 4, 3});
        Matrice_FCB.put("ORDRE SERRE", new int[]{3, 4, 3, 3, 3, 3, 3, 3, 3, 4, 4, 2, 3});
        Matrice_FCB.put("COMBAT", new int[]{2, 1, 2, 1, 2, 2, 1, 2, 2, 2, 1, 2, 2});
        Matrice_FCB.put("IST", new int[]{1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2});
        Matrice_FCB.put("REGLEMENT", new int[]{1, 0, 1, 1, 1, 1, 1, 1, 2, 0, 1, 1, 1});
        Matrice_FCB.put("HYGIENE ET SECOURISME", new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1});
        Matrice_FCB.put("COMMUNICATION GESTUELLE", new int[]{1, 0, 1, 0, 1, 1, 0, 1, 1, 0, 0, 0, 1});
        Matrice_FCB.put("STAL", new int[]{0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 1, 1, 1});
        Matrice_FCB.put("EDUCATION CIVIQUE ET MORALE", new int[]{1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 0});
        Matrice_FCB.put("SECURITE MILITAIRE", new int[]{0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0, 0});
        Matrice_FCB.put("TOPOGRAPHIE", new int[]{1, 1, 0, 0, 2, 0, 1, 1, 1, 0, 0, 0, 1});
        Matrice_FCB.put("TRANSMISSION", new int[]{1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 0});
        Matrice_FCB.put("GENIE MILITAIRE", new int[]{1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1});
    }

    public static final Map<String, int[]> Matrice_Spec = new HashMap<>();
    static {
        Matrice_Spec.put("MO", new int[]{3, 4, 4, 4, 3, 4, 4, 3, 4, 4, 3});
        Matrice_Spec.put("TONFA ET GTPI", new int[]{3, 2, 2, 2, 3, 2, 2, 3, 2, 2, 3});
        Matrice_Spec.put("MATERIEL ANTI-EMEUTE", new int[]{3, 4, 3, 3, 3, 4, 3, 3, 3, 4, 3});
        Matrice_Spec.put("EPS", new int[]{2, 1, 2, 2, 2, 1, 2, 2, 2, 1, 2});
        Matrice_Spec.put("NOTIONS D'EQUITATION", new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1});
        Matrice_Spec.put("LANGUE ARABE", new int[]{1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1});
        Matrice_Spec.put("LANGUE FRANCAISE", new int[]{1, 2, 1, 2, 1, 2, 1, 1, 1, 2, 1});
        Matrice_Spec.put("POLICE ADMINISTRATIVE", new int[]{1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0});
        Matrice_Spec.put("GESTION DES CATASTROPHES", new int[]{0, 1, 0, 1, 1, 0, 1, 1, 0, 0, 1});
        Matrice_Spec.put("SURVEILLANCE DES FRONTIERES", new int[]{0, 0, 1, 0, 0, 1, 0, 0, 1, 1, 1});
        Matrice_Spec.put("CADRE JURIDIQUE FA", new int[]{1, 0, 1, 0, 1, 0, 1, 1, 0, 1, 0});
        Matrice_Spec.put("LIBERTES PUBLIQUES ET DROITS DE L'HOMME", new int[]{1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1});
        Matrice_Spec.put("GENERALITE SUR LE DROITS PENAL", new int[]{0, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0});
        Matrice_Spec.put("ARMEMENT ET IST (REVISION)", new int[]{0, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1});
        Matrice_Spec.put("REGLEMENTE INTERIEUR CFPFA", new int[]{1, 0, 1, 1, 0, 0, 1, 0, 1, 1, 0});
        Matrice_Spec.put("CODE DE LA ROUTE", new int[]{1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1});
        Matrice_Spec.put("NOTIONS BUREAUTIQUE", new int[]{0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0});
        Matrice_Spec.put("JUSTICE MILITAIRE", new int[]{1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1});
        Matrice_Spec.put("THEMES DE SENSIBILISATION : PREVENTION ET SECURITE ROUTIERE", new int[]{0, 1, 0, 0, 1, 0, 0, 1, 0, 1, 1});
        Matrice_Spec.put("CORRESPONDANCE MILITAIRE", new int[]{1, 0, 1, 1, 0, 1, 0, 1, 1, 0, 0});
    }

    public static final String[] JOURS = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi"};
    public static final String[] HEURES_MATIN = {"08h00-10h00", "10h00-12h00"};
    public static final String[] HEURES_APRES_MIDI = {"14h30-16h00", "16h00-17h30"};
    public static final String[] CRENEAUX = {"08h00-10h00", "10h00-12h00", "14h30-16h00", "16h00-17h30"};

    static Parametre parametre = SerialisationManager.charger("parametres.ser");
    public static int nbCompagnies = parametre.getNbCompagnies();
    public static int nbIndispo = parametre.getNbCompagniesNonDisponibles();
    public static String periode = parametre.getPeriode();

    public static Set<String> matieresSpec= new HashSet<>(matieresSpec());

    public static Set<String> matieresSpec() {
        Set<String> matieresConflit = new HashSet<>();
        if(periode.equals("FCB")){
            matieresConflit = Set.of(
                    "COMMUNICATION GESTUELLE",
                    "HYGIENE ET SECOURISME",
                    "ARTS MARTIAUX",
                    "TRANSMISSION",
                    "STAL"
            );
        } else {
            matieresConflit = Set.of(
                    "NOTIONS D'EQUITATION",
                    "CODE DE LA ROUTE",
                    "NOTIONS BUREAUTIQUE",
                    "THEMES DE SENSIBILISATION : PREVENTION ET SECURITE ROUTIERE"
            );
        }
        return matieresConflit;
    }

    public static final List<String> compagnies = new ArrayList<>(getCompagnies());

    public static List<String> getCompagnies() {
        List<String> compagnies = new ArrayList<>();
        for (int i = 1; i <= nbCompagnies; i++) {
            compagnies.add("Compagnie " + i);
        }
        return compagnies;
    }
}
