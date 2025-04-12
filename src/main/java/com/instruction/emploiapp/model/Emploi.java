package com.instruction.emploiapp.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Emploi {
    private final List<Seance> seances;
    Map<String, List<String>> indisponibilites;
    private double fitness;

    public Emploi() {
        this.seances = new ArrayList<>();
        this.indisponibilites = new HashMap<>();
        this.fitness = 0;
    }
    public void ajouterSession(Seance seance) {
        this.seances.add(seance);
    }

    public List<Seance> getSeances() {
        return seances;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }
}
