package fr.cotedazur.univ.polytech.startingpoint.objectifs;

import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;

public abstract class Objectif {
    protected int points;
    protected boolean valide;

    public Objectif(int points) {
        this.points = points;
        this.valide = false;
    }

    public int getPoints() {
        return points;
    }

    public boolean isValide() {
        return valide;
    }

    // La méthode clé : on donne le plateau à l'objectif pour qu'il vérifie s'il est réalisé
    public abstract boolean valider(Plateau plateau);
}