package com.instruction.emploiapp.algo;

import java.util.*;

import static com.instruction.emploiapp.algo.Valeurs.JOURS;
import static com.instruction.emploiapp.algo.Valeurs.matieresSpec;

public class QuotaManager {
    private final Map<String, Map<String, Integer>> sauvegardeSem1;
    private final Map<String, Map<String, Integer>> sauvegardeSem2;
    private final Map<String, int[]> matrice;
    private final String periode;
    private final int semaine;
    private final Map<String, List<String>> indisponibilites;
    private final Map<String, Map<String, Integer>> compteurReductions;
    private final Set<String> compagniesReduites = new HashSet<>();

    public QuotaManager(String periode, int semaine,
                        Map<String, int[]> matrice,
                        Map<String, Map<String, Integer>> sauvegardeSem1,
                        Map<String, Map<String, Integer>> sauvegardeSem2,
                        Map<String, List<String>> indisponibilites,
                        Map<String, Map<String, Integer>> compteurReductions) {
        this.periode = periode;
        this.semaine = semaine;
        this.matrice = matrice;
        this.sauvegardeSem1 = sauvegardeSem1;
        this.sauvegardeSem2 = sauvegardeSem2;
        this.indisponibilites = indisponibilites;
        this.compteurReductions = compteurReductions;
    }

    public Map<String, Integer> getQuotasPourCompagnie(String compagnie) {
        Map<String, Integer> quotas = new HashMap<>();

        if (periode.equals("FCB")) {
            if (semaine == 13) {
                quotas = new HashMap<>(sauvegardeSem1.getOrDefault(compagnie, new HashMap<>()));
            } else if (semaine == 14) {
                quotas = new HashMap<>(sauvegardeSem2.getOrDefault(compagnie, new HashMap<>()));
            } else {
                for (Map.Entry<String, int[]> entry : matrice.entrySet()) {
                    quotas.put(entry.getKey(), entry.getValue()[semaine]);
                }
            }
        } else {
            if (semaine == 11) {
                quotas = new HashMap<>(sauvegardeSem1.getOrDefault(compagnie, new HashMap<>()));
            } else if (semaine == 12) {
                quotas = new HashMap<>(sauvegardeSem2.getOrDefault(compagnie, new HashMap<>()));
            } else {
                for (Map.Entry<String, int[]> entry : matrice.entrySet()) {
                    quotas.put(entry.getKey(), entry.getValue()[semaine]);
                }
            }
        }

        int reduction = calculerReduction(compagnie);

        if (reduction > 0 && !compagniesReduites.contains(compagnie)) {
            compagniesReduites.add(compagnie);

            List<String> matieresPrioritaires = trierMatieresMoinsServies(compagnie, quotas.keySet());

            sauvegardeSem1.putIfAbsent(compagnie, new HashMap<>());
            sauvegardeSem2.putIfAbsent(compagnie, new HashMap<>());

            int totalSem1 = sauvegardeSem1.get(compagnie).values().stream().mapToInt(Integer::intValue).sum();

            int count = 0;
            for (String matiere : matieresPrioritaires) {
                if (count >= reduction) break;

                //Ne pas toucher les matieres Spec
                if (matieresSpec.contains(matiere)) continue;

                //Ne pas toucher les matieres avec quota 0
                int actuel = quotas.getOrDefault(matiere, 0);
                if (actuel <= 0) continue;

                quotas.put(matiere, actuel - 1);

                compteurReductions.putIfAbsent(compagnie, new HashMap<>());
                int precedent = compteurReductions.get(compagnie).getOrDefault(matiere, 0);
                compteurReductions.get(compagnie).put(matiere, precedent + 1);

                boolean semSpeciale = (periode.equals("FCB") && semaine == 13) || (!periode.equals("FCB") && semaine == 11);

                if (semSpeciale || totalSem1 >= 21) {
                    // Toujours dans sauvegardeSem2
                    int dejaSem2 = sauvegardeSem2.get(compagnie).getOrDefault(matiere, 0);
                    sauvegardeSem2.get(compagnie).put(matiere, dejaSem2 + 1);
                } else {
                    // Sem1 si total seances != 21
                    int dejaSem1 = sauvegardeSem1.get(compagnie).getOrDefault(matiere, 0);
                    sauvegardeSem1.get(compagnie).put(matiere, dejaSem1 + 1);
                    totalSem1++;
                }

                count++;
            }
        }

        return quotas;
    }

    private int calculerReduction(String compagnie) {
        boolean samedi = indisponibilites.getOrDefault("Samedi", new ArrayList<>()).contains(compagnie);
        boolean joursNormaux = Arrays.stream(JOURS)
                .filter(j -> !j.equals("Samedi"))
                .anyMatch(j -> indisponibilites.getOrDefault(j, new ArrayList<>()).contains(compagnie));

        if (samedi && joursNormaux) return 5;
        if (joursNormaux) return 4;
        if (samedi) return 1;
        return 0;
    }

    private List<String> trierMatieresMoinsServies(String compagnie, Set<String> matieres) {
        List<String> liste = new ArrayList<>(matieres);
        liste.sort(Comparator.comparingInt(m ->
                compteurReductions.getOrDefault(compagnie, new HashMap<>()).getOrDefault(m, 0)
        ));
        return liste;
    }
}