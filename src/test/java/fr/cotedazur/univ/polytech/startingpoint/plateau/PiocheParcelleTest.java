package fr.cotedazur.univ.polytech.startingpoint.plateau;

import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static fr.cotedazur.univ.polytech.startingpoint.plateau.PiocheParcelle.NOMBRE_PARCELLES_INITIAL;
import static org.junit.jupiter.api.Assertions.*;

public class PiocheParcelleTest {
    PiocheParcelle piocheParcelle;

    @BeforeEach
    void init() {
        piocheParcelle = new PiocheParcelle();
    }

    @Test
    void testPiocheParcelles() {
        // Voir si le nombre de cartes dans la pioche est cohérent
        assertEquals(NOMBRE_PARCELLES_INITIAL, piocheParcelle.getSize());
        piocheParcelle.piocherParcelle();
        assertEquals(NOMBRE_PARCELLES_INITIAL - 1, piocheParcelle.getSize()); // On regarde si la pioche a été réduite
                                                                              // après qu'un joueur ait retiré une
                                                                              // parcelle
        // On tire jusqu'à ne plus rien avoir dans la pioche
        for (int i = 0; i < NOMBRE_PARCELLES_INITIAL - 1; i++) {
            piocheParcelle.piocherParcelle();
        }
        // La pioche est vide !
        assertEquals(0, piocheParcelle.getSize());
    }

    // ========== Tests pour les aménagements ==========

    @Test
    @DisplayName("Parcelles with amenagements are properly marked")
    void testParcellesWithAmenagementAreMarked() {
        int amenageesCount = 0;
        int totalParcelles = piocheParcelle.getSize();

        for (int i = 0; i < totalParcelles; i++) {
            Parcelle p = piocheParcelle.piocherParcelle();
            if (p != null && p.getIsAmenagee()) {
                amenageesCount++;
            }
        }

        // Total amenagements: 2+2+2 (green) + 1+1+1 (yellow) + 1+1+1 (pink) = 12
        assertEquals(12, amenageesCount,
                "Should have exactly 12 parcelles with amenagements");
    }

    @Test
    @DisplayName("Non-amenaged parcelles count is correct")
    void testNonAmenageParcellesCount() {
        int nonAmenagees = 0;
        int totalParcelles = piocheParcelle.getSize();

        for (int i = 0; i < totalParcelles; i++) {
            Parcelle p = piocheParcelle.piocherParcelle();
            if (p != null && !p.getIsAmenagee()) {
                nonAmenagees++;
            }
        }

        // Total non-amenaged: 5 (green) + 6 (yellow) + 4 (pink) = 15
        assertEquals(15, nonAmenagees,
                "Should have exactly 15 parcelles without amenagements");
    }

    @Test
    @DisplayName("Green parcelles distribution: 5 plain, 6 with amenagements")
    void testGreenParcellesDistribution() {
        int greenTotal = 0;
        int greenAmenagees = 0;
        int greenNonAmenagees = 0;

        for (int i = 0; i < PiocheParcelle.NOMBRE_PARCELLES_INITIAL; i++) {
            Parcelle p = piocheParcelle.piocherParcelle();
            if (p != null && p.getCouleur() == Couleur.VERT) {
                greenTotal++;
                if (p.getIsAmenagee()) {
                    greenAmenagees++;
                } else {
                    greenNonAmenagees++;
                }
            }
        }

        assertEquals(PiocheParcelle.NOMBRE_PARCELLES_INITIAL_VERTES, greenTotal,
                "Should have 11 green parcelles");
        assertEquals(PiocheParcelle.NOMBRE_PARCELLES_NOT_AMENAGEES_GREEN, greenNonAmenagees,
                "Should have 5 non-amenaged green parcelles");
        assertEquals(6, greenAmenagees,
                "Should have 6 amenaged green parcelles (2 bassin + 2 engrais + 2 enclos)");
    }

