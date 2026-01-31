package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.objectifs.ObjectifParcelle;
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
        // CORRECTION 1 : Constructeur à 3 arguments (2 points, 2 parcelles, Rose)
        ObjectifParcelle obj = new ObjectifParcelle(2, 2, Couleur.ROSE);
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

    @Test
    void testGestionBambous() {
        // CORRECTION 2 : La Map n'est pas vide, elle est initialisée à 0 partout.
        // On vérifie donc que la quantité de ROSE est bien à 0 au départ.
        assertEquals(0, inventaire.getBambous().get(Couleur.ROSE));

        // On ajoute un bambou rose
        inventaire.ajouterBambou(Couleur.ROSE);

        // On vérifie que le compteur est passé à 1
        assertEquals(1, inventaire.getBambous().get(Couleur.ROSE));

        // On le retire
        boolean retraitReussi = inventaire.retirerBambou(Couleur.ROSE);

        // Vérifications finales
        assertTrue(retraitReussi, "Le retrait aurait dû réussir");
        assertEquals(0, inventaire.getBambous().get(Couleur.ROSE), "Le compteur devrait être revenu à 0");
    }

    @Test
    void testRetirerBambouInexistant() {
        // Test supplémentaire : Essayer de retirer un bambou qu'on n'a pas
        boolean retrait = inventaire.retirerBambou(Couleur.VERT);
        assertFalse(retrait, "On ne devrait pas pouvoir retirer un bambou si le stock est à 0");
    }

    @Test
    void testCompteurObjectifsValides() {
        assertEquals(0, inventaire.getNombreObjectifsValides());

        inventaire.incrementerObjectifsValides();
        inventaire.incrementerObjectifsValides();

        assertEquals(2, inventaire.getNombreObjectifsValides());
    }

    @Test
    void testGestionIrrigation() {
        // Test pour les canaux d'irrigation
        assertEquals(0, inventaire.getNombreCanauxDisponibles());

        inventaire.ajouterIrrigation();
        assertEquals(1, inventaire.getNombreCanauxDisponibles());

        inventaire.retirerIrrigation();
        assertEquals(0, inventaire.getNombreCanauxDisponibles());
    }
}