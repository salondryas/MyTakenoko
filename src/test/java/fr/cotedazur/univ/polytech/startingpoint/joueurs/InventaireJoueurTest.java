package fr.cotedazur.univ.polytech.startingpoint.joueurs;

import fr.cotedazur.univ.polytech.startingpoint.objectifs.Objectif;
import fr.cotedazur.univ.polytech.startingpoint.plateau.AmenagmentAttribuable;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class InventaireJoueurTest {

    InventaireJoueur inventaire;

    @Mock
    Objectif objectifMock1;
    @Mock
    Objectif objectifMock2;
    @Mock
    AmenagmentAttribuable amenagementMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        inventaire = new InventaireJoueur();
    }

    // =================================================================================
    // 1. TESTS FONCTIONNELS BASIQUES (AVEC MOCKS)
    // =================================================================================

    @Test
    void testGestionObjectifs() {
        // Ajout
        inventaire.ajouterObjectif(objectifMock1);
        assertEquals(1, inventaire.getObjectifs().size());
        assertTrue(inventaire.getObjectifs().contains(objectifMock1));

        // Ajout multiple
        inventaire.ajouterObjectif(objectifMock2);
        assertEquals(2, inventaire.getObjectifs().size());

        // Retrait
        inventaire.retirerObjectif(objectifMock1);
        assertEquals(1, inventaire.getObjectifs().size());
        assertFalse(inventaire.getObjectifs().contains(objectifMock1));
        assertTrue(inventaire.getObjectifs().contains(objectifMock2));
    }

    @Test
    void testGestionAmenagements() {
        assertEquals(0, inventaire.getNombreAmenagements());

        // Ajout
        inventaire.ajouterAmenagement(amenagementMock);
        assertEquals(1, inventaire.getNombreAmenagements());
        assertTrue(inventaire.hasAmenagement(amenagementMock));

        // Retrait
        boolean succes = inventaire.retirerAmenagement(amenagementMock);
        assertTrue(succes);
        assertEquals(0, inventaire.getNombreAmenagements());
        assertFalse(inventaire.hasAmenagement(amenagementMock));
    }

    @Test
    void testRetirerAmenagementInexistant() {
        assertFalse(inventaire.retirerAmenagement(amenagementMock),
                "Ne doit pas pouvoir retirer un aménagement qu'on n'a pas");
    }

    // =================================================================================
    // 2. TESTS DES BAMBOUS & COULEURS (CAS LIMITES)
    // =================================================================================

    @Test
    void testAjouterBambouInvalide() {
        // Cas limite : Ajouter la couleur AUCUNE (l'étang)
        inventaire.ajouterBambou(Couleur.AUCUNE);

        // Vérifier que rien n'a bougé
        assertEquals(0, inventaire.getTotalNumberOfBambous());
        assertFalse(inventaire.getBambous().containsKey(Couleur.AUCUNE),
                "La map ne doit pas contenir la clé AUCUNE");
    }

    @Test
    void testRetirerBambouVide() {
        // Cas limite : Retirer quand stock à 0
        boolean resultat = inventaire.retirerBambou(Couleur.VERT);
        assertFalse(resultat, "Retirer un bambou inexistant doit renvoyer false");
        assertEquals(0, inventaire.getBambous().get(Couleur.VERT), "Le compteur ne doit pas passer en négatif");
    }

    @Test
    void testIsBambouEmpty() {
        assertTrue(inventaire.isBambouEmpty());

        inventaire.ajouterBambou(Couleur.JAUNE);
        assertFalse(inventaire.isBambouEmpty());

        inventaire.retirerBambou(Couleur.JAUNE);
        assertTrue(inventaire.isBambouEmpty());
    }

    // =================================================================================
    // 3. TESTS IRRIGATION
    // =================================================================================

    @Test
    void testIrrigationLimites() {
        assertFalse(inventaire.aDesCanaux());
        assertEquals(0, inventaire.getNombreCanauxDisponibles());

        // Retrait impossible à 0
        assertFalse(inventaire.retirerIrrigation());
        assertEquals(0, inventaire.getNombreCanauxDisponibles()); // Toujours 0, pas -1

        // Ajout
        inventaire.ajouterIrrigation();
        assertTrue(inventaire.aDesCanaux());
        assertEquals(1, inventaire.getNombreCanauxDisponibles());

        // Retrait réussi
        assertTrue(inventaire.retirerIrrigation());
        assertEquals(0, inventaire.getNombreCanauxDisponibles());
    }

    // =================================================================================
    // 4. STRESS TESTS (ROBUSTESSE & PERFORMANCE)
    // =================================================================================

    @Test
    void testStressAjoutBambous() {
        // Simulation d'une partie très longue ou d'un bot "Panda Glouton" extrême
        int quantity = 100_000;

        // On ajoute 100 000 bambous
        IntStream.range(0, quantity).forEach(i -> inventaire.ajouterBambou(Couleur.ROSE));

        assertEquals(quantity, inventaire.getBambous().get(Couleur.ROSE));
        assertEquals(quantity, inventaire.getTotalNumberOfBambous());

        // On retire tout
        IntStream.range(0, quantity).forEach(i -> {
            boolean res = inventaire.retirerBambou(Couleur.ROSE);
            assertTrue(res, "Devrait pouvoir retirer à l'index " + i);
        });

        assertEquals(0, inventaire.getBambous().get(Couleur.ROSE));
        assertTrue(inventaire.isBambouEmpty());
    }

    @Test
    void testStressAjoutObjectifs() {
        // Vérifie que la liste tient le coup avec beaucoup d'objets
        int quantity = 10_000;

        // On utilise un mock unique pour économiser la mémoire du test,
        // mais on simule l'ajout multiple
        for (int i = 0; i < quantity; i++) {
            inventaire.ajouterObjectif(mock(Objectif.class));
        }

        assertEquals(quantity, inventaire.getObjectifs().size());

        // Vérification que getObjectifs() renvoie bien une copie (encapsulation)
        List<Objectif> copie = inventaire.getObjectifs();
        copie.clear();
        assertEquals(quantity, inventaire.getObjectifs().size(), "La liste interne ne doit pas être affectée par la copie");
    }

    @Test
    void testStressScore() {
        // Vérifie qu'on peut accumuler beaucoup de points (pas d'overflow int simple)
        // Max int ~ 2 milliards. On teste une valeur raisonnablement haute.
        inventaire.ajouterPoints(1_000_000);
        inventaire.ajouterPoints(500_000);

        assertEquals(1_500_000, inventaire.getScore());
    }
}