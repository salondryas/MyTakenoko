package fr.cotedazur.univ.polytech.startingpoint.elements;

import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.Arete;
import org.junit.jupiter.api.Test;

import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;

import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de la classe Arete")
class AreteTest {

    @Test
    @DisplayName("Création d'une arête avec positions adjacentes")
    void testCreationAreteValide() {
        Position p1 = new Position(0, 0);
        Position p2 = new Position(1, 0);

        assertDoesNotThrow(() -> new Arete(p1, p2));
    }

    @Test
    @DisplayName("Création d'une arête avec positions non adjacentes doit échouer")
    void testCreationAreteNonAdjacente() {
        Position p1 = new Position(0, 0);
        Position p2 = new Position(2, 0);

        assertThrows(IllegalArgumentException.class, () -> new Arete(p1, p2));
    }

    @Test
    @DisplayName("Normalisation : (A,B) == (B,A)")
    void testNormalisationArete() {
        Position p1 = new Position(0, 0);
        Position p2 = new Position(1, 0);

        Arete arete1 = new Arete(p1, p2);
        Arete arete2 = new Arete(p2, p1);

        assertEquals(arete1, arete2);
        assertEquals(arete1.hashCode(), arete2.hashCode());
    }

    @Test
    @DisplayName("touchePosition retourne true si l'arête contient la position")
    void testTouchePosition() {
        Position p1 = new Position(0, 0);
        Position p2 = new Position(1, 0);
        Position p3 = new Position(2, 0);

        Arete arete = new Arete(p1, p2);

        assertTrue(arete.touchePosition(p1));
        assertTrue(arete.touchePosition(p2));
        assertFalse(arete.touchePosition(p3));
    }

    @Test
    @DisplayName("getPositions retourne les deux positions de l'arête")
    void testGetPositions() {
        Position p1 = new Position(0, 0);
        Position p2 = new Position(1, 0);

        Arete arete = new Arete(p1, p2);

        assertEquals(2, arete.getPositions().size());
        assertTrue(arete.getPositions().contains(p1));
        assertTrue(arete.getPositions().contains(p2));
    }
}