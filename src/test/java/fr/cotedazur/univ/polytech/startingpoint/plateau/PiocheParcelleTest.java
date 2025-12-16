package fr.cotedazur.univ.polytech.startingpoint.plateau;

import fr.cotedazur.univ.polytech.startingpoint.joueurs.Bot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.cotedazur.univ.polytech.startingpoint.plateau.PiocheParcelle.NOMBRE_PARCELLES_INITIAL;
import static org.junit.jupiter.api.Assertions.*;

public class PiocheParcelleTest {
    PiocheParcelle piocheParcelle;

    @BeforeEach
    void init() {
        piocheParcelle = new PiocheParcelle();
    }

    @Test
    void testPiocheParcelles() {
        // Voir si le nombre de cartes dans la pioche est cohérent
        assertEquals(NOMBRE_PARCELLES_INITIAL, piocheParcelle.getSize());
        piocheParcelle.piocherParcelle();
        assertEquals(NOMBRE_PARCELLES_INITIAL-1, piocheParcelle.getSize()); //On regarde si la pioche a été réduite après qu'un joueur ait retiré une parcelle
        // On tire jusqu'à ne plus rien avoir dans la pioche
        for (int i = 0; i<NOMBRE_PARCELLES_INITIAL-1; i++) {
            piocheParcelle.piocherParcelle();
        }
        // La pioche est vide !
        assertEquals(0,piocheParcelle.getSize());
    }
}
