package fr.cotedazur.univ.polytech.startingpoint.joueurs.Strategies;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.DeplacerPanda;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.Objectif;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.TypeObjectif;
import fr.cotedazur.univ.polytech.startingpoint.elements.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StrategiePandaGenerale{

    public DeplacerPanda getStrategiePandaGenerale(GameState gameState, Bot bot) {
        Plateau plateau = gameState.getPlateau();
        // ETAPE 1 : Identifier les couleurs dont on a besoin pour nos objectifs
        Set<Couleur> couleursRecherchees = new HashSet<>();
        Map<Couleur, Integer> bambousPossedes = bot.getInventaire().getBambous();


        for (Objectif obj : bot.getInventaire().getObjectifs()) {
            if (obj.getType() == TypeObjectif.PANDA) {
                Map<Couleur, Integer> requis = obj.getObjMap();
                for (Map.Entry<Couleur, Integer> entry : requis.entrySet()) {
                    Couleur c = entry.getKey();
                    int qteRequise = entry.getValue();
                    int qtePossedee = bambousPossedes.getOrDefault(c, 0);
                    if (qtePossedee < qteRequise) {
                        couleursRecherchees.add(c);
                    }
                }
            }
        }


        // ETAPE 2 : Chercher d'abord les bambous "utiles"
        if (!couleursRecherchees.isEmpty()) {
            for (Position pos : plateau.getPositionOccupees()) {
                if (plateau.getNombreDeSectionsAPosition(pos) > 0) {
                    Couleur couleurBambou = plateau.getParcelle(pos).getCouleur();
                    if (couleursRecherchees.contains(couleurBambou)) {
                        if (gameState.getPanda().accessibleEnUnCoupParPanda(gameState, pos)) {
                            return new DeplacerPanda(gameState.getPanda(), pos);
                        }
                    }
                }
            }
        }
        // ETAPE 3 : Sinon, manger n'importe quoi
        for (Position pos : plateau.getPositionOccupees()) {
            if (plateau.getNombreDeSectionsAPosition(pos) > 0) {
                if (gameState.getPanda().accessibleEnUnCoupParPanda(gameState, pos)) {
                    return new DeplacerPanda(gameState.getPanda(), pos);
                }
            }
        }
    return null;
    }
}

