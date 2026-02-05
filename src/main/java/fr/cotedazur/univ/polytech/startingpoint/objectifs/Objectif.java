package fr.cotedazur.univ.polytech.startingpoint.objectifs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;

import java.util.List;
import java.util.Map;

public abstract class Objectif {
    protected final int points;
    protected final TypeObjectif type;

    public Objectif(int points, TypeObjectif type) {
        this.points = points;
        this.type = type;
    }

    public abstract boolean valider(GameState gameState, Bot bot);

    public int getPoints() { return points; }
    public TypeObjectif getType() { return type; }
    public abstract List<Couleur> getCouleurs();
    public abstract Map<Couleur,Integer> getObjMap();
}