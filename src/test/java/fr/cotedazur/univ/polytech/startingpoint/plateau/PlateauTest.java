package fr.cotedazur.univ.polytech.startingpoint.plateau;

import fr.cotedazur.univ.polytech.startingpoint.plateau.amenagements.Bassin;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PlateauTest {

    Plateau plateau;

    @BeforeEach
    void init() {
        plateau = new Plateau();
    }

    // =================================================================================
    // 1. TESTS DE PLACEMENT DES PARCELLES
    // =================================================================================

    @Test
    @DisplayName("Une parcelle posée à côté de l'étang est irriguée immédiatement et gagne un bambou")
    void testIrrigationImmediateEtang() {
        Position pos = new Position(1, 0); // Adjacent à (0,0)
        Parcelle p = new Parcelle(Couleur.VERT);

        // Au début : pas de bambou (0)
        assertEquals(0, p.getBambou().getNumberOfSections());

        boolean placementReussi = plateau.placerParcelle(p, pos);

        assertTrue(placementReussi);
        assertTrue(p.estIrriguee(), "La parcelle adjacente à l'étang doit être irriguée");
        assertEquals(1, p.getNbSectionsSurParcelle(), "Le bambou doit pousser à la première irrigation");
        assertTrue(plateau.getParcellesIrriguees().contains(pos));
    }

    @Test
    @DisplayName("Une parcelle posée loin de l'étang n'est pas irriguée et n'a pas de bambou")
    void testPasIrrigationLoin() {
        // Setup : On pose une parcelle intermédiaire pour respecter la règle de pose
        plateau.placerParcelle(new Parcelle(Couleur.ROSE), new Position(1, 0));
        plateau.placerParcelle(new Parcelle(Couleur.ROSE), new Position(0, 1));

        // Test : On pose une parcelle en (1, 1) (touche les deux précédentes, mais pas l'étang direct)
        Position posLoin = new Position(1, 1);
        Parcelle p = new Parcelle(Couleur.JAUNE);

        assertTrue(plateau.placerParcelle(p, posLoin));
        assertFalse(p.estIrriguee());
        assertEquals(0, p.getNbSectionsSurParcelle());
    }

    @Test
    @DisplayName("Une parcelle avec aménagement BASSIN est irriguée immédiatement même loin")
    void testAmenagementBassin() {
        // Setup : Intermédiaires
        plateau.placerParcelle(new Parcelle(Couleur.ROSE), new Position(1, 0));
        plateau.placerParcelle(new Parcelle(Couleur.ROSE), new Position(0, 1));

        Position posLoin = new Position(1, 1);
        Parcelle pBassin = new Parcelle(Couleur.JAUNE);
        // On force l'aménagement Bassin (simule la pioche ou TuileType)
        pBassin.fetchAmenagementAcqui(new Bassin(pBassin, pBassin.getBambou()));

        plateau.placerParcelle(pBassin, posLoin);

        assertTrue(pBassin.estIrriguee(), "Le bassin doit irriguer la parcelle");
        assertEquals(1, pBassin.getNbSectionsSurParcelle(), "Le bambou doit pousser grâce au bassin");
    }

    // =================================================================================
    // 2. TESTS DE PLACEMENT DES CANAUX (RÈGLES STRICTES)
    // =================================================================================

    @Test
    @DisplayName("Impossible de poser un canal si les parcelles n'existent pas")
    void testCanalSansParcelles() {
        Position p1 = new Position(1, 0);
        Position p2 = new Position(0, 1);
        // Aucune parcelle posée

        assertFalse(plateau.peutPlacerCanal(p1, p2));
        assertFalse(plateau.placerCanal(p1, p2));
    }

    @Test
    @DisplayName("Impossible de poser un canal sur une arête de l'étang (0,0)")
    void testCanalSurAreteEtang() {
        Position etang = Plateau.POSITION_ORIGINE; // (0,0)
        Position voisin = new Position(1, 0);

        plateau.placerParcelle(new Parcelle(Couleur.VERT), voisin);

        // Tente de poser un canal entre (0,0) et (1,0) -> INTERDIT
        assertFalse(plateau.peutPlacerCanal(etang, voisin), "Interdit de poser sur les bords de l'étang");
        assertFalse(plateau.placerCanal(etang, voisin));
    }

    @Test
    @DisplayName("Placement valide : Canal entre deux parcelles qui touchent l'étang (Coin)")
    void testCanalDepartEtang() {
        // P1 et P2 touchent l'étang -> Le canal entre P1 et P2 touche l'étang par un sommet
        Position p1 = new Position(1, 0);
        Position p2 = new Position(0, 1); // p1 et p2 sont adjacents entre eux ET à (0,0)

        plateau.placerParcelle(new Parcelle(Couleur.VERT), p1);
        plateau.placerParcelle(new Parcelle(Couleur.JAUNE), p2);

        assertTrue(plateau.peutPlacerCanal(p1, p2));
        assertTrue(plateau.placerCanal(p1, p2));
        assertEquals(1, plateau.getCanaux().size());
    }

    @Test
    @DisplayName("Placement invalide : Canal isolé (ne touche ni étang ni autre canal)")
    void testCanalIsole() {
        // Setup : Créer un "trou" loin de l'étang
        // (0,0) -> (1,0), (0,1) -> (1,1), (0,2) ...
        // On pose des parcelles pour avoir le droit géométrique
        Position pA = new Position(1, 0);
        Position pB = new Position(2, 0);
        Position pC = new Position(2, 1);

        plateau.placerParcelle(new Parcelle(Couleur.VERT), pA);
        plateau.placerParcelle(new Parcelle(Couleur.VERT), pB);
        plateau.placerParcelle(new Parcelle(Couleur.VERT), pC);

        // On veut poser un canal entre pB et pC (loin de l'étang, pas de canal avant)
        // pB(2,0) et pC(2,1) sont adjacents. Mais ne touchent pas (0,0).
        assertFalse(plateau.peutPlacerCanal(pB, pC), "Canal isolé interdit");
    }

    @Test
    @DisplayName("Extension de réseau : On peut poser un canal s'il touche un canal existant")
    void testExtensionReseau() {
        Position p1 = new Position(1, 0); // Touche étang
        Position p2 = new Position(0, 1); // Touche étang
        Position p3 = new Position(1, 1); // Loin

        plateau.placerParcelle(new Parcelle(Couleur.VERT), p1);
        plateau.placerParcelle(new Parcelle(Couleur.VERT), p2);
        plateau.placerParcelle(new Parcelle(Couleur.VERT), p3);

        // 1. Premier canal (Validé par règle du Coin Étang)
        assertTrue(plateau.placerCanal(p1, p2));

        // 2. Deuxième canal : Entre p2 et p3.
        // Ce canal touche le point p2, qui est touché par le canal précédent (p1-p2).
        // Donc c'est valide.
        assertTrue(plateau.peutPlacerCanal(p2, p3));
        assertTrue(plateau.placerCanal(p2, p3));
    }

    @Test
    @DisplayName("Placer un canal irrigue les parcelles adjacentes (et fait pousser si première fois)")
    void testIrrigationParCanal() {
        Position p1 = new Position(1, 0);
        Position p2 = new Position(0, 1);
        Position p3 = new Position(1, 1);

        plateau.placerParcelle(new Parcelle(Couleur.VERT), p1);
        plateau.placerParcelle(new Parcelle(Couleur.VERT), p2);

        // p3 est loin, non irriguée au début
        Parcelle parcelle3 = new Parcelle(Couleur.ROSE);
        plateau.placerParcelle(parcelle3, p3);
        assertFalse(parcelle3.estIrriguee());
        assertEquals(0, parcelle3.getNbSectionsSurParcelle());

        // 1. Canal p1-p2 (départ étang) -> ne change rien pour p3
        plateau.placerCanal(p1, p2);
        assertFalse(parcelle3.estIrriguee());

        // 2. Canal p2-p3 (extension) -> Irrigue p3 !
        plateau.placerCanal(p2, p3);

        assertTrue(parcelle3.estIrriguee());
        assertEquals(1, parcelle3.getNbSectionsSurParcelle(), "Le bambou doit pousser à la première irrigation");
    }

    @Test
    @DisplayName("Le bambou ne pousse PAS une deuxième fois si on ajoute un autre canal")
    void testPasDeDoublePousse() {
        Position p1 = new Position(1, 0);
        Position p2 = new Position(0, 1);
        Position p3 = new Position(1, 1);

        plateau.placerParcelle(new Parcelle(Couleur.VERT), p1);
        plateau.placerParcelle(new Parcelle(Couleur.VERT), p2);
        Parcelle parcelle3 = new Parcelle(Couleur.ROSE);
        plateau.placerParcelle(parcelle3, p3);

        // Premier canal qui irrigue p3
        plateau.placerCanal(p1, p2); // base
        plateau.placerCanal(p2, p3); // irrigue p3
        assertEquals(1, parcelle3.getNbSectionsSurParcelle());

        // On ajoute un AUTRE canal qui touche aussi p3 (p1-p3)
        // p1(1,0) et p3(1,1) sont adjacents
        assertTrue(plateau.placerCanal(p1, p3));

        // Vérification : p3 est toujours irriguée, mais le bambou n'a PAS grandi (toujours 1)
        assertTrue(parcelle3.estIrriguee());
        assertEquals(1, parcelle3.getNbSectionsSurParcelle(), "Le bambou ne doit pas pousser une 2ème fois");
    }
}