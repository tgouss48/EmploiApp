package com.instruction.emploiapp.algo;

import static com.instruction.emploiapp.algo.Valeurs.compagnies;
import static com.instruction.emploiapp.algo.Valeurs.nbIndispo;

import com.instruction.emploiapp.serialisation.SerialisationManager;

import java.util.*;

public class Indisponibilite {

    public static Map<String, List<String>> genererIndisponibilites() {
        Map<String, List<String>> indispo = new HashMap<>();

        List<String> restantsSamedi = SerialisationManager.charger("samedi.ser");
        Map<String, Integer> compteur = SerialisationManager.charger("compteurNormal.ser");
        List<String> cies = new ArrayList<>(compagnies);

        List<String> temp = new ArrayList<>(compagnies);
        boolean is = false;

        if (restantsSamedi.isEmpty()) {
            restantsSamedi.addAll(cies);
        } else if (restantsSamedi.size() < nbIndispo) {
            int missing = nbIndispo - restantsSamedi.size();
            for (int i = 0; i < missing; i++) {
                restantsSamedi.add(cies.get(i % cies.size()));
            }
            for (int i = 0; i < missing && !temp.isEmpty(); i++) {
                temp.remove(0);
            }
            is = true;
        }

        // Trier les compagnies en fonction de nb d'indispo
        cies.sort(Comparator.comparingInt(compagnie -> compteur.getOrDefault(compagnie, 0)));

        // Selectionner les cies indispo pour jours lundi → vendredi
        int size = nbIndispo * 5;
        List<String> restantsNormal = new ArrayList<>();

        if (!cies.isEmpty()) {
            for (int i = 0; i < size; i++) {
                restantsNormal.add(cies.get(i % cies.size()));
            }
        }

        // Mettre à jour le compteur et le sauvegarder
        for (String compagnie : restantsNormal) {
            compteur.put(compagnie, compteur.getOrDefault(compagnie, 0) + 1);
        }
        SerialisationManager.sauvegarder(compteur, "compteurNormal.ser");

        Collections.shuffle(restantsNormal);
        int index = 0;

        for (String jour : Valeurs.JOURS) {
            List<String> list = new ArrayList<>();
            if(jour.equals("Samedi")){
                for (int i = 0; i < nbIndispo; i++) {
                    list.add(restantsSamedi.get(i));
                }
                if (nbIndispo > 0) {
                    restantsSamedi.subList(0, nbIndispo).clear();
                }

                if(is)
                    SerialisationManager.sauvegarder(temp,"samedi.ser");
                else
                    SerialisationManager.sauvegarder(restantsSamedi,"samedi.ser");
            } else {
                for (int i = 0; i < nbIndispo; i++) {
                    list.add(restantsNormal.get(index));
                    index++;
                }
            }
            indispo.put(jour, list);
        }
        return indispo;
    }
}
