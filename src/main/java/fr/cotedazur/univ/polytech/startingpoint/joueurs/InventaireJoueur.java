package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.objectifs.Objectif;
import java.util.ArrayList;
import java.util.List;

public class InventaireJoueur {
    private int score;
    private final List<Objectif> objectifs;

    public InventaireJoueur() {
        this.score = 0;
        this.objectifs = new ArrayList<>();
    }

    public void ajouterPoints(int points) {
        this.score += points;
    }

    public int getScore() {
        return score;
    }

    public void ajouterObjectif(Objectif obj) {
        this.objectifs.add(obj);
    }

    public List<Objectif> getObjectifs() {
        return objectifs;
    }
}