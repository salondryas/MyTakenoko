package fr.cotedazur.univ.polytech.startingpoint;

import fr.cotedazur.univ.polytech.startingpoint.joueurs.BotRandom;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {

    @Test
    void determinerMeilleurJoueur_RetourneNullEnCasDEgalite() {
        BotRandom botA = new BotRandom("Bot A");
        BotRandom botB = new BotRandom("Bot B");

        botA.getInventaire().ajouterPoints(12);
        botB.getInventaire().ajouterPoints(12);

        GameState gameState = new GameState(List.of(botA, botB));

        assertNull(gameState.determinerMeilleurJoueur(),
                "Une égalité de score ne doit pas attribuer la victoire au premier bot");
    }

    @Test
    void determinerMeilleurJoueur_RetourneLeBotAvecMeilleurScore() {
        BotRandom botA = new BotRandom("Bot A");
        BotRandom botB = new BotRandom("Bot B");

        botA.getInventaire().ajouterPoints(11);
        botB.getInventaire().ajouterPoints(14);

        GameState gameState = new GameState(List.of(botA, botB));

        assertSame(botB, gameState.determinerMeilleurJoueur());
    }
}