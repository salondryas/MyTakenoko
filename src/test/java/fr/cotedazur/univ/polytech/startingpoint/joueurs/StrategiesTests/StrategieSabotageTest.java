package fr.cotedazur.univ.polytech.startingpoint.joueurs.StrategiesTests;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.Action;
import fr.cotedazur.univ.polytech.startingpoint.actions.DeplacerPanda;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.InventaireJoueur;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Strategies.StrategieSabotage;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.Objectif;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.jardinier.ObjectifJardinier;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.TypeObjectif;
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
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StrategieSabotageTest {

    StrategieSabotage strategie;

    @Mock GameState gameState;
    @Mock Bot bot;
    @Mock InventaireJoueur inventaire;
    @Mock Plateau plateau;
    @Mock Panda panda;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        strategie = new StrategieSabotage();

        when(gameState.getPlateau()).thenReturn(plateau);
        when(gameState.getPanda()).thenReturn(panda);
        when(bot.getInventaire()).thenReturn(inventaire);
        // Par défaut, pas d'objectifs en main
        when(inventaire.getObjectifs()).thenReturn(Collections.emptyList());
    }

    @Test
    void testSabotageReussi() {
        // SCÉNARIO NOMINAL :
        // Il y a un bambou VERT de taille 3.
        // Le bot N'A PAS d'objectif jardinier sur le Vert.
        // -> IL DOIT LE MANGER pour saboter l'adversaire (qui a probablement fait pousser ça).

        Position posCible = new Position(1, 0);
        Parcelle pVert = mock(Parcelle.class);
        when(pVert.getCouleur()).thenReturn(Couleur.VERT);

        // Configuration Plateau
        when(plateau.getPositionOccupees()).thenReturn(Set.of(posCible));
        when(plateau.getParcelle(posCible)).thenReturn(pVert);
        when(plateau.getNombreDeSectionsAPosition(posCible)).thenReturn(3); // C'est une menace !

        // Accessibilité
        when(panda.accessibleEnUnCoupParPanda(gameState, posCible)).thenReturn(true);

        // TEST
        Action action = strategie.getActionSabotage(gameState, bot);

        assertInstanceOf(DeplacerPanda.class, action);
        assertEquals(posCible, ((DeplacerPanda) action).getDestination());
    }

    @Test
    void testEviterAutoSabotage() {
        // SCÉNARIO :
        // Il y a un bambou VERT de taille 3.
        // MAIS le bot a un objectif Jardinier qui demande du VERT taille 4.
        // -> IL NE DOIT PAS LE MANGER (sinon il se tire une balle dans le pied).

        // 1. Mise en place de l'objectif du bot
        ObjectifJardinier objJardinier = mock(ObjectifJardinier.class);
        when(objJardinier.getType()).thenReturn(TypeObjectif.JARDINIER);
        when(objJardinier.getCouleurs()).thenReturn(List.of(Couleur.VERT));
        when(objJardinier.getTaille()).thenReturn(4); // Je veux le faire grandir, pas le manger !

        when(inventaire.getObjectifs()).thenReturn(List.of(objJardinier));

        // 2. Configuration Plateau
        Position posCible = new Position(1, 0);
        Parcelle pVert = mock(Parcelle.class);
        when(pVert.getCouleur()).thenReturn(Couleur.VERT);

        when(plateau.getPositionOccupees()).thenReturn(Set.of(posCible));
        when(plateau.getParcelle(posCible)).thenReturn(pVert);
        when(plateau.getNombreDeSectionsAPosition(posCible)).thenReturn(3); // Taille 3

        when(panda.accessibleEnUnCoupParPanda(gameState, posCible)).thenReturn(true);

        // TEST
        Action action = strategie.getActionSabotage(gameState, bot);

        assertNull(action, "Le bot ne doit pas manger un bambou qu'il essaie de faire pousser.");
    }

    @Test
    void testRienASaboter() {
        // SCÉNARIO : Il n'y a que des petits bambous (Taille 1 ou 2).
        // Le sabotage ne s'active que sur les bambous de taille >= 3.

        Position pos = new Position(1, 0);
        Parcelle p = mock(Parcelle.class);
        when(p.getCouleur()).thenReturn(Couleur.VERT);

        when(plateau.getPositionOccupees()).thenReturn(Set.of(pos));
        when(plateau.getParcelle(pos)).thenReturn(p);
        when(plateau.getNombreDeSectionsAPosition(pos)).thenReturn(2); // Trop petit pour être une menace

        Action action = strategie.getActionSabotage(gameState, bot);

        assertNull(action, "Pas de menace (taille < 3), donc pas de sabotage.");
    }

    @Test
    void testCibleInaccessible() {
        // SCÉNARIO : Une cible parfaite pour le sabotage existe, mais le Panda ne peut pas y aller.

        Position posCible = new Position(1, 0);
        Parcelle pVert = mock(Parcelle.class);
        when(pVert.getCouleur()).thenReturn(Couleur.VERT);

        when(plateau.getPositionOccupees()).thenReturn(Set.of(posCible));
        when(plateau.getParcelle(posCible)).thenReturn(pVert);
        when(plateau.getNombreDeSectionsAPosition(posCible)).thenReturn(3);

        // BLOCAGE
        when(panda.accessibleEnUnCoupParPanda(gameState, posCible)).thenReturn(false);

        Action action = strategie.getActionSabotage(gameState, bot);

        assertNull(action, "Si le panda ne peut pas y aller, on ne peut pas saboter.");
    }

    @Test
    void testIgnorerObjectifsNonJardinier() {
        // SCÉNARIO : J'ai un objectif PANDA Vert.
        // Il y a un bambou VERT de taille 3.
        // -> JE DOIS LE MANGER (C'est doublement bénéfique : Sabotage + Objectif Panda).
        // Ce test vérifie que la méthode 'aiJeBesoinDeFairePousser' ignore les objectifs Panda.

        // Objectif Panda (Type != JARDINIER)
        Objectif objPanda = mock(Objectif.class);
        when(objPanda.getType()).thenReturn(TypeObjectif.PANDA);
        when(inventaire.getObjectifs()).thenReturn(List.of(objPanda));

        Position posCible = new Position(1, 0);
        Parcelle pVert = mock(Parcelle.class);
        when(pVert.getCouleur()).thenReturn(Couleur.VERT);

        when(plateau.getPositionOccupees()).thenReturn(Set.of(posCible));
        when(plateau.getParcelle(posCible)).thenReturn(pVert);
        when(plateau.getNombreDeSectionsAPosition(posCible)).thenReturn(3);
        when(panda.accessibleEnUnCoupParPanda(gameState, posCible)).thenReturn(true);

        Action action = strategie.getActionSabotage(gameState, bot);

        // Comme ce n'est pas un objectif Jardinier qui me demande de le laisser pousser, je mange !
        assertInstanceOf(DeplacerPanda.class, action);
    }
}