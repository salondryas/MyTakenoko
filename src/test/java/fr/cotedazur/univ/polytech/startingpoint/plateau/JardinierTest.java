package fr.cotedazur.univ.polytech.startingpoint.plateau;

import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JardinierTest {
    Jardinier jardinier;
    Plateau plateau;

    @BeforeEach
    void setUp() {
        jardinier = new Jardinier();
        plateau = new Plateau();
    }

    @Test
    void deplacerJardinier() {
        Position pos = new Position(1, 0);
        jardinier.setPosition(pos);
        assertEquals(pos, jardinier.getPosition());
    }

    @Test
    void verifierPousseBambou() {
        Position pos = new Position(0, 1);
        Parcelle parcelle = new Parcelle(pos, Couleur.VERT);

        // Le placement à côté de l'étang irrigue la parcelle -> 1ère section de bambou pousse auto
        plateau.placerParcelle(parcelle, pos); // +1 section (car irriguée auto)

        jardinier.setPosition(pos);
        parcelle.pousserBambou(); // +2ème section

        // CORRECTION : On attend 2 sections
        assertEquals(2, parcelle.getNbSectionsSurParcelle());
    }
}