package fr.cotedazur.univ.polytech.startingpoint.actions;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PoserParcelleTest {

    // On crée des "doublures" (Mocks) pour simuler le jeu
    @Mock private GameState gameState;
    @Mock private Plateau plateau;
    @Mock private Bot bot;

    @BeforeEach
    void setUp() {
        // Initialisation des mocks avant chaque test
        MockitoAnnotations.openMocks(this);
        // Quand on demande le plateau au gameState, il renvoie notre faux plateau
        when(gameState.getPlateau()).thenReturn(plateau);
    }

    @Test
    void testAppliquerPoseBienLaParcelle() {
        // 1. Préparer les données
        Position position = new Position(1, 0, -1);
        Parcelle parcelle = new Parcelle(position, Couleur.VERT);
        PoserParcelle action = new PoserParcelle(parcelle, position);

        // 2. Exécuter l'action
        action.appliquer(gameState, bot);

        // 3. Vérifier (C'est là que la magie Mockito opère)
        // On vérifie que la méthode 'placerParcelle' a été appelée 1 fois sur le plateau
        // avec exactement cette parcelle et cette position.
        verify(plateau, times(1)).placerParcelle(parcelle, position);
    }

    @Test
    void testToString() {
        Position position = new Position(0, 1, -1);
        Parcelle parcelle = new Parcelle(position, Couleur.ROSE);
        PoserParcelle action = new PoserParcelle(parcelle, position);

        // On vérifie juste que l'affichage est correct (pour les logs)
        assertEquals("pose une parcelle ROSE en (0, 1, -1)", action.toString());
    }
}