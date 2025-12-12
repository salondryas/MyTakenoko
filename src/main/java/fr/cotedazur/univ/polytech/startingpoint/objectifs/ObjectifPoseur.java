package fr.cotedazur.univ.polytech.startingpoint.objectifs;

import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

import java.util.Map;

public class ObjectifPoseur extends Objectif {
    private final Couleur couleur;
    private final int nombreRequis;

    public ObjectifPoseur(int points, Couleur couleur, int nombreRequis) {
        super(points);
        this.couleur = couleur;
        this.nombreRequis = nombreRequis;
    }

    @Override
    public boolean valider(Plateau plateau) {
        if (this.valide) return true;

        int compteur = 0;
        // Utilisation de ta nouvelle méthode getParcellesMap !
        for (Parcelle p : plateau.getParcellesMap().values()) {
            if (p.getCouleur() == this.couleur) {
                compteur++;
            }
        }

        if (compteur >= nombreRequis) {
            this.valide = true;
            return true;
        }
        return false;
    }
}