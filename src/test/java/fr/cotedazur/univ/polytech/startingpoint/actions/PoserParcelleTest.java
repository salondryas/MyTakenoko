package fr.cotedazur.univ.polytech.startingpoint.actions;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.elements.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.elements.pioche.PiocheParcelle;
import fr.cotedazur.univ.polytech.startingpoint.elements.pioche.SelectionParcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PoserParcelleTest {

    // On crée des "doublures" (Mocks) pour simuler le jeu
    @Mock private GameState gameState;
    @Mock private Plateau plateau;
    @Mock private Bot bot;
    @Mock private PiocheParcelle pioche;

    private PoserParcelle action;

    @BeforeEach
    void setUp() {
        // Initialisation des mocks avant chaque test
        MockitoAnnotations.openMocks(this);
        // Quand on demande le plateau au gameState, il renvoie notre faux plateau
        when(gameState.getPlateau()).thenReturn(plateau);
        when(gameState.getPiocheParcelle()).thenReturn(pioche);

        action = new PoserParcelle();
    }

    @Test
    void testAppliquerPoseBienLaParcelle() {
        // 1. Préparer les données
        Position position = new Position(1, 0, -1);
        Parcelle parcelle = new Parcelle(position, Couleur.VERT);
        PoserParcelle action = new PoserParcelle();

        // 2. Exécuter l'action
        action.poserParcelle(plateau, position, parcelle);

        // 3. Vérifier (C'est là que la magie Mockito opère)
        // On vérifie que la méthode 'placerParcelle' a été appelée 1 fois sur le plateau
        // avec exactement cette parcelle et cette position.
        verify(plateau, times(1)).placerParcelle(parcelle, position);
    }

    @Test
    void testToString() {
        Position position = new Position(0, 1, -1);
        Parcelle parcelle = new Parcelle(position, Couleur.ROSE);
        PoserParcelle action = new PoserParcelle();
        action.poserParcelle(plateau, position, parcelle);

        // On vérifie juste que l'affichage est correct (pour les logs)
        assertEquals("pose une parcelle ROSE en (0, 1, -1)", action.toString());
    }

    @Test
    void testPiocherTroisCartesNominal() {
        // Arrange : La pioche n'est pas vide
        when(pioche.estVide()).thenReturn(false);
        when(pioche.piocherParcelle()).thenReturn(new Parcelle(Couleur.VERT));

        List<Parcelle> destination = new ArrayList<>();

        // Act
        action.piocherTroisCartes(destination, pioche);

        // Assert
        assertEquals(3, destination.size(), "Doit piocher 3 cartes si dispo");
        verify(pioche, times(3)).piocherParcelle();
    }

    @Test
    void testPiocherTroisCartesPiochePresqueVide() {
        // Arrange : La pioche a 1 carte, puis devient vide
        when(pioche.estVide()).thenReturn(false, true); // 1ere fois false, ensuite true
        when(pioche.piocherParcelle()).thenReturn(new Parcelle(Couleur.VERT));

        List<Parcelle> destination = new ArrayList<>();

        // Act
        action.piocherTroisCartes(destination, pioche);

        // Assert
        assertEquals(1, destination.size(), "Ne doit piocher qu'une seule carte si la pioche se vide");
    }

    @Test
    void testAppliquerFluxComplet() {
        // --- ARRANGE (Préparation du scénario) ---

        // 1. Préparer 3 parcelles dans la pioche
        Parcelle p1 = new Parcelle(Couleur.VERT);
        Parcelle p2 = new Parcelle(Couleur.ROSE);
        Parcelle p3 = new Parcelle(Couleur.JAUNE);

        // La pioche renvoie successivement ces 3 cartes
        when(pioche.estVide()).thenReturn(false);
        when(pioche.piocherParcelle()).thenReturn(p1, p2, p3);

        // 2. Préparer le choix du Bot
        // Quand le bot devra choisir, on simule qu'il choisit P2 (ROSE) et une position (0,1)
        Position posChoisie = new Position(0, 1);

        // Captureur pour vérifier ce que l'action envoie au bot
        ArgumentCaptor<SelectionParcelle> sessionCaptor = ArgumentCaptor.forClass(SelectionParcelle.class);

        // Simulation de la réponse du bot : il valide P2 via la session qu'on lui donne
        when(bot.choisirParcelle(any(SelectionParcelle.class), eq(plateau))).thenAnswer(invocation -> {
            SelectionParcelle session = invocation.getArgument(0);
            return session.validerChoix(p2); // Le bot choisit P2
        });

        when(bot.choisirPosition(eq(p2), eq(plateau))).thenReturn(posChoisie);


        // --- ACT (Action) ---
        action.appliquer(gameState, bot);


        // --- ASSERT (Vérifications) ---

        // 1. Vérifier qu'on a bien pioché 3 fois
        verify(pioche, times(3)).piocherParcelle();

        // 2. Vérifier que le bot a reçu une session contenant les 3 cartes
        verify(bot).choisirParcelle(sessionCaptor.capture(), eq(plateau));
        List<Parcelle> optionsProposees = sessionCaptor.getValue().getParcellesAChoisir(); // Note : après validation, la liste est vidée de la carte choisie dans l'objet réel, attention

        // 3. Vérifier que la carte choisie a été placée sur le plateau
        verify(plateau).placerParcelle(p2, posChoisie);

        // 4. Vérifier que les cartes NON choisies (P1 et P3) sont retournées sous la pioche
        // (Ceci est géré par SelectionParcelle.validerChoix appelé par le mock du bot)
        verify(pioche).remettreEnDessous(p1);
        verify(pioche).remettreEnDessous(p3);
        verify(pioche, never()).remettreEnDessous(p2); // P2 est posée, pas remise

        // 5. Vérifier le toString (optionnel mais utile)
        assertEquals("pose une parcelle ROSE en "+posChoisie, action.toString());
    }
}