package fr.cotedazur.univ.polytech.startingpoint.joueurs.Strategies;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.*;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.TypeObjectif;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class StrategieAleatoire {

    private final Random random;

    public StrategieAleatoire() {
        this.random = new Random();
    }

    /**
     * Choisit une action au hasard parmi celles autorisées et possibles.
     */
    public Action getActionAleatoire(GameState gameState, Set<TypeAction> typesInterdits) {
        List<TypeAction> actionsPossibles = new ArrayList<>();

        // 1. On liste ce qu'on a le DROIT de faire
        if (!typesInterdits.contains(TypeAction.PIOCHER_OBJECTIF))
            actionsPossibles.add(TypeAction.PIOCHER_OBJECTIF);

        if (!typesInterdits.contains(TypeAction.POSER_PARCELLE) && !gameState.getPiocheParcelle().estVide())
            actionsPossibles.add(TypeAction.POSER_PARCELLE);

        if (!typesInterdits.contains(TypeAction.DEPLACER_JARDINIER))
            actionsPossibles.add(TypeAction.DEPLACER_JARDINIER);

        if (!typesInterdits.contains(TypeAction.DEPLACER_PANDA))
            actionsPossibles.add(TypeAction.DEPLACER_PANDA);

        if (actionsPossibles.isEmpty())
            return null;

        // 2. On mélange pour l'aléatoire
        Collections.shuffle(actionsPossibles, random);

        // 3. On essaie de créer l'action concrète
        for (TypeAction type : actionsPossibles) {
            switch (type) {
                case PIOCHER_OBJECTIF:
                    // On essaie de piocher (en vérifiant que les pioches ne sont pas vides)
                    // On tente 3 fois au hasard, sinon on prend la première dispo
                    int randType = random.nextInt(3);

                    // Essai prioritaire aléatoire
                    if (randType == 0 && gameState.getPiochePanda().getTaille() > 0)
                        return new PiocherObjectif(TypeObjectif.PANDA);
                    if (randType == 1 && gameState.getPiocheJardinier().getTaille() > 0)
                        return new PiocherObjectif(TypeObjectif.JARDINIER);
                    if (randType == 2 && gameState.getPiocheObjectifParcelle().getTaille() > 0)
                        return new PiocherObjectif(TypeObjectif.PARCELLE);

                    // Fallback (si le random est tombé sur une pioche vide)
                    if (gameState.getPiochePanda().getTaille() > 0) return new PiocherObjectif(TypeObjectif.PANDA);
                    if (gameState.getPiocheJardinier().getTaille() > 0) return new PiocherObjectif(TypeObjectif.JARDINIER);
                    if (gameState.getPiocheObjectifParcelle().getTaille() > 0) return new PiocherObjectif(TypeObjectif.PARCELLE);
                    break;

                case POSER_PARCELLE:
                    return new PoserParcelle();

                case DEPLACER_JARDINIER:
                    List<Position> posJ = gameState.getPlateau()
                            .getTrajetsLigneDroite(gameState.getJardinier().getPosition());
                    posJ.remove(gameState.getJardinier().getPosition()); // On ne reste pas sur place

                    if (!posJ.isEmpty())
                        return new DeplacerJardinier(gameState.getJardinier(), posJ.get(random.nextInt(posJ.size())));
                    break;

                case DEPLACER_PANDA:
                    List<Position> posP = gameState.getPlateau()
                            .getTrajetsLigneDroite(gameState.getPanda().getPositionPanda());
                    posP.remove(gameState.getPanda().getPositionPanda());

                    if (!posP.isEmpty())
                        return new DeplacerPanda(gameState.getPanda(), posP.get(random.nextInt(posP.size())));
                    break;
            }
        }
        return null;
    }
}