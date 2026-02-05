package fr.cotedazur.univ.polytech.startingpoint.utilitaires;

import fr.cotedazur.univ.polytech.startingpoint.elements.plateau.Plateau;

public abstract class Placable {
    protected Position position;

    public Placable(Position position) {
        this.position=position;
    }

    @Override
    public String toString() {
        return position.toString();
    }

    public Position getPosition(){
        return position;
    }

    public abstract void placer(Plateau plateau);
}
