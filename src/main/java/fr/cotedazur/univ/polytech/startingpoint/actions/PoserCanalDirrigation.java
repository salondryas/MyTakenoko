package fr.cotedazur.univ.polytech.startingpoint.actions;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

public class PoserCanalDirrigation implements Action {
    private Position position1;
    private Position position2;

    public PoserCanalDirrigation(Position position1, Position position2) {
        this.position1 = position1;
        this.position2 = position2;
    }

    @Override
    public void appliquer(GameState gameState, Bot joueur) {
        Plateau plateau = gameState.getPlateau();

        // On vérifie d'abord que le joueur a du stock
        if (joueur.getInventaire().getNombreCanauxDisponibles() > 0) {

            // Ensuite on tente de le poser sur le plateau
            if (plateau.placerCanal(position1, position2)) {
                // Si le plateau accepte (pas de doublon, placement valide), on débite
                joueur.getInventaire().retirerIrrigation();
            }
        }
    }

    @Override
    public TypeAction getType() {
        return TypeAction.POSER_IRRIGATION;
    }

    @Override
    public String toString() {
        return "pose un canal d'irrigation entre " + position1 + " et " + position2;
    }
}