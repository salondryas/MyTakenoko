package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.objectifs.ObjectifPoseur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventaireJoueurTest {
    InventaireJoueur inventaire;

    @BeforeEach
    void setUp() {
        inventaire = new InventaireJoueur();
    }

    @Test
    void testAjouterEtRecupererObjectif() {
        // Correction : Constructeur à 2 arguments (nombre requis, couleur)
        ObjectifPoseur obj = new ObjectifPoseur(3, Couleur.ROSE);

        inventaire.ajouterObjectif(obj);

        assertEquals(1, inventaire.getObjectifs().size());
        assertEquals(obj, inventaire.getObjectifs().get(0));
    }

    @Test
    void testScore() {
        assertEquals(0, inventaire.getScore());
        inventaire.ajouterPoints(10);
        assertEquals(10, inventaire.getScore());
    }
}