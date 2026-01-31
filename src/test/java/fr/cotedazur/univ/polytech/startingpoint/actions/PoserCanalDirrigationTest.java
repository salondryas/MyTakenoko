package fr.cotedazur.univ.polytech.startingpoint.actions;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.BotRandom;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations; // <--- Import nécessaire pour l'initialisation manuelle

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PoserCanalDirrigationTest {

    @Mock
    GameState gameStateMock;

    @Mock
    Plateau plateauMock;

    Bot bot;
    PoserCanalDirrigation action;
    Position pos1;
    Position pos2;

    @BeforeEach
    void setUp() {
        // INITIALISATION MANUELLE DES MOCKS (Remplace @ExtendWith)
        MockitoAnnotations.openMocks(this);

        // On utilise un vrai Bot pour tester son inventaire réellement
        bot = new BotRandom("BotTest");

        // On configure le mock GameState pour qu'il retourne notre mock Plateau
        // (lenient() permet d'éviter les warnings si le mock n'est pas utilisé dans certains tests)
        lenient().when(gameStateMock.getPlateau()).thenReturn(plateauMock);

        pos1 = new Position(0, 0);
        pos2 = new Position(0, 1);
        action = new PoserCanalDirrigation(pos1, pos2);
    }

    @Test
    void testAppliquerSucces() {
        // 1. ARRANGE : On donne 1 canal au bot
        bot.getInventaire().ajouterIrrigation();
        int stockAvant = bot.getInventaire().getNombreCanauxDisponibles(); // Stock = 1

        // On simule que le plateau accepte le canal (return true)
        when(plateauMock.placerCanal(pos1, pos2)).thenReturn(true);

        // 2. ACT
        action.appliquer(gameStateMock, bot);

        // 3. ASSERT
        // Vérifie que la méthode du plateau a bien été appelée
        verify(plateauMock, times(1)).placerCanal(pos1, pos2);

        // Vérifie que le canal a bien été consommé (Stock = 0)
        assertEquals(stockAvant - 1, bot.getInventaire().getNombreCanauxDisponibles());
    }

    @Test
    void testAppliquerEchecPasDeStock() {
        // 1. ARRANGE : Le bot a 0 canal par défaut
        int stockAvant = bot.getInventaire().getNombreCanauxDisponibles(); // Stock = 0

        // 2. ACT
        action.appliquer(gameStateMock, bot);

        // 3. ASSERT
        verify(plateauMock, never()).placerCanal(any(), any());
        assertEquals(stockAvant, bot.getInventaire().getNombreCanauxDisponibles());
    }

    @Test
    void testAppliquerEchecPlacementInvalideRefund() {
        // 1. ARRANGE
        bot.getInventaire().ajouterIrrigation();
        int stockAvant = bot.getInventaire().getNombreCanauxDisponibles(); // Stock = 1

        // On simule que le plateau REFUSE le canal (return false)
        when(plateauMock.placerCanal(pos1, pos2)).thenReturn(false);

        // 2. ACT
        action.appliquer(gameStateMock, bot);

        // 3. ASSERT
        verify(plateauMock, times(1)).placerCanal(pos1, pos2);

        // Vérifie que le stock est revenu à la normale (remboursé)
        assertEquals(stockAvant, bot.getInventaire().getNombreCanauxDisponibles(),
                "Le canal doit être remboursé si le placement échoue");
    }

    @Test
    void testToString() {
        assertEquals("pose un canal d'irrigation entre " + pos1 + " et " + pos2, action.toString());
    }
}