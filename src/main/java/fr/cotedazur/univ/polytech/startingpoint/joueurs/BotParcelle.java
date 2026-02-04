package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.Action;
import fr.cotedazur.univ.polytech.startingpoint.actions.PiocherObjectif;
import fr.cotedazur.univ.polytech.startingpoint.actions.PoserParcelle;
import fr.cotedazur.univ.polytech.startingpoint.actions.TypeAction;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.Objectif;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.TypeObjectif;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.plateau.pioche.SelectionParcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Logger;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.PositionsRelatives;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Bot spécialisé dans les Objectifs Parcelles (Jardinier Constructeur).
 * Stratégie : "Simuler et Noter" (Exhaustive Search).
 */
public class BotParcelle extends Bot {

    // Mémoire tampon pour stocker la décision prise lors de la phase "choisirParcelle"
    private Position positionMemoriseePourLeTour = null;

    public BotParcelle(String nom) {
        super(nom);
    }

    /**
     * LOGIQUE GÉNÉRALE DU TOUR
     * Priorité absolue : Avoir des objectifs parcelles et poser des tuiles.
     */
    @Override
    protected Action choisirUneAction(GameState gameState, Set<TypeAction> typesInterdits) {
        // 1. Si je n'ai pas d'objectifs Parcelle -> Je pioche (Urgence)
        if (getNombreObjectifsParcelleEnMain() == 0 && !typesInterdits.contains(TypeAction.PIOCHER_OBJECTIF)) {
            return new PiocherObjectif(TypeObjectif.PARCELLE);
        }

        // 2. Si je peux poser une parcelle -> C'est mon action principale
        // (La réflexion intelligente se fera dans choisirParcelle appelé par l'action)
        if (!typesInterdits.contains(TypeAction.POSER_PARCELLE) && !gameState.getPiocheParcelle().estVide()) {
            return new PoserParcelle();
        }

        // 3. Sinon, actions de remplissage (Piocher encore, ou Irrigation)
        if (!typesInterdits.contains(TypeAction.PIOCHER_OBJECTIF)) {
            return new PiocherObjectif(TypeObjectif.PARCELLE);
        }

        return null; // Passe son tour si bloqué
    }

    /**
     * LE CŒUR DU RÉACTEUR : SIMULER ET NOTER
     * Cette méthode choisit la meilleure tuile ET calcule déjà où la mettre.
     */
    @Override
    public Parcelle choisirParcelle(SelectionParcelle selection, Plateau plateau) {
        List<Parcelle> tuilesCandidates = selection.getParcellesAChoisir();
        List<Position> positionsLegales = plateau.getEmplacementsDisponibles();

        Parcelle meilleurTuile = tuilesCandidates.get(0);
        Position meilleurPos = (positionsLegales.isEmpty()) ? null : positionsLegales.get(0);
        int meilleurScore = -1;

        // --- ALGORITHME EXHAUSTIF (Brute Force Intelligent) ---
        // Pour chaque tuile disponible...
        for (Parcelle tuile : tuilesCandidates) {
            // Pour chaque emplacement légal sur le plateau...
            for (Position pos : positionsLegales) {

                // 1. Simuler & Noter
                int score = evaluerCoup(tuile, pos, plateau);

                // 2. Choisir (Max)
                if (score > meilleurScore) {
                    meilleurScore = score;
                    meilleurTuile = tuile;
                    meilleurPos = pos;
                }
            }
        }

        // 3. Mémorisation pour l'étape suivante
        this.positionMemoriseePourLeTour = meilleurPos;
        Logger.print(getNom() + " a calculé le meilleur coup : " + meilleurTuile.getCouleur() + " en " + meilleurPos + " (Score: " + meilleurScore + ")");

        // Validation du choix dans le moteur de jeu
        selection.validerChoix(meilleurTuile);
        return meilleurTuile;
    }

    /**
     * Récupère simplement la position calculée à l'étape précédente.
     */
    @Override
    public Position choisirPosition(Parcelle parcelleChoisie, Plateau plateau) {
        if (positionMemoriseePourLeTour != null && plateau.isPositionDisponible(positionMemoriseePourLeTour)) {
            Position pos = positionMemoriseePourLeTour;
            positionMemoriseePourLeTour = null; // Reset pour le prochain tour
            return pos;
        }
        // Fallback de sécurité (ne devrait pas arriver si le plateau n'a pas changé)
        List<Position> dispos = plateau.getEmplacementsDisponibles();
        return dispos.isEmpty() ? null : dispos.get(0);
    }

    // =================================================================================
    // FONCTION D'ÉVALUATION (SCORING)
    // =================================================================================

    private int evaluerCoup(Parcelle tuile, Position pos, Plateau plateau) {
        int score = 0;
        Couleur couleurTuile = tuile.getCouleur();

        // Critère 1 : Besoin Immédiat (Poids FORT : 20 pts)
        // Est-ce que cette couleur m'aide pour un de mes objectifs ?
        boolean couleurUtile = false;
        for (Objectif obj : getInventaire().getObjectifs()) {
            if (obj.getType() == TypeObjectif.PARCELLE && obj.getCouleurs().contains(couleurTuile)) {
                score += 20;
                couleurUtile = true;
                // Si la tuile complète potentiellement un motif (heuristic simple: on en a besoin), on booste
            }
        }

        // Si la couleur ne sert à rien, on penalise fortement ce coup
        if (!couleurUtile) {
            score -= 10;
        }

        // Critère 2 : Adjacence Utile / Regroupement (Poids MOYEN : 10 pts par voisin)
        // On regarde les voisins directs. Créer des zones unicolores est la clé des motifs.
        int voisinsMemeCouleur = 0;
        for (PositionsRelatives dir : PositionsRelatives.values()) {
            if (dir == PositionsRelatives.ZERO) continue;

            Position voisinPos = pos.add(dir.getPosition());
            Parcelle voisin = plateau.getParcelle(voisinPos);

            if (voisin != null && voisin.getCouleur() == couleurTuile) {
                voisinsMemeCouleur++;
            }
        }
        score += (voisinsMemeCouleur * 10);

        // Critère 3 : Irrigation (Poids VARIABLE : 15 pts)
        // Une parcelle irriguée vaut plus cher car elle valide les objectifs
        boolean accesEau = pos.estAdjacent(Plateau.POSITION_ORIGINE) || aCanalAdjacent(plateau, pos);
        if (accesEau) {
            score += 15;
        } else {
            // Petit malus si c'est sec, car il faudra dépenser des actions pour irriguer
            score -= 5;
        }

        // Critère 4 : Avancement précis du Motif (Bonus Spécial)
        // Si je pose une tuile qui crée une ligne ou un triangle avec mes voisins
        if (voisinsMemeCouleur >= 1) {
            score += 5; // Début de motif
        }
        if (voisinsMemeCouleur >= 2) {
            score += 20; // Motif avancé (Triangle ou Ligne probable)
        }

        return score;
    }

    // --- UTILITAIRES ---

    private int getNombreObjectifsParcelleEnMain() {
        return (int) getInventaire().getObjectifs().stream()
                .filter(o -> o.getType() == TypeObjectif.PARCELLE)
                .count();
    }

    private boolean aCanalAdjacent(Plateau plateau, Position pos) {
        // On vérifie si un canal touche cette position (simulation simplifiée)
        // Dans l'idéal, on utiliserait plateau.peutPlacerCanal ou verification des arêtes
        // Ici on suppose que si on est collé à une position irriguée, c'est bon signe (heuristique)
        return false; // À améliorer avec l'accès aux canaux du plateau si nécessaire
    }
}