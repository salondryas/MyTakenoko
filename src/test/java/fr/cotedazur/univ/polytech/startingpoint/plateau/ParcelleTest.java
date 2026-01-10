package fr.cotedazur.univ.polytech.startingpoint.plateau;

import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParcelleTest {
    public Parcelle origine;
    public Parcelle origine2;

    @BeforeEach
    void init() {
        origine = new Parcelle(new Position(0, 0), Couleur.AUCUNE);
        origine2 = new Parcelle(new Position(0, 0), Couleur.AUCUNE);
    }

    @Test
    void testToString() {
        assertEquals("AUCUNE : (0, 0, 0)", origine.toString(), "Le toString de l'origine est incorrect");
    }

    @Test
    void testEquals() {
        assertTrue(origine.equals(origine2));
    }

    @Test
    @DisplayName("Parcelle non irriguée par défaut")
    void testParcelleNonIrrigueeParDefaut() {
        Parcelle parcelle = new Parcelle(Couleur.VERT);

        assertFalse(parcelle.estIrriguee());
        assertEquals(0, parcelle.getNbSectionsSurParcelle());
    }

    @Test
    @DisplayName("triggerIrrigation irrigue la parcelle et fait apparaître le bambou")
    void testTriggerIrrigation() {
        Parcelle parcelle = new Parcelle(new Position(1, 0), Couleur.JAUNE);

        parcelle.triggerIrrigation();

        assertTrue(parcelle.estIrriguee());
        assertEquals(1, parcelle.getNbSectionsSurParcelle());
    }

    @Test
    @DisplayName("triggerIrrigation ne fait rien si déjà irriguée")
    void testTriggerIrrigationDejaIrriguee() {
        Parcelle parcelle = new Parcelle(new Position(1, 0), Couleur.ROSE);

        parcelle.triggerIrrigation();
        parcelle.getBambou().croissance(); // 1 -> 2 sections
        parcelle.triggerIrrigation(); // Ne doit rien faire

        assertTrue(parcelle.estIrriguee());
        assertEquals(2, parcelle.getNbSectionsSurParcelle()); // Toujours 2
    }
}