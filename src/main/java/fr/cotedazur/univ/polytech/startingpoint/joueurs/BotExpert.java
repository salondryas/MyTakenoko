package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.*;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.Objectif;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.TypeObjectif;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.plateau.pioche.SelectionParcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Logger;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.PositionsRelatives;

import java.util.*;

/**
 * Bot Expert (Heuristique).
 * Ne délègue pas. Évalue chaque action possible selon un score unifié (Points de victoire potentiels).
 */
public class BotExpert extends Bot {

    private Position positionMemorisee = null; // Pour la phase de pose

    public BotExpert(String nom) {
        super(nom);
    }

    @Override
    protected Action choisirUneAction(GameState gameState, Set<TypeAction> typesInterdits) {
        Action meilleureAction = null;
        double meilleurScore = Double.NEGATIVE_INFINITY;

        // 1. Évaluer : PIOCHER OBJECTIF (Si main vide ou presque)
        if (!typesInterdits.contains(TypeAction.PIOCHER_OBJECTIF)) {
            double score = evaluerPiocheObjectif(gameState);
            if (score > meilleurScore) {
                // Par défaut on alterne Panda/Jardinier si on pioche
                TypeObjectif type = (getInventaire().getObjectifs().size() % 2 == 0) ? TypeObjectif.PANDA : TypeObjectif.JARDINIER;
                meilleureAction = new PiocherObjectif(type);
                meilleurScore = score;
            }
        }

        // 2. Évaluer : POSER PARCELLE (Si disponible)
        if (!typesInterdits.contains(TypeAction.POSER_PARCELLE) && !gameState.getPiocheParcelle().estVide()) {
            double score = evaluerPoserParcelle(gameState);
            if (score > meilleurScore) {
                meilleureAction = new PoserParcelle();
                meilleurScore = score;
            }
        }

        // 3. Évaluer : DÉPLACER PANDA (Toutes les destinations possibles)
        if (!typesInterdits.contains(TypeAction.DEPLACER_PANDA)) {
            List<Position> destinations = gameState.getPlateau().getTrajetsLigneDroite(gameState.getPanda().getPositionPanda());
            for (Position dest : destinations) {
                if (dest.equals(gameState.getPanda().getPositionPanda())) continue;

                double score = evaluerCoupPanda(dest, gameState);
                if (score > meilleurScore) {
                    meilleureAction = new DeplacerPanda(gameState.getPanda(), dest);
                    meilleurScore = score;
                }
            }
        }

        // 4. Évaluer : DÉPLACER JARDINIER (Toutes les destinations possibles)
        if (!typesInterdits.contains(TypeAction.DEPLACER_JARDINIER)) {
            List<Position> destinations = gameState.getPlateau().getTrajetsLigneDroite(gameState.getJardinier().getPosition());
            for (Position dest : destinations) {
                if (dest.equals(gameState.getJardinier().getPosition())) continue;

                double score = evaluerCoupJardinier(dest, gameState);
                if (score > meilleurScore) {
                    meilleureAction = new DeplacerJardinier(gameState.getJardinier(), dest);
                    meilleurScore = score;
                }
            }
        }

        // 5. Évaluer : IRRIGATION (Si nécessaire)
        // (Simplification : on le fait si on a des canaux et un score critique d'irrigation, à ajouter si besoin)

        if (meilleureAction != null) {
            Logger.print(getNom() + " joue EXPERT : " + meilleureAction + " (Score=" + String.format("%.1f", meilleurScore) + ")");
        }
        return meilleureAction;
    }

    // =================================================================================
    // FONCTIONS D'ÉVALUATION (HEURISTIQUES)
    // =================================================================================

    /**
     * Score = Besoin de bambous de cette couleur * 100
     */
    private double evaluerCoupPanda(Position pos, GameState gs) {
        Parcelle p = gs.getPlateau().getParcelle(pos);
        if (p == null || p.getCouleur() == Couleur.AUCUNE || p.getNbSectionsSurParcelle() == 0) return -100;

        // Analyse des besoins
        Map<Couleur, Integer> besoins = calculerBesoinsPanda();
        int besoinCouleur = besoins.getOrDefault(p.getCouleur(), 0);

        if (besoinCouleur > 0) return 100.0 * besoinCouleur; // Très prioritaire si on en a besoin
        return 5.0; // Petit score par défaut (stocker du bambou c'est toujours bien)
    }

