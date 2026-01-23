package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.*;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.Objectif;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.ObjectifJardinier;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.TypeObjectif;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.PiocheParcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

import java.util.List;
import java.util.Random;

public class BotJardinier extends Bot {

    public BotJardinier(String nom) {
        super(nom);
    }

    @Override
    public void jouer(GameState gameState) {
        // 1. ANALYSE : Choisir un objectif Jardinier prioritaire
        ObjectifJardinier objectifPrioritaire = choisirMeilleurObjectif();

        // Si aucun objectif Jardinier en main
        if (objectifPrioritaire == null) {
            // CORRECTION IMPORTANTE : On vérifie si la pioche n'est pas vide AVANT
            if (!gameState.getPiocheJardinier().estVide()) {
                Action action = new PiocherObjectif(TypeObjectif.JARDINIER);
                System.out.println(getNom() + " joue : " + action.toString());
                action.appliquer(gameState, this);
            } else {
                // Si la pioche est vide, on force une autre action pour ne pas bloquer
                System.out.println(getNom() + " ne peut plus piocher (Pioche vide). Mode Aléatoire activé.");
                jouerAleatoirement(gameState);
            }
            return;
        }

        Couleur couleurCible = objectifPrioritaire.getCouleur();

        // 2. STRATÉGIE : Essayer de faire pousser du bambou
        if (tenterDeplacerJardinier(gameState, couleurCible)) {
            return;
        }

        // 3. PLAN B : Poser une parcelle
        if (tenterPoserParcelle(gameState, couleurCible)) {
            return;
        }

        // 4. DERNIER RECOURS
        jouerAleatoirement(gameState);
    }

    private ObjectifJardinier choisirMeilleurObjectif() {
        for (Objectif obj : this.getInventaire().getObjectifs()) {
            if (obj instanceof ObjectifJardinier) {
                return (ObjectifJardinier) obj;
            }
        }
        return null;
    }

    private boolean tenterDeplacerJardinier(GameState gameState, Couleur couleur) {
        Plateau plateau = gameState.getPlateau();
        Position positionJardinier = gameState.getJardinier().getPosition();
        List<Position> destinationsPossibles = plateau.getTrajetsLigneDroite(positionJardinier);

        for (Position destination : destinationsPossibles) {
            Parcelle parcelle = plateau.getParcelle(destination);

            if (parcelle.getCouleur() == couleur
                    && parcelle.estIrriguee()
                    && parcelle.getNbSectionsSurParcelle() < 4) {

                // MODIFICATION : Log
                Action action = new DeplacerJardinier(gameState.getJardinier(), destination);
                System.out.println(getNom() + " joue : " + action.toString());
                action.appliquer(gameState, this);
                return true;
            }
        }
        return false;
    }

    private boolean tenterPoserParcelle(GameState gameState, Couleur couleurVisee) {
        PiocheParcelle pioche = gameState.getPioche();
        Plateau plateau = gameState.getPlateau();

        if (pioche.getSize() > 0 && !plateau.getEmplacementsDisponibles().isEmpty()) {
            Parcelle parcellePiochee = pioche.piocherParcelle();

            if (parcellePiochee != null) {
                List<Position> emplacements = plateau.getEmplacementsDisponibles();
                Position meilleurEmplacement = emplacements.get(0);

                for(Position pos : emplacements) {
                    if (pos.estAdjacent(Plateau.POSITION_ORIGINE)) {
                        meilleurEmplacement = pos;
                        break;
                    }
                }

                // MODIFICATION : Log
                Action action = new PoserParcelle(new Parcelle(meilleurEmplacement, parcellePiochee.getCouleur()), meilleurEmplacement);
                System.out.println(getNom() + " joue : " + action.toString());
                action.appliquer(gameState, this);
                return true;
            }
        }
        return false;
    }

    private void jouerAleatoirement(GameState gameState) {
        Random random = new Random();

        // Petit changement : on évite de piocher JARDINIER si on sait que c'est vide
        boolean piocheJardinierVide = gameState.getPiocheJardinier().estVide();

        if (random.nextBoolean() && !piocheJardinierVide) {
            Action action = new PiocherObjectif(TypeObjectif.JARDINIER);
            System.out.println(getNom() + " (Aléatoire) joue : " + action.toString());
            action.appliquer(gameState, this);
        } else {
            List<Position> dep = gameState.getPlateau().getTrajetsLigneDroite(gameState.getPanda().getPositionPanda());
            if (!dep.isEmpty()) {
                Action action = new DeplacerPanda(gameState.getPanda(), dep.get(0));
                System.out.println(getNom() + " (Aléatoire) joue : " + action.toString());
                action.appliquer(gameState, this);
            } else {
                // Vraiment bloqué ? On pioche un objectif Panda
                Action action = new PiocherObjectif(TypeObjectif.PANDA);
                System.out.println(getNom() + " (Aléatoire) joue : " + action.toString());
                action.appliquer(gameState, this);
            }
        }
    }
}