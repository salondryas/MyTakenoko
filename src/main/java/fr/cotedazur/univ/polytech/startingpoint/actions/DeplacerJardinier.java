package fr.cotedazur.univ.polytech.startingpoint.actions;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Jardiner;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

public class DeplacerJardinier implements Action {
    private Jardiner jardinier;
    private Position destination;

    public DeplacerJardinier(Jardiner jardinier, Position destination) {
        this.jardinier = jardinier;
        this.destination = destination;
    }

    @Override
    public void appliquer(GameState gameState, Bot bot) {
        // 1. On récupère le plateau depuis le GameState
        Plateau plateau = gameState.getPlateau();

        // 2. On exécute l'action du jardinier
        // (Note: on suppose que votre classe Jardiner a une méthode pour pousser le bambou ou se déplacer)
        // On met à jour la position du jardinier (si votre classe le permet) ou on appelle directement l'effet
        jardinier.pousserBambou(destination, plateau);
    }

    @Override
    public String toString() {
        return "déplace le jardinier en " + destination;
    }
}