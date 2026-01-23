package fr.cotedazur.univ.polytech.startingpoint.actions;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.Objectif;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.PiocheObjectif; // Votre classe de pioche
import fr.cotedazur.univ.polytech.startingpoint.objectifs.TypeObjectif;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.InventaireJoueur; // Add this import

import java.util.Optional;

class PiocherObjectifTest {

    @Test
    void testAppliquerPiocheEtDonneAuJoueur() {
        GameState gameState = mock(GameState.class);
        Bot bot = mock(Bot.class);
        PiocheObjectif piocheMock = mock(PiocheObjectif.class);
        Objectif objectifMock = mock(Objectif.class);
        InventaireJoueur inventaireMock = mock(InventaireJoueur.class); // <--- Create Inventory mock

        // Setup dependencies
        when(gameState.getPiocheJardinier()).thenReturn(piocheMock);
        when(piocheMock.piocher()).thenReturn(Optional.ofNullable(objectifMock));
        when(bot.getInventaire()).thenReturn(inventaireMock); // <--- CRITICAL FIX: Link Bot to Inventory

        PiocherObjectif action = new PiocherObjectif(TypeObjectif.JARDINIER);
        action.appliquer(gameState, bot);

        verify(piocheMock, times(1)).piocher();
        // Verify that the inventory received the added objective
        verify(inventaireMock, times(1)).ajouterObjectif(objectifMock);
    }
}