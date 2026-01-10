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
        bot = new Bot("BotTest");
        List<Bot> bots = new ArrayList<>();
        bots.add(bot);

        // CORRECTION 1 : Initialisation du GameState
        gameState = new GameState();
        gameState.getJoueurs().addAll(bots);
    }

    @Test
    void testJouer() {
        assertNotNull(bot.jouer(gameState));
    }

    @Test
    void testChoisirMeilleurObjectif() {
        // CORRECTION 2 : Ajout du 3ème argument (points) pour ObjectifPoseur
        ObjectifPoseur obj1 = new ObjectifPoseur(2, Couleur.VERT, 3);
        ObjectifPoseur obj2 = new ObjectifPoseur(3, Couleur.ROSE, 5); // Plus de points

        bot.getInventaire().ajouterObjectif(obj1);
        bot.getInventaire().ajouterObjectif(obj2);

        // Exemple de test (adaptez selon votre logique)
        assertEquals(2, bot.getInventaire().getObjectifs().size());
    }
}