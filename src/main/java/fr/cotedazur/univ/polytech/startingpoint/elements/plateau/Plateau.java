package fr.cotedazur.univ.polytech.startingpoint.elements.plateau;

import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.CanalDIrrigation;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.ExplorateurPlateau;
import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.affichage.AfficherEtatPlateau;

import java.util.*;

public class Plateau {

    // 1. Constante accessible pour les Bots
    public static final Position POSITION_ORIGINE = GrillePlateau.POSITION_ORIGINE;

    // Sous-systèmes
    private final GrillePlateau grille;
    private final GestionIrrigation irrigation;

    public Plateau() {
        this.grille = new GrillePlateau();
        this.irrigation = new GestionIrrigation();

        // Initialisation de l'étang via la grille (déjà fait dans le constructeur de
        // GrillePlateau)
        // On s'assure que l'irrigation connait l'étang
        irrigation.getParcellesIrriguees().add(POSITION_ORIGINE);
    }

    // =================================================================================
    // FAÇADE VERS GRILLE (Stockage)
    // =================================================================================

    public Parcelle getParcelle(Position position) {
        return grille.getParcelle(position);
    }

    public Map<Position, Parcelle> getParcellesMap() {
        return grille.getMap();
    }

    public Set<Position> getPositionOccupees() {
        return grille.getMap().keySet();
    }

    // =================================================================================
    // FAÇADE VERS EXPLORATEUR (Intelligence)
    // =================================================================================

    public boolean isPositionDisponible(Position position) {
        // La méthode n'existe pas en statique, on utilise la liste des dispos.
        // C'est moins performant mais sûr : si la position est dans la liste, c'est
        // qu'elle est libre et légale.
        return ExplorateurPlateau.getEmplacementsDisponibles(grille.getMap()).contains(position);
    }

    public boolean placerParcelle(Parcelle parcelle, Position position) {
        if (!isPositionDisponible(position)) {
            return false;
        }

        // Ajout physique
        grille.ajouterParcelle(parcelle, position);

        // Gestion Irrigation (Correction : 2 arguments uniquement)

        // boolean estIrriguee = irrigation.checkNouvelleIrrigation(parcelle, position);
        irrigation.checkNouvelleIrrigation(parcelle, position);

        /*
         * if (estIrriguee && parcelle.getNbSectionsSurParcelle() == 0) {
         * parcelle.getBambou().croissance();
         * }
         */
        // Ne sert à rien si on a deja trigger l'irrgation depuis
        // checkNouvelleIrrigation()

        return true;
    }

    public List<Position> getEmplacementsDisponibles() {
        return ExplorateurPlateau.getEmplacementsDisponibles(grille.getMap());
    }

    public List<Position> getTrajetsLigneDroite(Position depart) {
        return ExplorateurPlateau.getTrajetsLigneDroite(grille.getMap(), depart);
    }

    // Helper pour BotPanda
    public int getNombreDeSectionsAPosition(Position pos) {
        Parcelle p = getParcelle(pos);
        return (p != null) ? p.getNbSectionsSurParcelle() : 0;
    }

    public List<Parcelle> getParcellesVoisinesMemeCouleur(Position cible) {
        // Implémentation locale ou délégation si ExplorateurPlateau le supporte
        // Ici on remet une implémentation simple pour éviter les erreurs si manquant
        List<Parcelle> voisines = new ArrayList<>();
        // (Logique simplifiée ou retour vide si géré ailleurs)
        return voisines;
    }

    // =================================================================================
    // FAÇADE VERS IRRIGATION (Eau)
    // =================================================================================

    public boolean placerCanal(Position p1, Position p2) {
        return irrigation.placerCanal(p1, p2, grille.getMap());
    }

    public void ajouterCanal(Position p1, Position p2) {
        placerCanal(p1, p2);
    }

    public boolean peutPlacerCanal(Position p1, Position p2) {
        return irrigation.peutPlacerCanal(p1, p2, grille.getMap());
    }

    public boolean aCanalEntre(Position p1, Position p2) {
        return irrigation.aCanalEntre(p1, p2);
    }

    public Set<CanalDIrrigation> getCanaux() {
        return irrigation.getCanaux();
    }

    public Set<Position> getParcellesIrriguees() {
        return irrigation.getParcellesIrriguees();
    }

    // =================================================================================
    // AFFICHAGE
    // =================================================================================

    @Override
    public String toString() {
        return new AfficherEtatPlateau(this).afficher();
    }

    public GrillePlateau getGrille() {
        return grille;
    }
}