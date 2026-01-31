package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.*;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.TypeObjectif;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.PiocheParcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
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
    protected Action choisirUneAction(GameState gameState, Set<TypeAction> typesInterdits) {
        Plateau plateau = gameState.getPlateau();
        PiocheParcelle pioche = gameState.getPioche();

        // On essaie X fois de trouver une action au hasard qui n'est pas interdite
        for (int i = 0; i < 20; i++) {
            int choix = random.nextInt(5); // 0 à 4 (avec Irrigation)

            switch (choix) {
                case 0: // POSER PARCELLE
                    if (!typesInterdits.contains(TypeAction.POSER_PARCELLE)
                            && pioche.getSize() > 0
                            && !plateau.getEmplacementsDisponibles().isEmpty()) {
                        Parcelle p = pioche.piocherParcelle();
                        if (p != null) {
                            List<Position> dispos = plateau.getEmplacementsDisponibles();
                            Position pos = dispos.get(random.nextInt(dispos.size()));
                            return new PoserParcelle(new Parcelle(pos, p.getCouleur()), pos);
                        }
                    }
                    break;

                case 1: // PANDA
                    if (!typesInterdits.contains(TypeAction.DEPLACER_PANDA)) {
                        List<Position> deplacements = plateau.getTrajetsLigneDroite(gameState.getPanda().getPositionPanda());
                        if (!deplacements.isEmpty()) {
                            return new DeplacerPanda(gameState.getPanda(), deplacements.get(random.nextInt(deplacements.size())));
                        }
                    }
                    break;

                case 2: // JARDINIER
                    if (!typesInterdits.contains(TypeAction.DEPLACER_JARDINIER)) {
                        List<Position> deplacements = plateau.getTrajetsLigneDroite(gameState.getJardinier().getPosition());
                        if (!deplacements.isEmpty()) {
                            return new DeplacerJardinier(gameState.getJardinier(), deplacements.get(random.nextInt(deplacements.size())));
                        }
                    }
                    break;

                case 3: // PIOCHER OBJECTIF
                    if (!typesInterdits.contains(TypeAction.PIOCHER_OBJECTIF)) {
                        TypeObjectif type = (random.nextBoolean()) ? TypeObjectif.JARDINIER : TypeObjectif.PANDA;
                        return new PiocherObjectif(type);
                    }
                    break;

                case 4: // PRENDRE IRRIGATION
                    if (!typesInterdits.contains(TypeAction.PRENDRE_IRRIGATION)) {
                        return new ObtenirCanalDirrigation();
                    }
                    break;
            }
        }
        return null; // Rien trouvé
    }
}