package fr.cotedazur.univ.polytech.startingpoint.plateau;

import fr.cotedazur.univ.polytech.startingpoint.plateau.amenagements.Bassin;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.PositionsRelatives;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static fr.cotedazur.univ.polytech.startingpoint.plateau.GrillePlateau.POSITION_ORIGINE;

public class GestionIrrigation {
    private final Set<CanalDIrrigation> canaux = new HashSet<>();
    private final Set<Position> parcellesDejaIrriguees = new HashSet<>();

    public GestionIrrigation() {
        // L'étang est irrigué dès le début.
        parcellesDejaIrriguees.add(POSITION_ORIGINE);
    }

    public Set<CanalDIrrigation> getCanaux() {
        return new HashSet<>(canaux);
    }

    public Set<Position> getParcellesIrriguees() {
        return new HashSet<>(parcellesDejaIrriguees);
    }

    /**
     * Tente de placer un canal et propage l'irrigation si réussi.
     */
    public boolean placerCanal(Position p1, Position p2, Map<Position, Parcelle> parcelles) {
        // 1. Vérification de la légalité du coup
        if (!peutPlacerCanal(p1, p2, parcelles))
            return false;

        // 2. Création et ajout
        CanalDIrrigation nouveauCanal = new CanalDIrrigation(p1, p2);

        // Sécurité doublon (bien que peutPlacerCanal le vérifie déjà)
        if (canaux.contains(nouveauCanal))
            return false;

        canaux.add(nouveauCanal);

        // 3. Propagation immédiate de l'eau
        irriguerParcellesTouchees(nouveauCanal, parcelles);

        return true;
    }

    public boolean peutPlacerCanal(Position p1, Position p2, Map<Position, Parcelle> parcelles) {
        // Règle 1 : Les positions doivent être adjacentes
        if (!p1.estAdjacent(p2))
            return false;

        // Règle 2 : Le canal ne doit pas déjà exister
        if (aCanalEntre(p1, p2))
            return false;

        // Règle 3 : Les parcelles doivent exister (sauf l'étang)
        // Note : Dans Takenoko, on pose des canaux sur les arêtes. Les règles disent
        // "entre deux parcelles".
        // On considère que l'étang (0,0) est une "parcelle" valide (elle est dans la
        // grille).
        boolean p1Existe = parcelles.containsKey(p1);
        boolean p2Existe = parcelles.containsKey(p2);

        if (!p1Existe || !p2Existe)
            return false;

        // Règle 4 : Connexion à l'eau (Source ou Réseau)
        boolean p1EstSource = p1.estAdjacent(POSITION_ORIGINE) || estConnecteAUnAutreCanal(p1);
        boolean p2EstSource = p2.estAdjacent(POSITION_ORIGINE) || estConnecteAUnAutreCanal(p2);

        // Il suffit qu'un des deux points soit connecté à l'eau pour qu'on puisse
        // étendre le réseau
        return p1EstSource || p2EstSource;
    }

    /**
     * Vérifie si une parcelle vient d'être irriguée suite à sa pose.
     * Appelé par Plateau.placerParcelle.
     */
    public void checkNouvelleIrrigation(Parcelle parcelle, Position position) {
        if (parcellesDejaIrriguees.contains(position))
            return;// Déjà traitée
        // return false; // Déjà traitée

        boolean estIrriguable = position.estAdjacent(POSITION_ORIGINE) // Adjacent Etang
                // || parcelle.getAmenagement() instanceof Bassin // Aménagement Bassin
                // redondant car c'est le boulot de Bassin lui-même de devoir irriguée toute
                // parcelle qui lui est associée donc
                || aUnCanalAdjacent(position); // Canal adjacent

        if (estIrriguable) {
            parcellesDejaIrriguees.add(position);
            parcelle.triggerIrrigation(); // IMPORTANT : Met à jour le boolean interne de la Parcelle
            // return true;
        }
        // return false;
    }

    public boolean aCanalEntre(Position p1, Position p2) {
        return canaux.contains(new CanalDIrrigation(p1, p2));
    }

    private boolean estConnecteAUnAutreCanal(Position p) {
        for (CanalDIrrigation canal : this.canaux) {
            if (canal.getPosition().touchePosition(p)) {
                return true;
            }
        }
        return false;
    }

    private boolean aUnCanalAdjacent(Position pos) {
        for (PositionsRelatives dir : PositionsRelatives.values()) {
            if (dir == PositionsRelatives.ZERO)
                continue;
            Position voisin = pos.add(dir.getPosition());
            if (aCanalEntre(pos, voisin))
                return true;
        }
        return false;
    }

    private void irriguerParcellesTouchees(CanalDIrrigation canal, Map<Position, Parcelle> parcelles) {
        Arete arete = canal.getPosition();
        Position[] positions = arete.getPositions().toArray(new Position[0]); // p1 et p2

        for (Position pos : positions) {
            Parcelle p = parcelles.get(pos);
            if (p != null) {
                // On vérifie si cette parcelle devient irriguée grâce à ce nouveau canal
                // (Elle l'est forcément puisqu'on vient de poser un canal à côté d'elle
                // connecté à l'eau)
                if (!parcellesDejaIrriguees.contains(pos)) {
                    parcellesDejaIrriguees.add(pos);
                    p.triggerIrrigation(); // Fait pousser le bambou si c'est la première fois

                    // Règle spécifique : Si c'est la première fois qu'elle est irriguée et qu'elle
                    // n'avait pas de bambou
                    if (p.getNbSectionsSurParcelle() == 0) {
                        p.getBambou().croissance();
                    }
                }
            }
        }
    }
}