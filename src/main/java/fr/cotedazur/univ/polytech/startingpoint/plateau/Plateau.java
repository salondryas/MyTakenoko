package fr.cotedazur.univ.polytech.startingpoint.plateau;

import fr.cotedazur.univ.polytech.startingpoint.plateau.amenagements.Bassin;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.PositionsRelatives;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.affichage.AfficherEtatPlateau;

import java.util.*;

public class Plateau {
    // Constantes statiques
    public static final Position POSITION_ORIGINE = new Position(0, 0, 0);
    public static final Parcelle PARCELLE_ORIGINE = new Parcelle(POSITION_ORIGINE, Couleur.AUCUNE);

    // Stockage
    private final Map<Position, Parcelle> parcelles;
    private final Set<CanalDirrigation> canaux;

    // Historique pour gérer la règle "Pousse de bambou à la PREMIÈRE irrigation"
    private final Set<Position> parcellesDejaIrriguees;

    public Plateau() {
        this.parcelles = new HashMap<>();
        this.canaux = new HashSet<>();
        this.parcellesDejaIrriguees = new HashSet<>();

        // Initialisation de l'étang
        parcelles.put(POSITION_ORIGINE, PARCELLE_ORIGINE);
        parcellesDejaIrriguees.add(POSITION_ORIGINE);
        PARCELLE_ORIGINE.triggerIrrigation(); // L'étang est toujours irrigué
    }

    // =================================================================================
    // 1. GESTION DES PARCELLES (POSE & ACCÈS)
    // =================================================================================

    public boolean placerParcelle(Parcelle parcelle, Position position) {
        if (!isPositionDisponible(position)) {
            return false;
        }

        parcelles.put(position, parcelle);

        // Dès qu'on pose une parcelle, on vérifie si elle est irriguée (ex: adjacente étang)
        verifierEtMettreAJourIrrigation(parcelle, position);

        return true;
    }

    public Parcelle getParcelle(Position position) {
        return parcelles.get(position);
    }

    public boolean isPositionDisponible(Position position) {
        return !parcelles.containsKey(position);
    }

    public Map<Position, Parcelle> getParcellesMap() {
        return new HashMap<>(parcelles);
    }

    // =================================================================================
    // 2. GESTION DES CANAUX (RÈGLES D'IRRIGATION STRICTES)
    // =================================================================================

    /**
     * Vérifie si un canal peut être posé entre p1 et p2 selon les règles officielles.
     */
    public boolean peutPlacerCanal(Position p1, Position p2) {
        // Règle 0 : Géométrie (Doivent être adjacentes)
        if (!p1.estAdjacent(p2)) return false;

        // Règle 1 : "Un canal doit être placé entre deux parcelles" (elles doivent exister)
        if (!parcelles.containsKey(p1) || !parcelles.containsKey(p2)) return false;

        // Règle 2 : "Les frontières de l'étang ne peuvent pas accueillir de canaux"
        if (p1.equals(POSITION_ORIGINE) || p2.equals(POSITION_ORIGINE)) return false;

        // Règle 3 : Unicité (Pas de doublon)
        if (aCanalEntre(p1, p2)) return false;

        // Règle 4 : Connexion au réseau
        // Cas A : Connecté à l'étang via un COIN (les deux parcelles touchent l'étang)
        boolean toucheEtangParCoin = p1.estAdjacent(POSITION_ORIGINE) && p2.estAdjacent(POSITION_ORIGINE);

        // Cas B : Connecté à un canal existant
        boolean toucheReseau = estConnecteAUnAutreCanal(p1) || estConnecteAUnAutreCanal(p2);

        return toucheEtangParCoin || toucheReseau;
    }

    public boolean placerCanal(Position p1, Position p2) {
        if (!peutPlacerCanal(p1, p2)) return false;

        // Création et ajout du canal (CanalDirrigation utilise Arete en interne)
        CanalDirrigation nouveauCanal = new CanalDirrigation(p1, p2);
        canaux.add(nouveauCanal);

        // Mise à jour de l'irrigation des parcelles adjacentes à ce nouveau canal
        irriguerParcellesTouchees(nouveauCanal);

        return true;
    }

