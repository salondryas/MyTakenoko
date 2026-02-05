package fr.cotedazur.univ.polytech.startingpoint.Strategies;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.Action;
import fr.cotedazur.univ.polytech.startingpoint.actions.DeplacerJardinier;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.Objectif;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.ObjectifJardinier;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.TypeObjectif;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Arrangement;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Jardinier;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

import java.util.List;

public class StrategieJardinierUnCoup {

    public Action getStrategieJardinierUnCoup(GameState gameState, Bot bot, Objectif objectifEnMain) {
        // Sécurité : On vérifie que c'est bien un objectif Jardinier
        if (objectifEnMain.getType() != TypeObjectif.JARDINIER) {
            return null;
        }

        Plateau plateau = gameState.getPlateau();
        ObjectifJardinier objJardinier = (ObjectifJardinier) objectifEnMain;

        Couleur couleurVisee = objJardinier.getCouleurs().get(0);
        int tailleVisee = objJardinier.getTaille();
        int nombreRequis = objJardinier.getNombreRequis();
        Arrangement arrangementRequis = objJardinier.getArrangementRequis();

        // 1. On compte combien de sous-objectifs sont DÉJÀ validés sur le plateau
        int compteurDejaValides = 0;
        for (Position pos : plateau.getPositionOccupees()) {
            Parcelle p = plateau.getParcelle(pos);
            if (p.getCouleur() == couleurVisee
                    && p.getNbSectionsSurParcelle() >= tailleVisee
                    && p.arrangementValide(arrangementRequis)) {
                compteurDejaValides++;
            }
        }

        // 2. Si il ne manque qu'UN SEUL bambou pour valider l'objectif
        if (nombreRequis - compteurDejaValides == 1) {
            // On cherche une parcelle candidate pour faire pousser ce dernier bambou
            for (Position pos : plateau.getPositionOccupees()) {
                Parcelle p = plateau.getParcelle(pos);

                boolean bonneCouleur = (p.getCouleur() == couleurVisee);
                boolean estIrriguee = p.estIrriguee();
                boolean bonAmenagement = p.arrangementValide(arrangementRequis);
                // La parcelle doit être à (TailleVisée - 1) pour être finie en un coup
                boolean estPretAFinir = (p.getNbSectionsSurParcelle() == tailleVisee - 1);

                // Note : Pour les objectifs max (4 sections), on vérifie < 4 car on ne peut pas dépasser 4
                if (p.getNbSectionsSurParcelle() >= 4) estPretAFinir = false;

                if (bonneCouleur && estIrriguee && bonAmenagement && estPretAFinir) {
                    // On cherche le mouvement (Direct ou via Voisin)
                    Action action = trouverCoupPourFairePousser(gameState, pos);
                    if (action != null) {
                        return action;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Calcule le déplacement du Jardinier :
     * Soit directement sur la case, soit sur une case voisine de même couleur.
     */
    private Action trouverCoupPourFairePousser(GameState gameState, Position cible) {
        Jardinier jardinier = gameState.getJardinier();
        Plateau plateau = gameState.getPlateau();
        Parcelle parcelleCible = plateau.getParcelle(cible);

        // Option A : Aller directement sur la cible
        if (jardinier.accessibleEnUnCoupParJardinier(gameState, cible)) {
            return new DeplacerJardinier(jardinier, cible);
        }

        // Option B : Aller sur un voisin de même couleur (Règle du Jardinier)
        List<Position> voisins = cible.getVoisins();
        for (Position voisin : voisins) {
            Parcelle parcelleVoisine = plateau.getParcelle(voisin);

            // Si le voisin existe et a la même couleur que la cible
            if (parcelleVoisine != null && parcelleVoisine.getCouleur() == parcelleCible.getCouleur()) {
                // Si ce voisin est accessible
                if (jardinier.accessibleEnUnCoupParJardinier(gameState, voisin)) {
                    return new DeplacerJardinier(jardinier, voisin);
                }
            }
        }
        return null;
    }
}