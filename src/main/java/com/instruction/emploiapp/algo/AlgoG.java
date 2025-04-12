package com.instruction.emploiapp.algo;

import com.instruction.emploiapp.algo.excel.ExportExcel;
import com.instruction.emploiapp.db.InstructeurDAO;
import com.instruction.emploiapp.db.SuiviDAO;
import com.instruction.emploiapp.model.*;
import com.instruction.emploiapp.serialisation.SerialisationManager;

import static com.instruction.emploiapp.algo.Valeurs.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class AlgoG {
    private static final int TAILLE_POPULATION = 20;
    private static final int MAX_GENERATIONS = 200;
    private static final double TAUX_MUTATION = 0.2;

    private final Map<String, Map<String, List<Integer>>> instructeursMatSpec = new HashMap<>();
    private final List<String> compagnies = new ArrayList<>(Valeurs.compagnies);
    private final List<Instructeur> instructeurs = new ArrayList<>();
    private static Map<String, List<String>> indisponibilites;
    private final List<Suivi> suivi = new ArrayList<>();

    private final Map<String, Map<String, Integer>> seancesAttribuees = new HashMap<>();

    int semaine = SerialisationManager.charger("semaine.ser");
    Map<String, Map<String, Integer>> sauvegardeSem1 = SerialisationManager.charger("sauvegardeSem1.ser");
    Map<String, Map<String, Integer>> sauvegardeSem2 = SerialisationManager.charger("sauvegardeSem2.ser");
    Map<String, Map<String, Integer>> compteurReductions = SerialisationManager.charger("compteurReductions.ser");
    private QuotaManager quotaManager;

    Map<String, int[]> Matrice;

    public AlgoG() {
        try {
            initialiserDonnees();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void initialiserDonnees() throws SQLException {
        try {
            if(periode.equals("FCB")){
                Matrice = Matrice_FCB;
            } else {
                Matrice = Matrice_Spec;
            }

            InstructeurDAO instructeurDAO = new InstructeurDAO();
            SuiviDAO suiviDAO = new SuiviDAO();
            instructeurs.clear();
            instructeurs.addAll(instructeurDAO.getAll());
            suivi.clear();
            suivi.addAll(suiviDAO.getAll());
            indisponibilites = Indisponibilite.genererIndisponibilites();

            quotaManager = new QuotaManager(periode, semaine, Matrice, sauvegardeSem1, sauvegardeSem2, indisponibilites, compteurReductions);

            // Recuperer les instructeurs qui enseignent plusieurs compagnies
            for (Instructeur i : instructeurs) {
                if (i.getIdCie().contains("-")) {
                    List<Integer> compagnies = Arrays.stream(i.getIdCie().split("-"))
                            .map(Integer::parseInt).toList();
                    instructeursMatSpec.putIfAbsent(i.getMatiere(), new HashMap<>());
                    instructeursMatSpec.get(i.getMatiere()).put(i.getGrade()+" "+i.getNom(), compagnies);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Emploi executer(String chemin) throws SQLException, IOException {
        List<Emploi> population = initialiserPopulation();
        for (int generation = 0; generation < MAX_GENERATIONS; generation++) {
            for (Emploi emploi : population) {
                emploi.setFitness(calculerFitness(emploi));
            }
            population = selectionnerMeilleursIndividus(population);
            population = croiserEtMuter(population);
        }
        Emploi meilleurEmploi = population.get(0);

        SurchageSalleEPS.repartirEPSChaqueJour(meilleurEmploi);
        STALConflit.corrigerConflitsSTAL(meilleurEmploi,chemin);
        InstructeurConflit.resoudreConflits(meilleurEmploi,chemin);

        SuiviDAO.decrementerVolumesHoraires(meilleurEmploi);
        SerialisationManager.sauvegarder(semaine+1,"semaine.ser");
        SerialisationManager.sauvegarder(compteurReductions, "compteurReductions.ser");
        SerialisationManager.sauvegarder(sauvegardeSem1, "sauvegardeSem1.ser");
        SerialisationManager.sauvegarder(sauvegardeSem2, "sauvegardeSem2.ser");
        ExportExcel.exporter(meilleurEmploi,chemin);

        return meilleurEmploi;
    }

    private Emploi genererEmploiAleatoire(int semaine) {
        Emploi emploi = new Emploi();

        for (String compagnie : compagnies) {
            Map<String, Integer> quotas = quotaManager.getQuotasPourCompagnie(compagnie);
            for (String jour : JOURS) {
                List<String> matieresDisponibles = getMatieresDisponibles(compagnie, quotas);
                Collections.shuffle(matieresDisponibles);

                matieresDisponibles.sort((m1, m2) -> Integer.compare(
                        quotas.getOrDefault(m2, 0),
                        quotas.getOrDefault(m1, 0)
                ));

                if (estIndisponible(compagnie, jour))
                    continue;

                if (jour.equals("Samedi")) {
                    Generer(emploi, compagnie, jour, matieresDisponibles, 0, HEURES_MATIN);
                } else {
                    for (int i = 0; i < 2; i++) { // 2 séances le matin
                        if (Generer(emploi, compagnie, jour, matieresDisponibles, i, HEURES_MATIN)) break;
                    }
                    for (int i = 0; i < 2; i++) {
                        if (Generer(emploi, compagnie, jour, matieresDisponibles, i, HEURES_APRES_MIDI)) break;
                    }
                }
            }
        }
        return emploi;
    }

    private boolean Generer(Emploi emploi, String compagnie, String jour, List<String> matieresDisponibles, int i, String[] heures) {
        if (matieresDisponibles.isEmpty()) return true;

        String matiere = matieresDisponibles.get(0);
        int dejaAttribuees = seancesAttribuees.getOrDefault(compagnie, new HashMap<>()).getOrDefault(matiere, 0);

        String instructeur = trouverInstructeur(matiere, compagnie.split(" ")[1]);
        String salle = "";
        String creneauHoraire = jour + " " + heures[i];

        Seance session = new Seance(compagnie, matiere, instructeur, salle, creneauHoraire);
        emploi.ajouterSession(session);

        seancesAttribuees.get(compagnie).put(matiere,dejaAttribuees + 1);

        matieresDisponibles.remove(matiere);
        return false;
    }

    public static boolean estIndisponible(String compagnie, String jour) {
        return indisponibilites.containsKey(jour) && indisponibilites.get(jour).contains(compagnie);
    }

    private List<String> getMatieresDisponibles(String compagnie,  Map<String, Integer> quotas) {
        List<String> matieresDisponibles = new ArrayList<>();
        seancesAttribuees.putIfAbsent(compagnie, new HashMap<>());

        // Trier les matières par ordre décroissant du quota
        List<Map.Entry<String, Integer>> matieresTriees = new ArrayList<>(quotas.entrySet());
        matieresTriees.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

        for (Map.Entry<String, Integer> entry : matieresTriees) {
            String matiere = entry.getKey();
            int quota = entry.getValue();

            seancesAttribuees.get(compagnie).putIfAbsent(matiere, 0);
            int dejaAttribuees = seancesAttribuees.get(compagnie).get(matiere);

            if (dejaAttribuees < quota) {
                matieresDisponibles.add(matiere);
            }
        }
        return matieresDisponibles;
    }

    private List<Emploi> initialiserPopulation() {
        List<Emploi> population = new ArrayList<>();
        for (int i = 0; i < AlgoG.TAILLE_POPULATION; i++) {
            population.add(genererEmploiAleatoire(semaine));
        }
        return population;
    }

    private double calculerFitness(Emploi emploi) {
        double score = 0;

        // Volume horaire depasser
        for (Seance session : emploi.getSeances()) {
            Suivi s = trouverSuivi(session.getCie(), session.getMatiere());
            if (s != null && s.getVolumeHoraire() < 1.5) {
                score -= 20;
            }
        }

        // Indispo non respecte
        for (Seance session : emploi.getSeances()) {
            List<String> indispo = indisponibilites.get(session.getCie());
            if (indispo != null && indispo.contains(session.getHoraire().split(" ")[0])) {
                score -= 10;
            }
        }

        // Matiere repetée dans une journée
        for (String compagnie : compagnies) {
            for (String jour : JOURS) {
                Set<String> matieresDuJour = new HashSet<>();
                for (Seance session : emploi.getSeances()) {
                    if (session.getCie().equals(compagnie) && session.getHoraire().startsWith(jour)) {
                        if (matieresDuJour.contains(session.getMatiere())) {
                            score -= 10;
                        }
                        matieresDuJour.add(session.getMatiere());
                    }
                }
            }
        }

        return score;
    }

    private List<Emploi> selectionnerMeilleursIndividus(List<Emploi> population) {
        population.sort((e1, e2) -> Double.compare(e2.getFitness(), e1.getFitness()));
        return population.subList(0, population.size() / 2);
    }

    private List<Emploi> croiserEtMuter(List<Emploi> population) {
        List<Emploi> nouvellePopulation = new ArrayList<>(population);
        for (int i = 0; i < population.size() / 2; i++) {
            Emploi parent1 = population.get((int) (Math.random() * population.size()));
            Emploi parent2 = population.get((int) (Math.random() * population.size()));
            Emploi enfant1 = croiser(parent1, parent2);
            Emploi enfant2 = croiser(parent2, parent1);
            if (Math.random() < TAUX_MUTATION) {
                muter(enfant1);
                muter(enfant2);
            }
            nouvellePopulation.add(enfant1);
            nouvellePopulation.add(enfant2);
        }
        return nouvellePopulation;
    }

    private Emploi croiser(Emploi parent1, Emploi parent2) {
        Emploi enfant = new Emploi();
        int pointDeCroisement = parent1.getSeances().size() / 2;
        for (int i = 0; i < pointDeCroisement; i++) {
            enfant.ajouterSession(parent1.getSeances().get(i));
        }
        for (int i = pointDeCroisement; i < parent2.getSeances().size(); i++) {
            enfant.ajouterSession(parent2.getSeances().get(i));
        }
        return enfant;
    }

    private void muter(Emploi emploi) {

        if (emploi.getSeances().isEmpty()) {
            return;
        }
        int index = (int) (Math.random() * emploi.getSeances().size());
        Seance session = emploi.getSeances().get(index);

        String[] parties = session.getHoraire().split(" ");
        String jour = parties[0];
        String compagnie = session.getCie();

        Map<String, Integer> quotas = quotaManager.getQuotasPourCompagnie(compagnie);
        List<String> matieresDisponibles = getMatieresDisponibles(compagnie, quotas);

        Set<String> matieresUtilisees = new HashSet<>();
        for (Seance s : emploi.getSeances()) {
            if (s.getCie().equals(compagnie) && s.getHoraire().startsWith(jour)) {
                matieresUtilisees.add(s.getMatiere());
            }
        }

        String nouvelleMatiere = null;
        Collections.shuffle(matieresDisponibles);
        for (String matiere : matieresDisponibles) {
            if (!matieresUtilisees.contains(matiere)) {
                nouvelleMatiere = matiere;
                break;
            }
        }

        if (nouvelleMatiere != null) {
            session.setMatiere(nouvelleMatiere);
            session.setInstructeur( trouverInstructeur(nouvelleMatiere, compagnie.split(" ")[1]) );
        }
    }

    private String trouverInstructeur(String matiere, String idCie) {
        if (instructeursMatSpec.containsKey(matiere)) {
            for (Map.Entry<String, List<Integer>> entry : instructeursMatSpec.get(matiere).entrySet()) {
                if (entry.getValue().contains(Integer.parseInt(idCie))) {
                    return entry.getKey();
                }
            }
        }

        for (Instructeur i : instructeurs) {
            if (i.getMatiere().equals(matiere) && i.getIdCie().equals(idCie)) {
                return i.getGrade() + " " + i.getNom();
            }
        }
        return null;
    }

    private Suivi trouverSuivi(String compagnie, String matiere) {
        for (Suivi s : suivi) {
            if (("Compagnie " + s.getIdCie()).equals(compagnie) && s.getMatiere().equals(matiere)) {
                return s;
            }
        }
        return null;
    }
}