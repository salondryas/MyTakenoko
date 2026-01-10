package fr.cotedazur.univ.polytech.startingpoint.utilitaires;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Position {
    private final int q;
    private final int r;
    private final int s;

    public Position(int q, int r, int s) {
        if (q + r + s != 0) {
            throw new InvalidCoordinatesException("Coordonnées invalides : q+r+s doit être égal à 0 ; ", q, r, s);
        }
        this.q = q;
        this.r = r;
        this.s = s;
    }

    // Constructeur simplifié pour l'axial (s est calculé automatiquement)
    public Position(int q, int r) {
        this(q, r, -q - r);
    }

    public int getQ() {
        return q;
    }

    public int getR() {
        return r;
    }

    public int getS() {
        return s;
    }

    // Indispensable pour utiliser Position comme clé dans une HashMap (le Plateau)
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Position position = (Position) o;
        return q == position.q && r == position.r && s == position.s;
    }

    @Override
    public int hashCode() {
        return Objects.hash(q, r, s);
    }

    @Override
    public String toString() {
        return "(" + q + ", " + r + ", " + s + ")";
    }

    public int distance(Position p) {
        Position vec = new Position(p.q - q, p.r - r, p.s - s);
        return (Math.abs(vec.q) + Math.abs(vec.r) + Math.abs(vec.s)) / 2;
    }

    public Position add(Position p) {
        return new Position(this.q + p.q, this.r + p.r, this.s + p.s);
    }

    // Rotation à gauche
    public Position rotate60() {
        return new Position(-r, -s, -q);
    }

    // Rotation à droite
    public Position rotateMinus60() {
        return new Position(-s, -q, -r);
    }

    public boolean estAdjacent(Position p) {
        return distance(p) == 1;
    }

    public List<Position> getVoisins() {
        return Arrays.asList(
                new Position(q + 1, r - 1, s),
                new Position(q + 1, r, s - 1),
                new Position(q, r + 1, s - 1),
                new Position(q - 1, r + 1, s),
                new Position(q - 1, r, s + 1),
                new Position(q, r - 1, s + 1));
    }

}