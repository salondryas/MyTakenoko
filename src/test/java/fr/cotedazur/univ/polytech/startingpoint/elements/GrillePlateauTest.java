package fr.cotedazur.univ.polytech.startingpoint.elements;

import fr.cotedazur.univ.polytech.startingpoint.elements.plateau.GrillePlateau;
import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GrillePlateauTest {

    @Test
    void testAjoutEtRecuperation() {
        GrillePlateau grille = new GrillePlateau();
        Position pos = new Position(1, -1);
        Parcelle p = new Parcelle(pos, Couleur.ROSE);

        grille.ajouterParcelle(p, pos);

        assertEquals(p, grille.getParcelle(pos));
        assertTrue(grille.getPositionsOccupees().contains(pos));
    }

    @Test
    void testOriginePresenteParDefaut() {
        GrillePlateau grille = new GrillePlateau();
        assertNotNull(grille.getParcelle(GrillePlateau.POSITION_ORIGINE));
        assertEquals(Couleur.AUCUNE, grille.getParcelle(GrillePlateau.POSITION_ORIGINE).getCouleur());
    }
}