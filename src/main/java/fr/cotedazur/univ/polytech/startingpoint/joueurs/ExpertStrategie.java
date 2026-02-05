package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.*;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.Objectif;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.TypeObjectif;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.plateau.pioche.SelectionParcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

import java.util.*;

public class ExpertStrategie {

    private final Bot bot; // Référence vers le bot pour connaître son inventaire

    public ExpertStrategie(Bot bot) {
        this.bot = bot;
    }

    /**
     * Evalue toutes les actions possibles et renvoie la meilleure.
     */
    public Action choisirMeilleureAction(GameState gameState, Set<TypeAction> typesInterdits) {
        List<Action> actionsPossibles = genererToutesActionsPossibles(gameState, typesInterdits);

        Action meilleurAction = null;
        double meilleurScore = -1.0;

        for (Action action : actionsPossibles) {
            double score = evaluerAction(action, gameState);
            if (score > meilleurScore) {
                meilleurScore = score;
                meilleurAction = action;
            }
        }
        return meilleurAction;
    }

    /**
     * Génère la liste brute de tout ce qu'on peut faire.
     */
    private List<Action> genererToutesActionsPossibles(GameState gameState, Set<TypeAction> typesInterdits) {
        List<Action> actions = new ArrayList<>();
        Plateau plateau = gameState.getPlateau();

        // 1. Piocher Objectif
        if (!typesInterdits.contains(TypeAction.PIOCHER_OBJECTIF)) {
            if (gameState.getPiochePanda().getTaille() > 0) actions.add(new PiocherObjectif(TypeObjectif.PANDA));
            if (gameState.getPiocheJardinier().getTaille() > 0) actions.add(new PiocherObjectif(TypeObjectif.JARDINIER));
            if (gameState.getPiocheObjectifParcelle().getTaille() > 0) actions.add(new PiocherObjectif(TypeObjectif.PARCELLE));
        }

        // 2. Déplacer Panda
        if (!typesInterdits.contains(TypeAction.DEPLACER_PANDA)) {
            Position posPanda = gameState.getPanda().getPosition();
            List<Position> destinations = plateau.getTrajetsLigneDroite(posPanda);
            for (Position dest : destinations) {
                actions.add(new DeplacerPanda(gameState.getPanda(), dest));
            }
        }

        // 3. Déplacer Jardinier
        if (!typesInterdits.contains(TypeAction.DEPLACER_JARDINIER)) {
            Position posJardinier = gameState.getJardinier().getPosition();
            List<Position> destinations = plateau.getTrajetsLigneDroite(posJardinier);
            for (Position dest : destinations) {
                actions.add(new DeplacerJardinier(gameState.getJardinier(), dest));
            }
        }

        // 4. Poser Parcelle (Simplifié : on considère juste l'action générique pour l'expert qui piochera)
        if (!typesInterdits.contains(TypeAction.POSER_PARCELLE) && !gameState.getPiocheParcelle().estVide()) {
            actions.add(new PoserParcelle());
        }

        // 5. Irrigation
        if (!typesInterdits.contains(TypeAction.PRENDRE_IRRIGATION) && gameState.getNbCanaux() > 0) {
            actions.add(new ObtenirCanalDirrigation());
        }

        return actions;
    }
    public Parcelle choisirMeilleureParcelle(SelectionParcelle session) {
        // Stratégie simple : prendre celle qui a la couleur dont on a le plus besoin
        // (Ici on prend la première pour l'exemple, mais c'est ici qu'on mettrait l'intelligence)
        if (session.getParcellesAChoisir().isEmpty()) return null;
        return session.getParcellesAChoisir().get(0);
    }

    public Position choisirMeilleurePosition(Plateau plateau) {
        List<Position> dispos = plateau.getEmplacementsDisponibles();
        if (dispos.isEmpty()) return null;

        // Stratégie : Maximiser le nombre de voisins ou l'irrigation
        // (Ici on prend la première pour corriger la compilation)
        return dispos.get(0);
    }

    /**
     * Fonction Heuristique : Donne une note à une action.
     */
    public double evaluerAction(Action action, GameState gameState) {
        double score = 0;

        // --- Stratégie basique d'exemple ---

        if (action instanceof PiocherObjectif) {
            // On aime piocher si on a peu d'objectifs
            if (bot.getInventaire().getObjectifs().size() < 3) score += 10;
            else score += 1;
        }
        else if (action instanceof DeplacerPanda) {
            DeplacerPanda move = (DeplacerPanda) action;
            // On aime manger du bambou si ça valide un objectif
            if (estUtilePourObjectifPanda(move.getDestination(), gameState)) {
                score += 20;
            } else {
                score += 2; // Manger c'est toujours bien
            }
        }
        else if (action instanceof DeplacerJardinier) {
            // Faire pousser c'est bien
            score += 5;
        }
        else if (action instanceof PoserParcelle) {
            score += 8; // Agrandir le plateau ouvre des possibilités
        }

        return score;
    }

    private boolean estUtilePourObjectifPanda(Position pos, GameState gs) {
        // Logique simplifiée : est-ce qu'il y a du bambou de la couleur dont j'ai besoin ?
        // (A raffiner selon vos besoins réels)
        return gs.getPlateau().getNombreDeSectionsAPosition(pos) > 0;
    }
}