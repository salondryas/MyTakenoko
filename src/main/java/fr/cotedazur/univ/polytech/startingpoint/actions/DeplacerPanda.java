package fr.cotedazur.univ.polytech.startingpoint.actions;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Panda;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Logger;
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
        Panda pandaJeu = gameState.getPanda(); // On récupère le panda du jeu

        // 1. Déplacement
        pandaJeu.setPositionPanda(destination);

        // 2. Manger le bambou
        Parcelle parcelle = plateau.getParcelle(destination);

        // CORRECTION : On vérifie que la parcelle existe, n'est pas l'étang, et a du bambou
        if (parcelle != null && parcelle.getCouleur() != Couleur.AUCUNE) {

            // On tente de manger (supposons que Parcelle a une méthode retirerBambou ou similaire)
            // Si vous n'avez pas de boolean de retour sur retirerSection(), on vérifie la taille avant
            if (parcelle.getNbSectionsSurParcelle() > 0) {
                parcelle.getBambou().retirerSection();

                // On ajoute à l'inventaire
                bot.getInventaire().ajouterBambou(parcelle.getCouleur());

                Logger.print(" Le panda mange du bambou " + parcelle.getCouleur() + " en " + destination);
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