package fr.cotedazur.univ.polytech.startingpoint.plateau;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.cotedazur.univ.polytech.startingpoint.utilitaires.QuantityException;

import static org.junit.jupiter.api.Assertions.*;

class StockSectionBambouTest {

    @BeforeEach
    void resetStockQuantities() throws QuantityException {
        while (StockSectionBambou.VERTES.getQuantity() < 36) {
            StockSectionBambou.VERTES.addToQuantity();
        }
        while (StockSectionBambou.JAUNES.getQuantity() < 30) {
            StockSectionBambou.JAUNES.addToQuantity();
        }
        while (StockSectionBambou.ROSES.getQuantity() < 28) {
            StockSectionBambou.ROSES.addToQuantity();
        }
    }

    @Test
    void testVertesInitialQuantity() {
        assertEquals(36, StockSectionBambou.VERTES.getQuantity());
    }

    @Test
    void testJaunesInitialQuantity() {
        assertEquals(30, StockSectionBambou.JAUNES.getQuantity());
    }

    @Test
    void testRosesInitialQuantity() {
        assertEquals(28, StockSectionBambou.ROSES.getQuantity());
    }

    @Test
    void testSubtractToQuantityDecreasesQuantity() throws QuantityException {
        int initialQuantity = StockSectionBambou.VERTES.getQuantity();
        StockSectionBambou.VERTES.subtractToQuantity();
        assertEquals(initialQuantity - 1, StockSectionBambou.VERTES.getQuantity());
    }

    @Test
    void testSubtractToQuantityAllColors() throws QuantityException {
        int vertesInitial = StockSectionBambou.VERTES.getQuantity();
        int jaunesInitial = StockSectionBambou.JAUNES.getQuantity();
        int rosesInitial = StockSectionBambou.ROSES.getQuantity();

        StockSectionBambou.VERTES.subtractToQuantity();
        StockSectionBambou.JAUNES.subtractToQuantity();
        StockSectionBambou.ROSES.subtractToQuantity();

        assertEquals(vertesInitial - 1, StockSectionBambou.VERTES.getQuantity());
        assertEquals(jaunesInitial - 1, StockSectionBambou.JAUNES.getQuantity());
        assertEquals(rosesInitial - 1, StockSectionBambou.ROSES.getQuantity());
    }

    @Test
    void testSubtractToQuantityThrowsExceptionWhenEmpty() throws QuantityException {
        for (int i = 0; i < 36; i++) {
            StockSectionBambou.VERTES.subtractToQuantity();
        }

        assertEquals(0, StockSectionBambou.VERTES.getQuantity());

        QuantityException exception = assertThrows(
                QuantityException.class,
                () -> StockSectionBambou.VERTES.subtractToQuantity());

        assertEquals("Toutes les sections vertes ont été jouées actuellement !", exception.getMessage());
    }

    @Test
    void testSubtractToQuantityCanReachZero() throws QuantityException {
        for (int i = 0; i < 29; i++) {
            StockSectionBambou.JAUNES.subtractToQuantity();
        }

        assertEquals(1, StockSectionBambou.JAUNES.getQuantity());

        StockSectionBambou.JAUNES.subtractToQuantity();
        assertEquals(0, StockSectionBambou.JAUNES.getQuantity());

        assertThrows(QuantityException.class,
                () -> StockSectionBambou.JAUNES.subtractToQuantity());
    }

    @Test
    void testMultipleSubtractions() throws QuantityException {
        int initialQuantity = StockSectionBambou.ROSES.getQuantity();
        int subtractions = 5;

        for (int i = 0; i < subtractions; i++) {
            StockSectionBambou.ROSES.subtractToQuantity();
        }

        assertEquals(initialQuantity - subtractions, StockSectionBambou.ROSES.getQuantity());
    }

    @Test
    void testAddToQuantityIncreasesQuantity() throws QuantityException {
        StockSectionBambou.VERTES.subtractToQuantity();
        int currentQuantity = StockSectionBambou.VERTES.getQuantity();

        StockSectionBambou.VERTES.addToQuantity();

        assertEquals(currentQuantity + 1, StockSectionBambou.VERTES.getQuantity());
    }