    /**
     * Score = Besoin de faire pousser cette couleur * 50
     */
    private double evaluerCoupJardinier(Position pos, GameState gs) {
        Parcelle p = gs.getPlateau().getParcelle(pos);
        if (p == null || p.getCouleur() == Couleur.AUCUNE || !p.estIrriguee() || p.getNbSectionsSurParcelle() >= 4) return -100;

        Map<Couleur, Integer> besoins = calculerBesoinsJardinier();
        int besoinCouleur = besoins.getOrDefault(p.getCouleur(), 0);

        // Bonus si ça aide un objectif
        double score = (besoinCouleur > 0) ? 50.0 * besoinCouleur : 2.0;

        // Bonus si ça aide les voisins (Jardinier fait pousser les voisins de même couleur)
        // (On pourrait scanner les voisins ici pour être encore plus expert)

        return score;
    }

    private double evaluerPoserParcelle(GameState gs) {
        // Si on a des objectifs Parcelles non réalisés, c'est prioritaire
        long nbObjParcelle = getInventaire().getObjectifs().stream()
                .filter(o -> o.getType() == TypeObjectif.PARCELLE).count();

        if (nbObjParcelle > 0) return 80.0;
        return 10.0; // Sinon c'est juste de l'expansion
    }

    private double evaluerPiocheObjectif(GameState gs) {
        int nbObjectifs = getInventaire().getObjectifs().size();
        if (nbObjectifs == 0) return 200.0; // URGENCE ABSOLUE
        if (nbObjectifs < 3) return 60.0;   // Confortable
        return -50.0; // Pénalité si main pleine
    }

    // --- ANALYSE DES BESOINS ---

    private Map<Couleur, Integer> calculerBesoinsPanda() {
        Map<Couleur, Integer> besoins = new HashMap<>();
        for (Objectif obj : getInventaire().getObjectifs()) {
            if (obj.getType() == TypeObjectif.PANDA) {
                for (Couleur c : obj.getCouleurs()) {
                    // On vérifie si on l'a déjà en stock
                    int enStock = getInventaire().getBambous().getOrDefault(c, 0);
                    if (enStock == 0) { // Simplification : Si on a 0, on en a besoin
                        besoins.merge(c, 1, Integer::sum);
                    }
                }
            }
        }
        return besoins;
    }

    private Map<Couleur, Integer> calculerBesoinsJardinier() {
        Map<Couleur, Integer> besoins = new HashMap<>();
        for (Objectif obj : getInventaire().getObjectifs()) {
            if (obj.getType() == TypeObjectif.JARDINIER) {
                // Pour le jardinier, on veut faire pousser la couleur demandée
                for (Couleur c : obj.getCouleurs()) {
                    besoins.merge(c, 1, Integer::sum);
                }
            }
        }
        return besoins;
    }

    // =================================================================================
    // LOGIQUE DE POSE INTELLIGENTE (Similaire BotParcelle)
    // =================================================================================

    @Override
    public Parcelle choisirParcelle(SelectionParcelle selection, Plateau plateau) {
        // C'est ici qu'on réutilise l'algo "Simuler et Noter"
        List<Parcelle> candidats = selection.getParcellesAChoisir();
        List<Position> slots = plateau.getEmplacementsDisponibles();

        Parcelle bestP = candidats.get(0);
        Position bestPos = (slots.isEmpty()) ? null : slots.get(0);
        double maxScore = -1;

        for (Parcelle p : candidats) {
            for (Position pos : slots) {
                double score = noterPose(p, pos, plateau);
                if (score > maxScore) {
                    maxScore = score;
                    bestP = p;
                    bestPos = pos;
                }
            }
        }

        this.positionMemorisee = bestPos;
        selection.validerChoix(bestP);
        return bestP;
    }

    @Override
    public Position choisirPosition(Parcelle parcelleChoisie, Plateau plateau) {
        if (positionMemorisee != null && plateau.isPositionDisponible(positionMemorisee)) {
            Position p = positionMemorisee;
            positionMemorisee = null;
            return p;
        }
        return plateau.getEmplacementsDisponibles().get(0);
    }

    private double noterPose(Parcelle p, Position pos, Plateau plateau) {
        double score = 0;
        // Critère 1 : Adjacence Couleur (Regroupement)
        for (PositionsRelatives dir : PositionsRelatives.values()) {
            if (dir == PositionsRelatives.ZERO) continue;
            Parcelle voisin = plateau.getParcelle(pos.add(dir.getPosition()));
            if (voisin != null && voisin.getCouleur() == p.getCouleur()) score += 10;
        }
        // Critère 2 : Irrigation
        if (pos.estAdjacent(Plateau.POSITION_ORIGINE)) score += 5;

        // Critère 3 : Objectif Parcelle
        // (On pourrait parcourir les objectifs Parcelles ici pour voir si ça complète un motif)

        return score;
    }
}