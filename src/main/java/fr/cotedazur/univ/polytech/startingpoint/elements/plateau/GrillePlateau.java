package fr.cotedazur.univ.polytech.startingpoint.elements.plateau;

import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GrillePlateau {
    // On déplace les constantes ici, car elles définissent la grille
    public static final Position POSITION_ORIGINE = new Position(0, 0, 0);
    public static final Parcelle PARCELLE_ORIGINE = new Parcelle(POSITION_ORIGINE, Couleur.AUCUNE);

    private final Map<Position, Parcelle> parcelles;

    public GrillePlateau() {
        this.parcelles = new HashMap<>();
        // Initialisation de l'étang
        ajouterParcelle(PARCELLE_ORIGINE, POSITION_ORIGINE);
    }

    public void ajouterParcelle(Parcelle parcelle, Position position) {
        parcelles.put(position, parcelle);
    }

    public Parcelle getParcelle(Position position) {
        return parcelles.get(position);
    }

    public boolean contient(Position position) {
        return parcelles.containsKey(position);
    }

    // Renvoie une copie pour protéger les données (Encapsulation)
    public Map<Position, Parcelle> getMap() {
        return new HashMap<>(parcelles);
    }

    public Set<Position> getPositionsOccupees() {
        return new HashSet<>(parcelles.keySet());
    }
}