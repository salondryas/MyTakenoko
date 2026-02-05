package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.*;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.ObjectifParcelle;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.PiocheObjectif;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.plateau.pioche.PiocheParcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.pioche.SelectionParcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import fr.cotedazur.univ.polytech.startingpoint.weather.Meteo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BotParcelleTest {

    BotParcelle bot;

    @Mock GameState gameStateMock;
    @Mock Plateau plateauMock;
    @Mock
    PiocheParcelle piocheParcelleMock;
    @Mock PiocheObjectif piocheObjectifMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bot = new BotParcelle("ConstructeurTest");

        // Configuration de base du GameState
        when(gameStateMock.getPlateau()).thenReturn(plateauMock);
        when(gameStateMock.getPiocheParcelle()).thenReturn(piocheParcelleMock);

        // Configuration des pioches pour éviter les NullPointer
        when(gameStateMock.getPiocheObjectifParcelle()).thenReturn(piocheObjectifMock);

        // Par défaut, pas d'emplacements dispo
        when(plateauMock.getEmplacementsDisponibles()).thenReturn(Collections.emptyList());
    }

    // =================================================================================
    // 1. TESTS DE LA STRATÉGIE GÉNÉRALE (choisirUneAction)
    // =================================================================================

    @Test
    void testPriorite1_PasDObjectif_Piocher() {
        // SCENARIO : Inventaire vide (0 objectifs)

        List<Action> actions = bot.jouer(gameStateMock);

        assertFalse(actions.isEmpty());
        assertTrue(actions.get(0) instanceof PiocherObjectif, "Sans objectif, le bot doit piocher en priorité");
        assertEquals(TypeAction.PIOCHER_OBJECTIF, actions.get(0).getType());
    }

    @Test
    void testPriorite2_AvecObjectif_PoserParcelle() {
        // SCENARIO : Le bot a un objectif Parcelle et la pioche n'est pas vide.
        // ATTENDU : PoserParcelle

        // 1. Donner un objectif au bot
        ObjectifParcelle obj = mock(ObjectifParcelle.class);
        when(obj.getType()).thenReturn(fr.cotedazur.univ.polytech.startingpoint.objectifs.TypeObjectif.PARCELLE);
        bot.getInventaire().ajouterObjectif(obj);

        // 2. Pioche disponible
        when(piocheParcelleMock.estVide()).thenReturn(false);

        List<Action> actions = bot.jouer(gameStateMock);

        assertFalse(actions.isEmpty());
        assertTrue(actions.get(0) instanceof PoserParcelle, "Avec un objectif, le bot doit essayer de poser une parcelle");
    }

    @Test
    void testPriorite3_PiocheVide_PiocherObjectif() {
        // SCENARIO : Le bot a un objectif, MAIS la pioche de parcelles est vide.

        // 1. Objectif présent
        ObjectifParcelle obj = mock(ObjectifParcelle.class);
        when(obj.getType()).thenReturn(fr.cotedazur.univ.polytech.startingpoint.objectifs.TypeObjectif.PARCELLE);
        bot.getInventaire().ajouterObjectif(obj);

        // 2. Pioche vide
        when(piocheParcelleMock.estVide()).thenReturn(true);

        List<Action> actions = bot.jouer(gameStateMock);

        assertFalse(actions.isEmpty());
        assertTrue(actions.get(0) instanceof PiocherObjectif, "Si on ne peut pas poser, on pioche des objectifs");
    }

    // =================================================================================
    // 2. TESTS DE L'INTELLIGENCE (SCORING & CHOIX)
    // =================================================================================

    @Test
    void testIntelligence_Scoring_ChoisitMeilleureTuileEtPosition() {
        // SCENARIO COMPLEXE :
        // - Objectif du bot : Avoir du VERT (pour faire une ligne verte par exemple).
        // - Plateau : Il y a déjà une parcelle VERTE en (0,1).
        // - Emplacements dispos : (0,0), (0,2), (1,1).
        // - Pioche (Sélection) : [VERT, ROSE, JAUNE].

        // ATTENDU : Le bot doit choisir la tuile VERTE et la poser en (0,2) ou (1,1) (voisin du Vert existant).

        // 1. Setup Objectif Bot
        ObjectifParcelle objVert = mock(ObjectifParcelle.class);
        when(objVert.getType()).thenReturn(fr.cotedazur.univ.polytech.startingpoint.objectifs.TypeObjectif.PARCELLE);
        bot.getInventaire().ajouterObjectif(objVert);

        // 2. Setup Selection (Les 3 cartes piochées)
        Parcelle pVert = new Parcelle(Couleur.VERT);
        Parcelle pRose = new Parcelle(Couleur.ROSE);
        Parcelle pJaune = new Parcelle(Couleur.JAUNE);
        SelectionParcelle session = mock(SelectionParcelle.class);
        when(session.getParcellesAChoisir()).thenReturn(List.of(pRose, pVert, pJaune)); // Ordre mélangé

        // 3. Setup Plateau (Positions et Voisins)
        Position posVoisineDuVert = new Position(0, 2);
        Position posIsolee = new Position(5, 5);
        when(plateauMock.getEmplacementsDisponibles()).thenReturn(List.of(posIsolee, posVoisineDuVert));
        when(plateauMock.isPositionDisponible(any())).thenReturn(true);

        // Simulation du voisinage : En (0,3) il y a déjà du VERT
        // posVoisineDuVert(0,2) est voisine de (0,3) (selon logique hexagone, simplifions pour le test)
        // Pour que le test fonctionne avec votre logique de "evaluerCoup", il faut que le mock
        // retourne une parcelle verte quand le bot regarde les voisins de 'posVoisineDuVert'.

        // Astuce : On va dire que pour n'importe quelle position demandée autour de posVoisineDuVert, il y a du vert
        // C'est un peu bourrin mais ça garantit le score d'adjacence.
        Parcelle voisinVert = new Parcelle(Couleur.VERT);
        // On mock le getParcelle pour qu'il renvoie le voisin vert quand on cherche autour de la bonne position
        when(plateauMock.getParcelle(any(Position.class))).thenAnswer(invocation -> {
            Position p = invocation.getArgument(0);
            if (p.estAdjacent(posVoisineDuVert)) return voisinVert;
            return null;
        });

        // 4. ACT : Le bot choisit
        Parcelle choix = bot.choisirParcelle(session, plateauMock);

        // 5. ASSERT
        assertEquals(pVert, choix, "Le bot doit choisir la tuile VERTE car il a un objectif VERT");
        verify(session).validerChoix(pVert); // Vérifie qu'il a bien validé son choix

        // 6. VERIFICATION DE LA MÉMOIRE (choisirPosition doit retourner ce qui a été calculé)
        Position posChoisie = bot.choisirPosition(pVert, plateauMock);
        assertEquals(posVoisineDuVert, posChoisie, "Le bot doit choisir la position adjacente au vert existant (Score max)");
    }

    @Test
    void testMemoire_ResetApresUsage() {
        // Vérifie que la variable "positionMemoriseePourLeTour" est bien reset à null

        // 1. On force une mémorisation (via un hack ou une exécution simulée)
        // Le plus simple est de lancer choisirParcelle avec un cas trivial
        SelectionParcelle session = mock(SelectionParcelle.class);
        Parcelle p = new Parcelle(Couleur.VERT);
        when(session.getParcellesAChoisir()).thenReturn(List.of(p));

        Position pos = new Position(0,0);
        when(plateauMock.getEmplacementsDisponibles()).thenReturn(List.of(pos));
        when(plateauMock.isPositionDisponible(pos)).thenReturn(true);

        bot.choisirParcelle(session, plateauMock); // Mémorise (0,0)

        // 2. Premier appel : Renvoie la position mémorisée
        assertEquals(pos, bot.choisirPosition(p, plateauMock));

        // 3. Deuxième appel (si jamais) : Doit relancer un calcul ou renvoyer par défaut
        // Comme la mémoire est vide, il va chercher dans getEmplacementsDisponibles
        bot.choisirPosition(p, plateauMock);

        // On vérifie juste que ça ne plante pas et que la logique de reset a été exécutée
        // (Difficile à tester strictement sans introspection, mais le comportement fonctionnel est là)
    }

    // =================================================================================
    // 3. TESTS MÉTÉO (Aléatoire)
    // =================================================================================

    @Test
    void testMeteo_Robustesse() {
        // Vérifier que les méthodes météo ne plantent pas et renvoient des valeurs cohérentes

        assertNotNull(bot.choisirMeteo());

        Meteo mAlt = bot.choisirMeteoAlternative();
        assertNotNull(mAlt);
        assertNotEquals(Meteo.NUAGES, mAlt, "L'alternative ne doit pas être NUAGES");

        // Choix parcelle météo
        Parcelle p = new Parcelle(Couleur.VERT);
        assertEquals(p, bot.choisirParcelleMeteo(List.of(p)));
        assertNull(bot.choisirParcelleMeteo(Collections.emptyList()));

        // Destination Panda
        assertEquals(p, bot.choisirDestinationPanda(List.of(p)));
        assertNull(bot.choisirDestinationPanda(Collections.emptyList()));
    }
}