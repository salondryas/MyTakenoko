package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.Strategies.StrategieJardinierUnCoup;
import fr.cotedazur.univ.polytech.startingpoint.Strategies.StrategiePandaUnCoup;
import fr.cotedazur.univ.polytech.startingpoint.Strategies.StrategieSabotage;
import fr.cotedazur.univ.polytech.startingpoint.actions.*;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.*;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.plateau.pioche.PiocheParcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Set;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


class BotTeacherTest {


    BotTeacher bot;


    // Mocks du Moteur de Jeu
    @Mock
    GameState gameState;
    @Mock Plateau plateau;
    @Mock PiocheObjectif piochePanda;
    @Mock PiocheObjectif piocheJardinier;
    @Mock PiocheObjectif piocheObjParcelle;
    @Mock PiocheParcelle piocheParcelle;


    // Mocks des Stratégies (On va les injecter à la place des vraies)
    @Mock StrategieSabotage strategieSabotageMock;
    @Mock StrategiePandaUnCoup strategiePandaMock;
    @Mock StrategieJardinierUnCoup strategieJardinierMock;


    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        bot = new BotTeacher("Professeur");


        // Configuration par défaut du GameState
        when(gameState.getPlateau()).thenReturn(plateau);
        when(gameState.getPiochePanda()).thenReturn(piochePanda);
        when(gameState.getPiocheJardinier()).thenReturn(piocheJardinier);
        when(gameState.getPiocheObjectifParcelle()).thenReturn(piocheObjParcelle);
        when(gameState.getPiocheParcelle()).thenReturn(piocheParcelle);


        // --- INJECTION MAGIQUE (RÉFLEXION) ---
        // On remplace les stratégies créées avec "new" par nos Mocks
        injecterMock(bot, "strategieSabotage", strategieSabotageMock);
        injecterMock(bot, "strategiePanda", strategiePandaMock);
        // ATTENTION : Vérifie que ta variable s'appelle bien "strategieJardinier" dans BotTeacher !
        injecterMock(bot, "strategieJardinier", strategieJardinierMock);
    }


    /**
     * Outil pour injecter un mock dans un attribut privé sans setter.
     */
    private void injecterMock(Object target, String fieldName, Object mock) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, mock);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // Si tu n'as pas encore créé le champ strategieJardinier, cela affichera juste un warning ici
            System.err.println("Impossible d'injecter le mock pour : " + fieldName + " (Le champ existe-t-il ?)");
        }
    }


    private void forcerFinPremierTour() {
        try {
            Field field = BotTeacher.class.getDeclaredField("estPremierTour");
            field.setAccessible(true);
            field.set(bot, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void remplirMainDuBot() {
        for (int i = 0; i < 5; i++) {
            // On ajoute des objectifs vides juste pour remplir le quota de 5 cartes
            bot.getInventaire().ajouterObjectif(new ObjectifPanda(0, List.of()));
        }
    }


    // ================= TESTS =================


    @Test
    void testPremierTour_DoitPiocher() {
        when(piochePanda.getTaille()).thenReturn(5);


        Action action = bot.choisirUneAction(gameState, Collections.emptySet());


        assertInstanceOf(PiocherObjectif.class, action);
    }


    @Test
    void testPriorite1_Sabotage() {
        forcerFinPremierTour();


        // SCÉNARIO : La stratégie Sabotage a trouvé une cible !
        DeplacerPanda actionSabotage = new DeplacerPanda(null, new Position(0, 0));
        when(strategieSabotageMock.getActionSabotage(any(), any())).thenReturn(actionSabotage);


        Action resultat = bot.choisirUneAction(gameState, Collections.emptySet());


        assertEquals(actionSabotage, resultat, "Le sabotage doit passer AVANT tout le reste.");
        // Vérifie qu'on n'a pas essayé de piocher
        verify(piochePanda, never()).getTaille();
    }


    @Test
    void testPriorite2_RemplirMain() {
        forcerFinPremierTour();
        // Sabotage ne renvoie rien (null)
        when(strategieSabotageMock.getActionSabotage(any(), any())).thenReturn(null);


        // Main vide (0 cartes)
        bot.getInventaire().getObjectifs().clear();
        when(piochePanda.getTaille()).thenReturn(1);


        Action resultat = bot.choisirUneAction(gameState, Collections.emptySet());


        assertInstanceOf(PiocherObjectif.class, resultat, "Le bot doit piocher si sa main n'est pas pleine.");
    }


    @Test
    void testPriorite3_StrategiePanda() {
        forcerFinPremierTour();
        when(strategieSabotageMock.getActionSabotage(any(), any())).thenReturn(null);
        remplirMainDuBot(); // Pour ne pas piocher


        // On ajoute un vrai Objectif Panda en main
        ObjectifPanda objPanda = new ObjectifPanda(1, List.of());
        bot.getInventaire().ajouterObjectif(objPanda);


        // On configure le Mock pour dire "Oui, j'ai une solution pour cet objectif"
        DeplacerPanda coupGagnant = new DeplacerPanda(null, new Position(1, 0));
        when(strategiePandaMock.getStrategiePandaUnCoup(any(), any(), eq(objPanda))).thenReturn(coupGagnant);


        Action resultat = bot.choisirUneAction(gameState, Collections.emptySet());


        assertEquals(coupGagnant, resultat);
    }


//    @Test
//    void testPriorite4_StrategieJardinier() {
//        forcerFinPremierTour();
//        when(strategieSabotageMock.getActionSabotage(any(), any())).thenReturn(null);
//        remplirMainDuBot();
//
//        // On ajoute un vrai Objectif Jardinier en main
//        ObjectifJardinier objJardinier = new ObjectifJardinier(null, 0, 0, null, 0);
//        bot.getInventaire().ajouterObjectif(objJardinier);
//
//        // On configure le Mock Jardinier
//        DeplacerJardinier coupGagnant = new DeplacerJardinier(null, new Position(0, 1));
//        when(strategieJardinierMock.getStrategieJardinier(any(), any(), eq(objJardinier))).thenReturn(coupGagnant);
//
//        Action resultat = bot.choisirUneAction(gameState, Collections.emptySet());
//
//        assertEquals(coupGagnant, resultat);
//    }


    @Test
    void testFallback_Glouton() {
        forcerFinPremierTour();
        when(strategieSabotageMock.getActionSabotage(any(), any())).thenReturn(null);
        remplirMainDuBot(); // Main pleine


        // ICI : Aucune stratégie (Panda/Jardinier) ne renvoie de coup (Mockito renvoie null par défaut)


        // SCÉNARIO : Il y a du bambou sur le plateau
        Position posBambou = new Position(1, 0);
        when(plateau.getPositionOccupees()).thenReturn(Set.of(posBambou));
        when(plateau.getNombreDeSectionsAPosition(posBambou)).thenReturn(1);


        // Configuration du Panda pour éviter le NullPointerException
        fr.cotedazur.univ.polytech.startingpoint.plateau.Panda vraiFauxPanda = mock(fr.cotedazur.univ.polytech.startingpoint.plateau.Panda.class);
        when(gameState.getPanda()).thenReturn(vraiFauxPanda);
        when(vraiFauxPanda.accessibleEnUnCoupParPanda(any(), any())).thenReturn(true);


        Action resultat = bot.choisirUneAction(gameState, Collections.emptySet());


        assertInstanceOf(DeplacerPanda.class, resultat, "Le bot doit manger du bambou par défaut.");
    }
}
