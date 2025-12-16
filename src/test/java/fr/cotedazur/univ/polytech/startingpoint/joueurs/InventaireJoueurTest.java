package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.objectifs.ObjectifPoseur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventaireJoueurTest {

    @Test
    void testAjoutObjectifEtPoints() {
        InventaireJoueur inventaire = new InventaireJoueur();
        assertEquals(0, inventaire.getScore());
        assertEquals(0, inventaire.getObjectifs().size());

        inventaire.ajouterObjectif(new ObjectifPoseur(10, Couleur.VERT, 2));
        assertEquals(1, inventaire.getObjectifs().size());

        inventaire.ajouterPoints(50);
        assertEquals(50, inventaire.getScore());
    }
}