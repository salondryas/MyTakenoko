package fr.cotedazur.univ.polytech.startingpoint.elements;

import fr.cotedazur.univ.polytech.startingpoint.elements.plateau.GrillePlateau;
import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.ExplorateurPlateau;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ExplorateurPlateauTest {

    Map<Position, Parcelle> fausseGrille;

    @BeforeEach
    void setUp() {
        fausseGrille = new HashMap<>();
        fausseGrille.put(GrillePlateau.POSITION_ORIGINE, GrillePlateau.PARCELLE_ORIGINE);
    }

    @Test
    void testEmplacementsDisponiblesDebutPartie() {
        // Autour de l'étang (0,0), les 6 positions sont disponibles
        List<Position> dispos = ExplorateurPlateau.getEmplacementsDisponibles(fausseGrille);
        assertEquals(6, dispos.size());
    }

    @Test
    void testRegleDesDeuxVoisins() {
        // On pose une parcelle en (1,0) (adjacent étang)
        Position p1 = new Position(1, 0);
        fausseGrille.put(p1, new Parcelle(p1, Couleur.VERT));

        // CAS 1 : (2,0)
        // Voisins de (2,0) : (1,0) existe. (3,0), (2,-1), (1,1)... n'existent pas.
        // Ne touche pas l'étang (0,0).
        // Donc 1 seul voisin -> Interdit.
        Position pInterdite = new Position(2, 0);
        assertFalse(ExplorateurPlateau.isPositionDisponible(fausseGrille, pInterdite),
                "Devrait être faux car (2,0) n'a qu'un seul voisin (1,0)");

        // CAS 2 : (1,1)
        // Voisins de (1,1) : (1,0) existe. (0,1), (0,2), (1,2), (2,1), (2,0).
        // (1,1) NE TOUCHE PAS l'étang (0,0) en coordonnées axiales !
        // Pour la rendre valide, il faut ajouter un deuxième voisin, par exemple (0,1).

        Position p2 = new Position(0, 1);
        fausseGrille.put(p2, new Parcelle(p2, Couleur.ROSE)); // On ajoute le 2ème voisin

        Position pAutorisee = new Position(1, 1);

        assertTrue(ExplorateurPlateau.isPositionDisponible(fausseGrille, pAutorisee),
                "Devrait être vrai car (1,1) a maintenant 2 voisins : (1,0) et (0,1)");
    }

    @Test
    void testTrajetsLigneDroite() {
        // Ligne droite (0,0) -> (1,0) -> (2,0)
        fausseGrille.put(new Position(1, 0), new Parcelle(Couleur.VERT));
        fausseGrille.put(new Position(2, 0), new Parcelle(Couleur.VERT));

        List<Position> trajets = ExplorateurPlateau.getTrajetsLigneDroite(fausseGrille, GrillePlateau.POSITION_ORIGINE);

        assertTrue(trajets.contains(new Position(1, 0)));
        assertTrue(trajets.contains(new Position(2, 0)));
        assertFalse(trajets.contains(new Position(3, 0)), "Ne doit pas contenir (3,0) car vide");
    }
}