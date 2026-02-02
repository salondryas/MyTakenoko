package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.*;
import fr.cotedazur.univ.polytech.startingpoint.actions.PoserParcelle;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.TypeObjectif;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.plateau.pioche.SelectionParcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class BotRandom extends Bot {
    private final Random random;

    public BotRandom(String nom) {
        super(nom);
        this.random = new Random();
    }

    @Override
    public Position choisirPosition(Parcelle parcelleChoisie, Plateau plateau) {
        List<Position> positionsDisponibles = plateau.getEmplacementsDisponibles();
        Position positionRandom = positionsDisponibles.get(random.nextInt(positionsDisponibles.size()));
        return positionRandom;
    }

    @Override
    protected Action choisirUneAction(GameState gameState, Set<TypeAction> typesInterdits) {
        Plateau plateau = gameState.getPlateau();

        for (int i = 0; i < 50; i++) { // Augmentation des essais pour trouver un coup valide
            int choix = random.nextInt(5);

            switch (choix) {
                case 0: // POSER PARCELLE
                    if (!typesInterdits.contains(TypeAction.POSER_PARCELLE)
                            && !gameState.getPiocheParcelle().estVide()) {

                        List<Position> dispos = plateau.getEmplacementsDisponibles();
                        if (!dispos.isEmpty()) {
                            Parcelle p = gameState.getPiocheParcelle().piocher();
                            if (p != null) {
                                return new PoserParcelle();
                            }
                        }
                    }
                    break;

                case 1: // DEPLACER PANDA
                    if (!typesInterdits.contains(TypeAction.DEPLACER_PANDA)) {
                        List<Position> dep = plateau.getTrajetsLigneDroite(gameState.getPanda().getPositionPanda());
                        if (!dep.isEmpty()) {
                            return new DeplacerPanda(gameState.getPanda(), dep.get(random.nextInt(dep.size())));
                        }
                    }
                    break;

                case 2: // DEPLACER JARDINIER
                    if (!typesInterdits.contains(TypeAction.DEPLACER_JARDINIER)) {
                        List<Position> dep = plateau.getTrajetsLigneDroite(gameState.getJardinier().getPosition());
                        if (!dep.isEmpty()) {
                            return new DeplacerJardinier(gameState.getJardinier(), dep.get(random.nextInt(dep.size())));
                        }
                    }
                    break;

                case 3: // PIOCHER OBJECTIF
                    if (!typesInterdits.contains(TypeAction.PIOCHER_OBJECTIF)) {
                        boolean jardinierVide = gameState.getPiocheJardinier().getTaille() == 0;
                        boolean pandaVide = gameState.getPiochePanda().getTaille() == 0;

                        if (!jardinierVide || !pandaVide) {
                            TypeObjectif type;
                            if (jardinierVide) type = TypeObjectif.PANDA;
                            else if (pandaVide) type = TypeObjectif.JARDINIER;
                            else type = random.nextBoolean() ? TypeObjectif.JARDINIER : TypeObjectif.PANDA;

                            return new PiocherObjectif(type);
                        }
                    }
                    break;

                case 4: // PRENDRE IRRIGATION
                    // CORRECTION : On interdit si on en a déjà plus de 2
                    if (!typesInterdits.contains(TypeAction.PRENDRE_IRRIGATION)
                            && getInventaire().getNombreCanauxDisponibles() < 2) {
                        return new ObtenirCanalDirrigation();
                    }
                    break;
            }
        }
        return null;
    }
}