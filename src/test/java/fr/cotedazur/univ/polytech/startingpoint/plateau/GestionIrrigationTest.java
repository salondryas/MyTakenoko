package fr.cotedazur.univ.polytech.startingpoint.plateau;

import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GestionIrrigationTest {

    GestionIrrigation gestionIrrigation;
    Map<Position, Parcelle> fausseGrille;

    @BeforeEach
    void setUp() {
        gestionIrrigation = new GestionIrrigation();
        fausseGrille = new HashMap<>();
        // On simule l'étang
        fausseGrille.put(GrillePlateau.POSITION_ORIGINE, GrillePlateau.PARCELLE_ORIGINE);
    }

    @Test
    void testPeutPlacerCanalDepuisEtang() {
        Position p1 = GrillePlateau.POSITION_ORIGINE;
        Position p2 = new Position(1, 0);
        fausseGrille.put(p2, new Parcelle(p2, Couleur.VERT));

        assertTrue(gestionIrrigation.peutPlacerCanal(p1, p2, fausseGrille),
                "On doit pouvoir placer un canal adjacent à l'étang");
    }

    @Test
    void testNePeutPasPlacerCanalFlottant() {
        Position p1 = new Position(1, 1);
        Position p2 = new Position(1, 2);
        fausseGrille.put(p1, new Parcelle(p1, Couleur.VERT));
        fausseGrille.put(p2, new Parcelle(p2, Couleur.VERT));

        assertFalse(gestionIrrigation.peutPlacerCanal(p1, p2, fausseGrille),
                "On ne peut pas placer un canal s'il n'est pas relié à une source d'eau");
    }

    @Test
    void testPropagationIrrigation() {
        Position p1 = GrillePlateau.POSITION_ORIGINE;
        Position p2 = new Position(1, 0);
        Parcelle parcelleVerte = new Parcelle(p2, Couleur.VERT);
        fausseGrille.put(p2, parcelleVerte);

        // Au début, pas irriguée
        assertFalse(parcelleVerte.estIrriguee());

        // On place le canal
        gestionIrrigation.placerCanal(p1, p2, fausseGrille);

        // La gestion irrigation doit avoir déclenché l'irrigation sur la parcelle
        assertTrue(parcelleVerte.estIrriguee());
    }
}