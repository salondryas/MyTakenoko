package fr.cotedazur.univ.polytech.startingpoint.plateau;

import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

public enum PositionsVoisinesOrigine {
    UN(new Position(0, -1)),            // Haut Gauche
    DEUX(new Position(1, -1)),          // Haut Droite
    TROIS(new Position(-1, 0)),         // Gauche
    QUATRE(new Position(1, 0)),         // Droite
    CINQ(new Position(-1, 1)),          // Bas Gauche
    SIX(new Position(0, 1)),            // Bas Droite
    ;
    private Position positionVoisine;

    PositionsVoisinesOrigine(Position positionVoisine) {
        this.positionVoisine = positionVoisine;
    }

    public Position getPositionVoisine() {
        return positionVoisine;
    }
}
