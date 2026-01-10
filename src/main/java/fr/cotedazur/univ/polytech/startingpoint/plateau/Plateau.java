package fr.cotedazur.univ.polytech.startingpoint.plateau;

import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.PositionsRelatives;

import java.util.*;

public class Plateau {
    public static final Position POSITION_ORIGINE = new Position(0, 0, 0);
    public static final Parcelle PARCELLE_ORIGINE = new Parcelle(POSITION_ORIGINE, Couleur.AUCUNE);

    private final Map<Position, Parcelle> parcelles;

    private Set<CanalDirrigation> canaux; // Ensemble des canaux placés
    private Set<Position> parcellesIrriguees; // Recensement des parcelles irriguées

    public Plateau() {
        parcelles = new HashMap<>();
        parcelles.put(POSITION_ORIGINE, PARCELLE_ORIGINE);
        this.canaux = new HashSet<>();
        this.parcellesIrriguees = new HashSet<>();
        parcellesIrriguees.add(POSITION_ORIGINE);
    }

    public void placerParcelle(Parcelle parcelle, Position position) {
        parcelles.put(position, parcelle);
        // Irrigation automatique si adjacente à l'étang
        if (position.estAdjacent(POSITION_ORIGINE)) {
            parcelle.triggerIrrigation();
            parcellesIrriguees.add(position);
        }
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
                if (voisin.equals(POSITION_ORIGINE))
                    return true;
                if (parcelles.containsKey(voisin))
                    nbVoisinsOccupes++;
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

                // On avance tant qu'il y a une parcelle (on ne peut pas traverser un trou).
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
            return parcelle.getNbSectionsSurParcelle();
        }
        return 0;
    }

    public Set<Position> getPositionOccupees() {
        Set<Position> positionsOccupees = new HashSet<>();
        for (Parcelle parcelle : parcelles.values()) {
            positionsOccupees.add(parcelle.getPosition());
        }
        return positionsOccupees;
    }

    /**
     * Récupère la liste des parcelles adjacentes à une position donnée
     * qui ont exactement la même couleur que la parcelle située à cette position.
     */
    public java.util.List<Parcelle> getParcellesVoisinesMemeCouleur(
            fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position positionCible) {
        java.util.List<Parcelle> voisinesMemeCouleur = new java.util.ArrayList<>();

        // 1. On récupère la parcelle centrale
        Parcelle parcelleCentrale = getParcelle(positionCible);
        if (parcelleCentrale == null)
            return voisinesMemeCouleur; // Sécurité

        // 2. CORRECTION : On parcourt l'Enum directement
        for (PositionsRelatives direction : PositionsRelatives.values()) {
            // On ignore la position ZERO (qui est la case elle-même).
            if (direction != PositionsRelatives.ZERO) {

                // On calcule la position de la voisine en ajoutant le vecteur de direction
                fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position posVoisine = positionCible
                        .add(direction.getPosition());

                // 3. On récupère la parcelle voisine
                Parcelle voisine = getParcelle(posVoisine);

                // 4. On vérifie : elle existe ET elle a la même couleur
                if (voisine != null && voisine.getCouleur() == parcelleCentrale.getCouleur()) {
                    voisinesMemeCouleur.add(voisine);
                }
            }
        }
        return voisinesMemeCouleur;
    }

    /**
     * Vérifie si un canal peut être placé entre deux positions.
     * Conditions :
     * - Les positions doivent être adjacentes
     * - Ne peut pas être sur une arête de l'étang
     * - Doit être connecté au réseau (chemin de canaux depuis l'étang)
     */
    public boolean peutPlacerCanal(Position p1, Position p2) {
        if (!p1.estAdjacent(p2)) {
            return false;
        }

        if (p1.equals(POSITION_ORIGINE) || p2.equals(POSITION_ORIGINE)) {
            return false;
        }

        return estConnecteAuReseau(p1, p2);
    }

    /**
     * Vérifie si une arête (p1, p2) est connectée au réseau d'irrigation.
     * Utilise un parcours BFS depuis l'étang.
     */
    /*
     * private boolean estConnecteAuReseau(Position p1, Position p2) {
     * if (p1.estAdjacent(POSITION_ORIGINE) || p2.estAdjacent(POSITION_ORIGINE)) {
     * return true;
     * }
     * 
     * Set<Position> positionsAccessibles = new HashSet<>();
     * Queue<Position> aExplorer = new LinkedList<>();
     * 
     * aExplorer.add(POSITION_ORIGINE);
     * positionsAccessibles.add(POSITION_ORIGINE);
     * 
     * while (!aExplorer.isEmpty()) {
     * Position courante = aExplorer.poll();
     * 
     * for (CanalDirrigation canal : canaux) {
     * Arete arete = canal.getPosition();
     * 
     * if (arete.touchePosition(courante)) {
     * Position autre = arete.getParcelle1().equals(courante)
     * ? arete.getParcelle2()
     * : arete.getParcelle1();
     * 
     * if (!positionsAccessibles.contains(autre)) {
     * positionsAccessibles.add(autre);
     * aExplorer.add(autre);
     * }
     * }
     * }
     * }
     * 
     * return positionsAccessibles.contains(p1) ||
     * positionsAccessibles.contains(p2);
     * }
     */
    private boolean estConnecteAuReseau(Position p1, Position p2) {
        if (p1.estAdjacent(POSITION_ORIGINE) || p2.estAdjacent(POSITION_ORIGINE)) {
            return true;
        }

        Set<Position> positionsAccessibles = new HashSet<>();
        Queue<Position> aExplorer = new LinkedList<>();

        // ========== ANCIEN CODE (BUGGÉ) ==========
        // aExplorer.add(PARCELLE_ORIGINE);
        // positionsAccessibles.add(PARCELLE_ORIGINE);
        // =========================================

        // ========== NOUVEAU CODE (CORRIGÉ) ==========
        aExplorer.add(POSITION_ORIGINE);
        positionsAccessibles.add(POSITION_ORIGINE);

        // AJOUT: Ajouter toutes les parcelles déjà irriguées comme points de départ
        for (Position posIrriguee : parcellesIrriguees) {
            if (!positionsAccessibles.contains(posIrriguee)) {
                positionsAccessibles.add(posIrriguee);
                aExplorer.add(posIrriguee);
            }
        }
        // ============================================

        // Le reste du BFS est identique...
        while (!aExplorer.isEmpty()) {
            Position courante = aExplorer.poll();

            for (CanalDirrigation canal : canaux) {
                Arete arete = canal.getPosition();

                if (arete.touchePosition(courante)) {
                    Position autre = arete.getParcelle1().equals(courante)
                            ? arete.getParcelle2()
                            : arete.getParcelle1();

                    if (!positionsAccessibles.contains(autre)) {
                        positionsAccessibles.add(autre);
                        aExplorer.add(autre);
                    }
                }
            }
        }

        return positionsAccessibles.contains(p1) || positionsAccessibles.contains(p2);
    }

    /**
     * Place un canal d'irrigation entre deux parcelles.
     * Déclenche l'irrigation sur toutes les parcelles non-irriguées touchées.
     * return true si le placement a réussi, false sinon
     */
    public boolean placerCanal(Position p1, Position p2) {
        if (!peutPlacerCanal(p1, p2)) {
            return false;
        }

        CanalDirrigation nouveauCanal = new CanalDirrigation(p1, p2);
        canaux.add(nouveauCanal);

        irriguerParcellesTouchees(nouveauCanal);

        return true;
    }

    /**
     * Irrigue toutes les parcelles non-irriguées touchées par un canal.
     * Méthode générique qui gère n'importe quel nombre de parcelles.
     */
    private void irriguerParcellesTouchees(CanalDirrigation canal) {
        Arete arete = canal.getPosition();

        for (Position pos : arete.getPositions()) {
            Parcelle parcelle = parcelles.get(pos);

            if (parcelle != null && !parcelle.estIrriguee()) {
                parcelle.triggerIrrigation();
                parcellesIrriguees.add(pos);
            }
        }
    }

    public Set<Position> getParcellesIrriguees() {
        return new HashSet<>(parcellesIrriguees);
    }

    public Set<CanalDirrigation> getCanaux() {
        return new HashSet<>(canaux);
    }
}