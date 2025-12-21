package fr.cotedazur.univ.polytech.startingpoint.plateau;

import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PandaTest {
    private Plateau plateau;
    private Panda panda;

    @BeforeEach
    void setUp() {
        plateau = new Plateau();
        panda = new Panda();
    }

    @Test
    void testCaseConnecteeMaisInaccessibleEnUnCoup() {
        // 1. La parcelle alignée (tout droit à l'Est)
        // C'est un voisin direct de (0,0,0) sur l'axe r = -1
        Position posAlignee = new Position(1, -1, 0);

        // 2. La parcelle en "Virage"
        // On prend posAlignee et on décale sur un AUTRE axe.
        // (2, -1, -1) est voisine de (1, -1, 0) mais n'est pas alignée avec (0,0,0)
        Position posVirage = new Position(2, -1, -1);

        plateau.placerParcelle(new Parcelle(Couleur.VERT), posAlignee);
        plateau.placerParcelle(new Parcelle(Couleur.ROSE), posVirage);

        List<Position> cibles = plateau.getTrajetsLigneDroite(panda.getPositionPanda());

        // ASSERT
        assertTrue(cibles.contains(posAlignee), "La parcelle alignée doit être accessible.");

        assertFalse(cibles.contains(posVirage),
                "La parcelle (2, -1, -1) nécessite un virage, elle ne doit pas être accessible en ligne droite.");
    }

    @Test
    void testChoisirEtDeplacerActionComplete() {
        Position destination = new Position(1, 0, -1);
        Parcelle parcelle = new Parcelle(Couleur.JAUNE);
        plateau.placerParcelle(parcelle, destination);

        // On s'assure que le bambou a bien 1 section (taille par défaut)
        assertEquals(1, plateau.getNombreDeSectionsAPosition(destination));

        panda.choisirEtDeplacer(plateau);

        assertEquals(destination, panda.getPositionPanda(), "Le panda doit avoir atteint la destination.");
        assertEquals(0, plateau.getNombreDeSectionsAPosition(destination), "Le bambou doit avoir été mangé (0 section).");
    }
}