    /**
     * Vérifie si un canal existe déjà sur cette arête.
     * Utilise l'égalité définie dans CanalDirrigation et Arete.
     */
    public boolean aCanalEntre(Position p1, Position p2) {
        return canaux.contains(new CanalDirrigation(p1, p2));
    }

    /**
     * Vérifie si la position 'p' est une extrémité d'un canal existant.
     */
    private boolean estConnecteAUnAutreCanal(Position p) {
        for (CanalDirrigation canal : this.canaux) {
            if (canal.getPosition().touchePosition(p)) {
                return true;
            }
        }
        return false;
    }

    // =================================================================================
    // 3. LOGIQUE D'IRRIGATION ET POUSSE DU BAMBOU
    // =================================================================================

    private void irriguerParcellesTouchees(CanalDirrigation canal) {
        Arete arete = canal.getPosition();
        // Un canal alimente potentiellement les deux parcelles qu'il sépare
        verifierEtMettreAJourIrrigation(getParcelle(arete.getParcelle1()), arete.getParcelle1());
        verifierEtMettreAJourIrrigation(getParcelle(arete.getParcelle2()), arete.getParcelle2());
    }

    /**
     * Méthode centrale : Vérifie les 3 conditions d'irrigation.
     * Si la parcelle devient irriguée pour la PREMIÈRE FOIS, ajoute un bambou.
     */
    private void verifierEtMettreAJourIrrigation(Parcelle p, Position pos) {
        if (p == null) return;

        // Si déjà irriguée et traitée, on arrête (optimisation et respect de la règle "première fois")
        if (parcellesDejaIrriguees.contains(pos)) return;

        boolean conditionRemplie = false;

        // Condition 1 : Adjacente à l'étang
        if (pos.estAdjacent(POSITION_ORIGINE)) {
            conditionRemplie = true;
        }
        // Condition 2 : Aménagement Bassin
        else if (p.getAmenagement() instanceof Bassin) {
            conditionRemplie = true;
        }
        // Condition 3 : Au moins un canal adjacent sur l'une des 6 arêtes
        else if (aUnCanalAdjacent(pos)) {
            conditionRemplie = true;
        }

        if (conditionRemplie) {
            p.triggerIrrigation(); // Change le statut interne boolean de la parcelle
            parcellesDejaIrriguees.add(pos);

            // Règle : "Au moment où une parcelle est irriguée pour la première fois, une section est ajoutée."
            // On s'assure qu'elle n'a pas déjà poussé (via Parcelle.java ou Bambou.java)
            if (p.getNbSectionsSurParcelle() == 0) {
                p.getBambou().croissance();
            }
        }
    }

    private boolean aUnCanalAdjacent(Position pos) {
        for (PositionsRelatives dir : PositionsRelatives.values()) {
            if (dir == PositionsRelatives.ZERO) continue;
            Position voisin = pos.add(dir.getPosition());
            // Si un canal existe sur l'arête entre pos et voisin
            if (aCanalEntre(pos, voisin)) {
                return true;
            }
        }
        return false;
    }

    // =================================================================================
    // 4. OUTILS POUR LES BOTS (EXPLORATION)
    // =================================================================================

    public List<Position> getEmplacementsDisponibles() {
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

        // On filtre selon la règle de pose (2 voisins ou adjacent étang)
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
                if (voisin.equals(POSITION_ORIGINE)) return true; // Adjacent étang = OK
                if (parcelles.containsKey(voisin)) nbVoisinsOccupes++;
            }
        }
        return nbVoisinsOccupes >= 2;
    }

    public List<Position> getTrajetsLigneDroite(Position depart) {
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

    // Méthode helper pour BotPanda
    public int getNombreDeSectionsAPosition(Position pos) {
        Parcelle p = parcelles.get(pos);
        return (p != null) ? p.getNbSectionsSurParcelle() : 0;
    }

    // =================================================================================
    // 5. GETTERS & AFFICHAGE
    // =================================================================================

    public Set<CanalDirrigation> getCanaux() {
        return new HashSet<>(canaux);
    }

    public Set<Position> getParcellesIrriguees() {
        return new HashSet<>(parcellesDejaIrriguees);
    }

    @Override
    public String toString() {
        return new AfficherEtatPlateau(this).afficher();
    }
}