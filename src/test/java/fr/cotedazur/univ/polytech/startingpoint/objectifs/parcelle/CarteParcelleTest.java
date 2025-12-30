package fr.cotedazur.univ.polytech.startingpoint.objectifs.parcelle;

import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.PositionsRelatives;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.cotedazur.univ.polytech.startingpoint.utilitaires.PositionsRelatives.*;
import static org.junit.jupiter.api.Assertions.*;

class CarteParcelleTest {
    private Plateau plateau;
    private Position centreRelatif;

    @BeforeEach
    void init() {
        plateau = new Plateau();
    }

    /**
     * Test ligne verte
     */
    @Test
    void testObjectifLigneVerteValide() {
        // Bonne forme, bonne couleur
        CarteParcelle objectif = CarteParcelle.LIGNE_VERTE;

        centreRelatif = new Position(0,-1); // En haut à gauche de l'origine

        // On pose trois parcelles autour du centre
        placerParcelleRelative(centreRelatif, TROIS, Couleur.VERT);
        placerParcelleRelative(centreRelatif, ZERO, Couleur.VERT);
        placerParcelleRelative(centreRelatif, QUATRE, Couleur.VERT);

        assertTrue(objectif.estValide(plateau),
                "L'objectif devrait être validé, on a la bonne forme (sans rotation) et la bonne couleur");
    }

    /**
     * Test ligne verte
     * Bonne forme, mais mauvaise couleur d'une parcelle
     * (Rose au lieu de Vert)
     */
    @Test
    void testObjectifLigneVerteInvalideMauvaiseCouleur() {
        CarteParcelle objectif = CarteParcelle.LIGNE_VERTE;

        centreRelatif = new Position(0,-1); // En haut à gauche de l'origine

        // On pose trois parcelles autour de la position en haut à gauche
        placerParcelleRelative(centreRelatif, TROIS, Couleur.ROSE); // Parcelle Rose et non verte
        placerParcelleRelative(centreRelatif, ZERO, Couleur.VERT);
        placerParcelleRelative(centreRelatif, QUATRE, Couleur.VERT);

        assertFalse(objectif.estValide(plateau),
                "L'objectif ne devrait pas être validé si une des parcelles n'est pas de la bonne couleur.");
    }

    /**
     * Test ligne verte
     * Mais il manque une parcelle pour finir la ligne
     */
    @Test
    void testLigneVerteInvalideManqueUneParcelle() {
        CarteParcelle objectif = CarteParcelle.LIGNE_VERTE;

        centreRelatif = new Position(0,-1); // En haut à gauche de l'origine

        // On pose deux parcelles au lieu de trois
        placerParcelleRelative(centreRelatif, TROIS, Couleur.VERT);
        placerParcelleRelative(centreRelatif, ZERO, Couleur.VERT);

        assertFalse(objectif.estValide(plateau),
                "L'objectif ne devrait pas être validé si le motif est incomplet.");
    }

    /**
     * Test ligne verte
     * Mais parcelles tournées de 60 degrés par rapport au motif original.
     */
    @Test
    void testLigneVerteValideAvecRotation() {
        CarteParcelle objectif = CarteParcelle.LIGNE_VERTE;

        centreRelatif = new Position(1,0); // À droite de l'origine

        // On place les parcelles vertes sur ces positions tournées
        placerParcelleRelative(centreRelatif, UN, Couleur.VERT);
        placerParcelleRelative(centreRelatif, ZERO, Couleur.VERT);
        placerParcelleRelative(centreRelatif, SIX, Couleur.VERT);

        assertTrue(objectif.estValide(plateau),
                "L'objectif devrait être validé même si le motif est tourné de 60 degrés sur le plateau.");
    }

    /**
     * Test ligne verte
     * Mais on déplace le motif loin du centre (5, -5)
     */
    @Test
    void testLigneVerteValideNimporteOuSurPlateau() {
        CarteParcelle objectif = CarteParcelle.LIGNE_VERTE;

        centreRelatif = new Position(5, -5, 0); // La position lointaine à tester

        placerParcelleRelative(centreRelatif, TROIS, Couleur.VERT);
        placerParcelleRelative(centreRelatif, ZERO, Couleur.VERT);
        placerParcelleRelative(centreRelatif, QUATRE, Couleur.VERT);

        assertTrue(objectif.estValide(plateau),
                "L'objectif devrait être détecté quel que soit l'endroit où il est posé sur le plateau.");
    }

