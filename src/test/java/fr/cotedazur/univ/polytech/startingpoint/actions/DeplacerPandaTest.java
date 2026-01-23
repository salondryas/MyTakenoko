package fr.cotedazur.univ.polytech.startingpoint.actions;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Panda;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

class DeplacerPandaTest {

    @Test
    void testAppliquerDeplaceLePanda() {
        GameState gameState = mock(GameState.class);
        Bot bot = mock(Bot.class);
        Panda pandaMock = mock(Panda.class);
        Plateau plateauMock = mock(Plateau.class); // <--- Create Plateau mock

        when(gameState.getPanda()).thenReturn(pandaMock);
        when(gameState.getPlateau()).thenReturn(plateauMock); // <--- CRITICAL FIX

        Position destination = new Position(1, 0, -1);
        DeplacerPanda action = new DeplacerPanda(pandaMock, destination);

        action.appliquer(gameState, bot);

        verify(pandaMock, times(1)).setPositionPanda(destination);
    }
}