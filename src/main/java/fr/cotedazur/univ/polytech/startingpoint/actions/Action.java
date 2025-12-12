package fr.cotedazur.univ.polytech.startingpoint.actions;

import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;

/**
 * Interface représentant une action qu'un bot peut effectuer.
 * C'est le coeur du Pattern Command.
 */
public interface Action {
    // La méthode qui effectue réellement le changement sur le plateau
    void appliquer(Plateau plateau);

    // Pour l'affichage dans la console ("Le bot a posé une tuile...")
    String toString();
}