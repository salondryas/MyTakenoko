package fr.cotedazur.univ.polytech.startingpoint.objectifs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;

public class ObjectifPoseur extends Objectif {
    private int nombre;
    private Couleur couleur;

    public ObjectifPoseur(int nombre, Couleur couleur) {
        this.nombre = nombre;
        this.couleur = couleur;
    }

    @Override
    public int getPoints() {
        return 2; // Score fixe pour ce Milestone
    }

    @Override
    public boolean valider(GameState gameState, Bot bot) {
        int compteur = 0;
        // On parcourt toutes les parcelles posées sur le plateau
        for (Parcelle p : gameState.getPlateau().getParcellesMap().values()) {
            if (p.getCouleur() == this.couleur) {
                compteur++;
            }
        }
        // Si on a assez de parcelles de la bonne couleur, c'est gagné
        return compteur >= nombre;
    }
}