    @Test
    @DisplayName("Yellow parcelles distribution: 6 plain, 3 with amenagements")
    void testYellowParcellesDistribution() {
        int yellowTotal = 0;
        int yellowAmenagees = 0;
        int yellowNonAmenagees = 0;

        for (int i = 0; i < PiocheParcelle.NOMBRE_PARCELLES_INITIAL; i++) {
            Parcelle p = piocheParcelle.piocherParcelle();
            if (p != null && p.getCouleur() == Couleur.JAUNE) {
                yellowTotal++;
                if (p.getIsAmenagee()) {
                    yellowAmenagees++;
                } else {
                    yellowNonAmenagees++;
                }
            }
        }

        assertEquals(PiocheParcelle.NOMBRE_PARCELLES_INITIAL_JAUNES, yellowTotal,
                "Should have 9 yellow parcelles");
        assertEquals(PiocheParcelle.NOMBRE_PARCELLES_NOT_AMENAGEES_YELLOW, yellowNonAmenagees,
                "Should have 6 non-amenaged yellow parcelles");
        assertEquals(3, yellowAmenagees,
                "Should have 3 amenaged yellow parcelles (1 bassin + 1 engrais + 1 enclos)");
    }

    @Test
    @DisplayName("Pink parcelles distribution: 4 plain, 3 with amenagements")
    void testPinkParcellesDistribution() {
        int pinkTotal = 0;
        int pinkAmenagees = 0;
        int pinkNonAmenagees = 0;

        for (int i = 0; i < PiocheParcelle.NOMBRE_PARCELLES_INITIAL; i++) {
            Parcelle p = piocheParcelle.piocherParcelle();
            if (p != null && p.getCouleur() == Couleur.ROSE) {
                pinkTotal++;
                if (p.getIsAmenagee()) {
                    pinkAmenagees++;
                } else {
                    pinkNonAmenagees++;
                }
            }
        }

        assertEquals(PiocheParcelle.NOMBRE_PARCELLES_INITIAL_ROSES, pinkTotal,
                "Should have 7 pink parcelles");
        assertEquals(PiocheParcelle.NOMBRE_PARCELLES_NOT_AMENAGEES_PINK, pinkNonAmenagees,
                "Should have 4 non-amenaged pink parcelles");
        assertEquals(3, pinkAmenagees,
                "Should have 3 amenaged pink parcelles (1 bassin + 1 engrais + 1 enclos)");
    }

    @Test
    @DisplayName("All parcelles are properly initialized")
    void testAllParcellesInitialization() {
        for (int i = 0; i < PiocheParcelle.NOMBRE_PARCELLES_INITIAL; i++) {
            Parcelle p = piocheParcelle.piocherParcelle();
            assertNotNull(p, "Each parcelle should not be null");
            assertNotNull(p.getCouleur(), "Each parcelle should have a color");
            assertNotNull(p.getBambou(), "Each parcelle should have a bambou");
        }
    }

    @Test
    @DisplayName("Total amenagement count matches specification")
    void testTotalAmenagementCount() {
        int amenaged = 0;
        int nonAmenaged = 0;

        for (int i = 0; i < PiocheParcelle.NOMBRE_PARCELLES_INITIAL; i++) {
            Parcelle p = piocheParcelle.piocherParcelle();
            if (p.getIsAmenagee()) {
                amenaged++;
            } else {
                nonAmenaged++;
            }
        }

        assertEquals(12, amenaged,
                "Should have 12 amenaged parcelles total (6 green + 3 yellow + 3 pink)");
        assertEquals(15, nonAmenaged,
                "Should have 15 non-amenaged parcelles total (5 green + 6 yellow + 4 pink)");
        assertEquals(27, amenaged + nonAmenaged,
                "Total should be 27 parcelles");
    }

    @Test
    @DisplayName("Parcelles color distribution is correct")
    void testParcellesColorDistribution() {
        int greenCount = 0;
        int yellowCount = 0;
        int pinkCount = 0;

        for (int i = 0; i < PiocheParcelle.NOMBRE_PARCELLES_INITIAL; i++) {
            Parcelle p = piocheParcelle.piocherParcelle();
            switch (p.getCouleur()) {
                case VERT:
                    greenCount++;
                    break;
                case JAUNE:
                    yellowCount++;
                    break;
                case ROSE:
                    pinkCount++;
                    break;
                default:
                    fail("Unexpected color in pioche: " + p.getCouleur());
            }
        }

        assertEquals(PiocheParcelle.NOMBRE_PARCELLES_INITIAL_VERTES, greenCount,
                "Should have 11 green parcelles");
        assertEquals(PiocheParcelle.NOMBRE_PARCELLES_INITIAL_JAUNES, yellowCount,
                "Should have 9 yellow parcelles");
        assertEquals(PiocheParcelle.NOMBRE_PARCELLES_INITIAL_ROSES, pinkCount,
                "Should have 7 pink parcelles");
    }

}
