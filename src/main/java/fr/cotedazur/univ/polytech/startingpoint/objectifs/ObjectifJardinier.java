package fr.cotedazur.univ.polytech.startingpoint.objectifs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

import java.util.Map;

public class ObjectifJardinier extends Objectif {
    private final Couleur couleur;
    private final int tailleRequise;
    private final int points; // Ajout : les points ne sont pas fixes

    public ObjectifJardinier(Couleur couleur, int tailleRequise, int points) {
        super(); // Appel au constructeur parent (Objectif)
        this.couleur = couleur;
        this.tailleRequise = tailleRequise;
        this.points = points;
    }

    @Override
    public int getPoints() {
        return points;
    }

    @Override
    public boolean valider(GameState gameState, Bot bot) {
        // On récupère toutes les parcelles du plateau
        Map<Position, Parcelle> parcelles = gameState.getPlateau().getParcellesMap();

        for (Parcelle p : parcelles.values()) {
            // Critère 1 : La bonne couleur
            // Critère 2 : La taille est suffisante (ou supérieure)
            if (p.getCouleur() == this.couleur && p.getNbSectionsSurParcelle() >= tailleRequise) {
                return true; // Dès qu'on en trouve UN qui match, c'est validé !
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Objectif Jardinier : Bambou " + couleur + " de taille " + tailleRequise + " (" + points + "pts)";
    }
}