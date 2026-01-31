package fr.cotedazur.univ.polytech.startingpoint.plateau;


import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CarteParcelleTest {

    @Test
    void testTotalParcelles() {
        int total = 0;
        for (CarteParcelle cp : CarteParcelle.values()) {
            total += cp.getNombreExemplaires();
        }
        assertEquals(27, total);
    }

    @Test
    void testDefinitionVerteBassin() {
        CarteParcelle cp = CarteParcelle.VERTE_BASSIN;
        assertEquals(Couleur.VERT, cp.getCouleur());
        assertEquals(2, cp.getNombreExemplaires());
        assertNotNull(cp.getGenerateurAmenagement());
    }
}