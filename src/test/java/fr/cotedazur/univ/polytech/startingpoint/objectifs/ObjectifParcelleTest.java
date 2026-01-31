package fr.cotedazur.univ.polytech.startingpoint.objectifs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.BotRandom; // IMPORT IMPORTANT
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
    ObjectifParcelle objectifVert;

    @BeforeEach
    void setUp() {
        // CORRECTION 1 : On utilise une classe concrète (Bot est abstrait)
        bot = new BotRandom("Testeur");

        // 2. On crée le GameState avec la liste des joueurs
        gameState = new GameState(List.of(bot));

        // CORRECTION 2 : Constructeur à 3 arguments
        // Signature : (int points, int nombreRequis, Couleur couleur)
        // Exemple : 2 points pour 3 parcelles VERTES
        objectifVert = new ObjectifParcelle(2, 3, Couleur.VERT);
    }

    @Test
    void testObjectifNonValideAuDebut() {
        // Le plateau est vide au début, l'objectif ne doit pas être validé
        assertFalse(objectifVert.valider(gameState, bot),
                "L'objectif ne doit pas être valide sur un plateau vide");
    }

    @Test
    void testObjectifValide() {
        // 1. On récupère le vrai plateau du jeu
        Plateau plateauDuJeu = gameState.getPlateau();

        // 2. On pose 2 tuiles Vertes (pas assez pour l'objectif de 3)
        // Note : Il est préférable de donner la position à la parcelle aussi pour la cohérence
        Position pos1 = new Position(1, -1, 0);
        plateauDuJeu.placerParcelle(new Parcelle(pos1, Couleur.VERT), pos1);

        Position pos2 = new Position(1, 0, -1);
        plateauDuJeu.placerParcelle(new Parcelle(pos2, Couleur.VERT), pos2);

        // Vérification intermédiaire
        assertFalse(objectifVert.valider(gameState, bot),
                "L'objectif ne devrait pas être validé avec seulement 2 tuiles");

        // 3. On pose la 3ème tuile Verte
        Position pos3 = new Position(0, 1, -1);
        plateauDuJeu.placerParcelle(new Parcelle(pos3, Couleur.VERT), pos3);

        // 4. Validation finale
        assertTrue(objectifVert.valider(gameState, bot),
                "L'objectif devrait être validé avec 3 tuiles vertes");
    }

    @Test
    void testObjectifMelangeCouleurs() {
        Plateau plateauDuJeu = gameState.getPlateau();

        // On pose 2 Vertes et 1 Rose (Total 3 tuiles, mais pas 3 Vertes)
        plateauDuJeu.placerParcelle(new Parcelle(Couleur.VERT), new Position(1, -1, 0));
        plateauDuJeu.placerParcelle(new Parcelle(Couleur.VERT), new Position(1, 0, -1));
        plateauDuJeu.placerParcelle(new Parcelle(Couleur.ROSE), new Position(0, 1, -1));

        // Cela ne doit pas valider l'objectif VERT
        assertFalse(objectifVert.valider(gameState, bot),
                "Les tuiles d'une autre couleur ne doivent pas compter");
    }
}