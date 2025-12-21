package fr.cotedazur.univ.polytech.startingpoint.plateau;

import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BambouTest {

    private Bambou bambouVert;

    @BeforeEach
    void setUp() {
        bambouVert = new Bambou(Couleur.VERT);
    }

    @Test
    void testInitialisation() {
        assertEquals(1, bambouVert.getNumberOfSections(), "Un bambou doit commencer avec 1 section.");
        assertEquals(Couleur.VERT, bambouVert.getSectionColour());
    }

    @Test
    void testCroissanceLimitee() {
        bambouVert.croissance(); // 2 sections
        bambouVert.croissance(); // 3 sections
        bambouVert.croissance(); // 4 sections
        bambouVert.croissance(); // Tentative de 5ème section

        assertEquals(4, bambouVert.getNumberOfSections(), "Un bambou ne peut pas dépasser 4 sections.");
    }

    @Test
    void testRetirerSection() {
        bambouVert.retirerSection();
        assertEquals(0, bambouVert.getNumberOfSections(), "Le bambou devrait avoir 0 section.");

        bambouVert.retirerSection(); // Tentative sous 0
        assertEquals(0, bambouVert.getNumberOfSections(), "Le nombre de sections ne peut pas être négatif.");
    }

    @Test
    void testSectionQuantityUpdate() {
        int stockInitial = Bambou.number_of_green_sections;
        bambouVert.sectionQuantityUpdate();

        assertEquals(stockInitial - 1, Bambou.number_of_green_sections,
                "Le stock statique de sections vertes doit diminuer de 1.");
    }
}