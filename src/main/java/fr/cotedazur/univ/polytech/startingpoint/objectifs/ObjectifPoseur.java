package fr.cotedazur.univ.polytech.startingpoint.objectifs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

import java.util.Map;

public class ObjectifPoseur extends Objectif {
    private int nombreParcelles;
    private Couleur couleur;
    private int points; // AJOUT : Champ pour les points

    // AJOUT : Le constructeur accepte maintenant 3 arguments (int, Couleur, int)
    public ObjectifPoseur(int nombreParcelles, Couleur couleur, int points) {
        super();
        this.nombreParcelles = nombreParcelles;
        this.couleur = couleur;
        this.points = points;
    }

    @Override
    public int getPoints() {
        return points; // On retourne les vrais points
    }

    @Override
    public boolean valider(GameState gameState, Bot bot) {
        int count = 0;
        Map<Position, Parcelle> parcelles = gameState.getPlateau().getParcellesMap();
        for (Parcelle p : parcelles.values()) {
            if (p.getCouleur() == couleur) {
                count++;
            }
        }
        return count >= nombreParcelles;
    }

    @Override
    public String toString() {
        return "Objectif Poseur : " + nombreParcelles + " parcelles " + couleur + " (" + points + " pts)";
    }
}