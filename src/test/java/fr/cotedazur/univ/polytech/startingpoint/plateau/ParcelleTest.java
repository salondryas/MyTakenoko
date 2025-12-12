package fr.cotedazur.univ.polytech.startingpoint.plateau;

import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParcelleTest {
    public Parcelle origine;
    public Parcelle origine2;

    @BeforeEach
    void init() {
        origine = new Parcelle(new Position(0,0), Couleur.AUCUNE);
        origine2 = new Parcelle(new Position(0,0), Couleur.AUCUNE);
    }

    @Test
    void testToString(){
        assertEquals("AUCUNE : (0, 0, 0)", origine.toString(), "Le toString de l'origine est incorrect");
    }

    @Test
    void testEquals() {
        assertTrue(origine.equals(origine2));
    }
}