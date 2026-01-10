package fr.cotedazur.univ.polytech.startingpoint.objectifs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ObjectifPoseurTest {
    GameState gameState;
    Bot bot;

    @BeforeEach
    void setUp() {
        // CORRECTION 1 : GameState
        gameState = new GameState();
        bot = new Bot("TestBot");
        gameState.getJoueurs().add(bot);
    }

    @Test
    void validerObjectifPoseur() {
        // CORRECTION 2 : Ajout des points (3ème argument)
        ObjectifPoseur objectif = new ObjectifPoseur(2, Couleur.VERT, 4);

        Plateau plateau = gameState.getPlateau();
        plateau.placerParcelle(new Parcelle(new Position(0,1), Couleur.VERT), new Position(0,1));

        // Pas assez de parcelles -> Faux
        assertFalse(objectif.valider(gameState, bot));

        plateau.placerParcelle(new Parcelle(new Position(1,0), Couleur.VERT), new Position(1,0));

        // 2 parcelles vertes -> Vrai
        assertTrue(objectif.valider(gameState, bot));
    }
}