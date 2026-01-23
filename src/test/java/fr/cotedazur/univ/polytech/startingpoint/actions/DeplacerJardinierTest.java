package fr.cotedazur.univ.polytech.startingpoint.actions;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Jardinier;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class DeplacerJardinierTest {

    @Test
    void testAppliquerDeplaceLeJardinier() {
        GameState gameState = mock(GameState.class);
        Bot bot = mock(Bot.class);
        Jardinier jardinierMock = mock(Jardinier.class);
        Plateau plateauMock = mock(Plateau.class); // <--- Create a Plateau mock

        when(gameState.getJardinier()).thenReturn(jardinierMock);
        when(gameState.getPlateau()).thenReturn(plateauMock); // <--- CRITICAL FIX: Link GameState to Plateau

        // You might also need to mock getParcelle if your code uses it immediately
        // For example: when(plateauMock.getParcelle(any())).thenReturn(new Parcelle(...));

        Position destination = new Position(0, 1, -1);
        DeplacerJardinier action = new DeplacerJardinier(jardinierMock, destination);

        action.appliquer(gameState, bot);

        verify(jardinierMock, times(1)).setPosition(destination);
    }
}