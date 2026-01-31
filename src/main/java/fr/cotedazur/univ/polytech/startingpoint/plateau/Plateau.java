package fr.cotedazur.univ.polytech.startingpoint.plateau;

import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.PositionsRelatives;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.affichage.AfficherEtatPlateau;

import java.util.*;

public class Plateau {
    public static final Position POSITION_ORIGINE = new Position(0, 0, 0);
    public static final Parcelle PARCELLE_ORIGINE = new Parcelle(POSITION_ORIGINE, Couleur.AUCUNE);

    private final Map<Position, Parcelle> parcelles;

    private Set<CanalDirrigation> canaux;
    private Set<Position> parcellesIrriguees;

    public Plateau() {
        parcelles = new HashMap<>();
        parcelles.put(POSITION_ORIGINE, PARCELLE_ORIGINE);
        this.canaux = new HashSet<>();
        this.parcellesIrriguees = new HashSet<>();
        parcellesIrriguees.add(POSITION_ORIGINE);
    }

    public void placerParcelle(Parcelle parcelle, Position position) {
        parcelles.put(position, parcelle);
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

    // --- PANDA / JARDINIER ---

    public List<Position> getTrajetsLigneDroite(Position depart) {
        List<Position> destinationsPossibles = new ArrayList<>();
        for (PositionsRelatives direction : PositionsRelatives.values()) {
            if (direction != PositionsRelatives.ZERO) {
                Position vecteur = direction.getPosition();
                Position testPos = depart.add(vecteur);

                while (parcelles.containsKey(testPos)) {
                    destinationsPossibles.add(testPos);
                    testPos = testPos.add(vecteur);
                }
            }
        }
        return destinationsPossibles;
    }

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

    public List<Parcelle> getParcellesVoisinesMemeCouleur(Position positionCible) {
        List<Parcelle> voisinesMemeCouleur = new ArrayList<>();
        Parcelle parcelleCentrale = getParcelle(positionCible);
        if (parcelleCentrale == null)
            return voisinesMemeCouleur;

        for (PositionsRelatives direction : PositionsRelatives.values()) {
            if (direction != PositionsRelatives.ZERO) {
                Position posVoisine = positionCible.add(direction.getPosition());
                Parcelle voisine = getParcelle(posVoisine);

                if (voisine != null && voisine.getCouleur() == parcelleCentrale.getCouleur()) {
                    voisinesMemeCouleur.add(voisine);
                }
            }
        }
        return voisinesMemeCouleur;
    }

    // --- IRRIGATION REFACTORISÉE (Cognitive Complexity Reduced) ---

    public boolean peutPlacerCanal(Position p1, Position p2) {
        if (!p1.estAdjacent(p2))
            return false;
        if (p1.equals(POSITION_ORIGINE) || p2.equals(POSITION_ORIGINE))
            return false;
        return estConnecteAuReseau(p1, p2);
    }

    /**
     * Vérifie la connexion au réseau via un BFS.
     * Complexité réduite par découpage en sous-méthodes.
     */
    private boolean estConnecteAuReseau(Position p1, Position p2) {
        // Cas trivial : adjacent à l'étang
        if (p1.estAdjacent(POSITION_ORIGINE) || p2.estAdjacent(POSITION_ORIGINE)) {
            return true;
        }

        // Initialisation du BFS
        Set<Position> accessibles = initialiserRecherche();
        Queue<Position> aExplorer = new LinkedList<>(accessibles);

        // Boucle principale du BFS
        while (!aExplorer.isEmpty()) {
            Position courante = aExplorer.poll();
            explorerVoisins(courante, accessibles, aExplorer);
        }

        return accessibles.contains(p1) || accessibles.contains(p2);
    }

    private Set<Position> initialiserRecherche() {
        Set<Position> accessibles = new HashSet<>();
        accessibles.add(POSITION_ORIGINE);
        // On part aussi de tout ce qui est déjà irrigué
        accessibles.addAll(parcellesIrriguees);
        return accessibles;
    }

    private void explorerVoisins(Position courante, Set<Position> accessibles, Queue<Position> aExplorer) {
        for (CanalDirrigation canal : canaux) {
            Arete arete = canal.getPosition();

            if (arete.touchePosition(courante)) {
                Position voisin = recupererAutreExtremite(arete, courante);

                if (!accessibles.contains(voisin)) {
                    accessibles.add(voisin);
                    aExplorer.add(voisin);
                }
            }
        }
    }

    private Position recupererAutreExtremite(Arete arete, Position courante) {
        return arete.getParcelle1().equals(courante)
                ? arete.getParcelle2()
                : arete.getParcelle1();
    }

    public boolean placerCanal(Position p1, Position p2) {
        if (!peutPlacerCanal(p1, p2)) return false;

        CanalDirrigation nouveauCanal = new CanalDirrigation(p1, p2);

        // --- REMETTRE CE BLOC DE SÉCURITÉ ---
        if (canaux.contains(nouveauCanal)) {
            return false;
        }
        // ------------------------------------

        canaux.add(nouveauCanal);
        irriguerParcellesTouchees(nouveauCanal);
        return true;
    }

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

    @Override
    public String toString() {
        AfficherEtatPlateau aep = new AfficherEtatPlateau(this);
        return aep.afficher();
    }

}