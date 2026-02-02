package fr.cotedazur.univ.polytech.startingpoint.plateau.pioche;

import fr.cotedazur.univ.polytech.startingpoint.plateau.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SelectionParcelleTest {

    @Mock
    private PiocheParcelle pioche; // On mock la pioche pour vérifier les appels "remettreEnDessous"

    private List<Parcelle> options;
    private Parcelle p1, p2, p3;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // On crée 3 fausses parcelles
        p1 = new Parcelle(Couleur.ROSE);
        p2 = new Parcelle(Couleur.ROSE);
        p3 = new Parcelle(Couleur.JAUNE);

        options = new ArrayList<>();
        options.add(p1);
        options.add(p2);
        options.add(p3);
    }

    @Test
    void testValiderChoixNominal() {
        SelectionParcelle session = new SelectionParcelle(options, pioche);

        // ACT : Le joueur choisit P2 (Rose)
        System.out.println(options);
        Parcelle choix = session.validerChoix(p2);

        // ASSERT
        assertEquals(p2, choix, "La méthode doit retourner la parcelle choisie");

        // Vérification critique : Les deux autres (P1 et P3) doivent être remises sous la pioche
        verify(pioche, times(1)).remettreEnDessous(p1);
        verify(pioche, times(1)).remettreEnDessous(p3);
    }

    @Test
    void testValiderChoixImpossibleSiDejaFait() {
        SelectionParcelle session = new SelectionParcelle(options, pioche);
        session.validerChoix(p1); // Premier choix valide

        // Second appel -> Doit lancer une exception
        assertThrows(IllegalStateException.class, () -> {
            session.validerChoix(p2);
        });
    }

    @Test
    void testValiderChoixParcelleInexistante() {
        SelectionParcelle session = new SelectionParcelle(options, pioche);
        System.out.println(options);
        Parcelle intrus = new Parcelle(Couleur.VERT); // Une nouvelle instance, pas dans la liste

        // Doit lancer une exception car l'intrus n'est pas dans les options
        assertThrows(IllegalArgumentException.class, () -> {
            session.validerChoix(intrus);
        });
    }

    @Test
    void testGetFirst() {
        SelectionParcelle session = new SelectionParcelle(options, pioche);
        assertEquals(p1, session.getFirst());
    }
}