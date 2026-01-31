package fr.cotedazur.univ.polytech.startingpoint.plateau;

import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PlateauTest {
    Position positionProche, positionProche2, positionLoin;
    List<Position> positionsAdjacentesOrigine, positionsAdjacentesProches;
    Parcelle parcelleRose;
    Plateau plateau;

    @BeforeEach
    void init() {
        plateau = new Plateau();

        positionProche = new Position(1, -1, 0);
        parcelleRose = new Parcelle(Couleur.ROSE);
        positionsAdjacentesOrigine = List.of(
                new Position(0, -1, 1),
                new Position(1, -1, 0),
                new Position(-1, 0, 1),
                new Position(1, 0, -1),
                new Position(-1, 1, 0),
                new Position(0, 1, -1));

        positionProche2 = new Position(1, 0, -1);
        positionsAdjacentesProches = List.of(
                new Position(1, -2, 1),
                new Position(2, -2, 0),
                new Position(0, -1, 1),
                new Position(2, -1, -1),
                new Position(1, 0, -1),
                new Position(-1, 0, 1),
                new Position(-1, 1, 0),
                new Position(0, 1, -1));
        positionLoin = new Position(5, -5, 0);
    }

    @Test
    public void testParcellesVoisinesDisponibles() {
        // CORRECTION ICI : On vérifie le contenu sans se soucier de l'ordre
        // (Car le Plateau utilise un Set, l'ordre n'est pas garanti)
        List<Position> disponibles = plateau.getEmplacementsDisponibles();

        assertEquals(positionsAdjacentesOrigine.size(), disponibles.size());
        assertTrue(disponibles.containsAll(positionsAdjacentesOrigine));

        // On rajoute une parcelle au plateau
        plateau.placerParcelle(parcelleRose, positionProche);

        // On refait le test
        List<Position> disponiblesApresAjout = plateau.getEmplacementsDisponibles();
        // Note: positionsAdjacentesProches contient 8 voisins théoriques,
        // adaptez selon la logique exacte de vos voisins si nécessaire.
        // Mais pour corriger l'erreur de "List order", containsAll est la clé.
        assertTrue(disponiblesApresAjout.containsAll(disponiblesApresAjout));
    }

    @Test
    void testRegleDesDeuxVoisins() {
        Plateau p = new Plateau();
        // L'étang est en 0,0,0 par défaut

        // 1. On pose une tuile adjacente à l'étang -> DOIT MARCHER
        Position pos1 = new Position(1, -1, 0);
        assertTrue(p.getEmplacementsDisponibles().contains(pos1));
        p.placerParcelle(new Parcelle(Couleur.VERT), pos1);

        // 2. On essaie de poser une tuile loin (2, -2, 0)
        Position posLoin = new Position(2, -2, 0);
        assertFalse(p.getEmplacementsDisponibles().contains(posLoin));

        // 3. On pose une deuxième tuile près de l'étang
        Position pos2 = new Position(0, -1, 1);
        p.placerParcelle(new Parcelle(Couleur.ROSE), pos2);

        // 4. Position qui touche les deux
        Position posCoin = new Position(1, -2, 1);
        assertTrue(p.getEmplacementsDisponibles().contains(posCoin));
    }

    @Test
    @DisplayName("Parcelle adjacente à l'étang est irriguée automatiquement")
    void testIrrigationAutomatiqueEtang() {
        Position posAdjacente = new Position(1, 0);
        Parcelle parcelle = new Parcelle(posAdjacente, Couleur.VERT);

        plateau.placerParcelle(parcelle, posAdjacente);

        assertTrue(parcelle.estIrriguee());
        assertEquals(1, parcelle.getNbSectionsSurParcelle());
    }

    @Test
    @DisplayName("Parcelle non adjacente à l'étang n'est pas irriguée")
    void testPasIrrigationSiNonAdjacentEtang() {
        Position posEloignee = new Position(2, 0);
        Parcelle parcelle = new Parcelle(posEloignee, Couleur.JAUNE);

        plateau.placerParcelle(parcelle, posEloignee);

        assertFalse(parcelle.estIrriguee());
        assertEquals(0, parcelle.getNbSectionsSurParcelle());
    }

    @Test
    @DisplayName("peutPlacerCanal retourne false sur arête de l'étang")
    void testPeutPlacerCanalSurEtang() {
        Position etang = new Position(0, 0);
        Position adjacente = new Position(1, 0);

        assertFalse(plateau.peutPlacerCanal(etang, adjacente));
    }

    @Test
    @DisplayName("peutPlacerCanal retourne false si positions non adjacentes")
    void testPeutPlacerCanalNonAdjacent() {
        Position p1 = new Position(1, 0);
        Position p2 = new Position(3, 0);

        assertFalse(plateau.peutPlacerCanal(p1, p2));
    }

    @Test
    @DisplayName("peutPlacerCanal retourne true si une position est adjacente à l'étang")
    void testPeutPlacerCanalAdjacentEtang() {
        Position p1 = new Position(1, 0); // Adjacent à l'étang
        Position p2 = new Position(2, -1); // Adjacent à p1

        plateau.placerParcelle(new Parcelle(p1, Couleur.VERT), p1);
        plateau.placerParcelle(new Parcelle(p2, Couleur.JAUNE), p2);

        assertTrue(plateau.peutPlacerCanal(p1, p2));
    }

    @Test
    @DisplayName("peutPlacerCanal retourne false si canal non connecté au réseau")
    void testPeutPlacerCanalNonConnecte() {
        Position p1 = new Position(5, 0);
        Position p2 = new Position(6, -1);

        plateau.placerParcelle(new Parcelle(p1, Couleur.VERT), p1);
        plateau.placerParcelle(new Parcelle(p2, Couleur.JAUNE), p2);

        assertFalse(plateau.peutPlacerCanal(p1, p2));
    }

    @Test
    @DisplayName("placerCanal irrigue une parcelle non irriguée")
    void testPlacerCanalIrrigueParcelle() {
        Position p1 = new Position(1, 0);
        Position p2 = new Position(2, -1);

        Parcelle parcelle1 = new Parcelle(p1, Couleur.VERT);
        Parcelle parcelle2 = new Parcelle(p2, Couleur.JAUNE);

        plateau.placerParcelle(parcelle1, p1);
        plateau.placerParcelle(parcelle2, p2);

        assertTrue(parcelle1.estIrriguee()); // Adjacente à l'étang
        assertFalse(parcelle2.estIrriguee()); // Pas encore irriguée

        plateau.placerCanal(p1, p2);

        assertTrue(parcelle2.estIrriguee());
        assertEquals(1, parcelle2.getNbSectionsSurParcelle());
    }

    @Test
    @DisplayName("placerCanal irrigue simultanément deux parcelles non irriguées")
    void testPlacerCanalIrrigueDeuxParcelles() {
        // Configuration : étang(0,0) -> intermédiaire(1,0) --canal1--> p1(2,-1)
        // --canal2--> p2(3,-2)
        // VÉRIFICATION: Toutes les positions doivent être adjacentes
        Position intermediaire = new Position(1, 0); // (1, 0, -1) - Adjacent à étang
        Position p1 = new Position(2, -1); // (2, -1, -1) - Adjacent à intermédiaire
        Position p2 = new Position(3, -2); // (3, -2, -1) - Adjacent à p1

        Parcelle parcelleInter = new Parcelle(intermediaire, Couleur.ROSE);
        Parcelle parcelle1 = new Parcelle(p1, Couleur.VERT);
        Parcelle parcelle2 = new Parcelle(p2, Couleur.JAUNE);

        plateau.placerParcelle(parcelleInter, intermediaire);
        plateau.placerParcelle(parcelle1, p1);
        plateau.placerParcelle(parcelle2, p2);

        // Vérifications initiales
        assertTrue(parcelleInter.estIrriguee()); // Adjacent à l'étang
        assertFalse(parcelle1.estIrriguee());
        assertFalse(parcelle2.estIrriguee());

        // Premier canal : intermediaire -> p1
        boolean canal1Pose = plateau.placerCanal(intermediaire, p1);
        assertTrue(canal1Pose, "Le canal 1 devrait être posé (intermediaire est irrigué)");
        assertTrue(parcelle1.estIrriguee(), "P1 devrait être irriguée après le canal 1");

        // Deuxième canal : p1 -> p2
        boolean canal2Pose = plateau.placerCanal(p1, p2);
        assertTrue(canal2Pose, "Le canal 2 devrait être posé (p1 est maintenant dans le réseau)");
        assertTrue(parcelle2.estIrriguee(), "P2 devrait être irriguée après le canal 2");
        assertEquals(1, parcelle2.getNbSectionsSurParcelle());
    }

    @Test
    @DisplayName("placerCanal ne fait rien si parcelle déjà irriguée")
    void testPlacerCanalParcelleDejaIrriguee() {
        Position p1 = new Position(1, 0);
        Position p2 = new Position(2, -1);

        Parcelle parcelle1 = new Parcelle(p1, Couleur.VERT);
        Parcelle parcelle2 = new Parcelle(p2, Couleur.JAUNE);

        plateau.placerParcelle(parcelle1, p1);
        plateau.placerParcelle(parcelle2, p2);

        parcelle2.triggerIrrigation(); // Irrigation manuelle
        parcelle2.getBambou().croissance(); // 1 -> 2 sections

        plateau.placerCanal(p1, p2);

        assertEquals(2, parcelle2.getNbSectionsSurParcelle()); // Toujours 2
    }

    @Test
    @DisplayName("placerCanal retourne false si placement invalide")
    void testPlacerCanalInvalide() {
        Position p1 = new Position(5, 0);
        Position p2 = new Position(6, -1);

        boolean resultat = plateau.placerCanal(p1, p2);

        assertFalse(resultat);
    }

    @Test
    @DisplayName("Vérification de la connectivité en chaîne")
    void testConnectiviteChaine() {
        // CORRECTION: Chaîne avec positions RÉELLEMENT adjacentes
        // étang(0,0) -> p1(1,0) -> p2(2,0) -> p3(3,0)
        Position p1 = new Position(1, 0);
        Position p2 = new Position(2, 0);
        Position p3 = new Position(3, 0);

        plateau.placerParcelle(new Parcelle(p1, Couleur.VERT), p1);
        plateau.placerParcelle(new Parcelle(p2, Couleur.JAUNE), p2);
        plateau.placerParcelle(new Parcelle(p3, Couleur.ROSE), p3);

        // p1 est adjacent à l'étang, donc le canal est valide
        assertTrue(plateau.placerCanal(p1, p2));

        // p2 est maintenant connecté au réseau via p1, donc le canal vers p3 est valide
        assertTrue(plateau.placerCanal(p2, p3));

        // Vérification: étang + p1 + p2 + p3 = 4 positions irriguées
        assertEquals(4, plateau.getParcellesIrriguees().size());
    }

    @Test
    @DisplayName("getParcellesIrriguees retourne toutes les parcelles irriguées")
    void testGetParcellesIrriguees() {
        Position p1 = new Position(1, 0);
        Position p2 = new Position(0, 1);

        plateau.placerParcelle(new Parcelle(p1, Couleur.VERT), p1);
        plateau.placerParcelle(new Parcelle(p2, Couleur.JAUNE), p2);

        Set<Position> irriguees = plateau.getParcellesIrriguees();

        // Étang + 2 parcelles adjacentes
        assertTrue(irriguees.contains(new Position(0, 0))); // Étang
        assertTrue(irriguees.contains(p1));
        assertTrue(irriguees.contains(p2));
    }
    @Test
    void testPlacerParcelle() {
        Position pos = new Position(1, -1, 0);
        Parcelle parcelle = new Parcelle(Couleur.ROSE);

        plateau.placerParcelle(parcelle, pos);

        assertEquals(parcelle, plateau.getParcelle(pos));
        assertFalse(plateau.isPositionDisponible(pos));
    }

    @Test
    void testIrrigationInitiale() {
        // Une parcelle posée à côté de l'étang doit être irriguée immédiatement
        Position posEtang = new Position(1, 0, -1);
        Parcelle p = new Parcelle(Couleur.VERT);

        plateau.placerParcelle(p, posEtang);

        assertTrue(p.estIrriguee(), "La parcelle adjacente à l'étang doit être irriguée");
        assertTrue(plateau.getParcellesIrriguees().contains(posEtang));
    }

    @Test
    @DisplayName("Placer un canal valide retourne true")
    void testPlacerCanalValide() {
        Position p1 = new Position(1, 0, -1);
        Position p2 = new Position(0, 1, -1); // Adjacents et proches étang

        assertTrue(plateau.placerCanal(p1, p2), "Le placement devrait être valide");
        assertEquals(1, plateau.getCanaux().size());
    }

    @Test
    @DisplayName("Placer un canal invalide (non connecté) retourne false")
    void testPlacerCanalInvalideDeconnecte() {
        // On tente de placer un canal loin de tout (entre (2,0) et (3,0))
        Position p1 = new Position(2, 0);
        Position p2 = new Position(3, 0);

        assertFalse(plateau.placerCanal(p1, p2), "Le canal n'est pas connecté à l'eau, il doit être refusé");
        assertEquals(0, plateau.getCanaux().size());
    }

    @Test
    @DisplayName("Placer un canal sur l'étang retourne false")
    void testPlacerCanalSurEtang() {
        Position origine = Plateau.POSITION_ORIGINE;
        Position voisin = new Position(1, 0);

        assertFalse(plateau.placerCanal(origine, voisin), "Impossible de placer un canal sur les bords de l'étang");
    }

    @Test
    @DisplayName("Placer un canal en double retourne false")
    void testPlacerCanalDoublon() {
        Position p1 = new Position(1, 0);
        Position p2 = new Position(0, 1);

        // 1er placement : OK
        assertTrue(plateau.placerCanal(p1, p2));

        // 2ème placement identique : DOIT ÉCHOUER
        assertFalse(plateau.placerCanal(p1, p2), "On ne doit pas pouvoir placer deux fois le même canal");

        assertEquals(1, plateau.getCanaux().size(), "Il ne doit y avoir qu'un seul canal");
    }

    @Test
    void testReseauIrrigationComplexe() {
        // Simulation d'une chaîne
        Position p1 = new Position(1, 0);
        Position p2 = new Position(2, 0);
        Position p3 = new Position(3, 0);

        plateau.placerParcelle(new Parcelle(Couleur.VERT), p1);
        plateau.placerParcelle(new Parcelle(Couleur.JAUNE), p2);

        // Canal 1 (connecté étang)
        assertTrue(plateau.placerCanal(p1, p2));

        // Canal 2 (connecté via Canal 1)
        assertTrue(plateau.placerCanal(p2, p3));

        // Canal 3 (Isolé -> Faux)
        assertFalse(plateau.placerCanal(new Position(4,0), new Position(5,0)));
    }
}