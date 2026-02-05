package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.Action;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EquipeStrategieTest {

    BotEquipe botChef;
    EquipeStrategie strategie;
    GameState gameState;

    @BeforeEach
    void setUp() {
        botChef = new BotEquipe("Chef");
        strategie = new EquipeStrategie(botChef);
        gameState = new GameState(List.of(botChef));
    }

    @Test
    void testArbitrerPropositions_RetourneActionValide() {
        // On teste que la stratégie renvoie bien quelque chose (même si c'est null faute d'action possible au tour 0)
        // ou une pioche si les inventaires sont vides.
        Action action = strategie.arbitrerLesPropositions(gameState, new HashSet<>());

        // Au début, les sous-bots devraient proposer de piocher
        assertNotNull(action, "La stratégie d'équipe doit aboutir à une décision");
    }

    @Test
    void testSynchronisationInventaires() {
        // On ajoute des points au chef
        botChef.getInventaire().ajouterPoints(10);

        // On lance l'arbitrage qui déclenche la synchro
        strategie.synchroniserInventaires();

        // Note : Comme les sous-bots sont privés dans Strategie, on ne peut pas vérifier directement leur état
        // sans modifier la visibilité ou utiliser la réflexion.
        // Mais on vérifie que la méthode ne plante pas.
        assertDoesNotThrow(() -> strategie.synchroniserInventaires());
    }
}