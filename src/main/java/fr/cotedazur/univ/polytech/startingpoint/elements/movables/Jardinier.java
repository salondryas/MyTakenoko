package fr.cotedazur.univ.polytech.startingpoint.elements.movables;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.elements.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

import java.util.List;

public class Jardinier {
    private Position position;
    private final Plateau plateau; // Référence au plateau

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

    public boolean accessibleEnUnCoupParJardinier(GameState gameState, Position positionAAcceder) {
        List<Position> trajets = plateau.getTrajetsLigneDroite(this.getPosition());
        return trajets.contains(positionAAcceder);
    }
}