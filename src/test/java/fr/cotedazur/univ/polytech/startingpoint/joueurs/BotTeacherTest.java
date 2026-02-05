package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.*;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Strategies.*;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.*;
import fr.cotedazur.univ.polytech.startingpoint.elements.pioche.PiocheParcelle;
import fr.cotedazur.univ.polytech.startingpoint.elements.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.jardinier.ObjectifJardinier;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.panda.ObjectifPanda;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class BotTeacherTest {

    BotTeacher bot;

    // --- Mocks du Moteur de Jeu ---
    @Mock GameState gameState;
    @Mock Plateau plateau;
    @Mock PiocheObjectif piochePanda;
    @Mock PiocheObjectif piocheJardinier;
    @Mock PiocheObjectif piocheObjParcelle;
    @Mock PiocheParcelle piocheParcelle;

    // --- Mocks des Stratégies (On remplace les vraies par des simulacres) ---
    @Mock StrategieSabotage sabotageMock;
    @Mock StrategiePandaUnCoup pandaUnCoupMock;
    @Mock StrategieJardinierUnCoup jardinierUnCoupMock;
    @Mock StrategiePandaGenerale pandaGeneraleMock;
    @Mock StrategieAleatoire aleatoireMock;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        bot = new BotTeacher("Prof");

        // Configuration par défaut du GameState
        when(gameState.getPlateau()).thenReturn(plateau);
        when(gameState.getPiochePanda()).thenReturn(piochePanda);
        when(gameState.getPiocheJardinier()).thenReturn(piocheJardinier);
        when(gameState.getPiocheObjectifParcelle()).thenReturn(piocheObjParcelle);
        when(gameState.getPiocheParcelle()).thenReturn(piocheParcelle);

        // --- INJECTION DES MOCKS (Reflection) ---
        // On remplace les "new Strategie...()" du Bot par nos Mocks pour contrôler le test
        injecterMock(bot, "strategieSabotage", sabotageMock);
        injecterMock(bot, "strategiePanda", pandaUnCoupMock);
        injecterMock(bot, "strategieJardinier", jardinierUnCoupMock);
        injecterMock(bot, "strategiePandaGenerale", pandaGeneraleMock);
        injecterMock(bot, "strategieAleatoire", aleatoireMock);
    }

    /**
     * Méthode utilitaire pour injecter des mocks dans les champs privés
     */
    private void injecterMock(Object target, String fieldName, Object mock) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, mock);
    }

    private void forcerFinPremierTour() throws Exception {
        Field field = BotTeacher.class.getDeclaredField("estPremierTour");
        field.setAccessible(true);
        field.set(bot, false);
    }

    // ================= TESTS DES PRIORITÉS =================

    @Test
    void testPriorite0_PremierTour() {
        // Au tout début, estPremierTour est true par défaut
        when(piochePanda.getTaille()).thenReturn(5);

        Action action = bot.choisirUneAction(gameState, Collections.emptySet());

        assertInstanceOf(PiocherObjectif.class, action, "Au premier tour, il doit piocher.");
        // On vérifie qu'aucune stratégie n'a été appelée inutilement
        verifyNoInteractions(sabotageMock);
    }

    @Test
    void testPriorite1_Sabotage() throws Exception {
        forcerFinPremierTour();

        // SCÉNARIO : Le sabotage trouve une action
        DeplacerPanda actionSabotage = new DeplacerPanda(null, new Position(0,0));
        when(sabotageMock.getActionSabotage(any(), any())).thenReturn(actionSabotage);

        Action result = bot.choisirUneAction(gameState, Collections.emptySet());

        assertEquals(actionSabotage, result, "Le Sabotage doit être la priorité absolue.");
        verify(sabotageMock).getActionSabotage(gameState, bot);
        // On vérifie qu'on s'arrête là et qu'on ne va pas piocher ou voir les objectifs
        verifyNoInteractions(pandaUnCoupMock);
    }

    @Test
    void testPriorite2_RemplirMain() throws Exception {
        forcerFinPremierTour();
        // Pas de sabotage
        when(sabotageMock.getActionSabotage(any(), any())).thenReturn(null);

        // Main vide (< 5 cartes)
        bot.getInventaire().getObjectifs().clear();
        when(piochePanda.getTaille()).thenReturn(1);

        Action result = bot.choisirUneAction(gameState, Collections.emptySet());

        assertInstanceOf(PiocherObjectif.class, result, "Il doit remplir sa main si elle n'est pas pleine.");
    }

    @Test
    void testPriorite3_ObjectifPandaUnCoup() throws Exception {
        forcerFinPremierTour();
        when(sabotageMock.getActionSabotage(any(), any())).thenReturn(null);
        remplirMain(bot); // Pour passer l'étape remplissage

        // On donne un objectif Panda
        ObjectifPanda obj = new ObjectifPanda(1, List.of());
        bot.getInventaire().ajouterObjectif(obj);

        // La stratégie Panda trouve une solution
        DeplacerPanda actionGagnante = new DeplacerPanda(null, new Position(1,0));
        when(pandaUnCoupMock.getStrategiePandaUnCoup(gameState, bot, obj)).thenReturn(actionGagnante);

        Action result = bot.choisirUneAction(gameState, Collections.emptySet());

        assertEquals(actionGagnante, result);
    }

    @Test
    void testPriorite3_ObjectifJardinierUnCoup() throws Exception {
        forcerFinPremierTour();
        when(sabotageMock.getActionSabotage(any(), any())).thenReturn(null);
        remplirMain(bot);

        // On donne un objectif Jardinier
        ObjectifJardinier obj = new ObjectifJardinier(null, 0,0,null,0);
        bot.getInventaire().ajouterObjectif(obj);

        // La stratégie Jardinier trouve une solution
        DeplacerJardinier actionGagnante = new DeplacerJardinier(null, new Position(1,0));
        when(jardinierUnCoupMock.getStrategieJardinierUnCoup(gameState, bot, obj)).thenReturn(actionGagnante);

        Action result = bot.choisirUneAction(gameState, Collections.emptySet());

        assertEquals(actionGagnante, result);
    }

    @Test
    void testPriorite4_PandaGloutonGenerale() throws Exception {
        forcerFinPremierTour();
        when(sabotageMock.getActionSabotage(any(), any())).thenReturn(null);
        remplirMain(bot);

        // Aucune stratégie "Un Coup" ne marche (renvoient null par défaut)

        // SCÉNARIO : La stratégie générale (glouton) trouve quelque chose
        DeplacerPanda actionManger = new DeplacerPanda(null, new Position(2,2));
        when(pandaGeneraleMock.getStrategiePandaGenerale(gameState, bot)).thenReturn(actionManger);

        Action result = bot.choisirUneAction(gameState, Collections.emptySet());

        assertEquals(actionManger, result, "Si on ne peut rien finir en un coup, on joue le Panda Général.");
    }

    @Test
    void testPriorite5_Aleatoire() throws Exception {
        forcerFinPremierTour();
        when(sabotageMock.getActionSabotage(any(), any())).thenReturn(null);
        remplirMain(bot);
        // Toutes les stratégies renvoient null...

        // SCÉNARIO : Dernier recours, l'aléatoire
        Action actionRandom = new PoserParcelle();
        when(aleatoireMock.getActionAleatoire(gameState, Collections.emptySet())).thenReturn(actionRandom);

        Action result = bot.choisirUneAction(gameState, Collections.emptySet());

        assertEquals(actionRandom, result, "En dernier recours, on appelle la stratégie aléatoire.");
    }

    // --- Utilitaire pour remplir la main ---
    private void remplirMain(BotTeacher bot) {
        for(int i=0; i<5; i++) {
            bot.getInventaire().ajouterObjectif(new ObjectifPanda(0, List.of()));
        }
    }
}