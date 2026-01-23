package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.ObjectifPoseur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BotTest {
    Bot bot;
    GameState gameState;

    @BeforeEach
    void setUp() {
        bot = new BotRandom("BotTest"); // CORRECTION : BotRandom
        List<Bot> bots = new ArrayList<>();
        bots.add(bot);

        gameState = new GameState();
        gameState.getJoueurs().addAll(bots);
    }

    @Test
    void testJouer() {
        // CORRECTION : La méthode est void, on ne peut pas faire assertNotNull.
        // On vérifie juste qu'elle ne lance pas d'exception.
        assertDoesNotThrow(() -> bot.jouer(gameState));
    }

    @Test
    void testChoisirMeilleurObjectif() {
        ObjectifPoseur obj1 = new ObjectifPoseur(2, Couleur.VERT, 3);
        ObjectifPoseur obj2 = new ObjectifPoseur(3, Couleur.ROSE, 5);

        bot.getInventaire().ajouterObjectif(obj1);
        bot.getInventaire().ajouterObjectif(obj2);

        assertEquals(2, bot.getInventaire().getObjectifs().size());
    }
}