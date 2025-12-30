package fr.cotedazur.univ.polytech.startingpoint.plateau;

import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.PositionsRelatives;

import java.util.*;

public class Plateau {
    public static final Position POSITION_ORIGINE = new Position(0, 0, 0);
    public static final Parcelle PARCELLE_ORIGINE = new Parcelle(POSITION_ORIGINE, Couleur.AUCUNE);

    private final Map<Position, Parcelle> parcelles;

    public Plateau() {
        parcelles = new HashMap<>();
        parcelles.put(POSITION_ORIGINE, PARCELLE_ORIGINE);
    }

    public void placerParcelle(Parcelle parcelle, Position position) {
        parcelles.put(position, parcelle);
    }

    public Parcelle getParcelle(Position position) {
        return parcelles.get(position);
    }

    public boolean isPositionDisponible(Position position) {
        return (!parcelles.containsKey(position));
    }

    public List<Position> getEmplacementsDisponibles() {
        List<Position> disponibles = new ArrayList<>();
        Set<Position> candidats = new HashSet<>();

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

        for (Position candidat : candidats) {
            if (respecteReglePose(candidat)) {
                disponibles.add(candidat);
            }
        }
        return disponibles;
    }

    private boolean respecteReglePose(Position position) {
        int nbVoisinsOccupes = 0;
        for (PositionsRelatives direction : PositionsRelatives.values()) {
            if (direction != PositionsRelatives.ZERO) {
                Position voisin = position.add(direction.getPosition());
                if (voisin.equals(POSITION_ORIGINE)) return true;
                if (parcelles.containsKey(voisin)) nbVoisinsOccupes++;
            }
        }
        return nbVoisinsOccupes >= 2;
    }

    public Map<Position, Parcelle> getParcellesMap() {
        return new HashMap<>(parcelles);
    }

    // --- AJOUTS OBLIGATOIRES POUR PANDA/JARDINIER ---

    /**
     * Calcule les déplacements possibles en ligne droite depuis une position.
     * Utilisé par le Panda et le Jardinier.
     */
    public List<Position> getTrajetsLigneDroite(Position depart) {
        List<Position> destinationsPossibles = new ArrayList<>();
        for (PositionsRelatives direction : PositionsRelatives.values()) {
            if (direction != PositionsRelatives.ZERO) {
                Position vecteur = direction.getPosition();
                Position testPos = depart.add(vecteur);

                // On avance tant qu'il y a une parcelle (on ne peut pas traverser un trou)
                while (parcelles.containsKey(testPos)) {
                    destinationsPossibles.add(testPos);
                    testPos = testPos.add(vecteur);
                }
            }
        }
        return destinationsPossibles;
    }

    /**
     * Méthode utilitaire pour le Panda : savoir s'il y a du bambou à manger ici.
     */
    public int getNombreDeSectionsAPosition(Position pos) {
        Parcelle parcelle = parcelles.get(pos);
        if (parcelle != null) {
            return parcelle.getNbSectionsSurParcelle(); // Utilise la méthode de votre collègue
        }
        return 0;
    }

    public Set<Position> getPositionOccupees(){
        Set<Position> positionsOccupees= new HashSet<>();
        for (Parcelle parcelle : parcelles.values()) {
            positionsOccupees.add(parcelle.getPosition());
        }
        return positionsOccupees;
    }
}