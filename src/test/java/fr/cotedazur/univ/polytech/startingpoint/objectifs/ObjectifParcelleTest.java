package fr.cotedazur.univ.polytech.startingpoint.objectifs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.BotRandom;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.parcelle.CarteParcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ObjectifParcelleTest {
    Bot bot;
    GameState gameState;
    ObjectifParcelle objectifLigneVerte;

    @BeforeEach
    void setUp() {
        bot = new BotRandom("Testeur");
        gameState = new GameState(List.of(bot));

        // CORRECTION MAJEURE : On utilise l'Enum CarteParcelle
        // LIGNE_VERTE : Motif de 3 parcelles vertes alignées (2 points)
        objectifLigneVerte = new ObjectifParcelle(CarteParcelle.LIGNE_VERTE);
    }

    @Test
    void testObjectifNonValideAuDebut() {
        assertFalse(objectifLigneVerte.valider(gameState, bot),
                "L'objectif ne doit pas être valide sur un plateau vide");
    }

    @Test
    void testObjectifValide() {
        Plateau plateauDuJeu = gameState.getPlateau();

        // Construction d'une Ligne Verte : (1,0) -> (2,0) -> (3,0)
        // Note : (0,0) est l'Etang, donc on commence à côté (1,0)

        Position p1 = new Position(1, 0);
        plateauDuJeu.placerParcelle(new Parcelle(p1, Couleur.VERT), p1);

        Position p2 = new Position(2, 0); // Adjacent à p1
        plateauDuJeu.placerParcelle(new Parcelle(p2, Couleur.VERT), p2);

        // Pas assez de tuiles (2 au lieu de 3 pour une ligne)
        assertFalse(objectifLigneVerte.valider(gameState, bot),
                "L'objectif ne doit pas être validé avec seulement 2 parcelles");

        // On pose la 3ème tuile Verte alignée
        Position p3 = new Position(3, 0); // Adjacent à p2, complète la ligne
        plateauDuJeu.placerParcelle(new Parcelle(p3, Couleur.VERT), p3);

        // Maintenant on a le motif exact -> Validé
        assertTrue(objectifLigneVerte.valider(gameState, bot),
                "L'objectif doit être validé avec 3 parcelles vertes alignées");
    }

    @Test
    void testObjectifNonValideMauvaiseCouleur() {
        Plateau plateauDuJeu = gameState.getPlateau();

        // On construit une ligne géométrique, mais avec une mauvaise couleur au milieu
        // (1,0)=VERT -> (2,0)=ROSE -> (3,0)=VERT

        Position p1 = new Position(1, 0);
        plateauDuJeu.placerParcelle(new Parcelle(p1, Couleur.VERT), p1);

        Position p2 = new Position(2, 0);
        plateauDuJeu.placerParcelle(new Parcelle(p2, Couleur.ROSE), p2); // Erreur de couleur

        Position p3 = new Position(3, 0);
        plateauDuJeu.placerParcelle(new Parcelle(p3, Couleur.VERT), p3);

        assertFalse(objectifLigneVerte.valider(gameState, bot),
                "Le motif géométrique est bon mais les couleurs ne correspondent pas");
    }

    @Test
    void testObjectifNonValideMauvaiseForme() {
        Plateau plateauDuJeu = gameState.getPlateau();

        // On pose 3 Vertes, mais en TRIANGLE au lieu de LIGNE
        // (1,0), (2,0) et (1,1) forment un triangle

        Position p1 = new Position(1, 0);
        plateauDuJeu.placerParcelle(new Parcelle(p1, Couleur.VERT), p1);

        Position p2 = new Position(2, 0);
        plateauDuJeu.placerParcelle(new Parcelle(p2, Couleur.VERT), p2);

        Position p3 = new Position(1, 1); // Casse l'alignement
        plateauDuJeu.placerParcelle(new Parcelle(p3, Couleur.VERT), p3);

        assertFalse(objectifLigneVerte.valider(gameState, bot),
                "Un motif Triangle ne doit pas valider un objectif Ligne");
    }
}