package com.instruction.emploiapp.serialisation;

import com.instruction.emploiapp.model.Parametre;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SerialisationManager {

    public static void sauvegarder(Object objet, String cheminFichier) {
        try {
            FileOutputStream fileOut = new FileOutputStream(cheminFichier);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(objet);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static <T> T charger(String cheminFichier) {
        File fichier = new File(cheminFichier);
        T objet = null;

        if (!fichier.exists()) {
            try {
                fichier.createNewFile();
                switch (cheminFichier) {
                    case "parametres.ser" -> {
                        Parametre parametre = new Parametre(14, 2,"FCB");
                        sauvegarder(parametre, cheminFichier);
                    }
                    case "semaine.ser" -> sauvegarder(0, cheminFichier);
                    case "compteurNormal.ser" -> sauvegarder(new HashMap<>(), cheminFichier);
                    case "sauvegardeSem2.ser","compteurReductions.ser","sauvegardeSem1.ser" -> {
                        Map<String, Map<String, Integer>> a = new HashMap<>();
                        sauvegarder(a, cheminFichier);
                    }
                    default -> sauvegarder(new ArrayList<String>(), cheminFichier);
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        try (FileInputStream fileIn = new FileInputStream(cheminFichier);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            objet = (T) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return objet;
    }
}