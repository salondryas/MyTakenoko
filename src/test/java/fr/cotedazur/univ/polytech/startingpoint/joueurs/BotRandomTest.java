package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.Action;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.PiocheObjectif;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Jardinier;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Panda;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.pioche.PiocheParcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

class BotRandomTest {

    BotRandom bot;
    @Mock GameState gameStateMock;
    @Mock Plateau plateauMock;
    @Mock PiocheParcelle piocheParcelleMock;
    @Mock PiocheObjectif piocheJardinierMock;
    @Mock PiocheObjectif piochePandaMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bot = new BotRandom("RandomTest");

        // Mock des getters principaux
        when(gameStateMock.getPlateau()).thenReturn(plateauMock);
        when(gameStateMock.getPiocheParcelle()).thenReturn(piocheParcelleMock);
        when(gameStateMock.getPiocheJardinier()).thenReturn(piocheJardinierMock);
        when(gameStateMock.getPiochePanda()).thenReturn(piochePandaMock);

        // CORRECTION 1 : On passe le plateauMock aux constructeurs des personnages
        when(gameStateMock.getJardinier()).thenReturn(new Jardinier(plateauMock));
        when(gameStateMock.getPanda()).thenReturn(new Panda(plateauMock));
    }

    @Test
    void testJouer_PiochesVides_NePlantePas() {
        // SCENARIO : Tout est vide.

        // 1. Pioche Parcelle (utilise estVide)
        when(piocheParcelleMock.estVide()).thenReturn(true);

        // CORRECTION 2 : Les pioches d'objectifs utilisent getTaille() dans le BotRandom corrigé
        when(piocheJardinierMock.getTaille()).thenReturn(0);
        when(piochePandaMock.getTaille()).thenReturn(0);

        // 3. Plateau plein
        when(plateauMock.getEmplacementsDisponibles()).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> bot.jouer(gameStateMock));
    }

    @Test
    void testJouer_TentePlusieursFois() {
        // Simulation : Pioche Parcelle disponible
        when(piocheParcelleMock.estVide()).thenReturn(false);
        when(piocheParcelleMock.piocher()).thenReturn(new Parcelle(Couleur.VERT));

        // Simulation : Emplacements disponibles
        when(plateauMock.getEmplacementsDisponibles()).thenReturn(List.of(new Position(1,0)));

        assertDoesNotThrow(() -> {
            List<Action> actions = bot.jouer(gameStateMock);
        });
    }
}