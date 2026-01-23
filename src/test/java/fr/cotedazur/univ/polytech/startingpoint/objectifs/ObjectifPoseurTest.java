package fr.cotedazur.univ.polytech.startingpoint.objectifs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.BotRandom; // IMPORT
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ObjectifPoseurTest {
    GameState gameState;
    Bot bot;

    @BeforeEach
    void setUp() {
        gameState = new GameState();
        bot = new BotRandom("TestBot"); // CORRECTION
        gameState.getJoueurs().add(bot);
    }

    // ... le reste du fichier ne change pas ...
    @Test
    void validerObjectifPoseur() {
        ObjectifPoseur objectif = new ObjectifPoseur(2, Couleur.VERT, 4);
        Plateau plateau = gameState.getPlateau();
        plateau.placerParcelle(new Parcelle(new Position(0,1), Couleur.VERT), new Position(0,1));

        assertFalse(objectif.valider(gameState, bot));

        plateau.placerParcelle(new Parcelle(new Position(1,0), Couleur.VERT), new Position(1,0));

        assertTrue(objectif.valider(gameState, bot));
    }
}