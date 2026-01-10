package fr.cotedazur.univ.polytech.startingpoint.actions;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

public class PoserParcelle implements Action {
    private final Parcelle parcelle;
    private final Position position;

    public PoserParcelle(Parcelle parcelle, Position position) {
        this.parcelle = parcelle;
        this.position = position;
    }

    @Override
    public void appliquer(GameState gameState, Bot joueur) {
        // CORRECTION : On récupère le plateau via le contexte GameState
        gameState.getPlateau().placerParcelle(parcelle, position);
    }

    @Override
    public String toString() {
        // Avant : return "pose " + parcelle + " en " + position;
        // Après : Plus propre, on prend juste la couleur de la parcelle
        return "pose une parcelle " + parcelle.getCouleur() + " en " + position;
    }
}