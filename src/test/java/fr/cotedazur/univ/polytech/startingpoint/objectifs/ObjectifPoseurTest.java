package fr.cotedazur.univ.polytech.startingpoint.objectifs;

import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ObjectifPoseurTest {
    Plateau plateau;
    ObjectifPoseur objectifVert;

    @BeforeEach
    void setUp() {
        plateau = new Plateau();
        // Objectif : Avoir 3 tuiles VERTES
        objectifVert = new ObjectifPoseur(10, Couleur.VERT, 3);
    }

    @Test
    void testObjectifNonValideAuDebut() {
        assertFalse(objectifVert.valider(plateau), "L'objectif ne doit pas être valide sur un plateau vide");
    }

    @Test
    void testObjectifValide() {
        // On pose 2 Vertes -> Pas assez
        plateau.placerParcelle(new Parcelle(Couleur.VERT), new Position(1, -1, 0));
        plateau.placerParcelle(new Parcelle(Couleur.VERT), new Position(1, 0, -1));
        assertFalse(objectifVert.valider(plateau));

        // On pose la 3ème Verte -> C'est bon !
        plateau.placerParcelle(new Parcelle(Couleur.VERT), new Position(0, 1, -1));
        assertTrue(objectifVert.valider(plateau), "L'objectif devrait être validé avec 3 tuiles vertes");
    }

    @Test
    void testObjectifMélangeCouleurs() {
        // On pose 2 Vertes et 10 Roses -> Ne doit pas valider l'objectif VERT
        plateau.placerParcelle(new Parcelle(Couleur.VERT), new Position(1, -1, 0));
        plateau.placerParcelle(new Parcelle(Couleur.VERT), new Position(1, 0, -1));
        plateau.placerParcelle(new Parcelle(Couleur.ROSE), new Position(0, 1, -1));

        assertFalse(objectifVert.valider(plateau));
    }
}