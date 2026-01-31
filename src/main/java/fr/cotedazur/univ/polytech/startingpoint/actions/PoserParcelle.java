package fr.cotedazur.univ.polytech.startingpoint.actions;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Logger;
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
        // On tente de placer et on récupère le résultat
        boolean succes = gameState.getPlateau().placerParcelle(parcelle, position);

        if (!succes) {
            Logger.print("ERREUR CRITIQUE : " + joueur.getNom() + " a tenté de poser une parcelle sur une case occupée !");
        }
    }

    @Override
    public TypeAction getType() {
        return TypeAction.POSER_PARCELLE;
    }

    @Override
    public String toString() {
        return "pose une parcelle " + parcelle.getCouleur() + " en " + position;
    }
}