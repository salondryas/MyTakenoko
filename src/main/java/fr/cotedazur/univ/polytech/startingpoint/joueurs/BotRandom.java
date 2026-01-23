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

public class BotRandom extends Bot {

    private final Random random;

    public BotRandom(String nom) {
        super(nom);
        this.random = new Random();
    }

    @Override
    public void jouer(GameState gameState) {
        Plateau plateau = gameState.getPlateau();
        PiocheParcelle pioche = gameState.getPioche();

        // On essaie de jouer (boucle de sécurité)
        for (int i = 0; i < 10; i++) {
            int choix = random.nextInt(4); // 0, 1, 2, 3

            switch (choix) {
                case 0: // POSER PARCELLE
                    if (pioche.getSize() > 0 && !plateau.getEmplacementsDisponibles().isEmpty()) {
                        Parcelle p = pioche.piocherParcelle();
                        if (p != null) {
                            List<Position> dispos = plateau.getEmplacementsDisponibles();
                            Position pos = dispos.get(random.nextInt(dispos.size()));

                            // MODIFICATION : Création de l'action séparée pour l'affichage
                            Action action = new PoserParcelle(new Parcelle(pos, p.getCouleur()), pos);
                            System.out.println(getNom() + " joue : " + action.toString());
                            action.appliquer(gameState, this);
                            return;
                        }
                    }
                    break;

                case 1: // PANDA
                    List<Position> deplacementsP = plateau.getTrajetsLigneDroite(gameState.getPanda().getPositionPanda());
                    if (!deplacementsP.isEmpty()) {
                        Position dest = deplacementsP.get(random.nextInt(deplacementsP.size()));

                        // MODIFICATION : Log
                        Action action = new DeplacerPanda(gameState.getPanda(), dest);
                        System.out.println(getNom() + " joue : " + action.toString());
                        action.appliquer(gameState, this);
                        return;
                    }
                    break;

                case 2: // JARDINIER
                    List<Position> deplacementsJ = plateau.getTrajetsLigneDroite(gameState.getJardinier().getPosition());
                    if (!deplacementsJ.isEmpty()) {
                        Position dest = deplacementsJ.get(random.nextInt(deplacementsJ.size()));

                        // MODIFICATION : Log
                        Action action = new DeplacerJardinier(gameState.getJardinier(), dest);
                        System.out.println(getNom() + " joue : " + action.toString());
                        action.appliquer(gameState, this);
                        return;
                    }
                    break;

                case 3: // PIOCHER OBJECTIF
                    int typeChoix = random.nextInt(2);
                    TypeObjectif type = (typeChoix == 0) ? TypeObjectif.JARDINIER : TypeObjectif.PANDA;

                    // MODIFICATION : Log
                    Action action = new PiocherObjectif(type);
                    System.out.println(getNom() + " joue : " + action.toString());
                    action.appliquer(gameState, this);
                    return;
            }
        }

        // Sécurité finale : fallback
        Action action = new PiocherObjectif(TypeObjectif.JARDINIER);
        System.out.println(getNom() + " (fallback) joue : " + action.toString());
        action.appliquer(gameState, this);
    }
}