package fr.cotedazur.univ.polytech.startingpoint.plateau;

import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

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
            for (PositionsVoisinesOrigine direction : PositionsVoisinesOrigine.values()) {
                Position voisin = posOccupee.add(direction.getPositionVoisine());
                if (!parcelles.containsKey(voisin)) {
                    candidats.add(voisin);
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
        for (PositionsVoisinesOrigine direction : PositionsVoisinesOrigine.values()) {
            Position voisin = position.add(direction.getPositionVoisine());
            if (voisin.equals(POSITION_ORIGINE)) return true;
            if (parcelles.containsKey(voisin)) nbVoisinsOccupes++;
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
        for (PositionsVoisinesOrigine direction : PositionsVoisinesOrigine.values()) {
            Position vecteur = direction.getPositionVoisine();
            Position testPos = depart.add(vecteur);

            // On avance tant qu'il y a une parcelle (on ne peut pas traverser un trou)
            while (parcelles.containsKey(testPos)) {
                destinationsPossibles.add(testPos);
                testPos = testPos.add(vecteur);
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
}