package com.instruction.emploiapp.algo;

import com.instruction.emploiapp.model.Emploi;
import com.instruction.emploiapp.model.Seance;
import com.instruction.emploiapp.serialisation.SerialisationManager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.instruction.emploiapp.algo.Valeurs.matieresSpec;

@SuppressWarnings("DuplicatedCode")
public class InstructeurConflit {
    private static Map<String, Map<String, List<Integer>>> detecterConflits(Emploi emploi) {
        Map<String, Map<String, List<Integer>>> conflits = new HashMap<>();

        for (Seance s : emploi.getSeances()) {
            String instructeur = s.getInstructeur();
            String horaire = s.getHoraire();
            int compagnie = Integer.parseInt(s.getCie().replace("Compagnie ", ""));

            conflits.putIfAbsent(instructeur, new HashMap<>());
            conflits.get(instructeur).putIfAbsent(horaire, new ArrayList<>());
            conflits.get(instructeur).get(horaire).add(compagnie);
        }

        return conflits.entrySet().stream()
                .filter(e -> e.getValue().values().stream().anyMatch(l -> l.size() > 1))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static void resoudreConflits(Emploi emploi,String chemin){
        int semaine = SerialisationManager.charger("semaine.ser");
        chemin += "\\log_conflits.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(chemin, true))) {
            while (true) {
                Map<String, Map<String, List<Integer>>> conflits = detecterConflits(emploi);
                if (conflits.isEmpty()) {
                    break;
                }

                boolean correctionEffectuee = false;

                for (String instructeur : conflits.keySet()) {
                    Map<String, List<Integer>> horaires = conflits.get(instructeur);

                    for (String horaireConflit : horaires.keySet()) {
                        List<Integer> compagniesEnConflit = horaires.get(horaireConflit);
                        if (compagniesEnConflit.size() <= 1) continue;

                        // Garder la première compagnie et essayer de déplacer les autres
                        List<Integer> aDeplacer = compagniesEnConflit.subList(1, compagniesEnConflit.size());


                        for (int cie : aDeplacer) {
                            List<String> horairesPossibles = trouverHorairesCompagnie(emploi, cie, instructeur);
                            if (horairesPossibles.isEmpty()) {
                                String message = "Sem : " + (semaine + 1) + " - Instructeur : " + instructeur +
                                        "\nListe d'échange vide pour la séance : " + horaireConflit +
                                        "\nCompagnies en conflit : " + compagniesEnConflit + "\n";
                                writer.write(message);
                                writer.newLine();
                                continue;
                            }

                            boolean switched = false;
                            // Parcourir tous les horaires candidats
                            for (String nouvelHoraire : horairesPossibles) {
                                if (effectuerSwitch(emploi, cie, horaireConflit, nouvelHoraire)) {
                                    correctionEffectuee = true;
                                    switched = true;
                                    break;
                                }
                            }
                            if (!switched) {
                                String message = "Sem : " + (semaine + 1) + " - Instructeur : " + instructeur + " - Cie : " + cie +
                                        "\nAucun échange possible pour la séance : " + horaireConflit +
                                        "\nCompagnies en conflit : " + compagniesEnConflit + "\n";
                                writer.write(message);
                                writer.newLine();
                            }
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

    private static List<String> trouverHorairesCompagnie(Emploi emploi, int compagnie, String instructeur) {
        // Horaires utilisés par la compagnie
        Set<String> horairesCompagnie = emploi.getSeances().stream()
                .filter(s -> s.getCie().equals("Compagnie " + compagnie))
                .map(Seance::getHoraire)
                .collect(Collectors.toSet());

        // Horaires déjà occupés par l'instructeur
        Set<String> horairesIndisponibles = emploi.getSeances().stream()
                .filter(s -> s.getInstructeur().equals(instructeur))
                .map(Seance::getHoraire)
                .collect(Collectors.toSet());

        // On garde seulement les horaires de la compagnie qui ne sont pas occupés par cet instructeur
        return horairesCompagnie.stream()
                .filter(h -> !horairesIndisponibles.contains(h))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("ControlFlowStatementWithoutBraces")
    private static boolean effectuerSwitch(Emploi emploi, int compagnie, String horaireConflictuel, String nouvelHoraire) {
        // Récupérer la séance en conflit (celle de la compagnie à l'horaire conflictuel)
        Seance seanceConflit = emploi.getSeances().stream()
                .filter(s -> s.getCie().equals("Compagnie " + compagnie) && s.getHoraire().equals(horaireConflictuel))
                .findFirst().orElse(null);

        // Récupérer la séance candidate (celle de la compagnie à l'horaire nouvelHoraire)
        Seance seanceCible = emploi.getSeances().stream()
                .filter(s -> s.getCie().equals("Compagnie " + compagnie) && s.getHoraire().equals(nouvelHoraire))
                .findFirst().orElse(null);

        if (seanceCible == null || seanceConflit == null) return false;

        // Ne pas switcher avec une Matiere Spec
        if (matieresSpec.contains(seanceCible.getMatiere())) {
            return false;
        }

        // Vérifier que le jour du horaireConflictuel ne contient pas déjà la matière de la séance cible
        String jourConflit = horaireConflictuel.split(" ")[0];
        String matiereCible = seanceCible.getMatiere();

        boolean doublonMatiereConflit = emploi.getSeances().stream()
                .filter(s -> s.getCie().equals("Compagnie " + compagnie))
                .filter(s -> s.getHoraire().startsWith(jourConflit))
                .anyMatch(s -> s.getMatiere().equalsIgnoreCase(matiereCible));

        if (doublonMatiereConflit) return false;

        // Vérifier que le jour du nouvel horaire ne contient pas déjà la matière de la séance en conflit
        String jourCible = nouvelHoraire.split(" ")[0];
        String matiereConflit = seanceConflit.getMatiere();

        boolean doublonMatiereCible = emploi.getSeances().stream()
                .filter(s -> s.getCie().equals("Compagnie " + compagnie))
                .filter(s -> s.getHoraire().startsWith(jourCible))
                .anyMatch(s -> s.getMatiere().equalsIgnoreCase(matiereConflit));

        if (doublonMatiereCible) return false;

        return echange(seanceConflit, seanceCible);
    }

    public static boolean echange(Seance seanceConflit, Seance seanceCible) {
        String matiereTemp = seanceConflit.getMatiere();
        String instructeurTemp = seanceConflit.getInstructeur();

        seanceConflit.setMatiere(seanceCible.getMatiere());
        seanceConflit.setInstructeur(seanceCible.getInstructeur());

        seanceCible.setMatiere(matiereTemp);
        seanceCible.setInstructeur(instructeurTemp);

        return true;
    }
}