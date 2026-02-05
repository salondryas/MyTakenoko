package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.Action;
import fr.cotedazur.univ.polytech.startingpoint.actions.PiocherObjectif;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.TypeObjectif;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ExpertStrategieTest {

    BotExpert bot;
    ExpertStrategie strategie;
    GameState gameState;

    @BeforeEach
    void setUp() {
        bot = new BotExpert("Strategiste");
        strategie = new ExpertStrategie(bot);
        gameState = new GameState(List.of(bot));
    }

    @Test
    void testChoisirMeilleureAction_DebutPartie_PiocheObjectif() {
        // Au début, inventaire vide -> la stratégie doit favoriser la pioche (score +10 dans notre heuristique)
        Action action = strategie.choisirMeilleureAction(gameState, new HashSet<>());

        assertNotNull(action, "La stratégie doit toujours retourner une action si possible");
        assertTrue(action instanceof PiocherObjectif, "Avec un inventaire vide, l'expert devrait piocher un objectif");
    }

    @Test
    void testEvaluerAction_ScorePositif() {
        // Test basique pour vérifier que l'heuristique renvoie bien un score
        Action action = new PiocherObjectif(TypeObjectif.PANDA);
        double score = strategie.evaluerAction(action, gameState);

        // Comme l'inventaire est vide, le score devrait être élevé
        assertTrue(score > 0, "L'évaluation doit retourner un score positif");
    }
}