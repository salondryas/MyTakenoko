package fr.cotedazur.univ.polytech.startingpoint.plateau;

import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

public class Jardinier {
    private Position position;

    public Jardinier() {
        // Le Jardinier commence toujours à l'étang (0,0)
        this.position = new Position(0, 0);
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}