    @Test
    void testAddToQuantityAllColors() throws QuantityException {
        StockSectionBambou.VERTES.subtractToQuantity();
        StockSectionBambou.JAUNES.subtractToQuantity();
        StockSectionBambou.ROSES.subtractToQuantity();

        int vertesAfterSubtract = StockSectionBambou.VERTES.getQuantity();
        int jaunesAfterSubtract = StockSectionBambou.JAUNES.getQuantity();
        int rosesAfterSubtract = StockSectionBambou.ROSES.getQuantity();

        StockSectionBambou.VERTES.addToQuantity();
        StockSectionBambou.JAUNES.addToQuantity();
        StockSectionBambou.ROSES.addToQuantity();

        assertEquals(vertesAfterSubtract + 1, StockSectionBambou.VERTES.getQuantity());
        assertEquals(jaunesAfterSubtract + 1, StockSectionBambou.JAUNES.getQuantity());
        assertEquals(rosesAfterSubtract + 1, StockSectionBambou.ROSES.getQuantity());
    }

    @Test
    void testAddToQuantityThrowsExceptionWhenFull() {
        assertEquals(36, StockSectionBambou.VERTES.getQuantity());

        QuantityException exception = assertThrows(
                QuantityException.class,
                () -> StockSectionBambou.VERTES.addToQuantity());

        assertEquals("Le nombre maximum de sections vertes est atteint !", exception.getMessage());
    }

    @Test
    void testAddToQuantityCanReachMaxButNotExceed() throws QuantityException {
        StockSectionBambou.JAUNES.subtractToQuantity();
        assertEquals(29, StockSectionBambou.JAUNES.getQuantity());

        StockSectionBambou.JAUNES.addToQuantity();
        assertEquals(30, StockSectionBambou.JAUNES.getQuantity());

        assertThrows(QuantityException.class,
                () -> StockSectionBambou.JAUNES.addToQuantity());
    }

    @Test
    void testMultipleAdditions() throws QuantityException {
        for (int i = 0; i < 5; i++) {
            StockSectionBambou.ROSES.subtractToQuantity();
        }

        int currentQuantity = StockSectionBambou.ROSES.getQuantity();
        int additions = 3;

        for (int i = 0; i < additions; i++) {
            StockSectionBambou.ROSES.addToQuantity();
        }

        assertEquals(currentQuantity + additions, StockSectionBambou.ROSES.getQuantity());
    }

    @Test
    void testAddSubtractReversible() throws QuantityException {
        int initialQuantity = StockSectionBambou.VERTES.getQuantity();

        StockSectionBambou.VERTES.subtractToQuantity();
        StockSectionBambou.VERTES.addToQuantity();

        assertEquals(initialQuantity, StockSectionBambou.VERTES.getQuantity());
    }

    @Test
    void testFullCycle() throws QuantityException {
        int maxQuantity = StockSectionBambou.ROSES.getQuantity();

        for (int i = 0; i < maxQuantity; i++) {
            StockSectionBambou.ROSES.subtractToQuantity();
        }
        assertEquals(0, StockSectionBambou.ROSES.getQuantity());

        for (int i = 0; i < maxQuantity; i++) {
            StockSectionBambou.ROSES.addToQuantity();
        }
        assertEquals(maxQuantity, StockSectionBambou.ROSES.getQuantity());
    }

    @Test
    void testIndependentQuantities() throws QuantityException {
        int vertesInitial = StockSectionBambou.VERTES.getQuantity();
        int jaunesInitial = StockSectionBambou.JAUNES.getQuantity();
        int rosesInitial = StockSectionBambou.ROSES.getQuantity();

        StockSectionBambou.VERTES.subtractToQuantity();
        StockSectionBambou.VERTES.subtractToQuantity();

        assertEquals(jaunesInitial, StockSectionBambou.JAUNES.getQuantity());
        assertEquals(rosesInitial, StockSectionBambou.ROSES.getQuantity());
        assertEquals(vertesInitial - 2, StockSectionBambou.VERTES.getQuantity());
    }

    @Test
    void testQuantityNeverNegative() throws QuantityException {
        for (int i = 0; i < 28; i++) {
            StockSectionBambou.ROSES.subtractToQuantity();
        }

        assertEquals(0, StockSectionBambou.ROSES.getQuantity());

        assertThrows(QuantityException.class,
                () -> StockSectionBambou.ROSES.subtractToQuantity());

        assertEquals(0, StockSectionBambou.ROSES.getQuantity());
    }

    @Test
    void testQuantityNeverExceedsMax() {
        assertEquals(30, StockSectionBambou.JAUNES.getQuantity());

        assertThrows(QuantityException.class,
                () -> StockSectionBambou.JAUNES.addToQuantity());

        assertEquals(30, StockSectionBambou.JAUNES.getQuantity());
    }
}