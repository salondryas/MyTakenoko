package fr.cotedazur.univ.polytech.startingpoint.plateau;

import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

public class Jardinier {
    private Position position;
    private final Plateau plateau; // Référence au plateau

    // CORRECTION : On demande le Plateau à la création
    public Jardinier(Plateau plateau) {
        this.plateau = plateau;
        // Le Jardinier commence toujours à l'étang (0,0)
        this.position = new Position(0, 0);
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    // Le jardinier peut maintenant utiliser 'this.plateau' si besoin pour des vérifications futures
}