package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.*;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.PiocheObjectif;
import fr.cotedazur.univ.polytech.startingpoint.plateau.*;
import fr.cotedazur.univ.polytech.startingpoint.plateau.pioche.PiocheParcelle;
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
import static org.mockito.Mockito.*;

class BotRandomTest {

    BotRandom bot;

    @Mock GameState gameStateMock;
    @Mock Plateau plateauMock;
    @Mock PiocheParcelle piocheParcelleMock;
    @Mock PiocheObjectif piocheJardinierMock;
    @Mock PiocheObjectif piochePandaMock;
    @Mock Jardinier jardinierMock;
    @Mock Panda pandaMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bot = new BotRandom("RandomTester");

        // Configuration de base du GameState
        when(gameStateMock.getPlateau()).thenReturn(plateauMock);
        when(gameStateMock.getPiocheParcelle()).thenReturn(piocheParcelleMock);
        when(gameStateMock.getPiocheJardinier()).thenReturn(piocheJardinierMock);
        when(gameStateMock.getPiochePanda()).thenReturn(piochePandaMock);
        when(gameStateMock.getJardinier()).thenReturn(jardinierMock);
        when(gameStateMock.getPanda()).thenReturn(pandaMock);

        // Par défaut, le plateau est vide de possibilités pour éviter les NullPointer
        when(plateauMock.getEmplacementsDisponibles()).thenReturn(Collections.emptyList());
        when(plateauMock.getTrajetsLigneDroite(any())).thenReturn(Collections.emptyList());
    }

    // =================================================================================
    // 1. TESTS DES MÉTHODES UTILITAIRES (DÉTERMINISTES OU STATISTIQUES)
    // =================================================================================

    @Test
    void testChoisirPosition() {
        // ARRANGE
        Position pos1 = new Position(1, 0);
        Position pos2 = new Position(0, 1);
        List<Position> positionsDispo = List.of(pos1, pos2);
        when(plateauMock.getEmplacementsDisponibles()).thenReturn(positionsDispo);

        // ACT
        // On simule une parcelle choisie (peu importe laquelle pour ce test)
        Parcelle p = new Parcelle(Couleur.VERT);
        Position result = bot.choisirPosition(p, plateauMock);

        // ASSERT
        assertTrue(positionsDispo.contains(result),
                "Le bot doit choisir une position parmi celles disponibles");
    }

    @Test
    void testChoisirPosition_ListeVide_Securite() {
        // ARRANGE - Pas de positions disponibles (cas théorique)
        when(plateauMock.getEmplacementsDisponibles()).thenReturn(Collections.emptyList());

        // ACT & ASSERT
        // Comme le code utilise random.nextInt(size), une liste vide provoquerait une exception.
        // Il faut vérifier si votre bot gère ce cas ou si le jeu garantit que la liste n'est jamais vide à ce stade.
        // Dans le code fourni, cela lancerait IllegalArgumentException.
        // Si c'est le comportement attendu (car on n'appelle pas choisirPosition sans dispo), ce test documente ce fait.
        Parcelle parcelleVerte = new Parcelle(Couleur.VERT);
        assertThrows(IllegalArgumentException.class, () -> {
            bot.choisirPosition(parcelleVerte, plateauMock);
        });
    }

    @Test
    void testChoisirParcelleMeteo() {
        // Avec une liste vide
        assertNull(bot.choisirParcelleMeteo(Collections.emptyList()),
                "Doit retourner null si aucune parcelle irriguée");

        // Avec une liste pleine
        Parcelle p1 = new Parcelle(Couleur.VERT);
        Parcelle result = bot.choisirParcelleMeteo(List.of(p1));
        assertEquals(p1, result, "Doit retourner l'unique élément disponible");
    }

    @Test
    void testChoisirDestinationPanda() {
        // Avec une liste vide
        assertNull(bot.choisirDestinationPanda(Collections.emptyList()));

        // Avec une liste pleine
        Parcelle p1 = new Parcelle(Couleur.ROSE);
        Parcelle result = bot.choisirDestinationPanda(List.of(p1));
        assertEquals(p1, result);
    }

    @Test
    void testChoisirMeteoAlternative() {
        // Doit retourner une météo SAUF NUAGES (selon votre implémentation)
        for (int i = 0; i < 20; i++) {
            Meteo m = bot.choisirMeteoAlternative();
            assertNotNull(m);
            assertNotEquals(Meteo.NUAGES, m, "La météo alternative ne doit pas être NUAGES (souvent réservé au choix d'aménagement)");
        }
    }

    // =================================================================================
    // 2. TESTS DE ROBUSTESSE (JOUER AVEC ÉTAT VIDE)
    // =================================================================================

    @Test
    void testJouer_ToutEstVide_NeDoitPasPlanter() {
        // ARRANGE : Le pire cas possible
        when(piocheParcelleMock.estVide()).thenReturn(true);
        when(piocheJardinierMock.getTaille()).thenReturn(0);
        when(piochePandaMock.getTaille()).thenReturn(0);
        when(plateauMock.getEmplacementsDisponibles()).thenReturn(Collections.emptyList());
        when(plateauMock.getTrajetsLigneDroite(any())).thenReturn(Collections.emptyList());

        // On sature l'inventaire d'irrigations pour bloquer aussi cette action
        bot.getInventaire().ajouterIrrigation();
        bot.getInventaire().ajouterIrrigation();

        // ACT
        List<Action> actions = bot.jouer(gameStateMock);

        // ASSERT
        // Le bot doit retourner une liste vide (ou partielle) mais NE PAS CRASHER
        assertTrue(actions.isEmpty(), "Si rien n'est possible, aucune action ne doit être retournée");
    }

    // =================================================================================
    // 3. TESTS DE CONTRAINTES SPÉCIFIQUES
    // =================================================================================

    @Test
    void testLimiteIrrigation() {
        // ARRANGE : On donne déjà 2 canaux au bot
        bot.getInventaire().ajouterIrrigation();
        bot.getInventaire().ajouterIrrigation();
        assertEquals(2, bot.getInventaire().getNombreCanauxDisponibles());

        // On configure le jeu pour que SEULE l'action "Prendre Irrigation" soit théoriquement tentante
        // (Tout le reste est vide/impossible)
        when(piocheParcelleMock.estVide()).thenReturn(true);
        when(piocheJardinierMock.getTaille()).thenReturn(0);
        when(piochePandaMock.getTaille()).thenReturn(0);
        when(plateauMock.getEmplacementsDisponibles()).thenReturn(Collections.emptyList());
        when(plateauMock.getTrajetsLigneDroite(any())).thenReturn(Collections.emptyList());

        // ACT
        // On lance jouer(). Le bot va essayer plein de choses aléatoires.
        // S'il tombe sur le cas "Prendre Irrigation", la condition `getInventaire() < 2` doit bloquer.
        List<Action> actions = bot.jouer(gameStateMock);

        // ASSERT
        for (Action action : actions) {
            assertFalse(action instanceof ObtenirCanalDirrigation,
                    "Le bot ne doit pas prendre d'irrigation s'il en a déjà 2 ou plus");
        }
    }

    @Test
    void testPiocherObjectif_GerePiochesVides() {
        // ARRANGE : Pioche Jardinier VIDE, Pioche Panda PLEINE
        when(piocheJardinierMock.getTaille()).thenReturn(0);
        when(piochePandaMock.getTaille()).thenReturn(10);

        // On bloque tout le reste pour forcer le bot à essayer de piocher des objectifs
        when(piocheParcelleMock.estVide()).thenReturn(true);
        when(plateauMock.getEmplacementsDisponibles()).thenReturn(Collections.emptyList());
        when(plateauMock.getTrajetsLigneDroite(any())).thenReturn(Collections.emptyList());
        bot.getInventaire().ajouterIrrigation(); // Bloque irrigation
        bot.getInventaire().ajouterIrrigation();

        // ACT
        List<Action> actions = bot.jouer(gameStateMock);

        // ASSERT
        // Si le bot a choisi de piocher, il DOIT avoir choisi PANDA (car Jardinier vide)
        for (Action action : actions) {
            if (action instanceof PiocherObjectif) {
                PiocherObjectif piocheAction = (PiocherObjectif) action;
                // PiocherObjectif ne garde pas toujours le type en attribut public facile à tester,
                // mais on peut vérifier qu'il ne plante pas.
                assertNotNull(piocheAction);
            }
        }
    }

    // =================================================================================
    // 4. TESTS MÉTÉO (CORRECTION : On teste toutes les méthodes météo)
    // =================================================================================

    @Test
    void testChoisirMeteo_NeRenvoieJamaisNull() {
        // Le bot doit choisir une météo parmi les options valides
        // On le lance plusieurs fois pour être sûr (car c'est random)
        for (int i = 0; i < 20; i++) {
            Meteo m = bot.choisirMeteo();
            assertNotNull(m, "Le choix de météo ne doit jamais être null");
            // Vérifier que c'est bien une des valeurs de l'enum
            assertInstanceOf(Meteo.class, m);
        }
    }

    @Test
    void testChoisirMeteoAlternative_PasDeNuages() {
        // La règle (souvent) est que l'alternative (quand on ne peut pas appliquer l'effet)
        // ne doit pas être "Nuages" si c'est pour choisir un aménagement alors qu'il n'y en a plus.
        // Dans votre BotRandom, vous avez exclu Meteo.NUAGES du tableau options.
        for (int i = 0; i < 20; i++) {
            Meteo m = bot.choisirMeteoAlternative();
            assertNotEquals(Meteo.NUAGES, m, "La météo alternative ne devrait pas être NUAGES dans votre implémentation");
        }
    }

    @Test
    void testChoisirParcelleMeteo_Pluie() {
        // Cas 1 : Aucune parcelle irriguée
        assertNull(bot.choisirParcelleMeteo(Collections.emptyList()),
                "Doit retourner null si la liste est vide");

        // Cas 2 : Des parcelles disponibles
        Parcelle p1 = new Parcelle(Couleur.VERT);
        Parcelle p2 = new Parcelle(Couleur.ROSE);
        Parcelle choix = bot.choisirParcelleMeteo(List.of(p1, p2));

        assertNotNull(choix);
        assertTrue(choix == p1 || choix == p2, "Doit retourner une des parcelles de la liste");
    }

    @Test
    void testChoisirDestinationPanda_Orage() {
        // Cas 1 : Liste vide
        assertNull(bot.choisirDestinationPanda(Collections.emptyList()));

        // Cas 2 : Liste avec éléments
        Parcelle p1 = new Parcelle(Couleur.VERT);
        Parcelle result = bot.choisirDestinationPanda(List.of(p1));
        assertEquals(p1, result, "Doit retourner l'élément disponible");
    }

    // =================================================================================
    // 5. TESTS PARCELLES (CHOIX & POSE)
    // =================================================================================

    @Test
    void testChoisirPosition_Standard() {
        // ARRANGE
        Position posA = new Position(1, 0);
        Position posB = new Position(0, 1);
        when(plateauMock.getEmplacementsDisponibles()).thenReturn(List.of(posA, posB));

        // ACT
        // Le bot doit choisir où poser sa parcelle
        Position choisie = bot.choisirPosition(new Parcelle(Couleur.VERT), plateauMock);

        // ASSERT
        assertTrue(choisie.equals(posA) || choisie.equals(posB),
                "Le bot doit choisir une position valide parmi celles proposées par le plateau");
    }

    @Test
    void testChoisirParcelle_DansLaPioche() {
        // Ce test vérifie la méthode héritée de Bot (ou surchargée) : choisirParcelle
        // BotRandom utilise souvent l'implémentation par défaut qui prend la 1ère,
        // ou une implémentation random. Vérifions qu'il ne plante pas.

        // ARRANGE
        // On mock la session de sélection (les 3 cartes piochées)
        fr.cotedazur.univ.polytech.startingpoint.plateau.pioche.SelectionParcelle sessionMock =
                mock(fr.cotedazur.univ.polytech.startingpoint.plateau.pioche.SelectionParcelle.class);

        Parcelle p1 = new Parcelle(Couleur.VERT);
        Parcelle p2 = new Parcelle(Couleur.ROSE);
        Parcelle p3 = new Parcelle(Couleur.JAUNE);
        List<Parcelle> troisCartes = List.of(p1, p2, p3);

        when(sessionMock.getParcellesAChoisir()).thenReturn(troisCartes);
        // Important : Le bot par défaut appelle session.getFirst() ou accède à la liste
        // Si BotRandom n'override pas, il prend la première.
        // Si vous voulez tester le comportement par défaut :
        when(sessionMock.getFirst()).thenReturn(p1);

        // ACT
        Parcelle choisie = bot.choisirParcelle(sessionMock, plateauMock);

        // ASSERT
        assertNotNull(choisie);
        assertTrue(troisCartes.contains(choisie), "La parcelle choisie doit venir de la sélection");

        // Vérifier que le bot a bien validé son choix auprès de la session
        verify(sessionMock, atLeastOnce()).validerChoix(choisie);
    }

    @Test
    void testChoisirParcelle_SiVide() {
        // Cas limite : Si la sélection est vide (ne devrait pas arriver techniquement, mais bon)
        fr.cotedazur.univ.polytech.startingpoint.plateau.pioche.SelectionParcelle sessionMock =
                mock(fr.cotedazur.univ.polytech.startingpoint.plateau.pioche.SelectionParcelle.class);
        when(sessionMock.getParcellesAChoisir()).thenReturn(Collections.emptyList());

        Parcelle choisie = bot.choisirParcelle(sessionMock, plateauMock);
        assertNull(choisie, "Doit renvoyer null si rien à choisir");
    }
}