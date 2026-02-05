package fr.cotedazur.univ.polytech.startingpoint.joueurs.StrategiesTests;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.Action;
import fr.cotedazur.univ.polytech.startingpoint.actions.DeplacerPanda;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.InventaireJoueur;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Strategies.StrategiePandaUnCoup;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.Objectif;
import fr.cotedazur.univ.polytech.startingpoint.elements.movables.Panda;
import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.elements.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StrategiePandaUnCoupTest {

    StrategiePandaUnCoup strategie;

    @Mock GameState gameState;
    @Mock Bot bot;
    @Mock InventaireJoueur inventaire;
    @Mock Plateau plateau;
    @Mock Panda panda;
    @Mock Objectif objectif;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        strategie = new StrategiePandaUnCoup();

        // Liaisons de base
        when(gameState.getPlateau()).thenReturn(plateau);
        when(gameState.getPanda()).thenReturn(panda);
        when(bot.getInventaire()).thenReturn(inventaire);
    }

    @Test
    void testIlManqueUnVertEtIlEstDispo() {
        // SCENARIO :
        // Objectif demande : 2 VERTS
        // Inventaire a : 1 VERT
        // -> Il manque exactement 1 VERT.
        // Plateau : Il y a une parcelle VERTE avec du bambou.

        // 1. Configuration des besoins
        when(objectif.getObjMap()).thenReturn(Map.of(Couleur.VERT, 2));
        when(inventaire.getBambous()).thenReturn(Map.of(Couleur.VERT, 1));

        // 2. Configuration du Plateau
        Position posCible = new Position(1, 0);
        Parcelle pVert = mock(Parcelle.class);
        when(pVert.getCouleur()).thenReturn(Couleur.VERT);

        when(plateau.getPositionOccupees()).thenReturn(Set.of(posCible));
        when(plateau.getParcelle(posCible)).thenReturn(pVert);
        when(plateau.getNombreDeSectionsAPosition(posCible)).thenReturn(1); // Il y a du bambou !

        // 3. Accessibilité
        when(panda.accessibleEnUnCoupParPanda(gameState, posCible)).thenReturn(true);

        // TEST
        Action action = strategie.getStrategiePandaUnCoup(gameState, bot, objectif);

        assertInstanceOf(DeplacerPanda.class, action);
        assertEquals(posCible, ((DeplacerPanda) action).getDestination());
    }

    @Test
    void testIlManqueUnRoseMaisPasSurPlateau() {
        // SCENARIO : Manque 1 Rose, mais il n'y a que du Vert sur le plateau.

        // 1. Besoins (0/1 Rose)
        when(objectif.getObjMap()).thenReturn(Map.of(Couleur.ROSE, 1));
        when(inventaire.getBambous()).thenReturn(Collections.emptyMap()); // 0 Rose

        // 2. Plateau (Juste du Vert)
        Position posVert = new Position(1, 0);
        Parcelle pVert = mock(Parcelle.class);
        when(pVert.getCouleur()).thenReturn(Couleur.VERT); // Mauvaise couleur

        when(plateau.getPositionOccupees()).thenReturn(Set.of(posVert));
        when(plateau.getParcelle(posVert)).thenReturn(pVert);
        when(plateau.getNombreDeSectionsAPosition(posVert)).thenReturn(1);
        when(panda.accessibleEnUnCoupParPanda(any(), any())).thenReturn(true);

        // TEST
        Action action = strategie.getStrategiePandaUnCoup(gameState, bot, objectif);

        assertNull(action, "Ne doit rien renvoyer si la couleur manquante est absente du plateau.");
    }

    @Test
    void testIlManqueDeuxBambous() {
        // SCENARIO : Objectif demande 2 JAUNES, on en a 0.
        // La stratégie 'Un Coup' ne doit pas s'activer (car il en manque > 1).

        when(objectif.getObjMap()).thenReturn(Map.of(Couleur.JAUNE, 2));
        when(inventaire.getBambous()).thenReturn(Collections.emptyMap()); // 0 Jaune

        // Même s'il y a du Jaune sur le plateau...
        Position posJaune = new Position(1, 0);
        Parcelle pJaune = mock(Parcelle.class);
        when(pJaune.getCouleur()).thenReturn(Couleur.JAUNE);
        when(plateau.getPositionOccupees()).thenReturn(Set.of(posJaune));
        when(plateau.getParcelle(posJaune)).thenReturn(pJaune);
        when(plateau.getNombreDeSectionsAPosition(posJaune)).thenReturn(1);
        when(panda.accessibleEnUnCoupParPanda(any(), any())).thenReturn(true);

        // TEST
        Action action = strategie.getStrategiePandaUnCoup(gameState, bot, objectif);

        assertNull(action, "Ne doit rien renvoyer s'il manque plus d'un bambou.");
    }

    @Test
    void testIlManqueUnBambouMaisCaseVide() {
        // SCENARIO : Manque 1 Vert. Il y a une parcelle Verte, mais elle a 0 bambou.

        when(objectif.getObjMap()).thenReturn(Map.of(Couleur.VERT, 1));
        when(inventaire.getBambous()).thenReturn(Collections.emptyMap());

        Position posVert = new Position(1, 0);
        Parcelle pVert = mock(Parcelle.class);
        when(pVert.getCouleur()).thenReturn(Couleur.VERT);

        when(plateau.getPositionOccupees()).thenReturn(Set.of(posVert));
        when(plateau.getParcelle(posVert)).thenReturn(pVert);
        // ICI : 0 section
        when(plateau.getNombreDeSectionsAPosition(posVert)).thenReturn(0);
        when(panda.accessibleEnUnCoupParPanda(any(), any())).thenReturn(true);

        // TEST
        Action action = strategie.getStrategiePandaUnCoup(gameState, bot, objectif);

        assertNull(action, "Ne doit pas aller sur une case sans bambou.");
    }

    @Test
    void testObjectifDejaFini() {
        // SCENARIO : On a déjà tout ce qu'il faut.
        when(objectif.getObjMap()).thenReturn(Map.of(Couleur.VERT, 1));
        when(inventaire.getBambous()).thenReturn(Map.of(Couleur.VERT, 1));

        Action action = strategie.getStrategiePandaUnCoup(gameState, bot, objectif);

        assertNull(action, "Si l'objectif est complet, on ne fait rien.");
    }
}