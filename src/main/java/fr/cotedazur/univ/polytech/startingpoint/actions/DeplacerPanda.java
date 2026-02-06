package fr.cotedazur.univ.polytech.startingpoint.actions;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.elements.movables.Panda;
import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.elements.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

import static fr.cotedazur.univ.polytech.startingpoint.GameEngine.LOGGER;

public class DeplacerPanda implements Action {
    private Panda panda;
    private Position destination;

    public DeplacerPanda(Panda panda, Position destination) {
        this.panda = panda;
        this.destination = destination;
    }

    public Position getDestination() {
        return destination;
    }

    @Override
    public void appliquer(GameState gameState, Bot bot) {
        Plateau plateau = gameState.getPlateau();
        Panda pandaJeu = gameState.getPanda(); // On récupère le panda du jeu

        // 1. Déplacement
        pandaJeu.setPositionPanda(destination);

        // 2. Manger le bambou
        Parcelle parcelle = plateau.getParcelle(destination);

        // On vérifie que la parcelle existe, n'est pas l'étang, et a du
        // bambou
        if (parcelle != null && parcelle.getCouleur() != Couleur.AUCUNE) {

            if (pandaJeu.mangerBambou(destination, plateau)) {

                // On ajoute à l'inventaire
                bot.getInventaire().ajouterBambou(parcelle.getCouleur());

                LOGGER.info(" Le panda mange du bambou " + parcelle.getCouleur() + " en " + destination);
            }
        }
    }

    @Override
    public TypeAction getType() {
        return TypeAction.DEPLACER_PANDA;
    }

    @Override
    public String toString() {
        return "déplace le panda en " + destination;
    }
}