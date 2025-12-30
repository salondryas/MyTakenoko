package fr.cotedazur.univ.polytech.startingpoint.plateau;

import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Couleur;
import fr.cotedazur.univ.polytech.startingpoint.utilitaires.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JardinierTest {

    private Plateau plateau;
    private Jardinier jardinier; // J'utilise ton nom de classe "Jardiner"

    @BeforeEach
    void setUp() {
        plateau = new Plateau();
        jardinier = new Jardinier();
    }

    @Test
    void testActionPousserBambou() {
        Position posCible = new Position(1, -1);
        Parcelle parcelle = new Parcelle(Couleur.VERT);
        plateau.placerParcelle(parcelle, posCible);

        // On note la taille avant l'action
        int tailleAvant = parcelle.getBambou().getNumberOfSections();

        // Le jardinier effectue son action sur la case
        jardinier.pousserBambou(posCible, plateau);

        // On vérifie que la taille a augmenté de 1
        int tailleApres = parcelle.getBambou().getNumberOfSections();

        assertEquals(tailleAvant + 1, tailleApres,
                "Le bambou doit grandir d'une section après l'action du jardinier.");
    }
}