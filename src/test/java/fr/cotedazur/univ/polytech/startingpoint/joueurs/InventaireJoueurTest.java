package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.objectifs.Objectif;
import fr.cotedazur.univ.polytech.startingpoint.objectifs.ObjectifPoseur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventaireJoueurTest {
    InventaireJoueur inventaire;

    @BeforeEach
    void setUp() {
        inventaire = new InventaireJoueur();
    }

    @Test
    void ajouterEtVerifierObjectif() {
        // CORRECTION 1 : Ajout du 3ème argument (points) pour correspondre à votre
        // nouvelle classe ObjectifPoseur
        Objectif obj = new ObjectifPoseur(1, Couleur.JAUNE, 2);

        inventaire.ajouterObjectif(obj);

        // CORRECTION 2 : On utilise .size() sur la liste retournée, car
        // getNombreObjectifs() n'existe pas
        assertEquals(1, inventaire.getObjectifs().size());

        assertTrue(inventaire.getObjectifs().contains(obj));
    }

    @Test
    void gestionBambous() {
        inventaire.ajouterBambou(Couleur.ROSE);
        assertEquals(1, inventaire.getBambous().size());
        assertTrue(inventaire.getBambous().contains(Couleur.ROSE));

        inventaire.retirerBambou(Couleur.ROSE);
        assertEquals(0, inventaire.getBambous().size());
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

    @Test
    @DisplayName("Inventaire initialisé avec 0 canal")
    void testInventaireInitial() {
        assertEquals(0, inventaire.getNombreCanauxDisponibles());
        assertFalse(inventaire.aDesCanaux());
    }

    @Test
    @DisplayName("ajouterIrrigation ajoute un canal")
    void testAjouterIrrigation() {
        inventaire.ajouterIrrigation();

        assertEquals(1, inventaire.getNombreCanauxDisponibles());
        assertTrue(inventaire.aDesCanaux());
    }

    @Test
    @DisplayName("Ajout de plusieurs canaux")
    void testAjouterPlusieursCanaux() {
        inventaire.ajouterIrrigation();
        inventaire.ajouterIrrigation();
        inventaire.ajouterIrrigation();

        assertEquals(3, inventaire.getNombreCanauxDisponibles());
    }

    @Test
    @DisplayName("retirerIrrigation retire un canal et retourne true")
    void testRetirerIrrigation() {
        inventaire.ajouterIrrigation();
        inventaire.ajouterIrrigation();

        boolean resultat = inventaire.retirerIrrigation();

        assertTrue(resultat);
        assertEquals(1, inventaire.getNombreCanauxDisponibles());
    }

    @Test
    @DisplayName("retirerIrrigation retourne false si aucun canal disponible")
    void testRetirerIrrigationVide() {
        boolean resultat = inventaire.retirerIrrigation();

        assertFalse(resultat);
        assertEquals(0, inventaire.getNombreCanauxDisponibles());
    }
}