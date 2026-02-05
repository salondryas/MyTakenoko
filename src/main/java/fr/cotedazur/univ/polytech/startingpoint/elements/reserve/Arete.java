package fr.cotedazur.univ.polytech.startingpoint.elements.reserve;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

/**
 * Représente une arête entre deux parcelles hexagonales adjacentes.
 * Utilisée pour positionner les canaux d'irrigation.
 */
public class Arete {
    private final Position parcelle1;
    private final Position parcelle2;

    public Arete(Position p1, Position p2) {
        if (!p1.estAdjacent(p2)) {
            throw new IllegalArgumentException("Les deux positions doivent être adjacentes");
        }
        // Normalisation pour que (A,B) == (B,A)
        if (comparePositions(p1, p2) <= 0) {
            this.parcelle1 = p1;
            this.parcelle2 = p2;
        } else {
            this.parcelle1 = p2;
            this.parcelle2 = p1;
        }
    }

    private int comparePositions(Position p1, Position p2) {
        if (p1.getQ() != p2.getQ())
            return Integer.compare(p1.getQ(), p2.getQ());
        if (p1.getR() != p2.getR())
            return Integer.compare(p1.getR(), p2.getR());
        return Integer.compare(p1.getS(), p2.getS());
    }

    public Position getParcelle1() {
        return parcelle1;
    }

    public Position getParcelle2() {
        return parcelle2;
    }

    public boolean touchePosition(Position p) {
        return parcelle1.equals(p) || parcelle2.equals(p);
    }

    public List<Position> getPositions() {
        return Arrays.asList(parcelle1, parcelle2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Arete arete = (Arete) o;
        return parcelle1.equals(arete.parcelle1) && parcelle2.equals(arete.parcelle2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parcelle1, parcelle2);
    }

    @Override
    public String toString() {
        return "[" + parcelle1 + " <-> " + parcelle2 + "]";
    }
}
