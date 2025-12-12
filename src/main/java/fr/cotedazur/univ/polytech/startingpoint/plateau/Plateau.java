package fr.cotedazur.univ.polytech.startingpoint.plateau;

import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Plateau {
    public static final Position POSITION_ORIGINE = new Position(0, 0);
    public static final Parcelle PARCELLE_ORIGINE = new Parcelle(POSITION_ORIGINE, Couleur.AUCUNE);
    // Utilisation d'une Map pour accès rapide par Position
    private final Map<Position, Parcelle> parcelles;

    public Plateau() {
        parcelles = new HashMap<>();
        // On pose l'étang
        parcelles.put(POSITION_ORIGINE, PARCELLE_ORIGINE);
    }

    // Permet de poser une tuile
    public void placerParcelle(Parcelle parcelle, Position position) {
        parcelles.put(position, parcelle);
    }

    public Parcelle getParcelle(Position position) {
        return parcelles.get(position);
    }

    public boolean isPositionDisponible(Position position) {
        return (!parcelles.containsKey(position));
    }

    // Algorithme correct pour trouver les places libres
    public List<Position> getEmplacementsDisponibles() {
        List<Position> disponibles = new ArrayList<>();

        // On regarde chaque parcelle déjà posée
        for (Position posOccupee : parcelles.keySet()) {
            // On regarde ses 6 voisins via ton Enum
            for (PositionsVoisinesOrigine direction : PositionsVoisinesOrigine.values()) {
                Position voisinPotentiel = posOccupee.add(direction.getPositionVoisine());

                // Si la place est libre et qu'on ne l'a pas déjà listée
                if (isPositionDisponible(voisinPotentiel) && !disponibles.contains(voisinPotentiel)) {
                    disponibles.add(voisinPotentiel);
                }
            }
        }
        return disponibles;
    }
    public Map<Position, Parcelle> getParcellesMap() {
        return new HashMap<>(parcelles); // Retourne une copie pour protéger l'original
    }
}
