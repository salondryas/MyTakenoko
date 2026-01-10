package fr.cotedazur.univ.polytech.startingpoint.actions;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;

/**
 * Action permettant au joueur de prendre 1 canal d'irrigation dans la réserve.
 * Le canal peut être utilisé immédiatement ou conservé pour les tours suivants.
 */
public class ObtenirCanalDirrigation implements Action {

    @Override
    public void appliquer(GameState gameState, Bot joueur) {
        // Ajoute 1 canal d'irrigation à l'inventaire du joueur
        joueur.getInventaire().ajouterIrrigation();
    }

    @Override
    public String toString() {
        return "prend 1 canal d'irrigation";
    }
}
