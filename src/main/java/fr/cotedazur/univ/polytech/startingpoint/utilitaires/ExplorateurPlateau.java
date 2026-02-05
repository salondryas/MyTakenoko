package fr.cotedazur.univ.polytech.startingpoint.utilitaires;

import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.elements.plateau.GrillePlateau;

import java.util.*;

public class ExplorateurPlateau {

    /**
     * Vérifie si une position spécifique est libre et jouable selon les règles.
     * Cette méthode manquait et causait l'erreur dans le test.
     */
    public static boolean isPositionDisponible(Map<Position, Parcelle> parcelles, Position position) {
        // 1. La place doit être libre
        if (parcelles.containsKey(position)) {
            return false;
        }
        // 2. Les règles de voisinage doivent être respectées
        return respecteReglePose(parcelles, position);
    }

    public static List<Position> getEmplacementsDisponibles(Map<Position, Parcelle> parcelles) {
        List<Position> disponibles = new ArrayList<>();
        Set<Position> candidats = new HashSet<>();

        // On cherche tous les voisins des parcelles existantes
        for (Position posOccupee : parcelles.keySet()) {
            for (PositionsRelatives direction : PositionsRelatives.values()) {
                if (direction != PositionsRelatives.ZERO) {
                    Position voisin = posOccupee.add(direction.getPosition());
                    if (!parcelles.containsKey(voisin)) {
                        candidats.add(voisin);
                    }
                }
            }
        }

        // On filtre selon la règle de pose
        for (Position candidat : candidats) {
            if (respecteReglePose(parcelles, candidat))
                disponibles.add(candidat);
        }
        return disponibles;
    }

    private static boolean respecteReglePose(Map<Position, Parcelle> parcelles, Position position) {
        int nbVoisinsOccupes = 0;
        for (PositionsRelatives direction : PositionsRelatives.values()) {
            if (direction != PositionsRelatives.ZERO) {
                Position voisin = position.add(direction.getPosition());
                if (voisin.equals(GrillePlateau.POSITION_ORIGINE))
                    return true; // Adjacent étang = OK
                if (parcelles.containsKey(voisin))
                    nbVoisinsOccupes++;
            }
        }
        return nbVoisinsOccupes >= 2;
    }

    public static List<Position> getTrajetsLigneDroite(Map<Position, Parcelle> parcelles, Position depart) {
        List<Position> destinationsPossibles = new ArrayList<>();
        for (PositionsRelatives direction : PositionsRelatives.values()) {
            if (direction != PositionsRelatives.ZERO) {
                Position vecteur = direction.getPosition();
                Position testPos = depart.add(vecteur);

                // On avance tant qu'il y a des parcelles
                while (parcelles.containsKey(testPos)) {
                    destinationsPossibles.add(testPos);
                    testPos = testPos.add(vecteur);
                }
            }
        }
        return destinationsPossibles;
    }

    /**
     * Utile pour les Bots (Jardinier notamment) pour trouver des groupes de
     * couleur.
     */
    public static List<Parcelle> getParcellesVoisinesMemeCouleur(Map<Position, Parcelle> parcelles,
            Position positionCible) {
        List<Parcelle> voisinesMemeCouleur = new ArrayList<>();
        Parcelle parcelleCentrale = parcelles.get(positionCible);

        if (parcelleCentrale == null)
            return voisinesMemeCouleur;

        for (PositionsRelatives direction : PositionsRelatives.values()) {
            if (direction != PositionsRelatives.ZERO) {
                Position posVoisine = positionCible.add(direction.getPosition());
                Parcelle voisine = parcelles.get(posVoisine);

                if (voisine != null && voisine.getCouleur() == parcelleCentrale.getCouleur()) {
                    voisinesMemeCouleur.add(voisine);
                }
            }
        }
        return voisinesMemeCouleur;
    }
}