package fr.cotedazur.univ.polytech.startingpoint.actions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;

public class ObtenirCanalDirrigationTest {
    @Test
    @DisplayName("appliquer ajoute un canal à l'inventaire du joueur")
    void testAppliquer() {
        GameState gameState = new GameState();
        Bot joueur = new Bot("TestBot");
        ObtenirCanalDirrigation action = new ObtenirCanalDirrigation();

        assertEquals(0, joueur.getInventaire().getNombreCanauxDisponibles());

        action.appliquer(gameState, joueur);

        assertEquals(1, joueur.getInventaire().getNombreCanauxDisponibles());
    }

    @Test
    @DisplayName("Plusieurs appels successifs ajoutent plusieurs canaux")
    void testAppliquersPlusieurs() {
        GameState gameState = new GameState();
        Bot joueur = new Bot("TestBot");
        ObtenirCanalDirrigation action = new ObtenirCanalDirrigation();

        action.appliquer(gameState, joueur);
        action.appliquer(gameState, joueur);
        action.appliquer(gameState, joueur);

        assertEquals(3, joueur.getInventaire().getNombreCanauxDisponibles());
    }
}
