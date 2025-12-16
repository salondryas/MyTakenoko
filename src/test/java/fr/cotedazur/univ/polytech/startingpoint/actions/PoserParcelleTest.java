package fr.cotedazur.univ.polytech.startingpoint.actions;

import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PoserParcelleTest {

    @Test
    void testAppliquer() {
        Plateau plateau = new Plateau();
        Position pos = new Position(1, -1, 0);
        Parcelle parcelle = new Parcelle(pos, Couleur.ROSE);

        // On crée l'action
        Action action = new PoserParcelle(parcelle, pos);

        // Avant l'action : la case est vide (ou contient null/rien)
        assertNull(plateau.getParcelle(pos));

        // On applique l'action
        action.appliquer(plateau);

        // Après l'action : la case doit contenir la parcelle
        assertEquals(parcelle, plateau.getParcelle(pos), "L'action aurait dû poser la parcelle sur le plateau");
    }
}