package fr.cotedazur.univ.polytech.startingpoint.plateau;

import fr.cotedazur.univ.polytech.startingpoint.plateau.amenagements.Bassin;
import fr.cotedazur.univ.polytech.startingpoint.plateau.amenagements.Enclos;
import fr.cotedazur.univ.polytech.startingpoint.plateau.amenagements.Engrais;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PiocheParcelleTest {
    PiocheParcelle pioche;

    @BeforeEach
    void setUp() {
        pioche = new PiocheParcelle();
    }

    @Test
    void testTailleInitiale() {
        // 11 Vertes + 9 Jaunes + 7 Roses = 27
        assertEquals(27, pioche.size());
    }

    @Test
    void testDistributionCouleurs() {
        int vertes = 0;
        int jaunes = 0;
        int roses = 0;

        while (!pioche.estVide()) {
            Parcelle p = pioche.piocher();
            if (p.getCouleur() == Couleur.VERT) vertes++;
            else if (p.getCouleur() == Couleur.JAUNE) jaunes++;
            else if (p.getCouleur() == Couleur.ROSE) roses++;
        }

        assertEquals(11, vertes, "Doit y avoir 11 parcelles vertes");
        assertEquals(9, jaunes, "Doit y avoir 9 parcelles jaunes");
        assertEquals(7, roses, "Doit y avoir 7 parcelles roses");
    }

    @Test
    void testPresenceAmenagements() {
        int bassins = 0;
        int enclos = 0;
        int engrais = 0;

        while (!pioche.estVide()) {
            Parcelle p = pioche.piocher();
            if (p.getIsAmenagee()) {
                if (p.getAmenagement() instanceof Bassin) bassins++;
                else if (p.getAmenagement() instanceof Enclos) enclos++;
                else if (p.getAmenagement() instanceof Engrais) engrais++;
            }
        }

        // Vérification des totaux selon les règles officielles
        // Vert(2) + Jaune(1) + Rose(1) = 4 de chaque type
        assertEquals(4, bassins, "Doit y avoir 4 bassins au total");
        assertEquals(4, enclos, "Doit y avoir 4 enclos au total");
        assertEquals(4, engrais, "Doit y avoir 4 engrais au total");
    }
}