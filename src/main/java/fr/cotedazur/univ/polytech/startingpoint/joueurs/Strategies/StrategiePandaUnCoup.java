package fr.cotedazur.univ.polytech.startingpoint.joueurs.Strategies;


import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.DeplacerPanda;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.Objectif;
import fr.cotedazur.univ.polytech.startingpoint.elements.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;


import java.util.Map;


public class StrategiePandaUnCoup {


    public DeplacerPanda getStrategiePandaUnCoup(GameState gameState, Bot bot, Objectif objectifEnMain) {
        Plateau plateau = gameState.getPlateau();
        Map<Couleur, Integer> bambousRequis = objectifEnMain.getObjMap();
        Map<Couleur, Integer> bambousPossedes = bot.getInventaire().getBambous();
        Couleur bambouManquant = null;


        int vertsPossedes = bambousPossedes.getOrDefault(Couleur.VERT, 0);
        int rosesPossedes = bambousPossedes.getOrDefault(Couleur.ROSE, 0);
        int jaunesPossedes = bambousPossedes.getOrDefault(Couleur.JAUNE, 0);


        int vertsRequis = bambousRequis.getOrDefault(Couleur.VERT, 0);
        int rosesRequis = bambousRequis.getOrDefault(Couleur.ROSE, 0);
        int jaunesRequis = bambousRequis.getOrDefault(Couleur.JAUNE, 0);


        if (jaunesPossedes == jaunesRequis - 1 && rosesPossedes >= rosesRequis && vertsPossedes >= vertsRequis)
            bambouManquant = Couleur.JAUNE;
        else if (jaunesPossedes >= jaunesRequis && rosesPossedes == rosesRequis - 1
                && vertsPossedes >= vertsRequis)
            bambouManquant = Couleur.ROSE;
        else if (jaunesPossedes >= jaunesRequis && rosesPossedes >= rosesRequis
                && vertsPossedes == vertsRequis - 1)
            bambouManquant = Couleur.VERT;


        if (bambouManquant != null) {
            for (Position pos : plateau.getPositionOccupees()) {
                if (plateau.getNombreDeSectionsAPosition(pos) > 0
                        && plateau.getParcelle(pos).getCouleur() == bambouManquant) {
                    if (gameState.getPanda().accessibleEnUnCoupParPanda(gameState, pos)) {
                        return new DeplacerPanda(gameState.getPanda(), pos);
                    }
                }
            }
        }
        return null;
    }
}
