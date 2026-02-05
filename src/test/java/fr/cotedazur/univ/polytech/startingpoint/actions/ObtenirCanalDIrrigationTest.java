package fr.cotedazur.univ.polytech.startingpoint.actions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import fr.cotedazur.univ.polytech.startingpoint.joueurs.BotRandom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;

import java.util.ArrayList; // Import ajouté
import java.util.List;      // Import ajouté

public class ObtenirCanalDIrrigationTest {
    @Test
    @DisplayName("appliquer ajoute un canal à l'inventaire du joueur")
    void testAppliquer() {
        // 1. On crée le bot et la liste nécessaire au GameState
        Bot joueur = new BotRandom("TestBot");
        List<Bot> bots = new ArrayList<>();
        bots.add(joueur);

        // 2. On passe la liste au constructeur (Correction de l'erreur)
        GameState gameState = new GameState(bots);

        ObtenirCanalDirrigation action = new ObtenirCanalDirrigation();

        assertEquals(0, joueur.getInventaire().getNombreCanauxDisponibles());

        action.appliquer(gameState, joueur);

        assertEquals(1, joueur.getInventaire().getNombreCanauxDisponibles());
    }

    @Test
    @DisplayName("Plusieurs appels successifs ajoutent plusieurs canaux")
    void testAppliquersPlusieurs() {
        // 1. Configuration initiale
        Bot joueur = new BotRandom("TestBot");
        List<Bot> bots = new ArrayList<>();
        bots.add(joueur);

        // 2. Création du GameState avec arguments
        GameState gameState = new GameState(bots);

        ObtenirCanalDirrigation action = new ObtenirCanalDirrigation();

        action.appliquer(gameState, joueur);
        action.appliquer(gameState, joueur);
        action.appliquer(gameState, joueur);

        assertEquals(3, joueur.getInventaire().getNombreCanauxDisponibles());
    }
}