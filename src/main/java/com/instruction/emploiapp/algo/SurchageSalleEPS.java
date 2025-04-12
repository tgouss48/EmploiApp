package com.instruction.emploiapp.algo;

import com.instruction.emploiapp.model.Emploi;
import com.instruction.emploiapp.model.Seance;

import java.util.*;

import static com.instruction.emploiapp.algo.Valeurs.CRENEAUX;
import static com.instruction.emploiapp.algo.Valeurs.JOURS;

public class SurchageSalleEPS {

    public static void repartirEPSChaqueJour(Emploi emploi) {
        String[] Days = new String[JOURS.length - 1];
        System.arraycopy(JOURS, 0, Days, 0, JOURS.length - 1);

        for (String jour : Days) {

            List<Integer> compagniesEPS = trouverCompagniesEPS(emploi, jour);
            if (compagniesEPS.isEmpty()) continue;

            Collections.shuffle(compagniesEPS);

            List<List<Integer>> groupes = diviserEn4Groupes(compagniesEPS);

            for (int i = 0; i < groupes.size(); i++) {
                List<Integer> groupe = groupes.get(i);
                String creneau = CRENEAUX[i];

                for (int cie : groupe) {
                    affecterEPS(emploi, jour, creneau, cie);
                }
            }
        }
    }

    private static List<Integer> trouverCompagniesEPS(Emploi emploi, String jour) {
        Set<Integer> result = new HashSet<>();
        for (Seance s : emploi.getSeances()) {
            if (s.getMatiere().equalsIgnoreCase("EPS")) {
                if (s.getHoraire().startsWith(jour)) {
                    int compagnie = Integer.parseInt(s.getCie().split(" ")[1]);
                    result.add(compagnie);
                }
            }
        }
        return new ArrayList<>(result);
    }

    private static List<List<Integer>> diviserEn4Groupes(List<Integer> compagnies) {
        List<List<Integer>> groupes = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            groupes.add(new ArrayList<>());
        }

        int index = 0;
        for (int cie : compagnies) {
            groupes.get(index % 4).add(cie);
            index++;
        }
        return groupes;
    }

    private static void affecterEPS(Emploi emploi, String jour, String creneauHeure, int compagnie) {
        Seance seanceEPS = emploi.getSeances().stream()
                .filter(s -> s.getCie().equals("Compagnie " + compagnie))
                .filter(s -> s.getMatiere().equalsIgnoreCase("EPS"))
                .filter(s -> s.getHoraire().startsWith(jour))
                .findFirst().orElse(null);

        if (seanceEPS == null) {
            return;
        }

        String horaireCible = jour + " " + creneauHeure;

        Seance occupant = emploi.getSeances().stream()
                .filter(s -> s.getCie().equals("Compagnie " + compagnie))
                .filter(s -> s.getHoraire().equals(horaireCible))
                .findFirst().orElse(null);

        if (occupant == null) {
            seanceEPS.setHoraire(horaireCible);
        } else {
            effectuerSwitch(seanceEPS, occupant);
        }
    }

    private static void effectuerSwitch(Seance seanceEPS, Seance occupant) {
        String matiereTemp = seanceEPS.getMatiere();
        String instructeurTemp = seanceEPS.getInstructeur();

        seanceEPS.setMatiere(occupant.getMatiere());
        seanceEPS.setInstructeur(occupant.getInstructeur());

        occupant.setMatiere(matiereTemp);
        occupant.setInstructeur(instructeurTemp);
    }
}
