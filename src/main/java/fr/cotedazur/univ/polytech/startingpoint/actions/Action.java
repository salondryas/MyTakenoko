package fr.cotedazur.univ.polytech.startingpoint.actions;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;

public interface Action {
    // La méthode reçoit maintenant le contexte global ET le joueur qui fait l'action
    void appliquer(GameState gameState, Bot joueur);

    TypeAction getType();

    String toString();
}