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

    // --- NOUVEAUX TESTS A AJOUTER ---

    @Test
    void testGestionBambous() {
        // Au début c'est vide
        assertTrue(inventaire.getBambous().isEmpty());

        // On ajoute un bambou rose
        inventaire.ajouterBambou(Couleur.ROSE);
        assertEquals(1, inventaire.getBambous().size());
        assertTrue(inventaire.getBambous().contains(Couleur.ROSE));

        // On le retire
        boolean retraitReussi = inventaire.retirerBambou(Couleur.ROSE);
        assertTrue(retraitReussi);
        assertTrue(inventaire.getBambous().isEmpty());
    }

    @Test
    void testCompteurObjectifsValides() {
        assertEquals(0, inventaire.getNombreObjectifsValides());

        inventaire.incrementerObjectifsValides();
        inventaire.incrementerObjectifsValides();

        assertEquals(2, inventaire.getNombreObjectifsValides());
    }
}