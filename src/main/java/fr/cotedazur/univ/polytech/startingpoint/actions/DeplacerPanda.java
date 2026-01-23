package fr.cotedazur.univ.polytech.startingpoint.actions;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Panda;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

public class DeplacerPanda implements Action {
    private Panda panda;
    private Position destination;

    public DeplacerPanda(Panda panda, Position destination) {
        this.panda = panda;
        this.destination = destination;
    }

    @Override
    public void appliquer(GameState gameState, Bot bot) {
        Plateau plateau = gameState.getPlateau();

        // CORRECTION 1 : On déplace le panda d'abord (C'est l'but de l'action !)
        // Cette ligne manquait, c'est pour ça que le test échouait.
        panda.setPositionPanda(destination);

        // CORRECTION 2 : Ensuite, on regarde s'il y a à manger sur la case d'arrivée
        if (plateau.getNombreDeSectionsAPosition(destination) > 0) {
            Parcelle parcelle = plateau.getParcelle(destination);
            Couleur couleurBambou = parcelle.getCouleur();

            // Le Panda mange
            panda.mangerBambou(destination, plateau);

            // Le Bot gagne le bambou
            bot.getInventaire().ajouterBambou(couleurBambou);
        }
    }

    @Override
    public String toString() {
        return "déplace le panda en " + destination;
    }
}