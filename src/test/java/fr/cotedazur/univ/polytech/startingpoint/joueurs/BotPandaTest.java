package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.*;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.ObjectifPanda;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.PiocheObjectif;
import fr.cotedazur.univ.polytech.startingpoint.plateau.*;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;


import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BotPandaTest {

    BotPanda bot;

    @Mock GameState gameStateMock;
    @Mock Plateau plateauMock;
    @Mock Jardinier jardinierMock;
    @Mock Panda pandaMock;
    @Mock PiocheParcelle piocheParcelleMock;
    @Mock PiocheObjectif piocheObjectifMock;

    @BeforeEach
    void setUp() {
        bot = new BotPanda("BotPandaTest");

        // 1. CRUCIAL : Initialisation manuelle des Mocks
        gameStateMock = mock(GameState.class);
        plateauMock = mock(Plateau.class);
        jardinierMock = mock(Jardinier.class);
        pandaMock = mock(Panda.class);
        piocheParcelleMock = mock(PiocheParcelle.class);
        piocheObjectifMock = mock(PiocheObjectif.class);

        // 2. Configuration des Mocks
        lenient().when(gameStateMock.getPlateau()).thenReturn(plateauMock);
        lenient().when(gameStateMock.getJardinier()).thenReturn(jardinierMock);
        lenient().when(gameStateMock.getPanda()).thenReturn(pandaMock);
        lenient().when(gameStateMock.getPioche()).thenReturn(piocheParcelleMock);

        // IMPORTANT : On dit que c'est la pioche PANDA
        lenient().when(gameStateMock.getPiochePanda()).thenReturn(piocheObjectifMock);

        // Par défaut, pas de déplacements possibles pour éviter les erreurs
        lenient().when(plateauMock.getTrajetsLigneDroite(any())).thenReturn(java.util.Collections.emptyList());
        lenient().when(plateauMock.getEmplacementsDisponibles()).thenReturn(java.util.Collections.emptyList());
    }

    // --- RAPPEL STRATÉGIE PANDA ---
    // 1. Manger (Priorité Absolue)
    // 2. Piocher si pas d'objectif
    // 3. Jardinier pour faire pousser
    // 4. Poser Parcelle
    // 5. Repli (Piocher / Bouger)

    @Test
    void testStrat1_PrioriteDeplacerPanda() {
        // SCÉNARIO : Le bot a un objectif Panda VERT.
        // Une parcelle VERTE avec du bambou est accessible au Panda.

        // 1. On donne un objectif PANDA au bot (2 points, VERT, 2 bambous)
        ObjectifPanda objVert = new ObjectifPanda(2, Couleur.VERT, 2);
        bot.getInventaire().ajouterObjectif(objVert);

        // 2. Setup du Panda et du Plateau
        Position posPanda = new Position(0,0);
        Position posCible = new Position(1,0);

        when(pandaMock.getPositionPanda()).thenReturn(posPanda);
        when(plateauMock.getTrajetsLigneDroite(posPanda)).thenReturn(List.of(posCible));

        // La parcelle cible est parfaite : VERTE, IRRIGUÉE, avec BAMBOU > 0
        Parcelle parcelleCible = mock(Parcelle.class);
        when(parcelleCible.getCouleur()).thenReturn(Couleur.VERT);
        when(plateauMock.getNombreDeSectionsAPosition(posCible)).thenReturn(1); // Il y a à manger !
        when(plateauMock.getParcelle(posCible)).thenReturn(parcelleCible);

        // 3. Action
        List<Action> actions = bot.jouer(gameStateMock);

        // 4. Vérification
        assertFalse(actions.isEmpty(), "Le bot devrait retourner une action");
        assertTrue(actions.get(0) instanceof DeplacerPanda, "Priorité = Manger (Déplacer Panda)");
    }

    @Test
    void testStrat2_PlanB_DeplacerJardinier() {
        // SCÉNARIO :
        // - Strat 1 (Manger) échoue : Pas de bambou vert disponible.
        // - Le Bot possède un objectif VERT.
        // - Le Jardinier peut arroser une parcelle VERTE vide.

        // 1. Objectif Vert
        bot.getInventaire().ajouterObjectif(new ObjectifPanda(2, Couleur.VERT, 2));

        // 2. Panda bloqué (aucun trajet intéressant pour manger)
        when(plateauMock.getTrajetsLigneDroite(any())).thenReturn(Collections.emptyList());

        // 3. Jardinier peut agir
        Position posJardinier = new Position(2,2);
        Position posCible = new Position(2,3);

        when(jardinierMock.getPosition()).thenReturn(posJardinier);
        when(plateauMock.getTrajetsLigneDroite(posJardinier)).thenReturn(List.of(posCible));

        // Setup de la Parcelle Cible pour le JARDINIER (Verte, Irriguée, Bambou < 4)
        Parcelle parcelleJardinier = mock(Parcelle.class);
        when(parcelleJardinier.getCouleur()).thenReturn(Couleur.VERT);
        when(parcelleJardinier.estIrriguee()).thenReturn(true);
        when(parcelleJardinier.getNbSectionsSurParcelle()).thenReturn(1);
        when(plateauMock.getParcelle(posCible)).thenReturn(parcelleJardinier);

        // 4. Exécution
        List<Action> actions = bot.jouer(gameStateMock);

        // 5. Vérification
        assertFalse(actions.isEmpty());
        assertTrue(actions.get(0) instanceof DeplacerJardinier, "Le bot devrait faire pousser du bambou pour plus tard");
    }

    @Test
    void testStrat3_RienEnMain_PiocherObjectif() {
        // SCÉNARIO : Le bot n'a aucun objectif.
        // ATTENDU : Il pioche direct un objectif PANDA.

        // 1. Inventaire vide par défaut

        // 2. On n'a pas besoin de mocker le retour de la pioche ici,
        // car le bot retourne juste l'action "Je veux piocher", il ne l'exécute pas.

        // 3. Action
        List<Action> actions = bot.jouer(gameStateMock);

        // 4. Vérification
        assertFalse(actions.isEmpty());
        assertTrue(actions.get(0) instanceof PiocherObjectif, "Sans objectif, il faut piocher");
    }
}