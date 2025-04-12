package com.instruction.emploiapp.algo;

import com.instruction.emploiapp.model.Emploi;
import com.instruction.emploiapp.model.Seance;
import com.instruction.emploiapp.serialisation.SerialisationManager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.instruction.emploiapp.algo.InstructeurConflit.echange;

public class STALConflit {
    private static Map<String, List<Seance>> detecterConflitsSTAL(Emploi emploi) {
        Map<String, List<Seance>> stalsParHoraire = new HashMap<>();

        for (Seance s : emploi.getSeances()) {
            if ("STAL".equalsIgnoreCase(s.getMatiere())) {
                stalsParHoraire.putIfAbsent(s.getHoraire(), new ArrayList<>());
                stalsParHoraire.get(s.getHoraire()).add(s);
            }
        }

        return stalsParHoraire.entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static void corrigerConflitsSTAL(Emploi emploi,String chemin) {
        int semaine = SerialisationManager.charger("semaine.ser");
        chemin += "\\log_conflits.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(chemin, true))){
            while (true) {
                Map<String, List<Seance>> conflitsSTAL = detecterConflitsSTAL(emploi);
                if (conflitsSTAL.isEmpty()) {
                    break;
                }

                boolean correctionEffectuee = false;

                for (String horaire : conflitsSTAL.keySet()) {
                    List<Seance> seancesConflit = conflitsSTAL.get(horaire);
                    if (seancesConflit.size() <= 1) continue; // Pas de conflit

                    // Extraire la liste des compagnies qui ont déjà une séance STAL à ce même horaire
                    List<String> compagniesEnConflit = seancesConflit.stream()
                            .map(Seance::getCie)
                            .distinct()
                            .toList();

                    // On garde la première séance et on essaie de déplacer les autres
                    for (int i = 1; i < seancesConflit.size(); i++) {
                        Seance seanceADeplacer = seancesConflit.get(i);

                        List<String> horairesPossibles = trouverHorairesCompagnieSTAL(emploi, seanceADeplacer);
                        if (horairesPossibles.isEmpty()) {
                            String message = "Sem : " + (semaine + 1) + " - STAL\n" +
                                    "Liste d'échange vide pour la séance : " + seanceADeplacer.getHoraire() +
                                    "\nCompagnies en conflit : " + compagniesEnConflit + "\n";
                            writer.write(message);
                            writer.newLine();
                            continue;
                        }

                        boolean switched = false;
                        for (String nouvelHoraire : horairesPossibles) {
                            if (effectuerSwitchSTAL(emploi, seanceADeplacer, nouvelHoraire)) {
                                correctionEffectuee = true;
                                switched = true;
                                break;
                            }
                        }
                        if (!switched) {
                            String message = "Sem : " + (semaine + 1) + " - STAL : Cie : " + seanceADeplacer.getCie() +
                                    "\nAucun échange possible pour la séance : " + seanceADeplacer.getHoraire() +
                                    "\nCompagnies en conflit : " + compagniesEnConflit + "\n";
                            writer.write(message);
                            writer.newLine();
                        }
                    }
                }
                if (!correctionEffectuee) {
                    break;
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private static List<String> trouverHorairesCompagnieSTAL(Emploi emploi, Seance seanceConflit) {
        String compagnie = seanceConflit.getCie();
        String horaireConflit = seanceConflit.getHoraire();

        // Récupérer tous les horaires ou la compagnie intervient
        Set<String> horairesCompagnie = emploi.getSeances().stream()
                .filter(s -> s.getCie().equals(compagnie))
                .map(Seance::getHoraire)
                .collect(Collectors.toSet());

        // Récupérer tous les horaires des séances STAL
        Set<String> horairesSTAL = emploi.getSeances().stream()
                .filter(s -> "STAL".equalsIgnoreCase(s.getMatiere()))
                .map(Seance::getHoraire)
                .collect(Collectors.toSet());

        // Ne pas considérer l'horaire en conflit
        horairesCompagnie.remove(horaireConflit);

        // Garder uniquement les horaires ou la compagnie intervient et qui ne sont pas déjà occupés par une séance STAL
        return horairesCompagnie.stream()
                .filter(h -> !horairesSTAL.contains(h))
                .collect(Collectors.toList());
    }

    private static boolean effectuerSwitchSTAL(Emploi emploi, Seance seanceConflit, String nouvelHoraire) {
        String compagnie = seanceConflit.getCie();

        // Récupérer la séance cible pour la compagnie au nouvel horaire
        Seance seanceCible = emploi.getSeances().stream()
                .filter(s -> s.getCie().equals(compagnie) && s.getHoraire().equals(nouvelHoraire))
                .findFirst().orElse(null);

        if (seanceCible == null) {
            return false;
        }

        // Vérifier que le jour de la séance cible ne contient pas déjà une séance STAL
        String jourCible = seanceCible.getHoraire().split(" ")[0];
        boolean stalDejaPresent = emploi.getSeances().stream()
                .anyMatch(s -> s.getCie().equals(compagnie)
                        && s.getHoraire().startsWith(jourCible)
                        && "STAL".equalsIgnoreCase(s.getMatiere()));

        if (stalDejaPresent) {
            return false;
        }

        // Vérifier que le jour de la séance de conflit ne contient pas déjà la matière de la séance cible
        String jourConflit = seanceConflit.getHoraire().split(" ")[0];
        String matiereCible = seanceCible.getMatiere();
        boolean memeMatiereDejaPresente = emploi.getSeances().stream()
                .anyMatch(s -> s.getCie().equals(compagnie)
                        && s.getHoraire().startsWith(jourConflit)
                        && s.getMatiere().equalsIgnoreCase(matiereCible));

        if (memeMatiereDejaPresente) {
            return false;
        }

        return echange(seanceConflit, seanceCible);
    }
}