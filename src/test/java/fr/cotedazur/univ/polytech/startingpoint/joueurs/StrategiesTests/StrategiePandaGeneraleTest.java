package fr.cotedazur.univ.polytech.startingpoint.joueurs.StrategiesTests;

import fr.cotedazur.univ.polytech.startingpoint.GameState;
import fr.cotedazur.univ.polytech.startingpoint.actions.Action;
import fr.cotedazur.univ.polytech.startingpoint.actions.DeplacerPanda;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.InventaireJoueur;
import fr.cotedazur.univ.polytech.startingpoint.joueurs.Strategies.StrategiePandaGenerale;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.Objectif;
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
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StrategiePandaGeneraleTest {

    StrategiePandaGenerale strategie;

    @Mock GameState gameState;
    @Mock Bot bot;
    @Mock InventaireJoueur inventaire;
    @Mock Plateau plateau;
    @Mock Panda panda;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        strategie = new StrategiePandaGenerale();

        // Liaisons de base
        when(gameState.getPlateau()).thenReturn(plateau);
        when(gameState.getPanda()).thenReturn(panda);
        when(bot.getInventaire()).thenReturn(inventaire);

        // Par défaut, le bot n'a pas de bambous en stock
        when(inventaire.getBambous()).thenReturn(Collections.emptyMap());
    }

    @Test
    void testPrioriteAuxBesoins() {
        // SCÉNARIO :
        // Le bot a besoin de ROSE (Objectif).
        // Sur le plateau, il y a du VERT (accessible) et du ROSE (accessible).
        // Le bot DOIT choisir le ROSE.

        // 1. Setup Objectif (Besoin de 1 Rose)
        Objectif obj = mock(Objectif.class);
        when(obj.getType()).thenReturn(TypeObjectif.PANDA);
        when(obj.getObjMap()).thenReturn(Map.of(Couleur.ROSE, 1));
        when(inventaire.getObjectifs()).thenReturn(List.of(obj));

        // 2. Setup Plateau (2 positions occupées)
        Position posVert = new Position(1, 0);
        Position posRose = new Position(0, 1);

        // Mock des parcelles
        Parcelle pVert = mock(Parcelle.class); when(pVert.getCouleur()).thenReturn(Couleur.VERT);
        Parcelle pRose = mock(Parcelle.class); when(pRose.getCouleur()).thenReturn(Couleur.ROSE);

        when(plateau.getPositionOccupees()).thenReturn(Set.of(posVert, posRose));
        when(plateau.getParcelle(posVert)).thenReturn(pVert);
        when(plateau.getParcelle(posRose)).thenReturn(pRose);

        // Il y a du bambou partout
        when(plateau.getNombreDeSectionsAPosition(posVert)).thenReturn(1);
        when(plateau.getNombreDeSectionsAPosition(posRose)).thenReturn(1);

        // Tout est accessible
        when(panda.accessibleEnUnCoupParPanda(any(), any())).thenReturn(true);

        // TEST
        Action action = strategie.getStrategiePandaGenerale(gameState, bot);

        assertInstanceOf(DeplacerPanda.class, action);
        // VERIFICATION CRUCIALE : Il a bien choisi la position ROSE
        assertEquals(posRose, ((DeplacerPanda) action).getDestination(), "Le bot aurait dû privilégier le bambou ROSE requis par l'objectif.");
    }

    @Test
    void testFallbackMangerNimporteQuoi() {
        // SCÉNARIO :
        // Le bot n'a AUCUN objectif (ou objectifs finis).
        // Il y a du bambou VERT sur le plateau.
        // Il doit le manger (Mode Glouton / Étape 3).

        // 1. Pas d'objectifs
        when(inventaire.getObjectifs()).thenReturn(Collections.emptyList());

        // 2. Plateau avec du Vert
        Position posVert = new Position(1, 0);
        Parcelle pVert = mock(Parcelle.class);
        when(pVert.getCouleur()).thenReturn(Couleur.VERT);

        when(plateau.getPositionOccupees()).thenReturn(Set.of(posVert));
        when(plateau.getParcelle(posVert)).thenReturn(pVert);
        when(plateau.getNombreDeSectionsAPosition(posVert)).thenReturn(1);
        when(panda.accessibleEnUnCoupParPanda(any(), any())).thenReturn(true);

        // TEST
        Action action = strategie.getStrategiePandaGenerale(gameState, bot);

        assertInstanceOf(DeplacerPanda.class, action);
        assertEquals(posVert, ((DeplacerPanda) action).getDestination(), "Sans objectif, le bot doit manger ce qui est dispo.");
    }

    @Test
    void testTargetPresentButInaccessible() {
        // SCÉNARIO :
        // Besoin de ROSE.
        // Il y a du ROSE (Inaccessible) et du VERT (Accessible).
        // Le bot doit se rabattre sur le VERT (Étape 3) car il ne peut pas atteindre le Rose.

        // 1. Objectif Rose
        Objectif obj = mock(Objectif.class);
        when(obj.getType()).thenReturn(TypeObjectif.PANDA);
        when(obj.getObjMap()).thenReturn(Map.of(Couleur.ROSE, 1));
        when(inventaire.getObjectifs()).thenReturn(List.of(obj));

        // 2. Plateau
        Position posRose = new Position(1, 0); // Inaccessible
        Position posVert = new Position(0, 1); // Accessible

        Parcelle pRose = mock(Parcelle.class); when(pRose.getCouleur()).thenReturn(Couleur.ROSE);
        Parcelle pVert = mock(Parcelle.class); when(pVert.getCouleur()).thenReturn(Couleur.VERT);

        when(plateau.getPositionOccupees()).thenReturn(Set.of(posRose, posVert));
        when(plateau.getParcelle(posRose)).thenReturn(pRose);
        when(plateau.getParcelle(posVert)).thenReturn(pVert);
        when(plateau.getNombreDeSectionsAPosition(posRose)).thenReturn(1);
        when(plateau.getNombreDeSectionsAPosition(posVert)).thenReturn(1);

        // 3. Accessibilité
        when(panda.accessibleEnUnCoupParPanda(gameState, posRose)).thenReturn(false); // BLOQUÉ !
        when(panda.accessibleEnUnCoupParPanda(gameState, posVert)).thenReturn(true);

        // TEST
        Action action = strategie.getStrategiePandaGenerale(gameState, bot);

        // Il doit manger le Vert (Mieux vaut manger quelque chose que rien du tout)
        assertEquals(posVert, ((DeplacerPanda) action).getDestination());
    }

    @Test
    void testRienAManger() {
        // SCÉNARIO : Plateau vide ou sans bambou.
        when(inventaire.getObjectifs()).thenReturn(Collections.emptyList());
        when(plateau.getPositionOccupees()).thenReturn(Set.of(new Position(0,0)));
        // 0 section sur la parcelle
        when(plateau.getNombreDeSectionsAPosition(any())).thenReturn(0);

        Action action = strategie.getStrategiePandaGenerale(gameState, bot);

        assertNull(action, "S'il n'y a pas de bambou, l'action doit être null.");
    }
}