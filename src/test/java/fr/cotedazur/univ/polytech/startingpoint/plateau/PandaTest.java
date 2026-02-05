package fr.cotedazur.univ.polytech.startingpoint.plateau;

import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PandaTest {
    Plateau plateau;
    Panda panda;

    @BeforeEach
    void setUp() {
        plateau = new Plateau();
        panda = new Panda(plateau);
    }

    @Test
    void testMangerBambouStandard() {
        Position pos = new Position(1, 0);
        Parcelle p = new Parcelle(pos, Couleur.VERT);

        // CORRECTION : On utilise la méthode existante
        // triggerIrrigation met irriguee=true ET ajoute la première section de bambou
        p.triggerIrrigation();

        // on fait pousser une fois le bambou
        p.getBambou().croissance();

        // On force l'ajout dans la grille (bypass règles d'adjacence pour le test
        // unitaire)
        plateau.getGrille().ajouterParcelle(p, pos);

        // 1. On déplace le panda sur la case
        panda.setPositionPanda(pos);

        // 2. Le panda mange
        boolean aMange = panda.mangerBambou(pos, plateau);

        assertTrue(aMange, "Le panda aurait dû manger");
        assertEquals(1, p.getNbSectionsSurParcelle(), "Le bambou devrait être à 0 après le passage du panda");
    }

    @Test
    void testCannotEatFlag() {
        Position pos = new Position(1, 0);
        Parcelle p = new Parcelle(pos, Couleur.VERT);

        // CORRECTION : triggerIrrigation() ajoute le premier bambou (taille = 1)
        p.triggerIrrigation();

        plateau.getGrille().ajouterParcelle(p, pos);

        // 1. D'ABORD on déplace le panda
        panda.setPositionPanda(pos);

        // 2. ENSUITE on lui interdit de manger (pour ce tour/action)
        panda.cannotEat();

        // 3. Action : Essayer de Manger
        boolean aMange = panda.mangerBambou(pos, plateau);

        // Vérif : Il n'a pas mangé
        assertEquals(1, p.getNbSectionsSurParcelle(), "Le bambou ne doit pas diminuer si cannotEat() est actif");
    }
}