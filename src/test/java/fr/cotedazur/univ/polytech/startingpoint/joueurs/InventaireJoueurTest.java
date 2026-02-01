package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.objectifs.ObjectifJardinier;
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
    void testAjouterPoints() {
        assertEquals(0, inventaire.getScore());
        inventaire.ajouterPoints(10);
        assertEquals(10, inventaire.getScore());
    }

    @Test
    void testGestionBambous() {
        assertTrue(inventaire.isBambouEmpty());

        inventaire.ajouterBambou(Couleur.ROSE);
        assertEquals(1, inventaire.getBambous().get(Couleur.ROSE));
        assertFalse(inventaire.isBambouEmpty());

        boolean retraitReussi = inventaire.retirerBambou(Couleur.ROSE);
        assertTrue(retraitReussi);
        assertEquals(0, inventaire.getBambous().get(Couleur.ROSE));

        boolean retraitImpossible = inventaire.retirerBambou(Couleur.VERT);
        assertFalse(retraitImpossible);
    }

    @Test
    void testGestionObjectifs() {
        // CORRECTION : Constructeur (Couleur, Taille, Points)
        ObjectifJardinier obj = new ObjectifJardinier(Couleur.VERT, 4, 5);

        inventaire.ajouterObjectif(obj);
        assertTrue(inventaire.getObjectifs().contains(obj));

        inventaire.retirerObjectif(obj);
        assertFalse(inventaire.getObjectifs().contains(obj));
    }
}