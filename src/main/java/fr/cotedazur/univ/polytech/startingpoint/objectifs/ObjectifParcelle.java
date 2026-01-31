package fr.cotedazur.univ.polytech.startingpoint.objectifs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

import java.util.Map;

public class ObjectifParcelle extends Objectif {
    private final int nombre;
    private final Couleur couleur;
    // L'attribut 'points' est supprimé car géré par la classe mère

    // CORRECTION : Ajout de 'int points' pour le passer au constructeur parent
    public ObjectifParcelle(int points, int nombre, Couleur couleur) {
        super(points, TypeObjectif.PARCELLE); // Envoi des infos au parent
        this.nombre = nombre;
        this.couleur = couleur;
    }

    @Override
    public boolean valider(GameState gameState, Bot bot) {
        int compteur = 0;
        // On récupère toutes les parcelles posées sur le plateau
        Map<Position, Parcelle> parcelles = gameState.getPlateau().getParcellesMap();

        for (Parcelle p : parcelles.values()) {
            // On compte celles qui correspondent à la couleur demandée
            if (p.getCouleur() == this.couleur) {
                compteur++;
            }
        }
        // Si on a assez de parcelles de la bonne couleur sur le plateau, c'est gagné
        return compteur >= nombre;
    }

    // INDISPENSABLE : Pour que le Bot sache quelle couleur poser
    public Couleur getCouleur() {
        return this.couleur;
    }

    @Override
    public String toString() {
        return "Objectif Parcelle : Avoir " + nombre + " parcelles " + couleur + " sur le plateau (" + super.getPoints() + "pts)";
    }
}