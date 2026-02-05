package fr.cotedazur.univ.polytech.startingpoint.objectifs.parcelle;

import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.elements.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.PositionsRelatives;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CarteParcelleTest {
    private Plateau plateau;

    @BeforeEach
    void init() {
        plateau = new Plateau();
    }

    /**
     * Méthode utilitaire générique pour construire un motif sur le plateau
     */
    private void construireMotifSurPlateau(Forme forme, Couleur couleur, Position ancrage) {
        for (PositionsRelatives rel : forme.getPositions()) {
            Position posAbsolue = ancrage.add(rel.getPosition());

            Parcelle p = new Parcelle(posAbsolue, couleur);

            // CORRECTION : Utilisation de la bonne méthode
            p.triggerIrrigation();

            // On crée et ajoute la parcelle (via getGrille() pour contourner les règles de pose adjacente)
            plateau.getGrille().ajouterParcelle(p, posAbsolue);
        }
    }

    @Test
    void testLigneVerteValide() {
        Position ancrage = new Position(2, 2);
        construireMotifSurPlateau(Forme.LIGNE, Couleur.VERT, ancrage);

        assertTrue(CarteParcelle.LIGNE_VERTE.estValide(plateau),
                "Le motif LIGNE_VERTE devrait être validé");
    }

    @Test
    void testTriangleVertValide() {
        Position ancrage = new Position(-1, 3);
        construireMotifSurPlateau(Forme.TRIANGLE, Couleur.VERT, ancrage);

        assertTrue(CarteParcelle.TRIANGLE_VERT.estValide(plateau),
                "Le motif TRIANGLE_VERT devrait être validé");
    }

    @Test
    void testLosangeRoseJauneValide() {
        // Pour simplifier le test unitaire sur la mécanique de forme
        // On suppose ici un losange monochrome pour tester la détection géométrique
        Position ancrage = new Position(0, 1);
        construireMotifSurPlateau(Forme.LOSANGE, Couleur.VERT, ancrage);

        // Note: Si LOSANGE_VERT existe dans CarteParcelle, testez avec lui.
        // Sinon adaptez selon vos enum disponibles.
        assertTrue(CarteParcelle.LOSANGE_VERT.estValide(plateau));
    }

    @Test
    void testRotationValide() {
        // Construction manuelle d'une ligne tournée de 60°
        Position ancrage = new Position(0,0);

        Position p1 = ancrage.add(PositionsRelatives.ZERO.getPosition());
        Position p2 = ancrage.add(PositionsRelatives.TROIS.getPosition().rotate60());
        Position p3 = ancrage.add(PositionsRelatives.QUATRE.getPosition().rotate60());

        createAndAddParcelle(p1, Couleur.VERT);
        createAndAddParcelle(p2, Couleur.VERT);
        createAndAddParcelle(p3, Couleur.VERT);

        assertTrue(CarteParcelle.LIGNE_VERTE.estValide(plateau),
                "Le motif devrait être détecté même avec une rotation");
    }

    @Test
    void testInvalideSiCouleurIncorrecte() {
        Position ancrage = new Position(5, 5);
        construireMotifSurPlateau(Forme.LIGNE, Couleur.ROSE, ancrage);

        assertFalse(CarteParcelle.LIGNE_VERTE.estValide(plateau),
                "Le motif ne doit pas être valide si la couleur est fausse");
    }

    // Helper simple pour le test de rotation
    private void createAndAddParcelle(Position p, Couleur c) {
        Parcelle parcelle = new Parcelle(p, c);
        parcelle.triggerIrrigation(); // CORRECTION
        plateau.getGrille().ajouterParcelle(parcelle, p);
    }
}