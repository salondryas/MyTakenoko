package fr.cotedazur.univ.polytech.startingpoint;

import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.PositionsVoisinesOrigine;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

public class MoteurObjectifs {

    public MoteurObjectifs() {}

    // Règle Milestone 1 : 1 point par couleur adjacente identique
    public int calculerPointsPose(Plateau plateau, Position positionPosee, Couleur couleur) {
        int points = 0;
        for (PositionsVoisinesOrigine direction : PositionsVoisinesOrigine.values()) {
            Position voisin = positionPosee.add(direction.getPositionVoisine());
            Parcelle parcelleVoisine = plateau.getParcelle(voisin);

            if (parcelleVoisine != null && parcelleVoisine.getCouleur() == couleur) {
                points++;
            }
        }
        return points;
    }
}