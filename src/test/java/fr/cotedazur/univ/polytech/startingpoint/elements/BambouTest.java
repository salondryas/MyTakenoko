package fr.cotedazur.univ.polytech.startingpoint.elements;

import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.Bambou;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BambouTest {

    private Bambou bambouVert;

    @BeforeEach
    void setUp() {
        bambouVert = new Bambou(Couleur.VERT);
    }

    /*
     * @Test
     * void testInitialisation() {
     * assertEquals(1, bambouVert.getNumberOfSections(),
     * "Un bambou doit commencer avec 1 section.");
     * assertEquals(Couleur.VERT, bambouVert.getSectionColour());
     * }
     */

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
    @DisplayName("Bambou initialisé avec 0 section par défaut")
    void testBambouInitial() {
        Bambou bambou = new Bambou(Couleur.VERT);

        assertEquals(0, bambou.getNumberOfSections());
    }

    @Test
    @DisplayName("faireApparaitre passe de 0 à 1 section")
    void testFaireApparaitre() {
        Bambou bambou = new Bambou(Couleur.VERT);

        boolean resultat = bambou.faireApparaitre();

        assertTrue(resultat);
        assertEquals(1, bambou.getNumberOfSections());
    }

    @Test
    @DisplayName("faireApparaitre ne fait rien si bambou déjà présent")
    void testFaireApparaitreDejaPresent() {
        Bambou bambou = new Bambou(Couleur.VERT);
        bambou.faireApparaitre(); // 0 -> 1

        boolean resultat = bambou.faireApparaitre(); // Tentative 1 -> 1

        assertFalse(resultat);
        assertEquals(1, bambou.getNumberOfSections());
    }

    @Test
    @DisplayName("faireApparaitre ne fait rien après croissance")
    void testFaireApparaitreApresCroissance() {
        Bambou bambou = new Bambou(Couleur.VERT);
        bambou.faireApparaitre(); // 0 -> 1
        bambou.croissance(); // 1 -> 2

        boolean resultat = bambou.faireApparaitre();

        assertFalse(resultat);
        assertEquals(2, bambou.getNumberOfSections());
    }
}
