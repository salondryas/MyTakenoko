package fr.cotedazur.univ.polytech.startingpoint.plateau;

import java.util.Objects;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

/**
 * Représente un canal d'irrigation placé sur une arête entre deux parcelles.
 * Nettoyé pour le Refactor Sonar (Suppression du static).
 */
public class CanalDirrigation {
    private final Arete position;

    public CanalDirrigation(Arete position) {
        this.position = position;
    }

    public CanalDirrigation(Position p1, Position p2) {
        this(new Arete(p1, p2));
    }

    public Arete getPosition() {
        return position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CanalDirrigation canal = (CanalDirrigation) o;
        return Objects.equals(position, canal.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position);
    }

    @Override
    public String toString() {
        return "Canal d'irrigation sur l'arête " + position;
    }
}