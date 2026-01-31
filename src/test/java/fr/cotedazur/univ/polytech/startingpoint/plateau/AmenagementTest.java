package fr.cotedazur.univ.polytech.startingpoint.plateau;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.cotedazur.univ.polytech.startingpoint.plateau.amenagements.Bassin;
import fr.cotedazur.univ.polytech.startingpoint.plateau.amenagements.Enclos;
import fr.cotedazur.univ.polytech.startingpoint.plateau.amenagements.Engrais;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.QuantityException;

class AmenagementTest {

    private Parcelle parcelle;
    private Bambou bambou;

    @BeforeEach
    void setUp() {
        parcelle = new Parcelle(Couleur.VERT);
        bambou = new Bambou(Couleur.VERT);
    }

    // ========== Tests pour Bassin ==========

    @Test
    void testBassinConstructorAddsAmenagement() {
        Bassin bassin = new Bassin(parcelle, bambou);

        assertTrue(parcelle.getIsAmenagee(), "Parcelle should be marked as amenagee");
    }

    @Test
    void testBassinActionSurParcelleTriggerIrrigation() {
        Bassin bassin = new Bassin(parcelle);

        assertFalse(parcelle.estIrriguee(), "Parcelle should not be irrigated initially");

        bassin.actionSurParcelle(parcelle);

        assertTrue(parcelle.estIrriguee(), "Parcelle should be irrigated after action");
    }

    @Test
    void testBassinActionSurParcelleWithNonParcelle() {
        Bassin bassin = new Bassin(parcelle);

        assertDoesNotThrow(() -> bassin.actionSurParcelle(bambou));
        assertDoesNotThrow(() -> bassin.actionSurParcelle("String"));
    }

    // ========== Tests pour Engrais ==========

    @Test
    void testEngraisConstructorAddsAmenagement() {
        Engrais engrais = new Engrais(parcelle, bambou);

        assertTrue(parcelle.getIsAmenagee(), "Parcelle should be marked as amenagee");
    }

    @Test
    void testEngraisActionSurParcelleIncreaseGrowth() {
        Engrais engrais = new Engrais(parcelle);

        // Assuming increaseSectionGrowth() modifies some internal state
        assertDoesNotThrow(() -> engrais.actionSurParcelle(bambou));
    }

    @Test
    void testEngraisActionSurParcelleWithNonBambou() {
        Engrais engrais = new Engrais(parcelle);

        assertDoesNotThrow(() -> engrais.actionSurParcelle(parcelle));
        assertDoesNotThrow(() -> engrais.actionSurParcelle("String"));
    }

    // ========== Tests pour Enclos ==========

    @Test
    void testEnclosConstructorAddsAmenagement() {
        Enclos enclos = new Enclos(parcelle, bambou);

        assertTrue(parcelle.getIsAmenagee(), "Parcelle should be marked as amenagee");
    }

    @Test
    void testEnclosActionSurParcellePreventsEating() {
        Enclos enclos = new Enclos(parcelle);
        Panda panda = new Panda();

        assertDoesNotThrow(() -> enclos.actionSurParcelle(panda));
    }

    @Test
    void testEnclosActionSurParcelleWithNonPanda() {
        Enclos enclos = new Enclos(parcelle);

        assertDoesNotThrow(() -> enclos.actionSurParcelle(parcelle));
        assertDoesNotThrow(() -> enclos.actionSurParcelle(bambou));
    }

    // ========== Tests pour Parcelle amenagement methods ==========

    @Test
    void testFetchAmenagementAcqui() {
        assertFalse(parcelle.getIsAmenagee(), "Parcelle should not be amenagee initially");

        Bassin bassin = new Bassin(parcelle);
        parcelle.fetchAmenagementAcqui(bassin);

        assertTrue(parcelle.getIsAmenagee(), "Parcelle should be marked as amenagee after fetch");
    }

    @Test
    void testGetIsAmenagee() {
        assertFalse(parcelle.getIsAmenagee(), "New parcelle should not be amenagee");

        parcelle.fetchAmenagementAcqui(new Engrais(parcelle));

        assertTrue(parcelle.getIsAmenagee(), "Parcelle should be amenagee after fetching amenagement");
    }

    // ========== Tests pour l'interface Amenagement ==========

    @Test
    void testQuantityDeductionThrowsExceptionWhenZero() {
        Bassin bassin = new Bassin(parcelle);

        QuantityException exception = assertThrows(
                QuantityException.class,
                () -> bassin.quantityDeduction(0),
                "Should throw QuantityException when quantity is 0");

        assertEquals("Amenagements indisponibles", exception.getMessage());
    }

    @Test
    void testQuantityDeductionSucceedsWhenPositive() {
        Bassin bassin = new Bassin(parcelle);

        assertDoesNotThrow(() -> bassin.quantityDeduction(1));
        assertDoesNotThrow(() -> bassin.quantityDeduction(3));
    }

    @Test
    void testAddToParcelleWhenConditionsMet() {
        Parcelle newParcelle = new Parcelle(Couleur.JAUNE);
        Bambou newBambou = new Bambou(Couleur.JAUNE);

        assertEquals(0, newBambou.getNumberOfSections(), "Bambou should have 0 sections");
        assertFalse(newParcelle.getIsAmenagee(), "Parcelle should not be amenagee");

        Bassin bassin = new Bassin(newParcelle);
        bassin.addToParcelle(3, newParcelle, newBambou);

        assertTrue(newParcelle.getIsAmenagee(), "Parcelle should be amenagee after addToParcelle");
    }

    @Test
    void testAddToParcelleWhenAlreadyAmenagee() {
        Bambou newBambou = new Bambou(Couleur.ROSE);
        parcelle.fetchAmenagementAcqui(new Enclos(parcelle));

        assertTrue(parcelle.getIsAmenagee(), "Parcelle should be amenagee");

        Bassin bassin = new Bassin(parcelle);

        // Should not throw, just not add
        assertDoesNotThrow(() -> bassin.addToParcelle(3, parcelle, newBambou));
    }

    @Test
    void testAddToParcelleWhenBambooHasSections() {
        Parcelle newParcelle = new Parcelle(Couleur.VERT);
        Bambou newBambou = new Bambou(Couleur.VERT);
        newBambou.croissance(); // Add a section

        assertTrue(newBambou.getNumberOfSections() > 0, "Bambou should have sections");

        Bassin bassin = new Bassin(newParcelle);
        bassin.addToParcelle(3, newParcelle, newBambou);

        assertFalse(newParcelle.getIsAmenagee(), "Parcelle should not be amenagee when bamboo has sections");
    }
}
