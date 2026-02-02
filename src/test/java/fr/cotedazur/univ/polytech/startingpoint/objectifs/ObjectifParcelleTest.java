package fr.cotedazur.univ.polytech.startingpoint.objectifs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.BotRandom;
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
        bot = new BotRandom("Testeur");
        gameState = new GameState(List.of(bot));

        // CORRECTION : On passe une Liste de couleurs
        // Objectif : Avoir 3 parcelles VERTES pour 2 points
        objectifVert = new ObjectifParcelle(2, 3, List.of(Couleur.VERT));
    }

    @Test
    void testObjectifNonValideAuDebut() {
        assertFalse(objectifVert.valider(gameState, bot),
                "L'objectif ne doit pas être valide sur un plateau vide");
    }

    @Test
    void testObjectifValide() {
        Plateau plateauDuJeu = gameState.getPlateau();

        // On pose 2 tuiles Vertes (pas assez)
        Position pos1 = new Position(1, -1, 0);
        plateauDuJeu.placerParcelle(new Parcelle(pos1, Couleur.VERT), pos1);

        Position pos2 = new Position(1, 0, -1);
        plateauDuJeu.placerParcelle(new Parcelle(pos2, Couleur.VERT), pos2);

        assertFalse(objectifVert.valider(gameState, bot));

        // On pose la 3ème tuile Verte
        Position pos3 = new Position(0, 1, -1);
        plateauDuJeu.placerParcelle(new Parcelle(pos3, Couleur.VERT), pos3);

        // Maintenant on a 3 Vertes -> Validé
        assertTrue(objectifVert.valider(gameState, bot));
    }

    @Test
    void testObjectifMelangeCouleurs() {
        Plateau plateauDuJeu = gameState.getPlateau();

        // On pose 2 Vertes
        Position p1 = new Position(1, -1, 0);
        Position p2 = new Position(1, 0, -1);
        plateauDuJeu.placerParcelle(new Parcelle(p1, Couleur.VERT), p1);
        plateauDuJeu.placerParcelle(new Parcelle(p2, Couleur.VERT), p2);

        // Et 1 Rose
        Position p3 = new Position(0, 1, -1);
        plateauDuJeu.placerParcelle(new Parcelle(p3, Couleur.ROSE), p3);

        // Total 3 parcelles, mais seulement 2 Vertes. L'objectif en veut 3 Vertes.
        assertFalse(objectifVert.valider(gameState, bot),
                "Les tuiles d'une autre couleur ne doivent pas compter pour un objectif unicolore");
    }
}