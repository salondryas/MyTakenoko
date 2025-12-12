package fr.cotedazur.univ.polytech.startingpoint.objectifs;

import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ObjectifParcelleFormeTest {
    private List<List<Position>> configurationPossiblesTriangle;
    private List<List<Position>> configurationPossiblesLigne;

    @BeforeEach
    void setUp() {
        configurationPossiblesTriangle = ObjectifParcelleForme.TRIANGLE.getConfigurationsPossibles();
        configurationPossiblesLigne = ObjectifParcelleForme.LIGNE_DE_TROIS.getConfigurationsPossibles();
    }

    @Test
    void testConfiguation(){
        assertEquals(6, configurationPossiblesTriangle.size());
        assertEquals(3, configurationPossiblesLigne.size());
    }
}