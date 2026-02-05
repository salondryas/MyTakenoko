package fr.cotedazur.univ.polytech.startingpoint.elements;

import fr.cotedazur.univ.polytech.startingpoint.elements.plateau.Plateau;
import fr.cotedazur.univ.polytech.startingpoint.elements.reserve.Parcelle;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlateauTest {

    Plateau plateau;

    @BeforeEach
    void init() {
        plateau = new Plateau();
    }

    @Test
    @DisplayName("placerParcelle doit mettre à jour la grille et l'irrigation (Cas adjacent étang)")
    void testPlacementAdjacentEtang() {
        Position pos = new Position(1, 0); // Adjacent (0,0)
        Parcelle p = new Parcelle(Couleur.VERT);

        // Ici on utilise la méthode normale car c'est un coup légal (touche l'étang)
        boolean succes = plateau.placerParcelle(p, pos);

        assertTrue(succes, "Le placement doit réussir");
        assertTrue(p.estIrriguee(), "La parcelle doit être irriguée car adjacente à l'étang");
        assertEquals(1, p.getNbSectionsSurParcelle(), "Un bambou doit avoir poussé");
    }

    @Test
    @DisplayName("placerCanal doit connecter l'irrigation")
    void testIrrigationParCanal() {
        // Setup : Etang(0,0) -- p1(1,0) -- p2(2,0)
        Position p1 = new Position(1, 0);
        Position p2 = new Position(2, 0);

        // 1. On injecte les parcelles directement dans la grille
        // pour ne pas être bloqué par la règle "il faut 2 voisins"
        Parcelle parcelle1 = new Parcelle(Couleur.VERT);
        parcelle1.triggerIrrigation(); // Elle touche l'étang
        plateau.getGrille().ajouterParcelle(parcelle1, p1);

        Parcelle parcelle2 = new Parcelle(Couleur.ROSE);
        // Elle ne touche pas l'étang, donc pas encore irriguée
        plateau.getGrille().ajouterParcelle(parcelle2, p2);

        assertFalse(parcelle2.estIrriguee(), "Au début p2 est sèche");

        // Action : Créer un canal entre p1 et p2
        // p1 est adjacente à l'étang, donc on peut tirer un canal depuis p1
        boolean succes = plateau.placerCanal(p1, p2);

        assertTrue(succes, "Le canal devrait être valide (connecté à p1 qui touche l'étang)");
        assertTrue(parcelle2.estIrriguee(), "p2 doit devenir irriguée grâce au canal");
        assertEquals(1, parcelle2.getNbSectionsSurParcelle(), "Le bambou doit pousser sur p2");
    }

    @Test
    @DisplayName("Le bambou ne pousse qu'une seule fois à la première irrigation")
    void testPasDeDoublePousse() {
        Position p1 = new Position(1, 0); // Touche Etang
        Position p2 = new Position(0, 1); // Touche Etang
        Position cible = new Position(1, 1); // Touche p1 et p2

        // Injection directe pour simplifier le setup
        Parcelle parcelle1 = new Parcelle(Couleur.VERT); parcelle1.triggerIrrigation();
        plateau.getGrille().ajouterParcelle(parcelle1, p1);

        Parcelle parcelle2 = new Parcelle(Couleur.VERT); parcelle2.triggerIrrigation();
        plateau.getGrille().ajouterParcelle(parcelle2, p2);

        Parcelle pCible = new Parcelle(Couleur.ROSE);
        plateau.getGrille().ajouterParcelle(pCible, cible);

        // 1. Irrigation via p1
        plateau.placerCanal(p1, cible);
        assertTrue(pCible.estIrriguee());
        assertEquals(1, pCible.getNbSectionsSurParcelle(), "1ère pousse");

        // 2. Irrigation redondante via p2
        boolean succes = plateau.placerCanal(p2, cible);
        assertTrue(succes, "On peut placer un deuxième canal");

        // Vérification cruciale
        assertEquals(1, pCible.getNbSectionsSurParcelle(), "Le bambou ne doit pas repousser si on rajoute de l'eau à une parcelle déjà irriguée");
    }
}