    /**
     * Test du Triangle Rose (On a jamais trop de tests)
     */
    @Test
    void testTriangleRoseValide() {
        CarteParcelle objectif = CarteParcelle.TRIANGLE_ROSE;

        centreRelatif = new Position( 1,0 ); // À droite de l'origine

        // Triangle Rose
        placerParcelleRelative(centreRelatif, ZERO, Couleur.ROSE);
        placerParcelleRelative(centreRelatif, UN, Couleur.ROSE);
        placerParcelleRelative(centreRelatif, DEUX, Couleur.ROSE);

        assertTrue(objectif.estValide(plateau), "On a la bonne forme (TRIANGLE) et la bonne couleur (VERT), c'est pas normal que tout soit cassé >:(");
    }

    /**
     * Teste la carte de couleur mixte LOSANGE_ROSE_JAUNE
     * Définition supposée : ROSE en (2, 4) et JAUNE en (0, 6)
     */
    @Test
    void testLosangeRoseJauneValide() {
        CarteParcelle objectif = CarteParcelle.LOSANGE_ROSE_JAUNE;

        centreRelatif = new Position( 2,0 ); // À droite de l'origine

        // 1. On place les parcelles ROSE aux positions 2 et 4
        placerParcelleRelative(centreRelatif, DEUX, Couleur.ROSE);
        placerParcelleRelative(centreRelatif, QUATRE, Couleur.ROSE);

        // 2. On place les parcelles JAUNE aux positions 0 et 6
        placerParcelleRelative(centreRelatif, ZERO, Couleur.JAUNE);
        placerParcelleRelative(centreRelatif, SIX, Couleur.JAUNE);

        assertTrue(objectif.estValide(plateau),
                "L'objectif devrait être validé : la forme et les couleurs correspondent parfaitement.");
    }

    /**
     * Teste la carte de couleur mixte LOSANGE_ROSE_JAUNE
     * Tourné de 60° : position relative supposée = ROSE en (2, 4) et JAUNE en (0, 6)
     */
    @Test
    void testLosangeVertJauneValide() {
        CarteParcelle objectif = CarteParcelle.LOSANGE_VERT_JAUNE;

        centreRelatif = new Position( -1,1 ); // En bas à gauche

        // On place les parcelles VERTEs aux positions relatives 4 et 6
        placerParcelleRelative(centreRelatif, QUATRE, Couleur.VERT);
        placerParcelleRelative(centreRelatif, SIX, Couleur.VERT);

        // On place les parcelles JAUNEs aux positions relatives 0 et 5
        placerParcelleRelative(centreRelatif, ZERO, Couleur.JAUNE);
        placerParcelleRelative(centreRelatif, CINQ, Couleur.JAUNE);

        assertTrue(objectif.estValide(plateau),
                "L'objectif devrait être validé : la forme et les couleurs correspondent parfaitement.");
    }

    /**
     * Teste cas des couleurs inversées
     * Les couleurs et les positions sont bonnes (2 ROSES, 2JAUNES), mais elles sont inversées.
     */
    @Test
    void testLosangeRoseJauneInvalideSiCouleursInversees() {
        CarteParcelle objectif = CarteParcelle.LOSANGE_ROSE_JAUNE;

        centreRelatif = new Position( 1,0 ); // À droite de l'origine

        // On met du JAUNE là où il faut du ROSE
        placerParcelleRelative(centreRelatif, DEUX, Couleur.JAUNE);
        placerParcelleRelative(centreRelatif, QUATRE, Couleur.JAUNE);

        // Et du ROSE là où il faut du JAUNE
        placerParcelleRelative(centreRelatif, ZERO, Couleur.ROSE);
        placerParcelleRelative(centreRelatif, SIX, Couleur.ROSE);

        assertFalse(objectif.estValide(plateau),
                "L'objectif ne doit PAS être validé si les couleurs sont inversées, même si la forme géométrique est la même.");
    }

    // Méthode utilitaire pour placer les parcelles autour d'une autre.
    private void placerParcelleRelative(Position positionAncrage, PositionsRelatives pvo, Couleur c) {
        Position posAbsolue = positionAncrage.add(pvo.getPosition());
        plateau.placerParcelle(new Parcelle(posAbsolue, c), posAbsolue);
    }
}